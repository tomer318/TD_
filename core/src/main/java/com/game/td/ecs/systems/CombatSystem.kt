package com.game.td.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.*
import com.game.td.managers.CheatManager
import com.game.td.managers.EconomyManager
import com.game.td.managers.SoundManager
import com.game.td.managers.UpgradeManager

private val ArmorType.physicalReduction: Float
    get() = when (this) {
        ArmorType.NONE -> 0f
        ArmorType.LIGHT_ARMOR -> 0.25f
        ArmorType.MEDIUM_ARMOR -> 0.50f
        ArmorType.HEAVY_ARMOR -> 0.75f
        ArmorType.GREAT_ARMOR -> 0.95f
    }

class CombatSystem(
    private val onDeathRayTriggered: (Vector2, Vector2) -> Unit = { _, _ -> },
    private val onTeleportTriggered: (Vector2) -> Unit = {},
    private val onSniperShotTriggered: (Vector2, Vector2) -> Unit = { _, _ -> },
    private val onShrapnelExploded: (Vector2) -> Unit = {},
    private val onForestWrathTriggered: (Vector2) -> Unit = {},
    private val onTeslaBoltTriggered: (Vector2, Vector2) -> Unit = { _, _ -> },
    private val onOverchargeTriggered: (Vector2, Float) -> Unit = { _, _ -> },
    private val onClusterBombsLaunched: (Vector2, Vector2, List<Vector2>, (Vector2) -> Unit) -> Unit = { _, _, _, _ -> },
    private val onEnemyKilled: (Entity) -> Unit = {}
) : IteratingSystem(
    Family.all(TowerComponent::class.java, TransformComponent::class.java).get()
) {
    private val towerMapper = ComponentMapper.getFor(TowerComponent::class.java)
    private val transformMapper = ComponentMapper.getFor(TransformComponent::class.java)
    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)

    private val enemyFamily = Family.all(
        EnemyComponent::class.java,
        TransformComponent::class.java,
        HealthComponent::class.java,
        MovementComponent::class.java
    ).exclude(SoldierComponent::class.java).get()

    private val soldierFamily = Family.all(SoldierComponent::class.java, TransformComponent::class.java, HealthComponent::class.java).get()

    private data class PoisonState(
        var dps: Float,
        var remainingDuration: Float = 3.0f,
        var tickTimer: Float = 0f
    )
    private val activePoisons = mutableMapOf<Entity, PoisonState>()
    private val wrathTrapCounts = mutableMapOf<Entity, Int>()

    private fun isBossOrMiniboss(type: EnemyType?): Boolean {
        return type in listOf(
            EnemyType.JUGGERNAUT, EnemyType.JT, EnemyType.VEZNAN, EnemyType.VEZNAN_DEMON,
            EnemyType.YETI, EnemyType.TROLL_CHIEFTAIN, EnemyType.OGRE, EnemyType.MAGMA_ELEMENTAL
        )
    }

    private fun isEnemyImmuneToPoison(type: EnemyType?): Boolean {
        if (type == null) return true
        return type.isImmuneToPoison || type in listOf(
            EnemyType.GIANT_SPIDER,
            EnemyType.SPIDER_MATRIARCH,
            EnemyType.SPIDER_HATCHLING,
            EnemyType.SKELETON,
            EnemyType.SKELETON_KNIGHT,
            EnemyType.MAGMA_ELEMENTAL
        )
    }

    // TÍNH TIẾN ĐỘ ĐI TRÊN ĐƯỜNG CỦA QUÁI (CÀNG GẦN CỔNG THOÁT GIÁ TRỊ CÀNG CAO)
    private fun getPathProgressScore(enemy: Entity): Float {
        val move = enemy.getComponent(MovementComponent::class.java) ?: return 0f
        val pos = transformMapper.get(enemy)?.position ?: return 0f
        val wpIdx = move.currentWaypointIndex
        val distToNextWp = if (wpIdx in move.waypoints.indices) pos.dst(move.waypoints[wpIdx]) else 0f
        return (wpIdx * 1000f) - distToNextWp
    }

    override fun update(deltaTime: Float) {
        if (activePoisons.isNotEmpty()) {
            val poisonedSnapshot = activePoisons.entries.toList()
            for ((enemy, state) in poisonedSnapshot) {
                if (!activePoisons.containsKey(enemy)) continue

                val hp = healthMapper.get(enemy)
                val move = enemy.getComponent(MovementComponent::class.java)

                if (hp == null || hp.isDead || hp.currentHealth <= 0f) {
                    activePoisons.remove(enemy)
                    wrathTrapCounts.remove(enemy)
                    continue
                }

                if (move?.isInsideTunnel == true) continue

                state.remainingDuration -= deltaTime
                state.tickTimer += deltaTime

                if (state.tickTimer >= 0.5f) {
                    state.tickTimer -= 0.5f
                    damageEnemy(enemy, hp, state.dps * 0.5f, DamageType.TRUE)
                    if (hp.isDead || hp.currentHealth <= 0f) {
                        activePoisons.remove(enemy)
                        wrathTrapCounts.remove(enemy)
                        continue
                    }
                }

                if (state.remainingDuration <= 0f) {
                    activePoisons.remove(enemy)
                }
            }
        }

        super.update(deltaTime)
    }

    override fun processEntity(tower: Entity, deltaTime: Float) {
        val towerComp = towerMapper.get(tower) ?: return
        val towerTransform = transformMapper.get(tower) ?: return

        if (towerComp.towerType == TowerType.BARRACKS) return
        if (towerComp.towerType == TowerType.SUNRAY) return
        if (towerComp.specialization == TowerSpecialization.SUNRAY_TOWER) return
        if (towerComp.isFrozen) return

        if (towerComp.attackCooldown > 0f) towerComp.attackCooldown -= deltaTime
        if (towerComp.skill1Cooldown > 0f) towerComp.skill1Cooldown -= deltaTime
        if (towerComp.skill2Cooldown > 0f) towerComp.skill2Cooldown -= deltaTime

        val enemiesSnapshot = engine.getEntitiesFor(enemyFamily).toList()
        val inRangeEnemies = mutableListOf<Entity>()

        val isRangerPoisonActive = (towerComp.specialization == TowerSpecialization.RANGERS_HIDEOUT && towerComp.skill1Level > 0)
        val isArtilleryGroundOnly = (towerComp.towerType == TowerType.ARTILLERY && towerComp.specialization != TowerSpecialization.TESLA_X104)

        for (enemy in enemiesSnapshot) {
            val enemyHealth = healthMapper.get(enemy)
            if (enemyHealth == null || enemyHealth.isDead || enemyHealth.currentHealth <= 0f) continue

            val move = enemy.getComponent(MovementComponent::class.java)
            if (move?.isInsideTunnel == true) continue

            val enemyComp = enemy.getComponent(EnemyComponent::class.java)
            if (isArtilleryGroundOnly && enemyComp?.enemyType?.isFlying == true) continue

            val enemyPos = transformMapper.get(enemy)?.position ?: continue
            val dist = towerTransform.position.dst(enemyPos)

            if (dist <= towerComp.range) {
                inRangeEnemies.add(enemy)
            }
        }

        // SẮP XẾP DANH SÁCH TRONG TẦM THEO THỨ TỰ GẦN CỔNG THOÁT NHẤT
        inRangeEnemies.sortByDescending { getPathProgressScore(it) }

        var targetEnemy: Entity? = null

        if (inRangeEnemies.size == 1) {
            targetEnemy = inRangeEnemies[0]
        } else if (inRangeEnemies.size > 1) {
            // FOCUS FIRE CHUẨN KINGDOM RUSH:
            // - Nếu là Rangers Hideout có Poison Arrows: Ưu tiên rải độc vào mục tiêu chưa trúng độc trước, sau đó dồn bắn con đi đầu.
            // - Tất cả các tháp khác: DỒN HỎA LỰC (Focus Fire) vào con đi đầu gần cổng thoát nhất để kết liễu dứt điểm từng con!
            targetEnemy = if (isRangerPoisonActive) {
                inRangeEnemies.firstOrNull { e ->
                    val ec = e.getComponent(EnemyComponent::class.java)
                    !isEnemyImmuneToPoison(ec?.enemyType) && !activePoisons.containsKey(e)
                } ?: inRangeEnemies.first()
            } else {
                inRangeEnemies.first()
            }
        }

        towerComp.currentTarget = targetEnemy

        // 1. KỸ NĂNG BIG BERTHA: DRAGON MISSILE & CLUSTER BOMBS
        if (towerComp.level == 4 && towerComp.specialization == TowerSpecialization.BIG_BERTHA) {
            if (towerComp.skill1Level > 0 && towerComp.skill1Cooldown <= 0f) {
                var missileVictim: Entity? = null
                var maxHpFound = -1f

                for (e in enemiesSnapshot) {
                    val hp = healthMapper.get(e)
                    val m = e.getComponent(MovementComponent::class.java)
                    val ec = e.getComponent(EnemyComponent::class.java)
                    if (hp != null && !hp.isDead && m?.isInsideTunnel != true && ec?.enemyType?.isFlying != true) {
                        if (hp.currentHealth > maxHpFound) {
                            maxHpFound = hp.currentHealth
                            missileVictim = e
                        }
                    }
                }

                missileVictim?.let { victim ->
                    val vTrans = transformMapper.get(victim)
                    val vHp = healthMapper.get(victim)
                    if (vTrans != null && vHp != null && !vHp.isDead) {
                        val impactPos = Vector2(vTrans.position)
                        SoundManager.playBombExplosion()
                        onSniperShotTriggered(Vector2(towerTransform.position), impactPos)
                        val missileDmg = 120f + towerComp.skill1Level * 100f

                        val snapshot = engine.getEntitiesFor(enemyFamily).toList()
                        for (nearE in snapshot) {
                            val nHp = healthMapper.get(nearE)
                            val nTrans = transformMapper.get(nearE)
                            val nEc = nearE.getComponent(EnemyComponent::class.java)
                            if (nHp != null && !nHp.isDead && nTrans != null && nEc?.enemyType?.isFlying != true) {
                                if (nTrans.position.dst(impactPos) <= 95f) {
                                    damageEnemy(nearE, nHp, missileDmg, DamageType.EXPLOSIVE)
                                }
                            }
                        }
                        towerComp.skill1Cooldown = 11.0f
                    }
                }
            }

            if (towerComp.skill2Level > 0 && towerComp.skill2Cooldown <= 0f && targetEnemy != null) {
                val tTrans = transformMapper.get(targetEnemy)
                val tHp = healthMapper.get(targetEnemy)

                if (tTrans != null && tHp != null && !tHp.isDead) {
                    val tPos = Vector2(tTrans.position)
                    val tMove = targetEnemy.getComponent(MovementComponent::class.java)

                    val roadDir = Vector2(1f, 0f)
                    if (tMove != null && tMove.waypoints.isNotEmpty() && tMove.currentWaypointIndex in tMove.waypoints.indices) {
                        val nextWp = tMove.waypoints[tMove.currentWaypointIndex]
                        roadDir.set(nextWp).sub(tPos).nor()
                        if (roadDir.isZero) roadDir.set(1f, 0f)
                    }

                    val spacing = 45f
                    val bombPoints = listOf(
                        Vector2(tPos).sub(Vector2(roadDir).scl(spacing)),
                        Vector2(tPos),
                        Vector2(tPos).add(Vector2(roadDir).scl(spacing))
                    )

                    val clusterDmg = 55f + towerComp.skill2Level * 35f
                    SoundManager.playArtilleryFire()

                    onClusterBombsLaunched(
                        Vector2(towerTransform.position),
                        Vector2(tPos),
                        bombPoints
                    ) { explodePos ->
                        SoundManager.playBombExplosion()
                        val snapshot = engine.getEntitiesFor(enemyFamily).toList()
                        for (nearE in snapshot) {
                            val nHp = healthMapper.get(nearE)
                            val nTrans = transformMapper.get(nearE)
                            val nEc = nearE.getComponent(EnemyComponent::class.java)
                            if (nHp != null && !nHp.isDead && nTrans != null && nEc?.enemyType?.isFlying != true) {
                                if (nTrans.position.dst(explodePos) <= 45f) {
                                    damageEnemy(nearE, nHp, clusterDmg, DamageType.EXPLOSIVE)
                                }
                            }
                        }
                    }
                    towerComp.skill2Cooldown = 9.0f
                }
            }
        }

        // 2. KỸ NĂNG MUSKETEER GARRISON: SNIPER SHOT & SHRAPNEL SHOT
        if (towerComp.level == 4 && towerComp.specialization == TowerSpecialization.MUSKETEER_GARRISON) {
            if (towerComp.skill1Level > 0 && towerComp.skill1Cooldown <= 0f) {
                var sniperTarget: Entity? = null
                var highestHp = -1f

                for (e in enemiesSnapshot) {
                    val hp = healthMapper.get(e)
                    val m = e.getComponent(MovementComponent::class.java)
                    val eTrans = transformMapper.get(e)
                    if (hp != null && !hp.isDead && m?.isInsideTunnel != true && eTrans != null &&
                        towerTransform.position.dst(eTrans.position) <= towerComp.range * 1.5f) {
                        if (hp.currentHealth > highestHp) {
                            highestHp = hp.currentHealth
                            sniperTarget = e
                        }
                    }
                }

                sniperTarget?.let { victim ->
                    val vTrans = transformMapper.get(victim)
                    val vHp = healthMapper.get(victim)
                    if (vTrans != null && vHp != null && !vHp.isDead) {
                        SoundManager.playSniperShot()
                        onSniperShotTriggered(Vector2(towerTransform.position), Vector2(vTrans.position))
                        val instantKillChance = 0.20f * towerComp.skill1Level
                        val eComp = victim.getComponent(EnemyComponent::class.java)
                        val isImmuneToInstakill = isBossOrMiniboss(eComp?.enemyType)

                        if (!isImmuneToInstakill && MathUtils.randomBoolean(instantKillChance)) {
                            damageEnemy(victim, vHp, vHp.currentHealth, DamageType.TRUE)
                        } else {
                            val regularDmg = MathUtils.random(towerComp.damageMin, towerComp.damageMax)
                            val failedSniperDmg = (instantKillChance * vHp.currentHealth) + regularDmg
                            damageEnemy(victim, vHp, failedSniperDmg, DamageType.PHYSICAL)
                        }
                        towerComp.skill1Cooldown = 14.0f
                    }
                }
            }

            if (towerComp.skill2Level > 0 && towerComp.skill2Cooldown <= 0f) {
                val shortRange = towerComp.range * 0.5f
                var closeGroundTarget: Entity? = null

                for (candidate in inRangeEnemies) {
                    val cTrans = transformMapper.get(candidate) ?: continue
                    val cHp = healthMapper.get(candidate) ?: continue
                    val cComp = candidate.getComponent(EnemyComponent::class.java)
                    val cMove = candidate.getComponent(MovementComponent::class.java)

                    if (!cHp.isDead && cMove?.isInsideTunnel != true && cComp?.enemyType?.isFlying != true) {
                        if (towerTransform.position.dst(cTrans.position) <= shortRange) {
                            closeGroundTarget = candidate
                            break
                        }
                    }
                }

                closeGroundTarget?.let { victim ->
                    val vTrans = transformMapper.get(victim)
                    val vHp = healthMapper.get(victim)
                    if (vTrans != null && vHp != null && !vHp.isDead) {
                        val impactPos = Vector2(vTrans.position)
                        SoundManager.playBombExplosion()
                        onShrapnelExploded(Vector2(impactPos))

                        val grapeMin = 10f * towerComp.skill2Level
                        val grapeMax = 40f * towerComp.skill2Level
                        val hasSmartTargeting = UpgradeManager.getEffectiveArtilleryLevel() >= 5
                        val blastRadius = 48f

                        var totalShrapnelDmg = 0f
                        for (g in 0 until 6) {
                            totalShrapnelDmg += if (hasSmartTargeting) grapeMax else MathUtils.random(grapeMin, grapeMax)
                        }

                        val snapshot = engine.getEntitiesFor(enemyFamily).toList()
                        for (otherE in snapshot) {
                            val oTrans = transformMapper.get(otherE)
                            val oHp = healthMapper.get(otherE)
                            val oMove = otherE.getComponent(MovementComponent::class.java)
                            if (oTrans != null && oHp != null && !oHp.isDead && oMove?.isInsideTunnel != true &&
                                oTrans.position.dst(impactPos) <= blastRadius) {
                                damageEnemy(otherE, oHp, totalShrapnelDmg, DamageType.EXPLOSIVE)
                            }
                        }
                        towerComp.skill2Cooldown = 9.0f
                    }
                }
            }
        }

        // 3. KỸ NĂNG ARCANE WIZARD: DEATH RAY & GROUP TELEPORT (DỊCH CHUYỂN CẢ CỤM 4/5/6 QUÁI CHUẨN WIKI)
        if (towerComp.level == 4 && towerComp.specialization == TowerSpecialization.ARCANE_WIZARD) {
            if (towerComp.skill1Level > 0 && towerComp.skill1Cooldown <= 0f) {
                val candidate = inRangeEnemies.filter {
                    val hp = healthMapper.get(it)
                    hp != null && !hp.isDead
                }.maxByOrNull {
                    healthMapper.get(it)?.currentHealth ?: 0f
                }
                candidate?.let { victim ->
                    val vTrans = transformMapper.get(victim)
                    val vHp = healthMapper.get(victim)
                    val vComp = victim.getComponent(EnemyComponent::class.java)
                    if (vTrans != null && vHp != null && !vHp.isDead) {
                        SoundManager.playDeathRay()
                        onDeathRayTriggered(Vector2(towerTransform.position), Vector2(vTrans.position))
                        val isBoss = (vComp?.enemyType in listOf(
                            EnemyType.JUGGERNAUT, EnemyType.JT, EnemyType.VEZNAN, EnemyType.VEZNAN_DEMON
                        ))

                        if (isBoss) {
                            damageEnemy(victim, vHp, 400f * towerComp.skill1Level, DamageType.TRUE)
                        } else {
                            damageEnemy(victim, vHp, vHp.currentHealth, DamageType.TRUE)
                        }
                        towerComp.skill1Cooldown = when (towerComp.skill1Level) {
                            1 -> 18f
                            2 -> 15f
                            else -> 12f
                        }
                    }
                }
            }

            if (towerComp.skill2Level > 0 && towerComp.skill2Cooldown <= 0f) {
                val eligibleForTeleport = inRangeEnemies.filter {
                    val hp = healthMapper.get(it)
                    val ec = it.getComponent(EnemyComponent::class.java)
                    hp != null && !hp.isDead && ec?.enemyType !in listOf(
                        EnemyType.JUGGERNAUT, EnemyType.JT, EnemyType.VEZNAN, EnemyType.VEZNAN_DEMON
                    )
                }

                if (eligibleForTeleport.isNotEmpty()) {
                    val leadVictim = eligibleForTeleport.first()
                    val leadPos = Vector2(transformMapper.get(leadVictim)?.position ?: Vector2())
                    val maxTeleportTargets = 3 + towerComp.skill2Level // Cấp 1: 4 con, Cấp 2: 5 con, Cấp 3: 6 con
                    val rollBackAmount = 2 + towerComp.skill2Level

                    // Gom các quái đứng gần con đi đầu để dịch chuyển cả cụm lùi lại
                    val groupToTeleport = eligibleForTeleport.filter {
                        val pos = transformMapper.get(it)?.position
                        pos != null && pos.dst(leadPos) <= 75f
                    }.take(maxTeleportTargets)

                    SoundManager.playTeleport()
                    onTeleportTriggered(leadPos)

                    for (victim in groupToTeleport) {
                        val vMove = victim.getComponent(MovementComponent::class.java)
                        val vTrans = transformMapper.get(victim)
                        if (vMove != null && vTrans != null && vMove.waypoints.isNotEmpty()) {
                            onTeleportTriggered(Vector2(vTrans.position))
                            vMove.currentWaypointIndex = (vMove.currentWaypointIndex - rollBackAmount).coerceAtLeast(0)
                            if (vMove.currentWaypointIndex in vMove.waypoints.indices) {
                                val wp = vMove.waypoints[vMove.currentWaypointIndex]
                                val jitterX = MathUtils.random(-8f, 8f)
                                val jitterY = MathUtils.random(-8f, 8f)
                                vTrans.position.set(wp.x + jitterX, wp.y + jitterY)
                            }
                        }
                    }
                    towerComp.skill2Cooldown = 9.5f
                }
            }
        }

        // 4. KỸ NĂNG RANGERS HIDEOUT: WRATH OF THE FOREST
        if (towerComp.level == 4 && towerComp.specialization == TowerSpecialization.RANGERS_HIDEOUT) {
            if (towerComp.skill2Level > 0 && towerComp.skill2Cooldown <= 0f) {
                val eligibleTargets = inRangeEnemies.filter {
                    val hp = healthMapper.get(it)
                    val ec = it.getComponent(EnemyComponent::class.java)
                    val timesTrapped = wrathTrapCounts.getOrDefault(it, 0)
                    hp != null && !hp.isDead &&
                        ec?.enemyType?.isFlying != true &&
                        !isBossOrMiniboss(ec?.enemyType) &&
                        timesTrapped < 3
                }

                if (eligibleTargets.size >= 2) {
                    val maxTrapped = when (towerComp.skill2Level) {
                        1 -> 4
                        2 -> 6
                        else -> 8
                    }
                    val trapDuration = when (towerComp.skill2Level) {
                        1 -> 1.0f
                        2 -> 2.0f
                        else -> 3.0f
                    }

                    SoundManager.playForestWrath()
                    val targetsToTrap = eligibleTargets.take(maxTrapped)
                    for (target in targetsToTrap) {
                        val oTrans = transformMapper.get(target)
                        val oHp = healthMapper.get(target)
                        val oMove = target.getComponent(MovementComponent::class.java)

                        if (oTrans != null && oHp != null && !oHp.isDead && oMove != null) {
                            wrathTrapCounts[target] = wrathTrapCounts.getOrDefault(target, 0) + 1
                            onForestWrathTriggered(Vector2(oTrans.position))
                            oMove.trapTimer = trapDuration
                            oMove.isBlocked = true
                            damageEnemy(target, oHp, 40f * trapDuration, DamageType.PHYSICAL)
                        }
                    }
                    towerComp.skill2Cooldown = 9.0f
                }
            }
        }

        // 5. KỸ NĂNG TESLA X104: OVERCHARGE
        if (towerComp.level == 4 && towerComp.specialization == TowerSpecialization.TESLA_X104) {
            if (towerComp.skill2Level > 0 && towerComp.skill2Cooldown <= 0f) {
                val overchargeRadius = 165f
                val (dMin, dMax) = when (towerComp.skill2Level) {
                    1 -> Pair(10f, 20f)
                    2 -> Pair(20f, 30f)
                    else -> Pair(30f, 40f)
                }
                var hitAny = false

                val snapshot = engine.getEntitiesFor(enemyFamily).toList()
                for (e in snapshot) {
                    val eHp = healthMapper.get(e)
                    val ePos = transformMapper.get(e)?.position
                    val m = e.getComponent(MovementComponent::class.java)
                    if (eHp != null && !eHp.isDead && m?.isInsideTunnel != true && ePos != null && ePos.dst(towerTransform.position) <= overchargeRadius) {
                        damageEnemy(e, eHp, MathUtils.random(dMin, dMax), DamageType.EXPLOSIVE)
                        hitAny = true
                    }
                }

                if (hitAny) {
                    SoundManager.playTeslaBolt()
                    onOverchargeTriggered(Vector2(towerTransform.position), overchargeRadius)
                    towerComp.skill2Cooldown = 2.5f
                }
            }
        }

        // 6. KỸ NĂNG SORCERER MAGE: HÓA CỪU (ƯU TIÊN KẺ ĐỊCH MÁU CAO NHẤT TRONG TẦM BẮN)
        if (towerComp.level == 4 && towerComp.specialization == TowerSpecialization.SORCERER_MAGE) {
            if (towerComp.skill1Level > 0 && towerComp.skill1Cooldown <= 0f) {
                val candidate = inRangeEnemies.filter {
                    val ec = it.getComponent(EnemyComponent::class.java)
                    val m = it.getComponent(MovementComponent::class.java)
                    val vHp = healthMapper.get(it)
                    ec != null && !ec.isSheep && m?.isInsideTunnel != true && vHp != null && !vHp.isDead &&
                        ec.enemyType !in listOf(
                        EnemyType.JUGGERNAUT, EnemyType.JT, EnemyType.VEZNAN, EnemyType.VEZNAN_DEMON,
                        EnemyType.SKELETON, EnemyType.SKELETON_KNIGHT
                    )
                }.maxByOrNull {
                    healthMapper.get(it)?.currentHealth ?: 0f
                }

                candidate?.let { victim ->
                    val ec = victim.getComponent(EnemyComponent::class.java)
                    val vTrans = transformMapper.get(victim)
                    val vMove = victim.getComponent(MovementComponent::class.java)
                    val vHp = healthMapper.get(victim)
                    val vArmor = victim.getComponent(ArmorComponent::class.java)
                    if (ec != null && vTrans != null && vHp != null && !vHp.isDead) {
                        ec.isSheep = true
                        ec.isRangedAttacking = false
                        ec.enrageDuration = 0f
                        SoundManager.playPolymorph()
                        onTeleportTriggered(Vector2(vTrans.position))

                        vHp.maxHealth = 25f
                        vHp.currentHealth = 25f.coerceAtMost(vHp.currentHealth)

                        if (vArmor != null) {
                            vArmor.armorType = ArmorType.NONE
                            vArmor.magicResistType = MagicResistType.NONE
                        }

                        val allSoldiers = engine.getEntitiesFor(soldierFamily)
                        for (sIdx in 0 until allSoldiers.size()) {
                            val sol = allSoldiers.get(sIdx)
                            val sComp = sol.getComponent(SoldierComponent::class.java)
                            if (sComp?.engagedEnemy == victim) {
                                sComp.engagedEnemy = null
                            }
                        }

                        if (vMove != null) {
                            vMove.isBlocked = false
                            vMove.speed = 28f
                        }
                        towerComp.skill1Cooldown = when (towerComp.skill1Level) {
                            1 -> 18.0f
                            2 -> 16.0f
                            else -> 14.0f
                        }
                    }
                }
            }
        }

        // 7. TẤN CÔNG BÌNH THƯỜNG & PHÁT ÂM THANH VŨ KHÍ TƯƠNG ỨNG
        targetEnemy?.let { enemy ->
            if (towerComp.attackCooldown <= 0f) {
                val eHp = healthMapper.get(enemy)
                val eTrans = transformMapper.get(enemy)
                if (eHp == null || eHp.isDead || eHp.currentHealth <= 0f || eTrans == null) return@let

                var calculatedDamage = if (CheatManager.isHighDamageEnabled) 300f else MathUtils.random(towerComp.damageMin, towerComp.damageMax)

                if (towerComp.towerType == TowerType.ARCHER && UpgradeManager.getEffectiveArcherLevel() >= 5) {
                    if (MathUtils.randomBoolean(0.10f)) calculatedDamage *= 2f
                }

                if (towerComp.towerType == TowerType.MAGE && UpgradeManager.getEffectiveMageLevel() >= 5) {
                    val moveComp = enemy.getComponent(MovementComponent::class.java)
                    val eComp = enemy.getComponent(EnemyComponent::class.java)
                    if (moveComp != null && eComp != null) {
                        moveComp.speed = eComp.enemyType.speed * 0.5f
                        moveComp.slowTimer = 0.5f
                    }
                }

                when (towerComp.specialization) {
                    TowerSpecialization.TESLA_X104 -> {
                        val firstHitPos = Vector2(eTrans.position)
                        SoundManager.playTeslaBolt()
                        onTeslaBoltTriggered(Vector2(towerTransform.position), firstHitPos)
                        damageEnemy(enemy, eHp, calculatedDamage, DamageType.EXPLOSIVE)

                        val maxChains = 3 + towerComp.skill1Level
                        var chained = 1
                        var lastPos = firstHitPos
                        for (otherE in inRangeEnemies) {
                            if (otherE == enemy) continue
                            val otherHp = healthMapper.get(otherE)
                            val otherTrans = transformMapper.get(otherE)
                            if (otherHp != null && !otherHp.isDead && otherHp.currentHealth > 0f && otherTrans != null) {
                                val nextPos = Vector2(otherTrans.position)
                                if (nextPos.dst(lastPos) <= 105f) {
                                    onTeslaBoltTriggered(Vector2(lastPos), nextPos)
                                    damageEnemy(otherE, otherHp, calculatedDamage * 0.85f, DamageType.EXPLOSIVE)
                                    lastPos = nextPos
                                    chained++
                                    if (chained >= maxChains) break
                                }
                            }
                        }
                    }
                    TowerSpecialization.RANGERS_HIDEOUT -> {
                        val eComp = enemy.getComponent(EnemyComponent::class.java)
                        val isImmune = isEnemyImmuneToPoison(eComp?.enemyType)
                        val canPoison = towerComp.skill1Level > 0 && !isImmune

                        if (canPoison) {
                            val poisonDps = towerComp.skill1Level * 5f
                            val existing = activePoisons[enemy]
                            if (existing != null) {
                                existing.dps = maxOf(existing.dps, poisonDps)
                                existing.remainingDuration = 3.0f
                            } else {
                                activePoisons[enemy] = PoisonState(dps = poisonDps, remainingDuration = 3.0f)
                            }
                        }

                        SoundManager.playArrowShoot()
                        spawnProjectile(
                            towerTransform.position,
                            enemy,
                            calculatedDamage,
                            towerComp.towerType,
                            towerComp.specialization,
                            isPoisoned = canPoison
                        )
                    }
                    TowerSpecialization.SORCERER_MAGE -> {
                        val eComp = enemy.getComponent(EnemyComponent::class.java)
                        if (eComp != null) {
                            eComp.isCursed = true
                            eComp.curseTimer = 5.0f
                        }
                        SoundManager.playMagicBolt()
                        spawnProjectile(towerTransform.position, enemy, calculatedDamage, towerComp.towerType, towerComp.specialization)
                    }
                    TowerSpecialization.MUSKETEER_GARRISON -> {
                        SoundManager.playMusketShot()
                        spawnProjectile(towerTransform.position, enemy, calculatedDamage, towerComp.towerType, towerComp.specialization)
                    }
                    else -> {
                        when (towerComp.towerType) {
                            TowerType.ARCHER -> SoundManager.playArrowShoot()
                            TowerType.MAGE -> SoundManager.playMagicBolt()
                            TowerType.ARTILLERY -> SoundManager.playArtilleryFire()
                            else -> {}
                        }
                        spawnProjectile(towerTransform.position, enemy, calculatedDamage, towerComp.towerType, towerComp.specialization)
                    }
                }
                towerComp.attackCooldown = towerComp.attackRate
            }
        }
    }

    private fun damageEnemy(enemy: Entity, hp: HealthComponent, rawDmg: Float, damageType: DamageType = DamageType.PHYSICAL) {
        if (hp.isDead || hp.currentHealth <= 0f) return

        val finalDmg = if (CheatManager.isHighDamageEnabled) 300f else rawDmg

        val eComp = enemy.getComponent(EnemyComponent::class.java)
        if (eComp != null && eComp.infernalShieldHitsRemaining > 0) {
            eComp.infernalShieldHitsRemaining--
            return
        }

        val armor = enemy.getComponent(ArmorComponent::class.java)
        val isCursed = (eComp?.isCursed == true || (eComp?.curseTimer ?: 0f) > 0f)

        var actualDmg = if (armor != null) {
            val baseReduction = armor.armorType.physicalReduction
            if (isCursed && damageType == DamageType.PHYSICAL) {
                val halfReduction = baseReduction * 0.5f
                finalDmg * (1f - halfReduction)
            } else if (isCursed && damageType == DamageType.EXPLOSIVE) {
                val halfReduction = (baseReduction * 0.5f) * 0.5f
                finalDmg * (1f - halfReduction)
            } else {
                armor.calculateDamage(finalDmg, damageType)
            }
        } else {
            finalDmg
        }

        if (armor != null && UpgradeManager.getEffectiveArcherLevel() >= 3 && damageType == DamageType.PHYSICAL && !CheatManager.isHighDamageEnabled) {
            actualDmg += finalDmg * 0.10f
        }

        hp.currentHealth -= actualDmg
        if (hp.isDead || hp.currentHealth <= 0f) {
            hp.currentHealth = 0f
            activePoisons.remove(enemy)
            wrathTrapCounts.remove(enemy)
            killEnemy(enemy)
        }
    }

    private fun killEnemy(enemy: Entity) {
        val eComp = enemy.getComponent(EnemyComponent::class.java)
        val eMove = enemy.getComponent(MovementComponent::class.java)
        val trans = enemy.getComponent(TransformComponent::class.java)

        SoundManager.playEnemyDeath()

        if (eComp != null && trans != null) {
            val deathPos = Vector2(trans.position)
            if (!eComp.isSummonedMinion) {
                EconomyManager.addGold(eComp.enemyType.rewardGold)
            }

            onEnemyKilled(enemy)

            if (eComp.enemyType in listOf(EnemyType.DEMON_SPAWN, EnemyType.DEMON_HOUND, EnemyType.DEMON_LORD)) {
                val explosionDmg = when (eComp.enemyType) {
                    EnemyType.DEMON_SPAWN -> MathUtils.random(50f, 100f)
                    EnemyType.DEMON_HOUND -> MathUtils.random(70f, 140f)
                    else -> MathUtils.random(100f, 180f)
                }
                SoundManager.playBombExplosion()

                val allSoldiers = engine.getEntitiesFor(soldierFamily).toList()
                for (sol in allSoldiers) {
                    val sHp = healthMapper.get(sol)
                    val sTrans = transformMapper.get(sol)
                    val sArmor = sol.getComponent(ArmorComponent::class.java)

                    if (sHp != null && !sHp.isDead && sTrans != null && sTrans.position.dst(deathPos) <= 45f) {
                        val finalD = sArmor?.calculateDamage(explosionDmg, DamageType.EXPLOSIVE) ?: explosionDmg
                        sHp.currentHealth -= finalD
                    }
                }
            }

            if (eComp.enemyType == EnemyType.SPIDER_MATRIARCH) {
                val remainingWps = eMove?.waypoints?.drop(eMove.currentWaypointIndex)?.toMutableList() ?: mutableListOf()
                val offsets = listOf(Vector2(-10f, 0f), Vector2(10f, 6f), Vector2(0f, -8f))
                for (i in 0 until 3) {
                    val hPos = Vector2(deathPos).add(offsets[i])
                    val hatchling = Entity().apply {
                        add(TransformComponent(position = hPos))
                        val maxHp = EnemyType.SPIDER_HATCHLING.hpNormal
                        add(HealthComponent(maxHealth = maxHp, currentHealth = maxHp))
                        add(ArmorComponent(armorType = ArmorType.NONE, magicResistType = MagicResistType.MEDIUM))
                        add(EnemyComponent(enemyType = EnemyType.SPIDER_HATCHLING))
                        add(MovementComponent(
                            speed = EnemyType.SPIDER_HATCHLING.speed,
                            waypoints = remainingWps.toMutableList()
                        ))
                    }
                    engine.addEntity(hatchling)
                }
            }
        }
        eMove?.isBlocked = false
        engine.removeEntity(enemy)
    }

    private fun spawnProjectile(
        startPos: Vector2,
        target: Entity,
        damage: Float,
        towerType: TowerType,
        spec: TowerSpecialization,
        isPoisoned: Boolean = false
    ) {
        val tPos = target.getComponent(TransformComponent::class.java)?.position ?: return
        val projectile = Entity().apply {
            val projType = when (towerType) {
                TowerType.ARCHER -> ProjectileType.ARROW
                TowerType.MAGE -> ProjectileType.MAGIC_BOLT
                TowerType.ARTILLERY -> ProjectileType.BOMB
                else -> ProjectileType.ARROW
            }

            val dmgType = when (towerType) {
                TowerType.MAGE -> DamageType.MAGICAL
                TowerType.ARTILLERY -> DamageType.EXPLOSIVE
                else -> DamageType.PHYSICAL
            }

            val splash = if (towerType == TowerType.ARTILLERY) {
                if (spec == TowerSpecialization.BIG_BERTHA) 115f
                else if (UpgradeManager.getEffectiveArtilleryLevel() >= 5) 105f else 90f
            } else 0f

            val pColor = when {
                isPoisoned -> Color(0.2f, 0.95f, 0.2f, 1f)
                spec == TowerSpecialization.ARCANE_WIZARD -> Color(0.85f, 0.2f, 0.95f, 1f)
                spec == TowerSpecialization.SORCERER_MAGE -> Color(0.95f, 0.65f, 0.1f, 1f)
                spec == TowerSpecialization.MUSKETEER_GARRISON -> Color.GOLD
                spec == TowerSpecialization.TESLA_X104 -> Color.CYAN
                spec == TowerSpecialization.BIG_BERTHA -> Color.ORANGE
                else -> Color.DARK_GRAY
            }

            val totalDist = startPos.dst(tPos).coerceAtLeast(1f)
            val maxArc = if (projType == ProjectileType.BOMB) (totalDist * 0.35f).coerceIn(40f, 95f) else 0f

            add(TransformComponent(position = Vector2(startPos)))
            add(ProjectileComponent(
                target = target,
                damage = damage,
                damageType = dmgType,
                speed = if (projType == ProjectileType.BOMB) 340f else 460f,
                type = projType,
                splashRadius = splash,
                color = pColor,
                targetPosition = Vector2(tPos),
                startPosition = Vector2(startPos),
                totalDistance = totalDist,
                maxArcHeight = maxArc
            ))
        }
        engine.addEntity(projectile)
    }
}

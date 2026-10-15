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
import com.game.td.managers.UpgradeManager

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

    override fun processEntity(tower: Entity, deltaTime: Float) {
        val towerComp = towerMapper.get(tower)
        val towerTransform = transformMapper.get(tower)

        if (towerComp.towerType == TowerType.BARRACKS) return
        if (towerComp.towerType == TowerType.SUNRAY) return
        if (towerComp.specialization == TowerSpecialization.SUNRAY_TOWER) return
        if (towerComp.isFrozen) return

        if (towerComp.attackCooldown > 0f) towerComp.attackCooldown -= deltaTime
        if (towerComp.skill1Cooldown > 0f) towerComp.skill1Cooldown -= deltaTime
        if (towerComp.skill2Cooldown > 0f) towerComp.skill2Cooldown -= deltaTime

        val enemies = engine.getEntitiesFor(enemyFamily)
        var targetEnemy: Entity? = null
        var minDistanceToTower = Float.MAX_VALUE
        val inRangeEnemies = mutableListOf<Entity>()

        val isRangerPoisonActive = (towerComp.specialization == TowerSpecialization.RANGERS_HIDEOUT && towerComp.skill1Level > 0)
        val isArtilleryGroundOnly = (towerComp.towerType == TowerType.ARTILLERY && towerComp.specialization != TowerSpecialization.TESLA_X104)

        for (i in 0 until enemies.size()) {
            val enemy = enemies.get(i)
            val enemyHealth = healthMapper.get(enemy)
            if (enemyHealth == null || enemyHealth.isDead) continue

            val move = enemy.getComponent(MovementComponent::class.java)
            if (move?.isInsideTunnel == true) continue

            val enemyComp = enemy.getComponent(EnemyComponent::class.java)
            if (isArtilleryGroundOnly && enemyComp?.enemyType?.isFlying == true) continue
            if (isRangerPoisonActive && enemyComp?.enemyType?.isImmuneToPoison == true) continue

            val enemyPos = transformMapper.get(enemy)?.position ?: continue
            val dist = towerTransform.position.dst(enemyPos)

            if (dist <= towerComp.range) {
                inRangeEnemies.add(enemy)
                if (dist < minDistanceToTower) {
                    minDistanceToTower = dist
                    targetEnemy = enemy
                }
            }
        }

        towerComp.currentTarget = targetEnemy

        // 1. KỸ NĂNG BIG BERTHA: DRAGON MISSILE & CLUSTER BOMBS
        if (towerComp.level == 4 && towerComp.specialization == TowerSpecialization.BIG_BERTHA) {
            if (towerComp.skill1Level > 0 && towerComp.skill1Cooldown <= 0f) {
                var missileVictim: Entity? = null
                var maxHpFound = -1f

                for (i in 0 until enemies.size()) {
                    val e = enemies.get(i)
                    val hp = healthMapper.get(e)
                    val m = e.getComponent(MovementComponent::class.java)
                    val ec = e.getComponent(EnemyComponent::class.java)
                    if (hp != null && !hp.isDead && m?.isInsideTunnel != true && ec?.enemyType?.isFlying != true) {
                        if (hp.maxHealth > maxHpFound) {
                            maxHpFound = hp.maxHealth
                            missileVictim = e
                        }
                    }
                }

                missileVictim?.let { victim ->
                    val vTrans = transformMapper.get(victim)
                    val vHp = healthMapper.get(victim)
                    if (vTrans != null && vHp != null && !vHp.isDead) {
                        onSniperShotTriggered(Vector2(towerTransform.position), Vector2(vTrans.position))
                        val missileDmg = 120f + towerComp.skill1Level * 100f

                        val currentEnemies = engine.getEntitiesFor(enemyFamily)
                        val snapshot = mutableListOf<Entity>()
                        for (i in 0 until currentEnemies.size()) snapshot.add(currentEnemies.get(i))

                        for (nearE in snapshot) {
                            val nHp = healthMapper.get(nearE)
                            val nTrans = transformMapper.get(nearE)
                            val nEc = nearE.getComponent(EnemyComponent::class.java)
                            if (nHp != null && !nHp.isDead && nTrans != null && nEc?.enemyType?.isFlying != true) {
                                if (nTrans.position.dst(vTrans.position) <= 95f) {
                                    damageEnemy(nearE, nHp, missileDmg)
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

                    onClusterBombsLaunched(
                        Vector2(towerTransform.position),
                        Vector2(tPos),
                        bombPoints
                    ) { explodePos ->
                        val currentEnemies = engine.getEntitiesFor(enemyFamily)
                        val snapshot = mutableListOf<Entity>()
                        for (idx in 0 until currentEnemies.size()) snapshot.add(currentEnemies.get(idx))

                        for (nearE in snapshot) {
                            val nHp = healthMapper.get(nearE)
                            val nTrans = transformMapper.get(nearE)
                            val nEc = nearE.getComponent(EnemyComponent::class.java)
                            if (nHp != null && !nHp.isDead && nTrans != null && nEc?.enemyType?.isFlying != true) {
                                if (nTrans.position.dst(explodePos) <= 45f) {
                                    damageEnemy(nearE, nHp, clusterDmg)
                                }
                            }
                        }
                    }
                    towerComp.skill2Cooldown = 9.0f
                }
            }
        }

        // 2. KỸ NĂNG MUSKETEER GARRISON
        if (towerComp.level == 4 && towerComp.specialization == TowerSpecialization.MUSKETEER_GARRISON) {
            if (towerComp.skill1Level > 0 && towerComp.skill1Cooldown <= 0f) {
                var sniperTarget: Entity? = null
                for (i in 0 until enemies.size()) {
                    val e = enemies.get(i)
                    val hp = healthMapper.get(e)
                    val m = e.getComponent(MovementComponent::class.java)
                    val eTrans = transformMapper.get(e)
                    if (hp != null && !hp.isDead && m?.isInsideTunnel != true && eTrans != null &&
                        towerTransform.position.dst(eTrans.position) <= towerComp.range * 1.5f) {
                        sniperTarget = e
                        break
                    }
                }

                sniperTarget?.let { victim ->
                    val vTrans = transformMapper.get(victim)
                    val vHp = healthMapper.get(victim)
                    if (vTrans != null && vHp != null && !vHp.isDead) {
                        onSniperShotTriggered(Vector2(towerTransform.position), Vector2(vTrans.position))
                        val instantKillChance = 0.20f + towerComp.skill1Level * 0.15f
                        val eComp = victim.getComponent(EnemyComponent::class.java)
                        val isBoss = (eComp?.enemyType in listOf(
                            EnemyType.JUGGERNAUT,
                            EnemyType.JT,
                            EnemyType.VEZNAN,
                            EnemyType.VEZNAN_DEMON
                        ))

                        if (!isBoss && MathUtils.randomBoolean(instantKillChance)) {
                            damageEnemy(victim, vHp, vHp.currentHealth)
                        } else {
                            damageEnemy(victim, vHp, 150f + towerComp.skill1Level * 100f)
                        }
                        towerComp.skill1Cooldown = 11f
                    }
                }
            }

            if (towerComp.skill2Level > 0 && towerComp.skill2Cooldown <= 0f && targetEnemy != null) {
                val tTrans = transformMapper.get(targetEnemy)
                val tHp = healthMapper.get(targetEnemy)
                if (tTrans != null && tHp != null && !tHp.isDead) {
                    val tPos = Vector2(tTrans.position)
                    onShrapnelExploded(Vector2(tPos))
                    val shrapnelDmg = 80f + towerComp.skill2Level * 50f
                    val currentEnemies = engine.getEntitiesFor(enemyFamily)
                    val snapshot = mutableListOf<Entity>()
                    for (i in 0 until currentEnemies.size()) snapshot.add(currentEnemies.get(i))

                    for (otherE in snapshot) {
                        val oTrans = transformMapper.get(otherE)
                        val oHp = healthMapper.get(otherE)
                        if (oTrans != null && oHp != null && !oHp.isDead && oTrans.position.dst(tPos) <= 85f) {
                            damageEnemy(otherE, oHp, shrapnelDmg)
                        }
                    }
                    towerComp.skill2Cooldown = 9f
                }
            }
        }

        // 3. KỸ NĂNG ARCANE WIZARD
        if (towerComp.level == 4 && towerComp.specialization == TowerSpecialization.ARCANE_WIZARD) {
            if (towerComp.skill1Level > 0 && towerComp.skill1Cooldown <= 0f) {
                val candidate = inRangeEnemies.maxByOrNull {
                    it.getComponent(HealthComponent::class.java)?.maxHealth ?: 0f
                }
                candidate?.let { victim ->
                    val vTrans = transformMapper.get(victim)
                    val vHp = healthMapper.get(victim)
                    val vComp = victim.getComponent(EnemyComponent::class.java)
                    if (vTrans != null && vHp != null && !vHp.isDead) {
                        onDeathRayTriggered(Vector2(towerTransform.position), Vector2(vTrans.position))
                        val isBoss = (vComp?.enemyType in listOf(
                            EnemyType.JUGGERNAUT,
                            EnemyType.JT,
                            EnemyType.VEZNAN,
                            EnemyType.VEZNAN_DEMON
                        ))

                        if (isBoss) {
                            damageEnemy(victim, vHp, 400f * towerComp.skill1Level)
                        } else {
                            damageEnemy(victim, vHp, vHp.currentHealth)
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
                val teleportVictim = inRangeEnemies.firstOrNull {
                    val ec = it.getComponent(EnemyComponent::class.java)
                    ec?.enemyType !in listOf(
                        EnemyType.JUGGERNAUT,
                        EnemyType.JT,
                        EnemyType.VEZNAN,
                        EnemyType.VEZNAN_DEMON
                    )
                }
                teleportVictim?.let { victim ->
                    val vMove = victim.getComponent(MovementComponent::class.java)
                    val vTrans = transformMapper.get(victim)
                    if (vMove != null && vTrans != null) {
                        onTeleportTriggered(Vector2(vTrans.position))
                        val rollBackAmount = 2 + towerComp.skill2Level * 2
                        vMove.currentWaypointIndex = (vMove.currentWaypointIndex - rollBackAmount).coerceAtLeast(0)
                        if (vMove.waypoints.isNotEmpty() && vMove.currentWaypointIndex in vMove.waypoints.indices) {
                            vTrans.position.set(vMove.waypoints[vMove.currentWaypointIndex])
                        }
                        towerComp.skill2Cooldown = 10f
                    }
                }
            }
        }

        // 4. KỸ NĂNG RANGERS HIDEOUT: WRATH OF THE FOREST
        if (towerComp.level == 4 && towerComp.specialization == TowerSpecialization.RANGERS_HIDEOUT) {
            if (towerComp.skill2Level > 0 && towerComp.skill2Cooldown <= 0f) {
                val nonFlyingInRange = inRangeEnemies.filter {
                    val ec = it.getComponent(EnemyComponent::class.java)
                    ec?.enemyType?.isFlying != true &&
                        ec?.enemyType !in listOf(
                        EnemyType.JUGGERNAUT,
                        EnemyType.JT,
                        EnemyType.VEZNAN,
                        EnemyType.VEZNAN_DEMON
                    )
                }

                if (nonFlyingInRange.isNotEmpty()) {
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

                    val targetsToTrap = nonFlyingInRange.take(maxTrapped)
                    for (target in targetsToTrap) {
                        val oTrans = transformMapper.get(target)
                        val oHp = healthMapper.get(target)
                        val oMove = target.getComponent(MovementComponent::class.java)

                        if (oTrans != null && oHp != null && !oHp.isDead && oMove != null) {
                            onForestWrathTriggered(Vector2(oTrans.position))
                            oMove.trapTimer = trapDuration
                            oMove.isBlocked = true
                            damageEnemy(target, oHp, 40f * trapDuration)
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

                val currentEnemies = engine.getEntitiesFor(enemyFamily)
                val snapshot = mutableListOf<Entity>()
                for (i in 0 until currentEnemies.size()) snapshot.add(currentEnemies.get(i))

                for (e in snapshot) {
                    val eHp = healthMapper.get(e)
                    val ePos = transformMapper.get(e)?.position
                    val m = e.getComponent(MovementComponent::class.java)
                    if (eHp != null && !eHp.isDead && m?.isInsideTunnel != true && ePos != null && ePos.dst(towerTransform.position) <= overchargeRadius) {
                        damageEnemy(e, eHp, MathUtils.random(dMin, dMax))
                        hitAny = true
                    }
                }

                if (hitAny) {
                    onOverchargeTriggered(Vector2(towerTransform.position), overchargeRadius)
                    towerComp.skill2Cooldown = 2.5f
                }
            }
        }

        // 6. KỸ NĂNG SORCERER MAGE: HÓA CỪU (POLYMORPH)
        if (towerComp.level == 4 && towerComp.specialization == TowerSpecialization.SORCERER_MAGE) {
            if (towerComp.skill1Level > 0 && towerComp.skill1Cooldown <= 0f) {
                val candidate = inRangeEnemies.firstOrNull {
                    val ec = it.getComponent(EnemyComponent::class.java)
                    val m = it.getComponent(MovementComponent::class.java)
                    val vHp = healthMapper.get(it)
                    ec != null && !ec.isSheep && m?.isInsideTunnel != true && vHp != null && !vHp.isDead &&
                        ec.enemyType !in listOf(
                        EnemyType.JUGGERNAUT,
                        EnemyType.JT,
                        EnemyType.VEZNAN,
                        EnemyType.VEZNAN_DEMON
                    )
                }
                candidate?.let { victim ->
                    val ec = victim.getComponent(EnemyComponent::class.java)
                    val vTrans = transformMapper.get(victim)
                    val vMove = victim.getComponent(MovementComponent::class.java)
                    val vHp = healthMapper.get(victim)
                    if (ec != null && vTrans != null) {
                        ec.isSheep = true
                        onTeleportTriggered(Vector2(vTrans.position))
                        if (vHp != null) {
                            vHp.maxHealth = 25f
                            vHp.currentHealth = 25f.coerceAtMost(vHp.currentHealth)
                        }
                        if (vMove != null) {
                            vMove.speed = 26f
                        }
                        towerComp.skill1Cooldown = when (towerComp.skill1Level) {
                            1 -> 20.0f
                            2 -> 16.0f
                            else -> 12.0f
                        }
                    }
                }
            }
        }

        // 7. TẤN CÔNG BÌNH THƯỜNG
        targetEnemy?.let { enemy ->
            if (towerComp.attackCooldown <= 0f) {
                val eTrans = transformMapper.get(enemy) ?: return
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
                        onTeslaBoltTriggered(towerTransform.position, eTrans.position)
                        val eHp = healthMapper.get(enemy)
                        if (eHp != null) damageEnemy(enemy, eHp, calculatedDamage)

                        val maxChains = 3 + towerComp.skill1Level
                        var chained = 1
                        var lastPos = eTrans.position
                        for (otherE in inRangeEnemies) {
                            if (otherE == enemy) continue
                            val otherHp = healthMapper.get(otherE)
                            val otherPos = transformMapper.get(otherE)?.position ?: continue
                            if (otherHp != null && !otherHp.isDead && otherPos.dst(lastPos) <= 105f) {
                                onTeslaBoltTriggered(lastPos, otherPos)
                                damageEnemy(otherE, otherHp, calculatedDamage * 0.85f)
                                lastPos = otherPos
                                chained++
                                if (chained >= maxChains) break
                            }
                        }
                    }
                    TowerSpecialization.RANGERS_HIDEOUT -> {
                        val eComp = enemy.getComponent(EnemyComponent::class.java)
                        val isImmune = eComp?.enemyType?.isImmuneToPoison == true
                        val isPoison = towerComp.skill1Level > 0 && !isImmune
                        val poisonDmgBonus = if (isPoison) towerComp.skill1Level * 10f else 0f
                        spawnProjectile(towerTransform.position, enemy, calculatedDamage + poisonDmgBonus, towerComp.towerType, towerComp.specialization, isPoisoned = isPoison)
                    }
                    else -> {
                        spawnProjectile(towerTransform.position, enemy, calculatedDamage, towerComp.towerType, towerComp.specialization)
                    }
                }
                towerComp.attackCooldown = towerComp.attackRate
            }
        }
    }

    private fun damageEnemy(enemy: Entity, hp: HealthComponent, rawDmg: Float) {
        val finalDmg = if (CheatManager.isHighDamageEnabled) 300f else rawDmg

        val eComp = enemy.getComponent(EnemyComponent::class.java)
        if (eComp != null && eComp.infernalShieldHitsRemaining > 0) {
            eComp.infernalShieldHitsRemaining--
            return
        }

        val armor = enemy.getComponent(ArmorComponent::class.java)
        var actualDmg = finalDmg
        if (armor != null && UpgradeManager.getEffectiveArcherLevel() >= 3 && !CheatManager.isHighDamageEnabled) {
            actualDmg += finalDmg * 0.10f
        }

        hp.currentHealth -= actualDmg
        if (hp.isDead || hp.currentHealth <= 0f) {
            killEnemy(enemy)
        }
    }

    private fun killEnemy(enemy: Entity) {
        val eComp = enemy.getComponent(EnemyComponent::class.java)
        val eMove = enemy.getComponent(MovementComponent::class.java)
        val trans = enemy.getComponent(TransformComponent::class.java)

        if (eComp != null && trans != null) {
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

                val allSoldiers = engine.getEntitiesFor(soldierFamily)
                for (sIdx in 0 until allSoldiers.size()) {
                    val sol = allSoldiers.get(sIdx)
                    val sHp = healthMapper.get(sol)
                    val sTrans = transformMapper.get(sol)
                    val sArmor = sol.getComponent(ArmorComponent::class.java)

                    if (sHp != null && !sHp.isDead && sTrans != null && sTrans.position.dst(trans.position) <= 45f) {
                        val finalD = sArmor?.calculateDamage(explosionDmg, DamageType.PHYSICAL) ?: explosionDmg
                        sHp.currentHealth -= finalD
                    }
                }
            }

            if (eComp.enemyType == EnemyType.SPIDER_MATRIARCH) {
                val offsets = listOf(Vector2(-10f, 0f), Vector2(10f, 6f), Vector2(0f, -8f))
                for (i in 0 until 3) {
                    val hPos = Vector2(trans.position).add(offsets[i])
                    val hatchling = Entity().apply {
                        add(TransformComponent(position = hPos))
                        val maxHp = EnemyType.SPIDER_HATCHLING.hpNormal
                        add(HealthComponent(maxHealth = maxHp, currentHealth = maxHp))
                        add(ArmorComponent(armorType = ArmorType.NONE, magicResistType = MagicResistType.MEDIUM))
                        add(EnemyComponent(enemyType = EnemyType.SPIDER_HATCHLING))
                        add(MovementComponent(
                            speed = EnemyType.SPIDER_HATCHLING.speed,
                            waypoints = eMove?.waypoints?.drop(eMove.currentWaypointIndex)?.toMutableList() ?: mutableListOf()
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
        val projectile = Entity().apply {
            val projType = when (towerType) {
                TowerType.ARCHER -> ProjectileType.ARROW
                TowerType.MAGE -> ProjectileType.MAGIC_BOLT
                TowerType.ARTILLERY -> ProjectileType.BOMB
                else -> ProjectileType.ARROW
            }
            val dmgType = if (towerType == TowerType.MAGE) DamageType.MAGICAL else DamageType.PHYSICAL
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

            add(TransformComponent(position = Vector2(startPos)))
            add(ProjectileComponent(
                target = target,
                speed = 460f,
                damage = damage,
                damageType = dmgType,
                type = projType,
                splashRadius = splash,
                color = pColor
            ))
        }
        engine.addEntity(projectile)
    }
}

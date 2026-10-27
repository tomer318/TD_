package com.game.td.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.*
import com.game.td.managers.EconomyManager
import com.game.td.managers.UpgradeManager
import com.game.td.managers.SoundManager

class BarracksSystem(
    private val onAxeThrown: (Vector2, Vector2) -> Unit = { _, _ -> },
    private val onHealingLight: (Vector2) -> Unit = {},
    private val onWhirlwindTriggered: (Vector2) -> Unit = {},
    private val onHolyStrikeTriggered: (Vector2) -> Unit = {}
) : IteratingSystem(
    Family.all(SoldierComponent::class.java, TransformComponent::class.java, HealthComponent::class.java).get()
) {
    private val soldierMapper = ComponentMapper.getFor(SoldierComponent::class.java)
    private val transformMapper = ComponentMapper.getFor(TransformComponent::class.java)
    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)

    private val towerFamily = Family.all(TowerComponent::class.java, BarracksComponent::class.java).get()
    private val soldierFamily = Family.all(SoldierComponent::class.java, TransformComponent::class.java, HealthComponent::class.java).get()
    private val enemyFamily = Family.all(
        EnemyComponent::class.java,
        TransformComponent::class.java,
        HealthComponent::class.java,
        MovementComponent::class.java
    ).exclude(SoldierComponent::class.java).get()

    private val soldierOffsets = listOf(
        Vector2(-14f, 0f),
        Vector2(14f, 8f),
        Vector2(0f, -12f)
    )

    override fun update(deltaTime: Float) {
        val deadSoldiers = mutableListOf<Entity>()
        val soldiersToInspect = engine.getEntitiesFor(soldierFamily).toList()
        for (sol in soldiersToInspect) {
            val hp = healthMapper.get(sol)
            val reinf = sol.getComponent(ReinforcementComponent::class.java)
            if (reinf != null) reinf.lifeTime -= deltaTime
            val isExpired = (reinf != null && reinf.lifeTime <= 0f)
            val isDead = (hp == null || hp.isDead || hp.currentHealth <= 0f)
            if (isDead || isExpired) {
                deadSoldiers.add(sol)
            }
        }

        for (sol in deadSoldiers) {
            val sComp = soldierMapper.get(sol)
            val engEnemy = sComp?.engagedEnemy
            val eMove = engEnemy?.getComponent(MovementComponent::class.java)
            val engEc = engEnemy?.getComponent(EnemyComponent::class.java)

            if (eMove != null && eMove.trapTimer <= 0f && engEc?.isRangedAttacking != true) {
                eMove.isBlocked = false
            }
            sComp?.engagedEnemy = null
            engine.removeEntity(sol)
        }

        val towers = engine.getEntitiesFor(towerFamily).toList()
        for (tower in towers) {
            val tComp = tower.getComponent(TowerComponent::class.java) ?: continue
            val bComp = tower.getComponent(BarracksComponent::class.java) ?: continue

            if (tComp.skill1Cooldown > 0f) tComp.skill1Cooldown -= deltaTime
            if (tComp.skill2Cooldown > 0f) tComp.skill2Cooldown -= deltaTime
            if (tComp.skill3Cooldown > 0f) tComp.skill3Cooldown -= deltaTime

            if (tComp.specialization == TowerSpecialization.HOLY_ORDER && tComp.skill1Level > 0 && tComp.skill1Cooldown <= 0f) {
                val injuredSoldier = bComp.soldiers.firstOrNull {
                    val hp = healthMapper.get(it)
                    hp != null && !hp.isDead && hp.currentHealth < hp.maxHealth * 0.7f
                }
                if (injuredSoldier != null) {
                    val healPos = transformMapper.get(injuredSoldier)?.position
                    if (healPos != null) onHealingLight(Vector2(healPos))
                    val healAmount = 40f + tComp.skill1Level * 30f
                    for (sol in bComp.soldiers) {
                        val sHp = healthMapper.get(sol)
                        if (sHp != null && !sHp.isDead) {
                            sHp.currentHealth = (sHp.currentHealth + healAmount).coerceAtMost(sHp.maxHealth)
                        }
                    }
                    tComp.skill1Cooldown = 10f
                }
            }

            // BARBARIAN PHÓNG RÌU TẦM XA: Vẫn nhắm bắn cả cừu chuẩn theo Wiki ("targeted by Barbarians with Throwing Axes")
            if (tComp.specialization == TowerSpecialization.BARBARIAN_MEAD_HALL && tComp.skill1Level > 0 && tComp.skill1Cooldown <= 0f) {
                val enemies = engine.getEntitiesFor(enemyFamily).toList()
                var targetEnemy: Entity? = null
                for (e in enemies) {
                    val eTrans = transformMapper.get(e)
                    val hp = healthMapper.get(e)
                    if (hp != null && !hp.isDead && eTrans != null && bComp.rallyPoint.dst(eTrans.position) <= 180f) {
                        targetEnemy = e
                        break
                    }
                }

                targetEnemy?.let { enemy ->
                    val tTrans = transformMapper.get(enemy)
                    val eHp = healthMapper.get(enemy)
                    if (tTrans != null && eHp != null) {
                        onAxeThrown(Vector2(bComp.rallyPoint), Vector2(tTrans.position))
                        val axeDmg = 35f + tComp.skill1Level * 20f
                        eHp.currentHealth -= axeDmg
                        val eComp = enemy.getComponent(EnemyComponent::class.java)
                        if (eHp.currentHealth <= 0f) {
                            eHp.currentHealth = 0f
                            if (eComp != null && !eComp.isSummonedMinion) EconomyManager.addGold(eComp.enemyType.rewardGold)
                            enemy.getComponent(MovementComponent::class.java)?.isBlocked = false
                            engine.removeEntity(enemy)
                        }
                        tComp.skill1Cooldown = 4.5f
                    }
                }
            }

            val it = bComp.soldiers.iterator()
            while (it.hasNext()) {
                val sol = it.next()
                val hp = healthMapper.get(sol)
                if (hp == null || hp.isDead || hp.currentHealth <= 0f) {
                    it.remove()
                }
            }

            val isElfHall = (tComp.specialization == TowerSpecialization.SYLVAN_ELF_HALL)
            if (!isElfHall && !tComp.isFrozen) {
                val baseRespawn = if (UpgradeManager.getEffectiveBarracksLevel() >= 3) {
                    if (tComp.specialization == TowerSpecialization.HOLY_ORDER) 7f else 8f
                } else 10f

                if (bComp.soldiers.size < 3) {
                    bComp.respawnTimer -= deltaTime
                    if (bComp.respawnTimer <= 0f) {
                        spawnSoldierForTower(tower, tComp, bComp, bComp.soldiers.size)
                        bComp.respawnTimer = baseRespawn
                    }
                } else {
                    bComp.respawnTimer = baseRespawn
                }
            }
        }

        distributeEngagements()

        val enemyListSnapshot = engine.getEntitiesFor(enemyFamily).toList()
        val allCurrentSoldiers = engine.getEntitiesFor(soldierFamily).toList()

        for (enemy in enemyListSnapshot) {
            val eComp = enemy.getComponent(EnemyComponent::class.java) ?: continue
            val eTrans = transformMapper.get(enemy)
            val eMove = enemy.getComponent(MovementComponent::class.java)
            val eHp = healthMapper.get(enemy)
            if (eHp == null || eHp.isDead || eHp.currentHealth <= 0f || eTrans == null || eMove == null) continue

            // CỪU VÔ HẠI: Không thể dùng kỹ năng (gõ trống) hay bắn xa, không bị chặn đường
            if (eComp.isSheep) {
                eComp.isRangedAttacking = false
                if (eMove.trapTimer <= 0f) {
                    eMove.isBlocked = false
                }
                continue
            }

            if (eComp.attackCooldown > 0f) {
                eComp.attackCooldown -= deltaTime
            }

            if (eComp.enemyType == EnemyType.TROLL_CHIEFTAIN) {
                eComp.drumTimer -= deltaTime
                if (eComp.drumTimer <= 0f) {
                    eComp.drumTimer = 7.0f
                    onHealingLight(Vector2(eTrans.position))

                    for (otherE in enemyListSnapshot) {
                        val oComp = otherE.getComponent(EnemyComponent::class.java)
                        val oTrans = transformMapper.get(otherE)
                        val oHp = healthMapper.get(otherE)
                        val isTroll = oComp?.enemyType in listOf(EnemyType.TROLL, EnemyType.TROLL_CHAMPION, EnemyType.TROLL_CHIEFTAIN)

                        if (isTroll && oComp?.isSheep != true && oTrans != null && oHp != null && !oHp.isDead && oTrans.position.dst(eTrans.position) <= 140f) {
                            oHp.currentHealth = (oHp.currentHealth + 50f).coerceAtMost(oHp.maxHealth)
                            oComp?.enrageDuration = 6.0f
                        }
                    }
                }
            }

            if (eComp.enrageDuration > 0f) {
                eComp.enrageDuration -= deltaTime
            }

            if (eComp.enemyType.canRangedAttack) {
                if (eComp.rangedCooldown > 0f) eComp.rangedCooldown -= deltaTime

                val isMeleeCombat = allCurrentSoldiers.any { sol ->
                    val sHp = healthMapper.get(sol)
                    val sTrans = transformMapper.get(sol)
                    val sComp = soldierMapper.get(sol)
                    sHp != null && !sHp.isDead && sTrans != null &&
                        (sComp?.engagedEnemy == enemy || sTrans.position.dst(eTrans.position) <= 35f)
                }

                if (isMeleeCombat || eHp.isDead || eMove.isInsideTunnel) {
                    if (eComp.isRangedAttacking) {
                        eComp.isRangedAttacking = false
                    }
                    continue
                }

                var closestSol: Entity? = null
                var minDist = eComp.enemyType.rangedRange

                for (sol in allCurrentSoldiers) {
                    val sHp = healthMapper.get(sol)
                    val sTrans = transformMapper.get(sol)
                    if (sHp == null || sHp.isDead || sTrans == null) continue

                    val d = eTrans.position.dst(sTrans.position)
                    if (d in 35f..minDist) {
                        minDist = d
                        closestSol = sol
                    }
                }

                if (closestSol != null) {
                    eComp.isRangedAttacking = true
                    eMove.isBlocked = true

                    if (eComp.rangedCooldown <= 0f) {
                        val sHp = healthMapper.get(closestSol)
                        val sArmor = closestSol.getComponent(ArmorComponent::class.java)
                        val rawDmg = MathUtils.random(eComp.enemyType.rangedDmgMin, eComp.enemyType.rangedDmgMax)

                        val isTrueDmg = (eComp.enemyType == EnemyType.TROLL_CHAMPION || eComp.enemyType == EnemyType.NECROMANCER)
                        val finalDmg = if (isTrueDmg) rawDmg else sArmor?.calculateDamage(rawDmg, DamageType.PHYSICAL) ?: rawDmg

                        if (eComp.enemyType == EnemyType.TROLL_CHAMPION) {
                            val solPos = transformMapper.get(closestSol)?.position
                            if (solPos != null) onAxeThrown(Vector2(eTrans.position), Vector2(solPos))
                            sHp?.currentHealth = (sHp?.currentHealth ?: 0f) - finalDmg
                        } else if (eComp.enemyType == EnemyType.NECROMANCER) {
                            spawnEnemyBolt(eTrans.position, closestSol, finalDmg)
                        } else {
                            spawnEnemyArrow(eTrans.position, closestSol, finalDmg)
                        }
                        eComp.rangedCooldown = eComp.rangedRate
                    }
                } else {
                    if (eComp.isRangedAttacking) {
                        eComp.isRangedAttacking = false
                        if (eMove.trapTimer <= 0f) {
                            eMove.isBlocked = false
                        }
                    }
                }
            }
        }

        super.update(deltaTime)
    }

    private fun spawnEnemyArrow(startPos: Vector2, target: Entity, damage: Float) {
        val proj = Entity().apply {
            add(TransformComponent(position = Vector2(startPos)))
            add(ProjectileComponent(
                target = target,
                speed = 360f,
                damage = damage,
                damageType = DamageType.PHYSICAL,
                type = ProjectileType.ARROW,
                splashRadius = 0f,
                color = Color(0.6f, 0.2f, 0.8f, 1f)
            ))
        }
        engine.addEntity(proj)
    }

    private fun spawnEnemyBolt(startPos: Vector2, target: Entity, damage: Float) {
        val proj = Entity().apply {
            add(TransformComponent(position = Vector2(startPos)))
            add(ProjectileComponent(
                target = target,
                speed = 340f,
                damage = damage,
                damageType = DamageType.MAGICAL,
                type = ProjectileType.MAGIC_BOLT,
                splashRadius = 0f,
                color = Color(0.25f, 0.95f, 0.35f, 1f)
            ))
        }
        engine.addEntity(proj)
    }

    // =========================================================================
    // PHÂN BỔ CHẶN QUÁI: CỪU (isSheep = true) KHÔNG BAO GIỜ BỊ LÍNH CHẶN ĐÁNH
    // =========================================================================
    private fun distributeEngagements() {
        val allEnemies = engine.getEntitiesFor(enemyFamily)
        val allSoldiers = engine.getEntitiesFor(soldierFamily)

        val blockersPerEnemy = mutableMapOf<Entity, MutableList<Entity>>()
        for (i in 0 until allEnemies.size()) {
            blockersPerEnemy[allEnemies.get(i)] = mutableListOf()
        }

        for (i in 0 until allSoldiers.size()) {
            val sol = allSoldiers.get(i)
            val hp = healthMapper.get(sol)
            if (hp == null || hp.isDead || hp.currentHealth <= 0f) continue

            val sComp = soldierMapper.get(sol)
            val enemy = sComp.engagedEnemy
            if (enemy != null) {
                val eHp = healthMapper.get(enemy)
                val eComp = enemy.getComponent(EnemyComponent::class.java)
                // Nếu mục tiêu đã chết hoặc biến thành Cừu -> nhả mục tiêu ngay lập tức
                if (eHp != null && !eHp.isDead && eHp.currentHealth > 0f && eComp?.isSheep != true) {
                    blockersPerEnemy[enemy]?.add(sol)
                } else {
                    sComp.engagedEnemy = null
                }
            }
        }

        // Giai đoạn 1: Ưu tiên mỗi quái chưa bị chặn nhận ít nhất 1 lính chặn đường (Bỏ qua quái bay và Cừu)
        for (i in 0 until allEnemies.size()) {
            val enemy = allEnemies.get(i)
            val eComp = enemy.getComponent(EnemyComponent::class.java)
            if (eComp?.enemyType?.isFlying == true || eComp?.isSheep == true) continue

            val eHp = healthMapper.get(enemy) ?: continue
            if (eHp.isDead || eHp.currentHealth <= 0f) continue
            val ePos = transformMapper.get(enemy)?.position ?: continue

            val currentBlockers = blockersPerEnemy[enemy] ?: mutableListOf()
            if (currentBlockers.isEmpty()) {
                var bestCandidate: Entity? = null
                var minDst = Float.MAX_VALUE

                for (j in 0 until allSoldiers.size()) {
                    val sol = allSoldiers.get(j)
                    val hp = healthMapper.get(sol)
                    if (hp == null || hp.isDead || hp.currentHealth <= 0f) continue

                    val sComp = soldierMapper.get(sol)
                    if (sComp.engagedEnemy == null) {
                        val dstToHome = sComp.targetPosition.dst(ePos)
                        if (dstToHome <= 90f && dstToHome < minDst) {
                            minDst = dstToHome
                            bestCandidate = sol
                        }
                    }
                }

                bestCandidate?.let { sol ->
                    val sComp = soldierMapper.get(sol)
                    sComp.engagedEnemy = enemy
                    blockersPerEnemy[enemy]?.add(sol)
                    enemy.getComponent(MovementComponent::class.java)?.isBlocked = true
                }
            }
        }

        // Giai đoạn 2: Những lính còn rảnh rỗi sẽ cùng lao vào hội đồng quái gần nhất (Bỏ qua quái bay và Cừu)
        for (j in 0 until allSoldiers.size()) {
            val sol = allSoldiers.get(j)
            val hp = healthMapper.get(sol)
            if (hp == null || hp.isDead || hp.currentHealth <= 0f) continue

            val sComp = soldierMapper.get(sol)
            if (sComp.engagedEnemy == null) {
                var bestEnemy: Entity? = null
                var minDst = Float.MAX_VALUE

                for (i in 0 until allEnemies.size()) {
                    val enemy = allEnemies.get(i)
                    val eComp = enemy.getComponent(EnemyComponent::class.java)
                    if (eComp?.enemyType?.isFlying == true || eComp?.isSheep == true) continue
                    val eHp = healthMapper.get(enemy) ?: continue
                    if (eHp.isDead || eHp.currentHealth <= 0f) continue
                    val ePos = transformMapper.get(enemy)?.position ?: continue

                    val blockers = blockersPerEnemy[enemy] ?: mutableListOf()
                    if (blockers.size < 3) {
                        val d = sComp.targetPosition.dst(ePos)
                        if (d <= 90f && d < minDst) {
                            minDst = d
                            bestEnemy = enemy
                        }
                    }
                }

                bestEnemy?.let { enemy ->
                    sComp.engagedEnemy = enemy
                    blockersPerEnemy[enemy]?.add(sol)
                    enemy.getComponent(MovementComponent::class.java)?.isBlocked = true
                }
            }
        }

        // Cập nhật trạng thái bị chặn của quái
        for (i in 0 until allEnemies.size()) {
            val enemy = allEnemies.get(i)
            val eMove = enemy.getComponent(MovementComponent::class.java)
            val eComp = enemy.getComponent(EnemyComponent::class.java)
            val isTrapped = (eMove?.trapTimer ?: 0f) > 0f

            if (eComp?.enemyType?.isFlying == true || eComp?.isSheep == true) {
                eMove?.isBlocked = isTrapped
                continue
            }
            val blockers = blockersPerEnemy[enemy] ?: mutableListOf()
            val isRangedAttacking = eComp?.isRangedAttacking == true
            eMove?.isBlocked = blockers.isNotEmpty() || isTrapped || isRangedAttacking
        }
    }

    override fun processEntity(sol: Entity, deltaTime: Float) {
        val sComp = soldierMapper.get(sol) ?: return
        val sTrans = transformMapper.get(sol) ?: return
        val hp = healthMapper.get(sol)

        if (hp == null || hp.isDead || hp.currentHealth <= 0f) {
            val engEnemy = sComp.engagedEnemy
            val eMove = engEnemy?.getComponent(MovementComponent::class.java)
            val engEc = engEnemy?.getComponent(EnemyComponent::class.java)
            if (eMove != null && eMove.trapTimer <= 0f && engEc?.isRangedAttacking != true) {
                eMove.isBlocked = false
            }
            sComp.engagedEnemy = null
            return
        }

        val enemy = sComp.engagedEnemy

        if (enemy != null) {
            val eTrans = transformMapper.get(enemy)
            val eHp = healthMapper.get(enemy)
            val eComp = enemy.getComponent(EnemyComponent::class.java)

            // Nếu mục tiêu chết hoặc vừa bị hóa cừu -> Lính nhả mục tiêu và để cừu đi qua
            if (eHp == null || eHp.isDead || eHp.currentHealth <= 0f || eTrans == null || eComp?.isSheep == true) {
                val eMove = enemy.getComponent(MovementComponent::class.java)
                val engEc = enemy.getComponent(EnemyComponent::class.java)
                if (eMove != null && eMove.trapTimer <= 0f && (engEc?.isRangedAttacking != true || engEc.isSheep)) {
                    eMove.isBlocked = false
                }
                sComp.engagedEnemy = null
                return
            }

            val dst = sTrans.position.dst(eTrans.position)

            if (dst > 20f) {
                val dir = Vector2(eTrans.position).sub(sTrans.position).nor()
                val moveSpd = if (sComp.moveSpeed > 0f) sComp.moveSpeed else 50f
                sTrans.position.add(dir.scl(moveSpd * deltaTime))
            }

            // 1. LÍNH VUNG KIẾM CHÉM QUÁI (TẦM <= 32px)
            if (sComp.attackCooldown > 0f) sComp.attackCooldown -= deltaTime
            if (sComp.attackCooldown <= 0f && dst <= 32f) {
                var dmg = MathUtils.random(sComp.attackDamageMin, sComp.attackDamageMax)

                val tComp = sComp.parentTower?.getComponent(TowerComponent::class.java)
                if (tComp?.specialization == TowerSpecialization.HOLY_ORDER && tComp.skill3Level > 0) {
                    if (MathUtils.randomBoolean(0.25f)) {
                        onHolyStrikeTriggered(Vector2(eTrans.position))
                        dmg += (25f + tComp.skill3Level * 25f)
                    }
                }

                if (tComp?.specialization == TowerSpecialization.BARBARIAN_MEAD_HALL && tComp.skill2Level > 0) {
                    if (MathUtils.randomBoolean(0.30f)) {
                        onWhirlwindTriggered(Vector2(sTrans.position))
                        dmg *= 1.5f
                    }
                }

                if (eComp != null && eComp.enrageDuration > 0f) {
                    dmg *= 0.5f
                }

                SoundManager.playSwordClash()
                eHp.currentHealth -= dmg
                sComp.attackCooldown = sComp.attackRate

                if (eHp.currentHealth <= 0f) {
                    eHp.currentHealth = 0f
                    if (eComp != null && !eComp.isSummonedMinion) EconomyManager.addGold(eComp.enemyType.rewardGold)
                    enemy.getComponent(MovementComponent::class.java)?.isBlocked = false
                    engine.removeEntity(enemy)
                    sComp.engagedEnemy = null
                    return
                }
            }

            // 2. QUÁI VUNG VŨ KHÍ TẤN CÔNG LẠI LÍNH (TẦM <= 35px, Cừu không bao giờ đánh lính)
            if (eComp != null && !eComp.isSheep && dst <= 35f) {
                if (eComp.attackCooldown <= 0f) {
                    var enemyDmg = MathUtils.random(eComp.enemyType.damageMin, eComp.enemyType.damageMax)
                    if (eComp.enrageDuration > 0f) {
                        enemyDmg += MathUtils.random(15f, 30f)
                    }

                    if (eComp.enemyType == EnemyType.YETI || eComp.enemyType == EnemyType.MAGMA_ELEMENTAL) {
                        val allSoldiers = engine.getEntitiesFor(soldierFamily).toList()
                        for (otherSol in allSoldiers) {
                            val oHp = healthMapper.get(otherSol)
                            val oTrans = transformMapper.get(otherSol)
                            if (oHp != null && !oHp.isDead && oTrans != null && oTrans.position.dst(eTrans.position) <= 42f) {
                                val sArmor = otherSol.getComponent(ArmorComponent::class.java)
                                val finalD = sArmor?.calculateDamage(enemyDmg, DamageType.PHYSICAL) ?: enemyDmg
                                oHp.currentHealth -= finalD
                            }
                        }
                    } else {
                        val sArmor = sol.getComponent(ArmorComponent::class.java)
                        val finalD = sArmor?.calculateDamage(enemyDmg, DamageType.PHYSICAL) ?: enemyDmg
                        hp.currentHealth -= finalD

                        if (UpgradeManager.getEffectiveBarracksLevel() >= 5) {
                            eHp.currentHealth -= (enemyDmg * 0.10f)
                            if (eHp.currentHealth <= 0f) {
                                eHp.currentHealth = 0f
                                if (!eComp.isSummonedMinion) EconomyManager.addGold(eComp.enemyType.rewardGold)
                                enemy.getComponent(MovementComponent::class.java)?.isBlocked = false
                                engine.removeEntity(enemy)
                                sComp.engagedEnemy = null
                                return
                            }
                        }
                    }

                    eComp.attackCooldown = eComp.attackRate

                    if (hp.isDead) {
                        val eMove = enemy.getComponent(MovementComponent::class.java)
                        val otherBlockers = engine.getEntitiesFor(soldierFamily).any { other ->
                            other != sol && soldierMapper.get(other)?.engagedEnemy == enemy &&
                                healthMapper.get(other)?.isDead == false
                        }
                        if (!otherBlockers && eMove != null && eMove.trapTimer <= 0f && !eComp.isRangedAttacking) {
                            eMove.isBlocked = false
                        }
                        sComp.engagedEnemy = null
                        return
                    }
                }
            }
        } else {
            if (sTrans.position.dst(sComp.targetPosition) > 2f) {
                val dir = Vector2(sComp.targetPosition).sub(sTrans.position).nor()
                val returnSpd = if (sComp.moveSpeed > 0f) sComp.moveSpeed else 50f
                sTrans.position.add(dir.scl(returnSpd * deltaTime))
            }

            if (hp.currentHealth < hp.maxHealth) {
                val regRate = if (sComp.soldierRankName == "Imperial Guard") 25f else sComp.regenPerSec
                hp.currentHealth = (hp.currentHealth + regRate * deltaTime).coerceAtMost(hp.maxHealth)
            }
        }
    }

    private fun spawnSoldierForTower(tower: Entity, tComp: TowerComponent, bComp: BarracksComponent, index: Int) {
        val offset = soldierOffsets.getOrElse(index) { Vector2(0f, 0f) }
        val targetPos = Vector2(bComp.rallyPoint).add(offset)
        val towerPos = transformMapper.get(tower)?.position ?: targetPos
        val spawnPos = Vector2(towerPos)

        val hpBonus = when {
            UpgradeManager.getEffectiveBarracksLevel() >= 4 -> 1.20f
            UpgradeManager.getEffectiveBarracksLevel() >= 1 -> 1.10f
            else -> 1.0f
        }

        val isArmorLevel2 = (UpgradeManager.getEffectiveBarracksLevel() >= 2)
        val isArmorLevel5 = (UpgradeManager.getEffectiveBarracksLevel() >= 5)

        val (rank, maxHp, armorType, dMin, dMax, aRate, regen) = when (tComp.level) {
            2 -> {
                val a = if (isArmorLevel5) ArmorType.MEDIUM_ARMOR else if (isArmorLevel2) ArmorType.MEDIUM_ARMOR else ArmorType.LIGHT_ARMOR
                SoldierStats("Footman", 100f * hpBonus, a, 3f, 4f, 1.36f, 7f)
            }
            3 -> {
                val a = if (isArmorLevel5 || isArmorLevel2) ArmorType.HEAVY_ARMOR else ArmorType.MEDIUM_ARMOR
                SoldierStats("Knight", 150f * hpBonus, a, 6f, 10f, 1.36f, 10f)
            }
            4 -> if (tComp.specialization == TowerSpecialization.HOLY_ORDER) {
                val holyArmor = if (tComp.skill2Level > 0 || isArmorLevel2 || isArmorLevel5) ArmorType.HEAVY_ARMOR else ArmorType.MEDIUM_ARMOR
                SoldierStats("Paladin", 200f * hpBonus, holyArmor, 12f, 18f, 1.47f, 25f)
            } else {
                val bonusDmg = tComp.skill3Level * 10f
                val barbArmor = if (isArmorLevel5) ArmorType.LIGHT_ARMOR else if (isArmorLevel2) ArmorType.LIGHT_ARMOR else ArmorType.NONE
                SoldierStats("Barbarian", 250f * hpBonus, barbArmor, 16f + bonusDmg, 24f + bonusDmg, 1.37f, 20f)
            }
            else -> {
                val a = if (isArmorLevel5 || isArmorLevel2) ArmorType.LIGHT_ARMOR else ArmorType.NONE
                SoldierStats("Militia", 50f * hpBonus, a, 1f, 3f, 1.0f, 5f)
            }
        }

        val soldier = Entity().apply {
            add(TransformComponent(position = Vector2(spawnPos)))
            add(HealthComponent(maxHealth = maxHp, currentHealth = maxHp))
            add(ArmorComponent(armorType = armorType))
            add(SoldierComponent(
                attackDamageMin = dMin,
                attackDamageMax = dMax,
                attackRate = aRate,
                regenPerSec = regen,
                targetPosition = Vector2(targetPos),
                parentTower = tower,
                soldierRankName = rank,
                moveSpeed = 55f
            ))
        }
        bComp.soldiers.add(soldier)
        engine.addEntity(soldier)
    }

    private data class SoldierStats(
        val rank: String,
        val hp: Float,
        val armor: ArmorType,
        val minDmg: Float,
        val maxDmg: Float,
        val rate: Float,
        val regen: Float
    )
}

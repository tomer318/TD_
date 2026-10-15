package com.game.td.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.*
import com.game.td.managers.EconomyManager

class MovementSystem(
    private val onHealingTriggered: (Vector2) -> Unit = {},
    private val getLevelNumber: () -> Int = { 0 }
) : IteratingSystem(
    Family.all(TransformComponent::class.java, MovementComponent::class.java).get()
) {
    private val direction = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val movement = entity.getComponent(MovementComponent::class.java) ?: return
        val transform = entity.getComponent(TransformComponent::class.java) ?: return
        val eComp = entity.getComponent(EnemyComponent::class.java)
        val hp = entity.getComponent(HealthComponent::class.java)

        // 1. TỰ HỒI MÁU THEO THỜI GIAN
        val isTrappedByWrath = movement.trapTimer > 0f
        if (eComp != null && hp != null && !hp.isDead && !isTrappedByWrath) {
            if (eComp.regenPerSec > 0f && hp.currentHealth < hp.maxHealth) {
                hp.currentHealth = (hp.currentHealth + eComp.regenPerSec * deltaTime).coerceAtMost(hp.maxHealth)
            }
        }

        // 2. KỸ NĂNG SHAMAN: HỒI MÁU ĐỒNG MINH
        if (eComp?.enemyType == EnemyType.SHAMAN && hp != null && !hp.isDead && !isTrappedByWrath) {
            eComp.shamanHealTimer -= deltaTime
            if (eComp.shamanHealTimer <= 0f) {
                eComp.shamanHealTimer = 8.0f
                eComp.shamanCastDuration = 1.0f

                onHealingTriggered(Vector2(transform.position))

                val allEnemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java, HealthComponent::class.java, TransformComponent::class.java).get())
                for (other in allEnemies) {
                    val oHp = other.getComponent(HealthComponent::class.java)
                    val oTrans = other.getComponent(TransformComponent::class.java)
                    if (oHp != null && !oHp.isDead && oTrans != null && oTrans.position.dst(transform.position) <= 95f) {
                        oHp.currentHealth = (oHp.currentHealth + 50f).coerceAtMost(oHp.maxHealth)
                    }
                }
            }

            if (eComp.shamanCastDuration > 0f) {
                eComp.shamanCastDuration -= deltaTime
                return
            }
        }

        // 3. KỸ NĂNG DEMON LORD
        if (eComp?.enemyType == EnemyType.DEMON_LORD && hp != null && !hp.isDead && !isTrappedByWrath) {
            eComp.demonLordShieldTimer -= deltaTime
            if (eComp.demonLordShieldTimer <= 0f) {
                val allEnemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java, HealthComponent::class.java, TransformComponent::class.java).get())
                val unshieldedDemons = mutableListOf<EnemyComponent>()

                for (idx in 0 until allEnemies.size()) {
                    val other = allEnemies.get(idx)
                    if (other == entity) continue
                    val oEc = other.getComponent(EnemyComponent::class.java)
                    val oHp = other.getComponent(HealthComponent::class.java)
                    val oTrans = other.getComponent(TransformComponent::class.java)

                    if (oEc != null && oHp != null && !oHp.isDead && oTrans != null) {
                        val isDemonMinion = oEc.enemyType in listOf(EnemyType.DEMON_SPAWN, EnemyType.DEMON_HOUND)
                        val inRange = oTrans.position.dst(transform.position) <= 165f
                        val hasNoShield = oEc.infernalShieldHitsRemaining <= 0

                        if (isDemonMinion && inRange && hasNoShield) {
                            unshieldedDemons.add(oEc)
                        }
                    }
                }

                if (unshieldedDemons.size >= 3) {
                    onHealingTriggered(Vector2(transform.position))
                    for (demonComp in unshieldedDemons) {
                        demonComp.infernalShieldHitsRemaining = 4
                    }
                    eComp.demonLordShieldTimer = 7.0f
                } else {
                    eComp.demonLordShieldTimer = 1.0f
                }
            }
        }

        // 4. KỸ NĂNG NECROMANCER
        if (eComp?.enemyType == EnemyType.NECROMANCER && hp != null && !hp.isDead && !isTrappedByWrath) {
            eComp.necromancerSummonTimer -= deltaTime
            if (eComp.necromancerSummonTimer <= 0f && !movement.isInsideTunnel) {
                eComp.necromancerSummonTimer = 9.5f
                onHealingTriggered(Vector2(transform.position))

                val summonCount = MathUtils.random(4, 6)
                for (sIdx in 0 until summonCount) {
                    val isKnight = MathUtils.randomBoolean(0.05f)
                    val sType = if (isKnight) EnemyType.SKELETON_KNIGHT else EnemyType.SKELETON
                    val offX = MathUtils.random(-22f, 22f)
                    val offY = MathUtils.random(-22f, 22f)

                    val skeleton = Entity().apply {
                        add(TransformComponent(position = Vector2(transform.position.x + offX, transform.position.y + offY)))
                        val sMax = sType.hpNormal
                        add(HealthComponent(maxHealth = sMax, currentHealth = sMax))
                        add(ArmorComponent(armorType = sType.armorType, magicResistType = sType.magicResist))
                        add(EnemyComponent(enemyType = sType, isSummonedMinion = true))
                        add(MovementComponent(
                            speed = sType.speed,
                            waypoints = movement.waypoints.drop(movement.currentWaypointIndex).toMutableList(),
                            isInsideTunnel = true,
                            tunnelTimer = 1.0f
                        ))
                    }
                    engine.addEntity(skeleton)
                }
            }
        }

        // 5. BOSS VEZ'NAN DỪNG BƯỚC ĐỨNG YÊN KHI ĐANG NIỆM PHÉP
        if (eComp?.enemyType == EnemyType.VEZNAN && eComp.veznanLockTowerCooldown in 13.5f..16.0f) {
            return
        }

        // 6. XỬ LÝ QUÁI ĐANG Ở TRONG ĐƯỜNG HẦM
        if (movement.isInsideTunnel) {
            movement.tunnelTimer -= deltaTime
            if (movement.tunnelTimer <= 0f) {
                movement.isInsideTunnel = false
                if (movement.hasTunnelTeleported) {
                    movement.hasTunnelTeleported = false
                    if (movement.currentWaypointIndex < movement.waypoints.size) {
                        transform.position.set(movement.waypoints[movement.currentWaypointIndex])
                        movement.currentWaypointIndex++
                        if (movement.currentWaypointIndex >= movement.waypoints.size) {
                            movement.isFinished = true
                        }
                    }
                }
            }
            return
        }

        // 7. TRÓI CHÂN BỞI FOREST WRATH
        if (movement.trapTimer > 0f) {
            movement.trapTimer -= deltaTime
            if (movement.trapTimer <= 0f) {
                movement.trapTimer = 0f
                movement.isBlocked = false
            } else {
                movement.isBlocked = true
                return
            }
        }

        // 8. KHÓA BƯỚC ĐI KHI ĐANG ĐỨNG LẠI TẤN CÔNG HOẶC BỊ LÍNH CHẶN
        if (movement.isBlocked || eComp?.isRangedAttacking == true) return

        if (movement.isFinished) {
            val penalty = eComp?.enemyType?.livesTaken ?: 1
            EconomyManager.takeDamage(penalty)
            engine.removeEntity(entity)
            return
        }

        if (movement.currentWaypointIndex >= movement.waypoints.size) {
            movement.isFinished = true
            return
        }

        // 9. KỸ NĂNG TURBO CỦA ROCKET RIDER
        if (eComp?.enemyType == EnemyType.ROCKET_RIDER) {
            if (!eComp.hasTriggeredTurbo && hp != null && hp.currentHealth < hp.maxHealth) {
                eComp.hasTriggeredTurbo = true
                eComp.turboDuration = 2f
            }

            if (eComp.turboDuration > 0f) {
                eComp.turboDuration -= deltaTime
                movement.speed = 190f
            } else {
                movement.speed = EnemyType.ROCKET_RIDER.speed
            }
        }

        val targetWaypoint = movement.waypoints[movement.currentWaypointIndex]
        direction.set(targetWaypoint).sub(transform.position)
        val distance = direction.len()

        // GIẢM 15% TỐC ĐỘ DI CHUYỂN TOÀN BỘ QUÁI THEO YÊU CẦU
        val step = (movement.speed * 0.85f) * deltaTime

        if (distance <= step) {
            transform.position.set(targetWaypoint)
            movement.currentWaypointIndex++

            if (movement.currentWaypointIndex >= movement.waypoints.size) {
                movement.isFinished = true
            } else {
                val nextWaypoint = movement.waypoints[movement.currentWaypointIndex]
                val jumpDist = transform.position.dst(nextWaypoint)
                val lvl = getLevelNumber()
                val isTunnelLevel = (lvl == 7 || lvl == 8 || lvl == 0)

                if (isTunnelLevel && jumpDist > 220f) {
                    movement.isInsideTunnel = true
                    movement.tunnelTimer = 2.0f
                    movement.hasTunnelTeleported = true

                    movement.isBlocked = false
                    val soldierFamily = Family.all(SoldierComponent::class.java).get()
                    val allSoldiers = engine.getEntitiesFor(soldierFamily)
                    for (i in 0 until allSoldiers.size()) {
                        val sComp = allSoldiers.get(i).getComponent(SoldierComponent::class.java)
                        if (sComp?.engagedEnemy == entity) {
                            sComp.engagedEnemy = null
                        }
                    }
                    return
                }
            }
        } else {
            direction.nor().scl(step)
            transform.position.add(direction)
        }
    }
}

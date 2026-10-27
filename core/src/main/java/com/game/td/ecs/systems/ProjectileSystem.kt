package com.game.td.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.*
import com.game.td.managers.EconomyManager
import com.game.td.managers.SoundManager
import com.game.td.managers.UpgradeManager

class ProjectileSystem(
    private val onExplosion: (Vector2, Float) -> Unit = { _, _ -> }
) : IteratingSystem(
    Family.all(TransformComponent::class.java, ProjectileComponent::class.java).get()
) {
    private val transformMapper = ComponentMapper.getFor(TransformComponent::class.java)
    private val projMapper = ComponentMapper.getFor(ProjectileComponent::class.java)
    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)

    private val enemyFamily = Family.all(
        EnemyComponent::class.java,
        TransformComponent::class.java,
        HealthComponent::class.java
    ).get()

    private val dir = Vector2()

    override fun processEntity(projectile: Entity, deltaTime: Float) {
        val projTrans = transformMapper.get(projectile) ?: return
        val proj = projMapper.get(projectile) ?: return

        val currentTarget = proj.target
        if (currentTarget != null) {
            val targetTrans = currentTarget.getComponent(TransformComponent::class.java)
            val targetHealth = currentTarget.getComponent(HealthComponent::class.java)
            if (targetTrans != null && targetHealth != null && !targetHealth.isDead && targetHealth.currentHealth > 0f) {
                proj.targetPosition.set(targetTrans.position)
            } else {
                proj.isTargetDead = true
                proj.target = null
            }
        }

        dir.set(proj.targetPosition).sub(projTrans.position)
        val dist = dir.len()
        val step = proj.speed * deltaTime

        if (dist > 0.001f) {
            proj.velocity.set(dir).nor()
            proj.rotationAngle = proj.velocity.angleDeg()
        }

        proj.traveledDistance += step

        if (dist <= step) {
            projTrans.position.set(proj.targetPosition)

            if (proj.type == ProjectileType.BOMB || proj.splashRadius > 0f) {
                onExplosion(Vector2(proj.targetPosition), proj.splashRadius)

                val enemiesSnapshot = engine.getEntitiesFor(enemyFamily).toList()
                for (e in enemiesSnapshot) {
                    val eTrans = transformMapper.get(e)
                    val eHp = healthMapper.get(e)
                    val eMove = e.getComponent(MovementComponent::class.java)
                    if (eTrans != null && eHp != null && !eHp.isDead && eHp.currentHealth > 0f && eMove?.isInsideTunnel != true) {
                        if (eTrans.position.dst(proj.targetPosition) <= proj.splashRadius) {
                            applyDamage(e, eHp, proj.damage, proj.damageType, proj.type)
                        }
                    }
                }
            } else {
                if (!proj.isTargetDead && currentTarget != null) {
                    val tHp = currentTarget.getComponent(HealthComponent::class.java)
                    if (tHp != null && !tHp.isDead && tHp.currentHealth > 0f) {
                        applyDamage(currentTarget, tHp, proj.damage, proj.damageType, proj.type)
                    }
                }
            }
            engine.removeEntity(projectile)
        } else {
            dir.nor().scl(step)
            projTrans.position.add(dir)
        }
    }

    private fun applyDamage(enemy: Entity, hp: HealthComponent, rawDamage: Float, damageType: DamageType, projType: ProjectileType) {
        if (hp.isDead || hp.currentHealth <= 0f) return

        val armor = enemy.getComponent(ArmorComponent::class.java)
        var finalDamage = armor?.calculateDamage(rawDamage, damageType) ?: rawDamage

        if (projType == ProjectileType.ARROW && UpgradeManager.getEffectiveArcherLevel() >= 3 && armor != null && armor.armorType != ArmorType.NONE) {
            finalDamage = (finalDamage * 1.10f).coerceAtMost(rawDamage)
        }

        if (projType == ProjectileType.MAGIC_BOLT && UpgradeManager.getEffectiveMageLevel() >= 2 && armor != null) {
            if (armor.armorType == ArmorType.GREAT_ARMOR) armor.armorType = ArmorType.HEAVY_ARMOR
            else if (armor.armorType == ArmorType.HEAVY_ARMOR) armor.armorType = ArmorType.MEDIUM_ARMOR
            else if (armor.armorType == ArmorType.MEDIUM_ARMOR) armor.armorType = ArmorType.LIGHT_ARMOR
        }

        hp.currentHealth -= finalDamage
        if (hp.isDead || hp.currentHealth <= 0f) {
            hp.currentHealth = 0f
            // PHÁT TIẾNG QUÁI CHẾT KHI BỊ ĐẠN TIÊU DIỆT
            SoundManager.playEnemyDeath()
            val enemyComp = enemy.getComponent(EnemyComponent::class.java)
            if (enemyComp != null && !enemyComp.isSummonedMinion) {
                EconomyManager.addGold(enemyComp.enemyType.rewardGold)
            }
            val moveComp = enemy.getComponent(MovementComponent::class.java)
            moveComp?.isBlocked = false
            engine.removeEntity(enemy)
        }
    }
}

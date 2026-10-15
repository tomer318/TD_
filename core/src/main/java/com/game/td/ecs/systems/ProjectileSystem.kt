package com.game.td.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.*
import com.game.td.managers.EconomyManager
import com.game.td.managers.UpgradeManager

class ProjectileSystem : IteratingSystem(
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
        val projTrans = transformMapper.get(projectile)
        val proj = projMapper.get(projectile)

        val targetTrans = proj.target.getComponent(TransformComponent::class.java)
        val targetHealth = proj.target.getComponent(HealthComponent::class.java)
        if (targetTrans == null || targetHealth == null || targetHealth.isDead) {
            engine.removeEntity(projectile)
            return
        }

        dir.set(targetTrans.position).sub(projTrans.position)
        val dist = dir.len()
        val step = proj.speed * deltaTime

        if (dist <= step) {
            if (proj.splashRadius > 0f) {
                val enemies = engine.getEntitiesFor(enemyFamily)
                for (e in enemies) {
                    val eTrans = transformMapper.get(e)
                    val eHp = healthMapper.get(e)
                    if (eTrans.position.dst(targetTrans.position) <= proj.splashRadius) {
                        applyDamage(e, eHp, proj.damage, proj.damageType, proj.type)
                    }
                }
            } else {
                applyDamage(proj.target, targetHealth, proj.damage, proj.damageType, proj.type)
            }
            engine.removeEntity(projectile)
        } else {
            dir.nor().scl(step)
            projTrans.position.add(dir)
        }
    }

    private fun applyDamage(enemy: Entity, hp: HealthComponent, rawDamage: Float, damageType: DamageType, projType: ProjectileType) {
        val armor = enemy.getComponent(ArmorComponent::class.java)
        var finalDamage = armor?.calculateDamage(rawDamage, damageType) ?: rawDamage

        // ARCHER TIER 3 (PIERCING SHOTS): Mũi tên bỏ qua 10% giáp của địch
        if (projType == ProjectileType.ARROW && UpgradeManager.getEffectiveArcherLevel() >= 3 && armor != null && armor.armorType != ArmorType.NONE) {
            finalDamage = (finalDamage * 1.10f).coerceAtMost(rawDamage)
        }

        // MAGE TIER 2 (ARCANE SHATTER): Phép bào mòn lớp giáp vật lý của mục tiêu
        if (projType == ProjectileType.MAGIC_BOLT && UpgradeManager.getEffectiveMageLevel() >= 2 && armor != null) {
            if (armor.armorType == ArmorType.GREAT_ARMOR) armor.armorType = ArmorType.HEAVY_ARMOR
            else if (armor.armorType == ArmorType.HEAVY_ARMOR) armor.armorType = ArmorType.MEDIUM_ARMOR
            else if (armor.armorType == ArmorType.MEDIUM_ARMOR) armor.armorType = ArmorType.LIGHT_ARMOR
        }

        hp.currentHealth -= finalDamage
        if (hp.isDead) {
            val enemyComp = enemy.getComponent(EnemyComponent::class.java)
            if (enemyComp != null) {
                EconomyManager.addGold(enemyComp.enemyType.rewardGold)
            }
            val moveComp = enemy.getComponent(MovementComponent::class.java)
            moveComp?.isBlocked = false
            engine.removeEntity(enemy)
        }
    }
}

package com.game.td.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.*

class SpiderEggSystem(
    private val difficulty: GameDifficulty = GameDifficulty.NORMAL
) : IteratingSystem(
    Family.all(SpiderEggComponent::class.java, TransformComponent::class.java).get()
) {
    override fun processEntity(egg: Entity, deltaTime: Float) {
        val eggComp = egg.getComponent(SpiderEggComponent::class.java) ?: return
        val trans = egg.getComponent(TransformComponent::class.java) ?: return

        eggComp.hatchTimer -= deltaTime
        if (eggComp.hatchTimer <= 0f) {
            val basePos = trans.position
            val offsets = listOf(Vector2(-10f, 0f), Vector2(10f, 6f), Vector2(0f, -8f))

            // Nở ra 3 Spider Hatchlings
            for (i in 0 until 3) {
                val hatchPos = Vector2(basePos).add(offsets[i])
                val hatchling = Entity().apply {
                    add(TransformComponent(position = hatchPos))
                    val maxHp = EnemyType.SPIDER_HATCHLING.getMaxHp(difficulty)
                    add(HealthComponent(maxHealth = maxHp, currentHealth = maxHp))
                    add(ArmorComponent(armorType = ArmorType.NONE, magicResistType = MagicResistType.MEDIUM))
                    add(EnemyComponent(enemyType = EnemyType.SPIDER_HATCHLING))
                    add(MovementComponent(
                        speed = EnemyType.SPIDER_HATCHLING.speed,
                        waypoints = eggComp.waypointsRemaining.toMutableList()
                    ))
                }
                engine.addEntity(hatchling)
            }
            engine.removeEntity(egg)
        }
    }
}

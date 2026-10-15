package com.game.td.ecs.components

import com.badlogic.ashley.core.Component

class HealthComponent(
    var maxHealth: Float = 100f,
    var currentHealth: Float = 100f
) : Component {
    val isDead: Boolean
        get() = currentHealth <= 0f
}

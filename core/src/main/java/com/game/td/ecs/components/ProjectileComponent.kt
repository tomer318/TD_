package com.game.td.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color

enum class ProjectileType {
    ARROW,      // Mũi tên (Cung thủ)
    MAGIC_BOLT, // Cầu phép (Tháp phép)
    BOMB        // Bom nổ diện rộng (Pháo cối)
}

class ProjectileComponent(
    val target: Entity,
    val damage: Float,
    val damageType: DamageType = DamageType.PHYSICAL,
    val speed: Float = 420f,
    val type: ProjectileType = ProjectileType.ARROW,
    val splashRadius: Float = 0f,
    val color: Color = Color.WHITE
) : Component

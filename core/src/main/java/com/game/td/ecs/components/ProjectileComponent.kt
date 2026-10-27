package com.game.td.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

enum class ProjectileType {
    ARROW,      // Mũi tên (Cung thủ)
    MAGIC_BOLT, // Cầu phép (Tháp phép)
    BOMB        // Bom nổ diện rộng (Pháo cối)
}

class ProjectileComponent(
    var target: Entity? = null,
    val damage: Float = 0f,
    val damageType: DamageType = DamageType.PHYSICAL,
    val speed: Float = 420f,
    val type: ProjectileType = ProjectileType.ARROW,
    val splashRadius: Float = 0f,
    val color: Color = Color.WHITE,
    val targetPosition: Vector2 = Vector2(),
    var velocity: Vector2 = Vector2(),
    var startPosition: Vector2 = Vector2(),
    var totalDistance: Float = 1f,
    var traveledDistance: Float = 0f,
    var isTargetDead: Boolean = false,
    var maxArcHeight: Float = 0f,
    var rotationAngle: Float = 0f
) : Component

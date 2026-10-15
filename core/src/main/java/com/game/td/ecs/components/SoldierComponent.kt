package com.game.td.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2

class SoldierComponent(
    var attackDamageMin: Float = 1f,
    var attackDamageMax: Float = 3f,
    var attackRate: Float = 1.0f,
    var attackCooldown: Float = 0f,
    var engagedEnemy: Entity? = null,
    val targetPosition: Vector2 = Vector2(),
    var moveSpeed: Float = 60f,
    var parentTower: Entity? = null,
    var soldierRankName: String = "Militia",

    // Tự động hồi phục HP mỗi giây khi ngoài giao tranh
    var regenPerSec: Float = 5f,

    // Bộ đếm kỹ năng đặc trưng cấp 4
    var specialCooldown: Float = 0f,
    var whirlwindChance: Float = 0f,
    var throwingAxeCooldown: Float = 0f
) : Component

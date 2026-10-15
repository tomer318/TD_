package com.game.td.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class SpiderEggComponent(
    var hatchTimer: Float = 8.0f,
    val pathIndex: Int = 0,
    val waypointsRemaining: List<Vector2> = emptyList()
) : Component

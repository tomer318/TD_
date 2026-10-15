package com.game.td.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class MovementComponent(
    var speed: Float = 60f,
    val waypoints: MutableList<Vector2> = mutableListOf(),
    var currentWaypointIndex: Int = 0,
    var isBlocked: Boolean = false,
    var isFinished: Boolean = false,
    var trapTimer: Float = 0f,
    var slowTimer: Float = 0f,

    // CƠ CHẾ CHUI HẦM MỎ (COLDSTEP MINES)
    var isInsideTunnel: Boolean = false,
    var tunnelTimer: Float = 0f,
    var hasTunnelTeleported: Boolean = false
) : Component

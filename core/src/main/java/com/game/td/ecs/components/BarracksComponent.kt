package com.game.td.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2

class BarracksComponent(
    val rallyPoint: Vector2 = Vector2(),
    val soldiers: MutableList<Entity> = mutableListOf(),
    var respawnTimer: Float = 10f
) : Component

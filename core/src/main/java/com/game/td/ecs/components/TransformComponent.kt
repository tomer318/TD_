package com.game.td.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class TransformComponent(
    var position: Vector2 = Vector2(),
) : Component

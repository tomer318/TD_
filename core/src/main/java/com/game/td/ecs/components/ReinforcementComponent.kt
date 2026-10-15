package com.game.td.ecs.components

import com.badlogic.ashley.core.Component

class ReinforcementComponent(
    var lifeTime: Float = 20f // Tồn tại trong 20s sau đó tự biến mất
) : Component

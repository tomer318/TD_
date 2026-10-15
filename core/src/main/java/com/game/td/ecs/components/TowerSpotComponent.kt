package com.game.td.ecs.components

import com.badlogic.ashley.core.Component

class TowerSpotComponent(
    var isOccupied: Boolean = false // false: Đất trống, true: Đã có trụ
) : Component

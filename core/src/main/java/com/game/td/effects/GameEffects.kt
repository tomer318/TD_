package com.game.td.effects

import com.badlogic.gdx.math.Vector2

class FireRainEffect(
    val x: Float,
    val y: Float,
    var delay: Float,
    val onImpact: (Float, Float) -> Unit = { _, _ -> }
) {
    var progress: Float = 0f
    var isFinished: Boolean = false
    private var hasImpacted: Boolean = false

    fun update(delta: Float) {
        if (delay > 0f) {
            delay -= delta
            return
        }
        progress += delta * 1.5f
        if (!hasImpacted && progress >= 0.45f) {
            hasImpacted = true
            onImpact(x, y)
        }
        if (progress >= 1f) isFinished = true
    }
}

class DeathRayBeam(val from: Vector2, val to: Vector2) {
    var progress: Float = 0f
    var isFinished: Boolean = false
    fun update(srcDelta: Float) {
        progress += srcDelta * 2.8f
        if (progress >= 1f) isFinished = true
    }
}

class MagicSparkEffect(val pos: Vector2) {
    var progress: Float = 0f
    var isFinished: Boolean = false
    fun update(srcDelta: Float) {
        progress += srcDelta * 2.2f
        if (progress >= 1f) isFinished = true
    }
}

class SniperTrailEffect(val from: Vector2, val to: Vector2) {
    var progress: Float = 0f
    var isFinished: Boolean = false
    fun update(srcDelta: Float) {
        progress += srcDelta * 3.5f
        if (progress >= 1f) isFinished = true
    }
}

class ShrapnelExplosionEffect(val pos: Vector2) {
    var progress: Float = 0f
    var isFinished: Boolean = false
    fun update(srcDelta: Float) {
        progress += srcDelta * 2.5f
        if (progress >= 1f) isFinished = true
    }
}

class ForestWrathEffect(val pos: Vector2) {
    var progress: Float = 0f
    var isFinished: Boolean = false
    fun update(srcDelta: Float) {
        progress += srcDelta * 0.8f
        if (progress >= 1f) isFinished = true
    }
}

class ThrownAxeEffect(val from: Vector2, val to: Vector2) {
    var progress: Float = 0f
    var isFinished: Boolean = false
    fun update(srcDelta: Float) {
        progress += srcDelta * 3.2f
        if (progress >= 1f) isFinished = true
    }
}

class HealingAuraEffect(val pos: Vector2) {
    var progress: Float = 0f
    var isFinished: Boolean = false
    fun update(srcDelta: Float) {
        progress += srcDelta * 1.5f
        if (progress >= 1f) isFinished = true
    }
}

class WhirlwindEffect(val pos: Vector2) {
    var progress: Float = 0f
    var isFinished: Boolean = false
    fun update(srcDelta: Float) {
        progress += srcDelta * 2.8f
        if (progress >= 1f) isFinished = true
    }
}

class HolyStrikeEffect(val pos: Vector2) {
    var progress: Float = 0f
    var isFinished: Boolean = false
    fun update(srcDelta: Float) {
        progress += srcDelta * 3.0f
        if (progress >= 1f) isFinished = true
    }
}

class TeslaBoltEffect(val from: Vector2, val to: Vector2) {
    var progress: Float = 0f
    var isFinished: Boolean = false
    fun update(srcDelta: Float) {
        progress += srcDelta * 4.0f
        if (progress >= 1f) isFinished = true
    }
}

class ClusterBombEffect(
    val towerPos: Vector2,
    val centerImpactPos: Vector2,
    val subBombPositions: List<Vector2>,
    val onSubBombExplode: (Vector2) -> Unit = {}
) {
    var flightProgress: Float = 0f
    var isBombLanded: Boolean = false
    var explosionTimer: Float = 0f
    val explosionDuration: Float = 0.85f
    var isFinished: Boolean = false

    private val explodedFlags = BooleanArray(subBombPositions.size) { false }

    fun update(delta: Float) {
        if (!isBombLanded) {
            flightProgress += delta * 2.2f
            if (flightProgress >= 1f) {
                flightProgress = 1f
                isBombLanded = true
            }
            return
        }

        explosionTimer += delta
        for (i in subBombPositions.indices) {
            val explodeDelay = i * 0.15f
            if (!explodedFlags[i] && explosionTimer >= explodeDelay) {
                explodedFlags[i] = true
                onSubBombExplode(subBombPositions[i])
            }
        }

        if (explosionTimer >= explosionDuration) {
            isFinished = true
        }
    }
}

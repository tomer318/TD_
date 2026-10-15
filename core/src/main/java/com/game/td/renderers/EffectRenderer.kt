package com.game.td.renderers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.game.td.effects.*

class TeslaOverchargeEffect(val center: Vector2, val maxRadius: Float) {
    var progress: Float = 0f
    var isFinished: Boolean = false
    fun update(delta: Float) {
        progress += delta * 2.5f
        if (progress >= 1f) isFinished = true
    }
}

class SunrayBeamEffect(val from: Vector2, val to: Vector2) {
    var progress: Float = 0f
    var isFinished: Boolean = false
    fun update(delta: Float) {
        progress += delta * 2.0f
        if (progress >= 1f) isFinished = true
    }
}

class ScorchedEarthPatch(
    val x: Float,
    val y: Float,
    var duration: Float,
    val maxDuration: Float,
    val dps: Float,
    val radius: Float = 24f
) {
    var tickTimer: Float = 0f
    var isFinished: Boolean = false

    fun update(delta: Float, onTickDamage: (Float, Float, Float, Float) -> Unit) {
        duration -= delta
        tickTimer += delta
        if (tickTimer >= 0.4f) {
            tickTimer -= 0.4f
            onTickDamage(x, y, radius, dps * 0.4f)
        }
        if (duration <= 0f) isFinished = true
    }
}

class EffectRenderer {
    val fireRainEffects = mutableListOf<FireRainEffect>()
    val scorchedEarthPatches = mutableListOf<ScorchedEarthPatch>()
    val deathRayBeams = mutableListOf<DeathRayBeam>()
    val sunrayBeams = mutableListOf<SunrayBeamEffect>()
    val magicSparks = mutableListOf<MagicSparkEffect>()
    val sniperTrails = mutableListOf<SniperTrailEffect>()
    val shrapnelExplosions = mutableListOf<ShrapnelExplosionEffect>()
    val forestWrathVines = mutableListOf<ForestWrathEffect>()
    val axeProjectiles = mutableListOf<ThrownAxeEffect>()
    val healingAuras = mutableListOf<HealingAuraEffect>()
    val whirlwindSlashes = mutableListOf<WhirlwindEffect>()
    val holyStrikeBeams = mutableListOf<HolyStrikeEffect>()
    val teslaBolts = mutableListOf<TeslaBoltEffect>()
    val clusterBombs = mutableListOf<ClusterBombEffect>()
    val overchargeEffects = mutableListOf<TeslaOverchargeEffect>()

    fun clear() {
        fireRainEffects.clear()
        scorchedEarthPatches.clear()
        deathRayBeams.clear()
        sunrayBeams.clear()
        magicSparks.clear()
        sniperTrails.clear()
        shrapnelExplosions.clear()
        forestWrathVines.clear()
        axeProjectiles.clear()
        healingAuras.clear()
        whirlwindSlashes.clear()
        holyStrikeBeams.clear()
        teslaBolts.clear()
        clusterBombs.clear()
        overchargeEffects.clear()
    }

    fun update(delta: Float, onScorchedTick: (Float, Float, Float, Float) -> Unit = { _, _, _, _ -> }) {
        for (i in fireRainEffects.size - 1 downTo 0) {
            fireRainEffects[i].update(delta)
            if (fireRainEffects[i].isFinished) fireRainEffects.removeAt(i)
        }
        for (i in scorchedEarthPatches.size - 1 downTo 0) {
            scorchedEarthPatches[i].update(delta, onScorchedTick)
            if (scorchedEarthPatches[i].isFinished) scorchedEarthPatches.removeAt(i)
        }
        for (i in deathRayBeams.size - 1 downTo 0) {
            deathRayBeams[i].update(delta)
            if (deathRayBeams[i].isFinished) deathRayBeams.removeAt(i)
        }
        for (i in sunrayBeams.size - 1 downTo 0) {
            sunrayBeams[i].update(delta)
            if (sunrayBeams[i].isFinished) sunrayBeams.removeAt(i)
        }
        for (i in magicSparks.size - 1 downTo 0) {
            magicSparks[i].update(delta)
            if (magicSparks[i].isFinished) magicSparks.removeAt(i)
        }
        for (i in sniperTrails.size - 1 downTo 0) {
            sniperTrails[i].update(delta)
            if (sniperTrails[i].isFinished) sniperTrails.removeAt(i)
        }
        for (i in shrapnelExplosions.size - 1 downTo 0) {
            shrapnelExplosions[i].update(delta)
            if (shrapnelExplosions[i].isFinished) shrapnelExplosions.removeAt(i)
        }
        for (i in forestWrathVines.size - 1 downTo 0) {
            forestWrathVines[i].update(delta)
            if (forestWrathVines[i].isFinished) forestWrathVines.removeAt(i)
        }
        for (i in axeProjectiles.size - 1 downTo 0) {
            axeProjectiles[i].update(delta)
            if (axeProjectiles[i].isFinished) axeProjectiles.removeAt(i)
        }
        for (i in healingAuras.size - 1 downTo 0) {
            healingAuras[i].update(delta)
            if (healingAuras[i].isFinished) healingAuras.removeAt(i)
        }
        for (i in whirlwindSlashes.size - 1 downTo 0) {
            whirlwindSlashes[i].update(delta)
            if (whirlwindSlashes[i].isFinished) whirlwindSlashes.removeAt(i)
        }
        for (i in holyStrikeBeams.size - 1 downTo 0) {
            holyStrikeBeams[i].update(delta)
            if (holyStrikeBeams[i].isFinished) holyStrikeBeams.removeAt(i)
        }
        for (i in teslaBolts.size - 1 downTo 0) {
            teslaBolts[i].update(delta)
            if (teslaBolts[i].isFinished) teslaBolts.removeAt(i)
        }
        for (i in clusterBombs.size - 1 downTo 0) {
            clusterBombs[i].update(delta)
            if (clusterBombs[i].isFinished) clusterBombs.removeAt(i)
        }
        for (i in overchargeEffects.size - 1 downTo 0) {
            overchargeEffects[i].update(delta)
            if (overchargeEffects[i].isFinished) overchargeEffects.removeAt(i)
        }
    }

    fun renderGroundPatches(shapeRenderer: ShapeRenderer) {
        if (scorchedEarthPatches.isEmpty()) return

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (patch in scorchedEarthPatches) {
            val alpha = (patch.duration / patch.maxDuration).coerceIn(0.2f, 0.85f)
            shapeRenderer.color = Color(0.20f, 0.12f, 0.08f, alpha * 0.75f)
            shapeRenderer.circle(patch.x, patch.y, patch.radius)
            shapeRenderer.color = Color(0.95f, 0.35f, 0.05f, alpha * 0.85f)
            shapeRenderer.circle(patch.x, patch.y, patch.radius * 0.65f)
            shapeRenderer.color = Color(1f, 0.88f, 0.20f, alpha)
            shapeRenderer.circle(patch.x, patch.y, patch.radius * 0.35f)
        }
        shapeRenderer.end()
    }

    fun render(shapeRenderer: ShapeRenderer) {
        if (fireRainEffects.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (eff in fireRainEffects) {
                if (eff.delay <= 0f) {
                    val alpha = (1f - eff.progress).coerceIn(0f, 1f)
                    shapeRenderer.color = Color(1f, 0.35f, 0.05f, alpha)
                    shapeRenderer.circle(eff.x, eff.y, 45f * eff.progress)
                    shapeRenderer.color = Color(1f, 0.95f, 0.2f, alpha)
                    shapeRenderer.circle(eff.x, eff.y, 25f * eff.progress)
                    shapeRenderer.color = Color(0.2f, 0.1f, 0.05f, alpha * 0.6f)
                    shapeRenderer.circle(eff.x, eff.y, 14f)
                }
            }
            shapeRenderer.end()
        }

        if (deathRayBeams.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (beam in deathRayBeams) {
                val alpha = (1f - beam.progress).coerceIn(0f, 1f)
                shapeRenderer.color = Color(0.9f, 0.1f, 1f, alpha)
                shapeRenderer.rectLine(beam.from, beam.to, 12f * alpha)
                shapeRenderer.color = Color(1f, 0.95f, 1f, alpha)
                shapeRenderer.rectLine(beam.from, beam.to, 4f * alpha)
                shapeRenderer.color = Color(0.8f, 0.2f, 1f, alpha)
                shapeRenderer.circle(beam.to.x, beam.to.y, 26f * beam.progress)
            }
            shapeRenderer.end()
        }

        if (sunrayBeams.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (beam in sunrayBeams) {
                val alpha = (1f - beam.progress).coerceIn(0f, 1f)
                shapeRenderer.color = Color(1f, 0.45f, 0.05f, alpha * 0.85f)
                shapeRenderer.rectLine(beam.from, beam.to, 18f * alpha)
                shapeRenderer.color = Color(1f, 0.85f, 0.15f, alpha)
                shapeRenderer.rectLine(beam.from, beam.to, 10f * alpha)
                shapeRenderer.color = Color(1f, 1f, 0.95f, alpha)
                shapeRenderer.rectLine(beam.from, beam.to, 4f * alpha)

                shapeRenderer.color = Color(1f, 0.45f, 0.05f, alpha * 0.8f)
                shapeRenderer.circle(beam.to.x, beam.to.y, 32f * beam.progress)
                shapeRenderer.color = Color(1f, 0.95f, 0.3f, alpha)
                shapeRenderer.circle(beam.to.x, beam.to.y, 18f * beam.progress)
                shapeRenderer.color = Color.WHITE
                shapeRenderer.circle(beam.to.x, beam.to.y, 8f * beam.progress)
            }
            shapeRenderer.end()
        }

        if (magicSparks.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            for (spark in magicSparks) {
                val alpha = (1f - spark.progress).coerceIn(0f, 1f)
                shapeRenderer.color = Color(0.85f, 0.3f, 1f, alpha)
                shapeRenderer.circle(spark.pos.x, spark.pos.y, 35f * spark.progress)
                shapeRenderer.circle(spark.pos.x, spark.pos.y, 18f * spark.progress)
            }
            shapeRenderer.end()
        }

        if (sniperTrails.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            for (trail in sniperTrails) {
                val alpha = (1f - trail.progress).coerceIn(0f, 1f)
                shapeRenderer.color = Color(1f, 0.85f, 0.2f, alpha)
                shapeRenderer.line(trail.from.x, trail.from.y, trail.to.x, trail.to.y)
                shapeRenderer.circle(trail.to.x, trail.to.y, 16f * trail.progress)
            }
            shapeRenderer.end()
        }

        if (shrapnelExplosions.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (exp in shrapnelExplosions) {
                val alpha = (1f - exp.progress).coerceIn(0f, 1f)
                shapeRenderer.color = Color(1f, 0.45f, 0.1f, alpha)
                shapeRenderer.circle(exp.pos.x, exp.pos.y, 48f * exp.progress)
                shapeRenderer.color = Color(1f, 0.9f, 0.2f, alpha)
                shapeRenderer.circle(exp.pos.x, exp.pos.y, 24f * exp.progress)
            }
            shapeRenderer.end()
        }

        if (forestWrathVines.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (vine in forestWrathVines) {
                val alpha = (1f - vine.progress).coerceIn(0f, 1f)
                shapeRenderer.color = Color(0.2f, 0.55f, 0.15f, alpha)
                shapeRenderer.circle(vine.pos.x - 8f, vine.pos.y - 6f, 14f)
                shapeRenderer.circle(vine.pos.x + 8f, vine.pos.y + 6f, 14f)
                shapeRenderer.circle(vine.pos.x, vine.pos.y, 18f)
                shapeRenderer.color = Color(0.42f, 0.25f, 0.12f, alpha)
                shapeRenderer.rect(vine.pos.x - 18f, vine.pos.y - 3f, 36f, 6f)
            }
            shapeRenderer.end()
        }

        if (healingAuras.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (aura in healingAuras) {
                val alpha = (1f - aura.progress).coerceIn(0f, 1f)
                shapeRenderer.color = Color(1f, 0.95f, 0.3f, alpha * 0.45f)
                shapeRenderer.circle(aura.pos.x, aura.pos.y, 45f * aura.progress)
                shapeRenderer.color = Color(1f, 1f, 0.8f, alpha)
                shapeRenderer.circle(aura.pos.x, aura.pos.y, 16f * aura.progress)
            }
            shapeRenderer.end()
        }

        if (axeProjectiles.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (axe in axeProjectiles) {
                val p = Vector2(axe.from).lerp(axe.to, axe.progress)
                shapeRenderer.color = Color(0.6f, 0.35f, 0.15f, 1f)
                shapeRenderer.rect(p.x - 2f, p.y - 7f, 4f, 14f)
                shapeRenderer.color = Color.LIGHT_GRAY
                shapeRenderer.circle(p.x, p.y + 4f, 5.5f)
            }
            shapeRenderer.end()
        }

        if (whirlwindSlashes.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            for (ww in whirlwindSlashes) {
                val alpha = (1f - ww.progress).coerceIn(0f, 1f)
                shapeRenderer.color = Color(1f, 1f, 1f, alpha)
                shapeRenderer.circle(ww.pos.x, ww.pos.y, 38f * ww.progress)
                shapeRenderer.color = Color(0.85f, 0.2f, 0.2f, alpha)
                shapeRenderer.circle(ww.pos.x, ww.pos.y, 24f * ww.progress)
            }
            shapeRenderer.end()
        }

        if (holyStrikeBeams.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (hs in holyStrikeBeams) {
                val alpha = (1f - hs.progress).coerceIn(0f, 1f)
                shapeRenderer.color = Color(1f, 0.9f, 0.2f, alpha)
                shapeRenderer.circle(hs.pos.x, hs.pos.y, 42f * hs.progress)
                shapeRenderer.color = Color(1f, 1f, 1f, alpha)
                shapeRenderer.circle(hs.pos.x, hs.pos.y, 20f * hs.progress)
            }
            shapeRenderer.end()
        }

        if (teslaBolts.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (bolt in teslaBolts) {
                val alpha = (1f - bolt.progress).coerceIn(0f, 1f)
                shapeRenderer.color = Color(0.35f, 0.85f, 1f, alpha)
                shapeRenderer.rectLine(bolt.from, bolt.to, 6f * alpha)
                shapeRenderer.color = Color(1f, 1f, 1f, alpha)
                shapeRenderer.rectLine(bolt.from, bolt.to, 2.5f * alpha)
            }
            shapeRenderer.end()
        }

        // VẼ HIỆU ỨNG OVERCHARGE TỎA XUNG ĐIỆN CỦA THÁP TESLA
        if (overchargeEffects.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            for (oc in overchargeEffects) {
                val alpha = (1f - oc.progress).coerceIn(0f, 1f)
                shapeRenderer.color = Color(0.2f, 0.85f, 1f, alpha)
                shapeRenderer.circle(oc.center.x, oc.center.y, oc.maxRadius * oc.progress)
                shapeRenderer.color = Color(1f, 1f, 1f, alpha * 0.9f)
                shapeRenderer.circle(oc.center.x, oc.center.y, (oc.maxRadius * oc.progress) * 0.85f)
            }
            shapeRenderer.end()
        }

        if (clusterBombs.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (cb in clusterBombs) {
                if (!cb.isBombLanded) {
                    val t = cb.flightProgress
                    val currentX = cb.towerPos.x + (cb.centerImpactPos.x - cb.towerPos.x) * t
                    val arcHeight = 75f * (4f * t * (1f - t))
                    val currentY = (cb.towerPos.y + (cb.centerImpactPos.y - cb.towerPos.y) * t) + arcHeight

                    shapeRenderer.color = Color(0.4f, 0.4f, 0.4f, 0.45f)
                    shapeRenderer.circle(currentX - 5f, currentY - 3f, 4f)

                    shapeRenderer.color = Color(0.18f, 0.18f, 0.22f, 1f)
                    shapeRenderer.circle(currentX, currentY, 8.5f)
                    shapeRenderer.color = Color(1f, 0.45f, 0.1f, 1f)
                    shapeRenderer.circle(currentX, currentY + 1.5f, 4.5f)
                } else {
                    for (i in cb.subBombPositions.indices) {
                        val pos = cb.subBombPositions[i]
                        val delay = i * 0.14f
                        val life = cb.explosionTimer - delay

                        if (life in 0f..0.45f) {
                            val subProgress = (life / 0.45f).coerceIn(0f, 1f)
                            val alpha = 1f - subProgress

                            shapeRenderer.color = Color(1f, 0.38f, 0.05f, alpha)
                            shapeRenderer.circle(pos.x, pos.y, 28f * subProgress)

                            shapeRenderer.color = Color(1f, 0.92f, 0.25f, alpha)
                            shapeRenderer.circle(pos.x, pos.y, 16f * subProgress)

                            shapeRenderer.color = Color(0.22f, 0.18f, 0.16f, alpha * 0.6f)
                            shapeRenderer.circle(pos.x - 2f, pos.y + 4f, 9f * subProgress)
                            shapeRenderer.circle(pos.x + 3f, pos.y - 2f, 8f * subProgress)
                        }
                    }
                }
            }
            shapeRenderer.end()
        }
    }
}

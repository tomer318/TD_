package com.game.td.map

import com.badlogic.gdx.math.CatmullRomSpline
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level1Southport {

    private val pathControlPoints = arrayOf(
        Vector2(535f, 750f),
        Vector2(535f, 720f),
        Vector2(535f, 550f),
        Vector2(510f, 440f),
        Vector2(400f, 360f),
        Vector2(370f, 250f),
        Vector2(450f, 175f),
        Vector2(560f, 175f),
        Vector2(680f, 225f),
        Vector2(820f, 255f),
        Vector2(1000f, 255f),
        Vector2(1320f, 255f),
        Vector2(1350f, 255f)
    )

    private fun generateSmoothPath(controls: Array<Vector2>, samples: Int = 100): List<Vector2> {
        val spline = CatmullRomSpline(controls, false)
        val points = mutableListOf<Vector2>()
        for (i in 0..samples) {
            val out = Vector2()
            spline.valueAt(out, i.toFloat() / samples)
            points.add(out)
        }
        return points
    }

    val waypointsList = generateSmoothPath(pathControlPoints)

    val towerSpotsList = listOf(
        Vector2(385f, 445f),
        Vector2(455f, 500f),
        Vector2(475f, 320f),
        Vector2(475f, 245f),
        Vector2(545f, 245f),
        Vector2(795f, 325f),
        Vector2(560f, 100f),
        Vector2(725f, 160f)
    )

    // Wave Campaign chuẩn
    val campaignWaves = listOf(
        WaveData(listOf(WaveSpawnInfo(EnemyType.GOBLIN, 6, 1.4f))),
        WaveData(listOf(WaveSpawnInfo(EnemyType.GOBLIN, 10, 1.1f))),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 4, 1.0f),
            WaveSpawnInfo(EnemyType.ORC, 2, 2.2f)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 8, 0.9f),
            WaveSpawnInfo(EnemyType.ORC, 3, 1.8f)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ORC, 6, 1.6f)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 12, 0.8f),
            WaveSpawnInfo(EnemyType.ORC, 4, 1.5f)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 16, 0.7f),
            WaveSpawnInfo(EnemyType.ORC, 8, 1.4f)
        ))
    )

    // Wave Heroic: 6 đợt quái[cite: 57]
    val heroicWaves = listOf(
        WaveData(listOf(WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.8f))),
        WaveData(listOf(WaveSpawnInfo(EnemyType.GOBLIN, 10, 0.9f), WaveSpawnInfo(EnemyType.ORC, 6, 1.8f))),
        WaveData(listOf(WaveSpawnInfo(EnemyType.GOBLIN, 24, 0.7f))),
        WaveData(listOf(WaveSpawnInfo(EnemyType.GOBLIN, 12, 0.8f), WaveSpawnInfo(EnemyType.ORC, 4, 1.6f))),
        WaveData(listOf(WaveSpawnInfo(EnemyType.ORC, 6, 1.5f))),
        WaveData(listOf(WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.6f), WaveSpawnInfo(EnemyType.ORC, 4, 1.4f)))
    )

    // Wave Iron: 1 đợt duy nhất dồn dập 70 Goblin và 16 Orc[cite: 58]
    val ironWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 70, 0.45f),
            WaveSpawnInfo(EnemyType.ORC, 16, 1.8f)
        ))
    )

    val data = LevelData(
        levelNumber = 1,
        levelName = "Southport",
        startingGold = 240,
        startingLives = 20,
        maxTowerLevel = 1,
        waypoints = waypointsList,
        towerSpots = towerSpotsList,
        waves = campaignWaves
    )
}

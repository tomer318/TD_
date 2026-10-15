package com.game.td.map

import com.badlogic.gdx.math.CatmullRomSpline
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level3Pagras {

    private val topControlPoints = arrayOf(
        Vector2(1360f, 580f),
        Vector2(1330f, 580f),
        Vector2(1100f, 580f),
        Vector2(930f, 595f),
        Vector2(770f, 600f),
        Vector2(610f, 590f),
        Vector2(480f, 550f),
        Vector2(420f, 470f),
        Vector2(430f, 385f),
        Vector2(490f, 340f),
        Vector2(570f, 310f),
        Vector2(670f, 290f),
        Vector2(750f, 240f),
        Vector2(765f, 170f),
        Vector2(690f, 130f),
        Vector2(530f, 160f),
        Vector2(320f, 195f),
        Vector2(120f, 205f),
        Vector2(-40f, 205f),
        Vector2(-70f, 205f)
    )

    private val bottomControlPoints = arrayOf(
        Vector2(1360f, 435f),
        Vector2(1330f, 435f),
        Vector2(1100f, 435f),
        Vector2(930f, 425f),
        Vector2(770f, 415f),
        Vector2(640f, 405f),
        Vector2(560f, 375f),
        Vector2(490f, 340f),
        Vector2(570f, 310f),
        Vector2(670f, 290f),
        Vector2(750f, 240f),
        Vector2(765f, 170f),
        Vector2(690f, 130f),
        Vector2(530f, 160f),
        Vector2(320f, 195f),
        Vector2(120f, 205f),
        Vector2(-40f, 205f),
        Vector2(-70f, 205f)
    )

    private fun generateSmoothPath(controls: Array<Vector2>, samples: Int = 110): List<Vector2> {
        val spline = CatmullRomSpline(controls, false)
        val points = mutableListOf<Vector2>()
        for (i in 0..samples) {
            val out = Vector2()
            spline.valueAt(out, i.toFloat() / samples)
            points.add(out)
        }
        return points
    }

    val topWaypoints = generateSmoothPath(topControlPoints)
    val bottomWaypoints = generateSmoothPath(bottomControlPoints)

    val towerSpotsList = listOf(
        Vector2(510f, 660f),  // 1. Phía Bắc
        Vector2(620f, 670f),  // 2. Phía Bắc
        Vector2(510f, 455f),  // 3. Dải giữa (trái)
        Vector2(630f, 495f),  // 4. Dải giữa (tâm)
        Vector2(755f, 515f),  // 5. Dải giữa (phải)
        Vector2(743f, 342f),  // 6. [VỊ TRÍ VÀNG]
        Vector2(470f, 255f),  // 7. Bờ trên sau hợp lưu
        Vector2(570f, 230f),  // 8. Bờ trên khúc chữ S
        Vector2(670f, 210f),  // 9. Bờ ngoài khúc cua Đông
        Vector2(450f, 90f),   // 10. Bờ Nam (trái)
        Vector2(560f, 78f),   // 11. [VỊ TRÍ ĐỎ]
        Vector2(820f, 125f)   // 12. Góc Đông Nam
    )

    // Wave Campaign chuẩn
    val campaignWaves = listOf(
        WaveData(listOf(WaveSpawnInfo(EnemyType.GOBLIN, 24, 1.0f, pathIndex = 0))),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 5, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 5, 2.0f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 6, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 6, 1.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.8f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 10, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 25, 0.7f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ORC, 5, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 5, 1.8f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ORC, 9, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.7f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ORC, 12, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 25, 0.6f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 30, 0.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 6, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WULF, 10, 0.8f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 6, 1.3f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 10, 0.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 8, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 10, 0.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.ORC, 8, 1.2f, pathIndex = 1)
        ))
    )

    // Wave Heroic: 6 đợt quái phân phối 2 cổng (pathIndex 0: Northeast, pathIndex 1: East)[cite: 84]
    val heroicWaves = listOf(
        // Wave 1: Northeast (5 Wulf) | East (5 Wulf)[cite: 84]
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 5, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 5, 0.8f, pathIndex = 1)
        )),
        // Wave 2: 25 Goblin, 2 Orc, 1 Shaman mỗi cổng[cite: 84]
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 25, 0.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 2, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 1, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 25, 0.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.ORC, 2, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHAMAN, 1, 2.0f, pathIndex = 1)
        )),
        // Wave 3: 18 Goblin, 8 Orc, 2 Shaman mỗi cổng[cite: 84]
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 18, 0.65f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 8, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 18, 0.65f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.ORC, 8, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.2f, pathIndex = 1)
        )),
        // Wave 4: 3 Orc, 2 Shaman, 1 Ogre mỗi cổng[cite: 84]
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ORC, 3, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.OGRE, 1, 3.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 3, 1.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.OGRE, 1, 3.5f, pathIndex = 1)
        )),
        // Wave 5: 30 Goblin, 4 Orc, 2 Shaman, 2 Ogre mỗi cổng[cite: 84]
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 30, 0.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 4, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.OGRE, 2, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 30, 0.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.ORC, 4, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.OGRE, 2, 3.0f, pathIndex = 1)
        )),
        // Wave 6: Northeast (5 Ogre) | East (15 Shaman)[cite: 84]
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.OGRE, 5, 2.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 15, 0.85f, pathIndex = 1)
        ))
    )

    // Wave Iron: 1 đợt duy nhất dồn dập
    // Northeast: 80 Goblin, 6 Shaman, 2 Ogre
    // East: 45 Orc, 2 Ogre[cite: 85]
    val ironWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 80, 0.45f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 6, 1.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.OGRE, 2, 3.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 45, 0.9f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.OGRE, 2, 3.2f, pathIndex = 1)
        ))
    )

    val data = LevelData(
        levelNumber = 3,
        levelName = "Pagras",
        startingGold = 300,
        startingLives = 20,
        maxTowerLevel = 3,
        waypoints = topWaypoints,
        secondaryWaypoints = bottomWaypoints,
        towerSpots = towerSpotsList,
        waves = campaignWaves
    )
}

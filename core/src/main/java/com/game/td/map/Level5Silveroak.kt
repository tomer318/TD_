package com.game.td.map

import com.badlogic.gdx.math.CatmullRomSpline
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level5Silveroak {

    private const val SOUTH_GATE_X = 675f
    private const val NORTH_EXIT_X = 730f

    // NHÁNH 0: TỪ CỔNG NAM UỐN CONG QUA SƯỜN TÂY LÊN CỔNG BẮC
    private val southToWestControls = arrayOf(
        Vector2(SOUTH_GATE_X, -40f),
        Vector2(SOUTH_GATE_X, 0f),
        Vector2(SOUTH_GATE_X, 100f),
        Vector2(580f, 180f),
        Vector2(450f, 270f),
        Vector2(420f, 390f),
        Vector2(460f, 510f),
        Vector2(570f, 580f),
        Vector2(NORTH_EXIT_X, 640f),
        Vector2(NORTH_EXIT_X, 720f),
        Vector2(NORTH_EXIT_X, 760f)
    )

    // NHÁNH 1: TỪ CỔNG NAM CHẠY TRỤC GIỮA LÊN CỔNG BẮC
    private val southToNorthControls = arrayOf(
        Vector2(SOUTH_GATE_X, -40f),
        Vector2(SOUTH_GATE_X, 0f),
        Vector2(SOUTH_GATE_X, 100f),
        Vector2(640f, 220f),
        Vector2(600f, 350f),
        Vector2(650f, 470f),
        Vector2(NORTH_EXIT_X, 640f),
        Vector2(NORTH_EXIT_X, 720f),
        Vector2(NORTH_EXIT_X, 760f)
    )

    // NHÁNH 2: TỪ CỬA HANG ĐÔNG BĂNG QUA GIAO LỘ HỢP LƯU LÊN CỔNG BẮC
    private val eastControls = arrayOf(
        Vector2(1150f, 240f),
        Vector2(1080f, 240f),
        Vector2(970f, 250f),
        Vector2(860f, 310f),
        Vector2(790f, 420f),
        Vector2(750f, 530f),
        Vector2(NORTH_EXIT_X, 640f),
        Vector2(NORTH_EXIT_X, 720f),
        Vector2(NORTH_EXIT_X, 760f)
    )

    private fun generatePath(controls: Array<Vector2>, samples: Int = 110): List<Vector2> {
        val spline = CatmullRomSpline(controls, false)
        val points = mutableListOf<Vector2>()
        for (i in 0..samples) {
            val out = Vector2()
            spline.valueAt(out, i.toFloat() / samples)
            points.add(out)
        }
        return points
    }

    val towerSpotsList = listOf(
        Vector2(405f, 565f),
        Vector2(575f, 655f),
        Vector2(525f, 420f),
        Vector2(820f, 545f),
        Vector2(855f, 455f),
        Vector2(520f, 315f),
        Vector2(680f, 380f), // Vị trí tháp hồng
        Vector2(750f, 345f),
        Vector2(680f, 305f),
        Vector2(790f, 290f),
        Vector2(535f, 120f),
        Vector2(715f, 245f),
    )

    // TỌA ĐỘ CHUẨN XÁC CHO MÀN 5
    val prebuiltArcherSpot = Vector2(590f, 505f) // Vị trí màu hồng: Tháp Cung cấp 3
    val sylvanElfHallSpot = Vector2(465f, 615f)    // Vị trí màu vàng: Sylvan Elf Hall

    // 15 ĐỢT QUÁI CAMPAIGN
    val campaignWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 4, 1.4f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 4, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 8, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 7, 1.3f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 8, 1.1f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 5, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 5, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 2.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 3, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 5, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 10, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 2.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 15, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 15, 0.7f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WORG, 3, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 3, 2.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 5, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 6, 1.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 5, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 6, 1.6f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 20, 0.6f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 3, 3.5f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 4, 3.0f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WORG, 6, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 15, 0.7f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 6, 2.8f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 12, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 12, 0.9f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 8, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 6, 2.5f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 6, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 2, 4.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 6, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 1.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 1, 4.0f, pathIndex = 1)
        ))
    )

    // HEROIC CHALLENGE MÀN 5
    val heroicWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 7, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 5, 1.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 7, 1.3f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 30, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 15, 0.9f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 12, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 2, 4.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 12, 1.0f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 18, 0.8f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 5, 2.8f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 28, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 15, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 24, 0.6f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.MARAUDER, 4, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 4, 2.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 4, 3.0f, pathIndex = 2)
        ))
    )

    val data = LevelData(
        levelNumber = 5,
        levelName = "Silveroak Forest",
        startingGold = 700,
        startingLives = 20,
        maxTowerLevel = 3,
        paths = listOf(
            generatePath(southToWestControls),
            generatePath(southToNorthControls),
            generatePath(eastControls)
        ),
        towerSpots = towerSpotsList,
        waves = campaignWaves
    )
}

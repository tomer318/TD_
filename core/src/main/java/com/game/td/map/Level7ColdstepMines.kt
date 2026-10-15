package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level7ColdstepMines {

    // 1. ĐƯỜNG CHÍNH (ĐỎ): Bo tròn các khúc cua
    val pathMain = listOf(
        Vector2(750f, 750f),
        Vector2(750f, 650f),
        Vector2(735f, 570f),
        Vector2(680f, 535f),
        Vector2(560f, 520f),
        Vector2(450f, 500f),
        Vector2(395f, 460f),
        Vector2(380f, 410f),
        Vector2(405f, 365f),
        Vector2(480f, 345f),
        Vector2(650f, 345f),
        Vector2(800f, 345f),
        Vector2(855f, 320f),
        Vector2(870f, 265f),
        Vector2(845f, 215f),
        Vector2(760f, 185f),
        Vector2(550f, 185f),
        Vector2(450f, 175f),
        Vector2(405f, 135f),
        Vector2(420f, 85f),
        Vector2(480f, 50f),
        Vector2(620f, 25f),
        Vector2(760f, 5f),
        Vector2(760f, -40f)
    )

    // 2. ĐƯỜNG PHỤ 1 (VÀNG - CAVE > EAST CAVE)
    val pathCaveEast = listOf(
        Vector2(750f, 750f),
        Vector2(750f, 650f),
        Vector2(710f, 600f),
        Vector2(600f, 615f),
        Vector2(480f, 655f),
        Vector2(470f, 670f),
        Vector2(870f, 410f),
        Vector2(855f, 350f),
        Vector2(855f, 320f),
        Vector2(870f, 265f),
        Vector2(845f, 215f),
        Vector2(760f, 185f),
        Vector2(550f, 185f),
        Vector2(450f, 175f),
        Vector2(405f, 135f),
        Vector2(420f, 85f),
        Vector2(480f, 50f),
        Vector2(620f, 25f),
        Vector2(760f, 5f),
        Vector2(760f, -40f)
    )

    // 3. ĐƯỜNG PHỤ 2 (XANH DƯƠNG - CAVE > SOUTH-WEST CAVE)
    val pathCaveSouthWest = listOf(
        Vector2(750f, 750f),
        Vector2(750f, 650f),
        Vector2(710f, 600f),
        Vector2(600f, 615f),
        Vector2(480f, 655f),
        Vector2(470f, 670f),
        Vector2(370f, 220f),
        Vector2(415f, 160f),
        Vector2(405f, 135f),
        Vector2(420f, 85f),
        Vector2(480f, 50f),
        Vector2(620f, 25f),
        Vector2(760f, 5f),
        Vector2(760f, -40f)
    )

    val allLevelPaths = listOf(
        pathMain,
        pathCaveEast,
        pathCaveSouthWest
    )

    // 11 VỊ TRÍ CỌC THÁP
    // Chỉ số 3, 4, 5 tương ứng với 3 vị trí khoanh đỏ ở tầng giữa
    val towerSpotsList = listOf(
        // Cụm tầng trên (3 cọc): index 0, 1, 2
        Vector2(510f, 435f),
        Vector2(600f, 435f),
        Vector2(690f, 435f),

        // Cụm tầng giữa (4 cọc): index 3, 4, 5, 6
        Vector2(480f, 260f), // [3]
        Vector2(570f, 260f), // [4] - 3 cọc này khoanh đỏ cho Iron Challenge
        Vector2(660f, 260f), // [5] -
        Vector2(750f, 260f), // [6] -

        // Cụm tầng dưới (4 cọc): index 7, 8, 9, 10
        Vector2(560f, 115f),
        Vector2(645f, 115f),
        Vector2(730f, 115f),
        Vector2(810f, 115f)
    )

    // 16 WAVES CHI TIẾT THEO WIKI CAMPAIGN
    val campaignWaves = listOf(
        WaveData(listOf(WaveSpawnInfo(EnemyType.BANDIT, 10, 1.2f, pathIndex = 0))),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 4, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 4, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 3, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 2, 1.2f, pathIndex = 2)
        )),
        WaveData(listOf(WaveSpawnInfo(EnemyType.GARGOYLE, 12, 0.8f, pathIndex = 0))),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 6, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 3, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 5, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 2, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WULF, 4, 0.8f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.WORG, 1, 1.5f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 6, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 3, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 3, 1.2f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WORG, 6, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL, 8, 1.8f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 6, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 3, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 4, 1.4f, pathIndex = 0)
        )),
        WaveData(listOf(WaveSpawnInfo(EnemyType.GARGOYLE, 20, 0.7f, pathIndex = 0))),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.MARAUDER, 5, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 8, 1.2f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 10, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 2, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 4, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 5, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 1, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 3, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BANDIT, 5, 0.8f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 1, 2.0f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 3, 1.2f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.MARAUDER, 6, 2.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.2f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 6, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 2, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 1, 1.0f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 16, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 4, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 3, 1.0f, pathIndex = 2)
        )),
        WaveData(listOf(WaveSpawnInfo(EnemyType.GARGOYLE, 40, 0.5f, pathIndex = 0))),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 12, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 9, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL, 4, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 4, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.TROLL, 4, 1.5f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 2, 0.8f, pathIndex = 2)
        ))
    )

    // HEROIC CHALLENGE MAP 7 (CHUẨN 100% THEO WIKI HÌNH 1)
    val heroicWaves = listOf(
        // Wave 1: Main (24 Bandit)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 24, 0.9f, pathIndex = 0)
        )),
        // Wave 2: Main (2 Wulf, 14 Bandit, 2 Marauder) | Cave East (4 Wulf, 2 Worg, 6 Bandit) | Cave SW (4 Wulf, 2 Worg, 6 Bandit)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 2, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 14, 0.9f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 2, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 4, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 2, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BANDIT, 6, 0.9f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WULF, 4, 0.8f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.WORG, 2, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.BANDIT, 6, 0.9f, pathIndex = 2)
        )),
        // Wave 3: Main (18 Gargoyle) | Cave East (6 Wulf, 4 Worg) | Cave SW (6 Wulf, 4 Worg)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 18, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 6, 0.7f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 4, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WULF, 6, 0.7f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.WORG, 4, 1.2f, pathIndex = 2)
        )),
        // Wave 4: Main (3 Worg, 3 Winter Wolf, 18 Troll)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WORG, 3, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 3, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL, 18, 1.4f, pathIndex = 0)
        )),
        // Wave 5: Main (8 Worg, 6 Winter Wolf, 8 Troll) | Cave East (6 Troll, 1 Worg) | Cave SW (6 Troll, 1 Worg)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WORG, 8, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 6, 0.9f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL, 8, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL, 6, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 1, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.TROLL, 6, 1.4f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.WORG, 1, 1.0f, pathIndex = 2)
        )),
        // Wave 6: Main (24 Troll, 6 Winter Wolf) | Cave East (5 Troll, 1 Winter Wolf) | Cave SW (5 Troll, 1 Winter Wolf)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 24, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 6, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL, 5, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 1, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.TROLL, 5, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 1, 0.8f, pathIndex = 2)
        ))
    )

    // IRON CHALLENGE MAP 7 (CHUẨN 100% THEO WIKI HÌNH 2)
    // 1 Wave duy nhất:
    // Main: 57 Bandit, 16 Brigand, 62 Troll, 6 Winter Wolf, 6 Marauder
    // Cave East: 9 Bandit, 4 Brigand, 17 Troll, 2 Winter Wolf, 1 Marauder
    // Cave SW: 6 Bandit, 3 Brigand, 12 Troll, 2 Winter Wolf
    val ironWaves = listOf(
        WaveData(listOf(
            // Main Path
            WaveSpawnInfo(EnemyType.BANDIT, 57, 0.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 16, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL, 62, 1.1f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 6, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 6, 2.5f, pathIndex = 0),

            // Cave East
            WaveSpawnInfo(EnemyType.BANDIT, 9, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 4, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.TROLL, 17, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 2, 0.9f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 1, 3.0f, pathIndex = 1),

            // Cave South-West
            WaveSpawnInfo(EnemyType.BANDIT, 6, 0.8f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.TROLL, 12, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 2, 0.9f, pathIndex = 2)
        ))
    )

    val data = LevelData(
        levelNumber = 7,
        levelName = "Coldstep Mines",
        startingGold = 700,
        startingLives = 20,
        paths = allLevelPaths,
        towerSpots = towerSpotsList,
        waves = campaignWaves,
        maxTowerLevel = 4
    )
}

package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level6Citadel {

    val pathWest = listOf(
        Vector2(-40f, 260f),
        Vector2(0f, 260f),
        Vector2(350f, 260f),
        Vector2(340f, 330f),
        Vector2(320f, 430f),
        Vector2(480f, 430f),
        Vector2(640f, 430f),
        Vector2(640f, 575f),
        Vector2(640f, 720f)
    )

    val pathEast = listOf(
        Vector2(1320f, 260f),
        Vector2(1280f, 260f),
        Vector2(930f, 260f),
        Vector2(940f, 330f),
        Vector2(960f, 430f),
        Vector2(800f, 430f),
        Vector2(640f, 430f),
        Vector2(640f, 575f),
        Vector2(640f, 720f)
    )

    val pathSouthWest = listOf(
        Vector2(626f, -40f),
        Vector2(626f, 150f),
        Vector2(626f, 260f),
        Vector2(500f, 260f),
        Vector2(350f, 260f),
        Vector2(330f, 350f),
        Vector2(310f, 440f),
        Vector2(330f, 530f),
        Vector2(370f, 575f),
        Vector2(530f, 575f),
        Vector2(640f, 575f),
        Vector2(640f, 720f)
    )

    val pathSouthEast = listOf(
        Vector2(654f, -40f),
        Vector2(654f, 150f),
        Vector2(654f, 260f),
        Vector2(780f, 260f),
        Vector2(930f, 260f),
        Vector2(950f, 350f),
        Vector2(970f, 440f),
        Vector2(950f, 530f),
        Vector2(910f, 575f),
        Vector2(750f, 575f),
        Vector2(640f, 575f),
        Vector2(640f, 720f)
    )

    val pathBossJuggernaut = listOf(
        Vector2(1320f, 260f),
        Vector2(930f, 260f),
        Vector2(640f, 260f),
        Vector2(350f, 260f),
        Vector2(340f, 330f),
        Vector2(320f, 430f),
        Vector2(480f, 430f),
        Vector2(640f, 430f),
        Vector2(640f, 575f),
        Vector2(640f, 720f)
    )

    val pathAuxiliaryEastMiddle = listOf(
        Vector2(960f, 430f),
        Vector2(640f, 430f),
        Vector2(640f, 575f)
    )

    val allLevelPaths = listOf(
        pathWest,                 // 0: West[cite: 86]
        pathSouthWest,            // 1: South > West[cite: 86]
        pathSouthEast,            // 2: South > East[cite: 86]
        pathEast,                 // 3: East[cite: 86]
        pathBossJuggernaut,       // 4: Boss Path[cite: 86]
        pathAuxiliaryEastMiddle   // 5: Auxiliary[cite: 86]
    )

    val towerSpotsList = listOf(
        Vector2(480f, 640f),
        Vector2(570f, 640f),
        Vector2(710f, 640f),
        Vector2(800f, 640f),
        Vector2(470f, 505f),
        Vector2(555f, 505f),
        Vector2(725f, 505f),
        Vector2(810f, 505f),
        Vector2(260f, 330f),
        Vector2(1020f, 330f),
        Vector2(550f, 355f),
        Vector2(640f, 355f),
        Vector2(730f, 355f),
        Vector2(550f, 180f),
        Vector2(730f, 180f)
    )

    val imperialGuardPositions = listOf(
        Vector2(615f, 580f), Vector2(640f, 580f), Vector2(665f, 580f),
        Vector2(350f, 430f), Vector2(375f, 430f),
        Vector2(905f, 430f), Vector2(930f, 430f)
    )

    // 17 WAVES CAMPAIGN[cite: 86]
    val campaignWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 7, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 7, 1.2f, pathIndex = 3)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 5, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 5, 1.4f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 1, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 1, 2.0f, pathIndex = 3)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 12, 0.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.ORC, 12, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHAMAN, 5, 1.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GOBLIN, 12, 0.6f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.ORC, 12, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHAMAN, 5, 1.8f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 5, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 5, 1.2f, pathIndex = 3)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 7, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 1, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 7, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.MARAUDER, 1, 3.0f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 6, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 3, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 6, 1.0f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 3, 1.5f, pathIndex = 3)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WORG, 6, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.WORG, 6, 0.8f, pathIndex = 3)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 12, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 12, 0.8f, pathIndex = 3)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 15, 0.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.ORC, 17, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHAMAN, 6, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.OGRE, 1, 4.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GOBLIN, 15, 0.5f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.ORC, 17, 1.0f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHAMAN, 6, 1.5f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.OGRE, 1, 4.0f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 5, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 5, 2.0f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 4, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 7, 1.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 7, 1.8f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 4, 1.2f, pathIndex = 3)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 1, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 1.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 1, 3.0f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 1.6f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.0f, pathIndex = 3)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.OGRE, 3, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 10, 0.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.4f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.OGRE, 3, 2.5f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.WULF, 10, 0.6f, pathIndex = 3)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 15, 0.7f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GARGOYLE, 15, 0.7f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.MARAUDER, 3, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 4, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 3, 2.0f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 1.5f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 4, 1.0f, pathIndex = 3)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.JUGGERNAUT, 1, 1.0f, pathIndex = 4)
        ))
    )

    // HEROIC CHALLENGE MÀN 6 (CHUẨN 100% THEO WIKI)
    val heroicWaves = listOf(
        // Wave 1: South>West (10 Bandit, 6 Brigand, 4 Dark Knight) | South>East (10 Bandit, 6 Brigand, 4 Dark Knight)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 10, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BANDIT, 10, 0.8f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 2.0f, pathIndex = 2)
        )),
        // Wave 2: West (12 Gargoyle) | East (12 Gargoyle)[cite: 90]
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 12, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 12, 0.7f, pathIndex = 3)
        )),
        // Wave 3: West (18 Bandit) | South>West (10 Brigand, 4 Dark Knight) | South>East (10 Brigand, 3 Dark Knight) | East (18 Bandit)[cite: 90]
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 18, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 10, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 10, 1.0f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 3, 2.0f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.BANDIT, 18, 0.7f, pathIndex = 3)
        )),
        // Wave 4: West (3 Marauder) | South>West (29 Brigand) | South>East (29 Brigand) | East (3 Marauder)[cite: 90]
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.MARAUDER, 3, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 29, 0.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 29, 0.6f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.MARAUDER, 3, 2.5f, pathIndex = 3)
        )),
        // Wave 5: West (10 Worg) | South>West (24 Dark Knight, 5 Shadow Archer) | South>East (24 Dark Knight, 5 Shadow Archer) | East (10 Worg)[cite: 90]
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WORG, 10, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 24, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 5, 1.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 24, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 5, 1.8f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.WORG, 10, 0.8f, pathIndex = 3)
        )),
        // Wave 6: West (10 Brigand, 4 Marauder, 6 Shadow Archer) | South>West (2 Marauder, 14 Dark Knight, 8 Shadow Archer) | South>East (2 Marauder, 14 Dark Knight, 8 Shadow Archer) | East (10 Brigand, 4 Marauder, 6 Shadow Archer)[cite: 90]
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 10, 0.9f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 4, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 2, 2.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 14, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 8, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 2, 2.5f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 14, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 8, 1.4f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.BRIGAND, 10, 0.9f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.MARAUDER, 4, 2.5f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.4f, pathIndex = 3)
        ))
    )

    val ironWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 15, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 20, 0.6f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 12, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 4, 3.0f, pathIndex = 2)
        ))
    )

    val data = LevelData(
        levelNumber = 6,
        levelName = "The Citadel",
        startingGold = 700,
        startingLives = 20,
        paths = allLevelPaths,
        towerSpots = towerSpotsList,
        waves = campaignWaves,
        maxTowerLevel = 4
    )
}

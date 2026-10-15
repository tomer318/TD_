package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level6Citadel {

    val pathWest = listOf(
        Vector2(-40f, 210f),
        Vector2(0f, 210f),
        Vector2(350f, 210f),
        Vector2(340f, 280f),
        Vector2(320f, 380f),
        Vector2(480f, 380f),
        Vector2(640f, 380f),
        Vector2(640f, 525f),
        Vector2(640f, 720f)
    )

    val pathEast = listOf(
        Vector2(1320f, 210f),
        Vector2(1280f, 210f),
        Vector2(930f, 210f),
        Vector2(940f, 280f),
        Vector2(960f, 380f),
        Vector2(800f, 380f),
        Vector2(640f, 380f),
        Vector2(640f, 525f),
        Vector2(640f, 720f)
    )

    val pathSouthWest = listOf(
        Vector2(626f, -40f),
        Vector2(626f, 120f),
        Vector2(626f, 210f),
        Vector2(500f, 210f),
        Vector2(350f, 210f),
        Vector2(330f, 300f),
        Vector2(310f, 390f),
        Vector2(330f, 480f),
        Vector2(370f, 525f),
        Vector2(530f, 525f),
        Vector2(640f, 525f),
        Vector2(640f, 720f)
    )

    val pathSouthEast = listOf(
        Vector2(654f, -40f),
        Vector2(654f, 120f),
        Vector2(654f, 210f),
        Vector2(780f, 210f),
        Vector2(930f, 210f),
        Vector2(950f, 300f),
        Vector2(970f, 390f),
        Vector2(950f, 480f),
        Vector2(910f, 525f),
        Vector2(750f, 525f),
        Vector2(640f, 525f),
        Vector2(640f, 720f)
    )

    val pathBossJuggernaut = listOf(
        Vector2(1320f, 210f),
        Vector2(930f, 210f),
        Vector2(640f, 210f),
        Vector2(350f, 210f),
        Vector2(340f, 280f),
        Vector2(320f, 380f),
        Vector2(480f, 380f),
        Vector2(640f, 380f),
        Vector2(640f, 525f),
        Vector2(640f, 720f)
    )

    val pathAuxiliaryEastMiddle = listOf(
        Vector2(960f, 380f),
        Vector2(640f, 380f),
        Vector2(640f, 525f)
    )

    val allLevelPaths = listOf(
        pathWest,
        pathSouthWest,
        pathSouthEast,
        pathEast,
        pathBossJuggernaut,
        pathAuxiliaryEastMiddle
    )

    // 15 CỌC THÁP ĐÃ ĐẨY XUỐNG 50PX (4 CỌC TRÊN Ở Y=590 NẰM THOÁNG KHÔNG CHẠM THÀNH)
    val towerSpotsList = listOf(
        Vector2(480f, 590f),
        Vector2(570f, 590f),
        Vector2(710f, 590f),
        Vector2(800f, 590f),
        Vector2(470f, 455f),
        Vector2(555f, 455f),
        Vector2(725f, 455f),
        Vector2(810f, 455f),
        Vector2(260f, 280f),
        Vector2(1020f, 280f),
        Vector2(550f, 305f),
        Vector2(640f, 305f),
        Vector2(730f, 305f),
        Vector2(550f, 130f),
        Vector2(730f, 130f)
    )

    val imperialGuardPositions = listOf(
        Vector2(615f, 530f), Vector2(640f, 530f), Vector2(665f, 530f),
        Vector2(320f, 380f), Vector2(345f, 380f),
        Vector2(935f, 380f), Vector2(960f, 380f)
    )

    // 17 WAVES CAMPAIGN
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
    val heroicWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.MARAUDER, 3, 2.0f, pathIndex = 0),
        ))
    )

    val ironWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.MARAUDER, 3, 2.0f, pathIndex = 0),
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

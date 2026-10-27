package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level6Citadel {

    const val campaignStartingGold = 700
    const val heroicStartingGold = 1200
    const val ironStartingGold = 800

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

    // 0 = West, 1 = South > West, 2 = South > East, 3 = East, 4 = Boss Juggernaut, 5 = Aux
    val allLevelPaths = listOf(
        pathWest,
        pathSouthWest,
        pathSouthEast,
        pathEast,
        pathBossJuggernaut,
        pathAuxiliaryEastMiddle
    )

    // 2 VỊ TRÍ THÁP PHÉP CẤP 3 (WIZARD TOWER) XÂY SẴN TRONG CHẾ ĐỘ IRON (HÌNH 3)
    val ironPrebuiltWizardSpotLeft = Vector2(470f, 455f)
    val ironPrebuiltWizardSpotRight = Vector2(810f, 455f)
    val ironPrebuiltWizardSpots = listOf(
        ironPrebuiltWizardSpotLeft,
        ironPrebuiltWizardSpotRight
    )

    // 15 CỌC THÁP CHIẾN LƯỢC (IRON MODE: 2 WIZARD TOWER + 13 CỌC TRỐNG = 15)
    val towerSpotsList = listOf(
        Vector2(480f, 590f),
        Vector2(570f, 590f),
        Vector2(710f, 590f),
        Vector2(800f, 590f),
        ironPrebuiltWizardSpotLeft,  // [5] Đồi cỏ trên trái - ngoài (Iron: Wizard Tower #1)
        Vector2(555f, 455f),         // [6] Đồi cỏ trên trái - trong
        Vector2(725f, 455f),         // [7] Đồi cỏ trên phải - trong
        ironPrebuiltWizardSpotRight, // [8] Đồi cỏ trên phải - ngoài (Iron: Wizard Tower #2)
        Vector2(260f, 280f),
        Vector2(1020f, 280f),
        Vector2(550f, 305f),
        Vector2(640f, 305f),
        Vector2(730f, 305f),
        Vector2(550f, 130f),
        Vector2(730f, 130f)
    )

    // 7 LÍNH CANH IMPERIAL GUARDS (CHỈ XUẤT HIỆN Ở CAMPAIGN & HEROIC, KHÔNG CÓ Ở IRON)
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

    // =========================================================================
    // HEROIC CHALLENGE MÀN 6 CHUẨN WIKI (6 WAVES - 1200 GOLD - 7 IMPERIAL GUARDS)
    // =========================================================================
    val heroicWaves = listOf(
        // Wave 1: South>West: Bandit x10, Brigand x6, Dark Knight x4 | South>East: Bandit x10, Brigand x6, Dark Knight x4
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 10, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 1.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BANDIT, 10, 1.0f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.4f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 1.8f, pathIndex = 2)
        )),
        // Wave 2: West: Gargoyle x12 | East: Gargoyle x12
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 12, 0.9f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 12, 0.9f, pathIndex = 3)
        )),
        // Wave 3 (Asymmetrical per Wiki): West: Bandit x18 | South>West: Brigand x10, Dark Knight x4 | South>East: Brigand x10, Dark Knight x3 | East: Bandit x18
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 18, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 10, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 1.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 10, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 3, 1.8f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.BANDIT, 18, 0.8f, pathIndex = 3)
        )),
        // Wave 4: West: Marauder x3 | South>West: Brigand x29 | South>East: Brigand x29 | East: Marauder x3
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.MARAUDER, 3, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 29, 0.75f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 29, 0.75f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.MARAUDER, 3, 3.0f, pathIndex = 3)
        )),
        // Wave 5: West: Worg x10 | South>West: Dark Knight x24, Shadow Archer x5 | South>East: Dark Knight x24, Shadow Archer x5 | East: Worg x10
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WORG, 10, 1.1f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 24, 0.95f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 5, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 24, 0.95f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 5, 1.4f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.WORG, 10, 1.1f, pathIndex = 3)
        )),
        // Wave 6: West & East: Brigand x10, Marauder x4, Shadow Archer x6 | South>West & South>East: Marauder x2, Dark Knight x14, Shadow Archer x8
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 10, 1.1f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 4, 2.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 2, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 14, 1.1f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 8, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 2, 3.0f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 14, 1.1f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 8, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.BRIGAND, 10, 1.1f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.MARAUDER, 4, 2.6f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.2f, pathIndex = 3)
        ))
    )

    // =========================================================================
    // IRON CHALLENGE MÀN 6 CHUẨN WIKI (1 WAVE - 800 GOLD - 2 WIZARD TOWERS - BARRACKS ONLY)
    // =========================================================================
    val ironWaves = listOf(
        WaveData(listOf(
            // West (pathIndex = 0): Goblin x26, Orc x20, Brigand x22, Shaman x3, Marauder x3, Dark Knight x6
            WaveSpawnInfo(EnemyType.GOBLIN, 26, 0.65f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 20, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 22, 1.05f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 3, 2.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 3, 3.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 1.6f, pathIndex = 0),

            // South > West (pathIndex = 1): Goblin x26, Orc x20, Brigand x22, Shaman x3, Marauder x5, Dark Knight x6
            WaveSpawnInfo(EnemyType.GOBLIN, 26, 0.65f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.ORC, 20, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 22, 1.05f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHAMAN, 3, 2.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 5, 2.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 1.6f, pathIndex = 1),

            // South > East (pathIndex = 2): Goblin x26, Orc x20, Brigand x22, Shaman x3, Marauder x5, Dark Knight x6
            WaveSpawnInfo(EnemyType.GOBLIN, 26, 0.65f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.ORC, 20, 1.0f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.BRIGAND, 22, 1.05f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHAMAN, 3, 2.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.MARAUDER, 5, 2.8f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 1.6f, pathIndex = 2),

            // East (pathIndex = 3): Goblin x26, Orc x20, Brigand x22, Shaman x3, Marauder x3, Dark Knight x6
            WaveSpawnInfo(EnemyType.GOBLIN, 26, 0.65f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.ORC, 20, 1.0f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.BRIGAND, 22, 1.05f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.SHAMAN, 3, 2.2f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.MARAUDER, 3, 3.2f, pathIndex = 3),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 1.6f, pathIndex = 3)
        ))
    )

    val data = LevelData(
        levelNumber = 6,
        levelName = "The Citadel",
        startingGold = campaignStartingGold,
        startingLives = 20,
        paths = allLevelPaths,
        towerSpots = towerSpotsList,
        waves = campaignWaves,
        maxTowerLevel = 4
    )
}

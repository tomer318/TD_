package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level11ForsakenValley {

    const val campaignStartingGold = 1300
    const val heroicStartingGold = 2100
    const val ironStartingGold = 2600

    // 1. ĐƯỜNG TRÁI (SOUTHWEST - pathIndex = 0)
    val pathSouthwest = listOf(
        Vector2(460f, -40f),
        Vector2(460f, 90f),
        Vector2(420f, 180f),
        Vector2(350f, 250f),
        Vector2(290f, 330f),
        Vector2(280f, 410f),
        Vector2(320f, 480f),
        Vector2(410f, 520f),
        Vector2(520f, 530f),
        Vector2(640f, 530f),
        Vector2(640f, 620f),
        Vector2(640f, 720f),
        Vector2(640f, 760f)
    )

    // 2. ĐƯỜNG PHẢI (SOUTHEAST - pathIndex = 1)
    val pathSoutheast = listOf(
        Vector2(820f, -40f),
        Vector2(820f, 90f),
        Vector2(860f, 180f),
        Vector2(930f, 250f),
        Vector2(990f, 330f),
        Vector2(1000f, 410f),
        Vector2(960f, 480f),
        Vector2(870f, 520f),
        Vector2(760f, 530f),
        Vector2(640f, 530f),
        Vector2(640f, 620f),
        Vector2(640f, 720f),
        Vector2(640f, 760f)
    )

    // TÂM MIỆNG NÚI LỬA MAGMA FIELD
    val magmaFieldPosition = Vector2(640f, 360f)

    // 3. ĐƯỜNG TỪ MAGMA PIT RẼ TRÁI
    val pathMagmaLeft = listOf(
        Vector2(640f, 360f),
        Vector2(640f, 280f),
        Vector2(570f, 240f),
        Vector2(490f, 220f),
        Vector2(420f, 240f),
        Vector2(350f, 250f),
        Vector2(290f, 330f),
        Vector2(280f, 410f),
        Vector2(320f, 480f),
        Vector2(410f, 520f),
        Vector2(520f, 530f),
        Vector2(640f, 530f),
        Vector2(640f, 760f)
    )

    // 4. ĐƯỜNG TỪ MAGMA PIT RẼ PHẢI (EASTERN PATH CHO MAGMA ELEMENTAL SPAWN)
    val pathMagmaRight = listOf(
        Vector2(640f, 360f),
        Vector2(640f, 280f),
        Vector2(710f, 240f),
        Vector2(790f, 220f),
        Vector2(860f, 240f),
        Vector2(930f, 250f),
        Vector2(990f, 330f),
        Vector2(1000f, 410f),
        Vector2(960f, 480f),
        Vector2(870f, 520f),
        Vector2(760f, 530f),
        Vector2(640f, 530f),
        Vector2(640f, 760f)
    )

    val allLevelPaths = listOf(
        pathSouthwest,   // 0: Nhánh Tây Nam (Southwest)
        pathSoutheast,   // 1: Nhánh Đông Nam (Southeast)
        pathMagmaRight,  // 2: Nhánh Magma rẽ phải (Eastern path)
        pathMagmaLeft    // 3: Nhánh Magma rẽ trái
    )

    // 18 CỌC XÂY THÁP CHIẾN LƯỢC
    val towerSpotsList = listOf(
        Vector2(560f, 600f),
        Vector2(720f, 600f),
        Vector2(460f, 460f),
        Vector2(540f, 465f),
        Vector2(740f, 465f),
        Vector2(820f, 460f),
        Vector2(390f, 440f),
        Vector2(890f, 440f),
        Vector2(370f, 360f),
        Vector2(910f, 360f),
        Vector2(500f, 300f),
        Vector2(780f, 300f),
        Vector2(420f, 310f),
        Vector2(870f, 310f),
        Vector2(530f, 150f),
        Vector2(750f, 150f),
        Vector2(360f, 130f),
        Vector2(930f, 130f)
    )

    // 19 WAVES CAMPAIGN CHUẨN WIKI
    val campaignWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 4, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 5, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 8, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_IMP, 1, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 3, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 1, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GARGOYLE, 3, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 18, 0.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 3, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 18, 0.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 3, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 9, 1.1f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 3, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 3, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 11, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 8, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 3, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 4, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 1, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.NECROMANCER, 1, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.NECROMANCER, 1, 2.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 12, 0.9f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 1, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 8, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_IMP, 6, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 9, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 6, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GARGOYLE, 9, 0.8f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 12, 0.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 4, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.NECROMANCER, 2, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 2, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 1, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 8, 0.9f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_LORD, 2, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 3, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 15, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 3, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.NECROMANCER, 2, 2.5f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 25, 0.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 25, 0.6f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 12, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 3, 2.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 18, 0.8f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_IMP, 10, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 10, 0.8f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_LORD, 2, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 12, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 12, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 4, 2.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 12, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BANDIT, 20, 0.6f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 1, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.NECROMANCER, 3, 2.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 10, 0.9f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 4, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 12, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 3, 2.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 24, 0.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 3, 3.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.NECROMANCER, 1, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 24, 0.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 3, 3.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 1, 2.5f, pathIndex = 1)
        ))
    )

    // =========================================================================
    // 6 WAVES HEROIC CHALLENGE MÀN 11 CHUẨN WIKI (2100 GOLD)
    // =========================================================================
    val heroicWaves = listOf(
        // Wave 1: Southwest (Demon Spawn x18) | Southeast (Bandit x10, Brigand x25)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 18, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 10, 0.9f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 25, 0.95f, pathIndex = 1)
        )),
        // Wave 2: Southwest (Worg x4, Demon Spawn x40) | Southeast (Worg x4, Brigand x36, Dark Knight x11)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WORG, 4, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 40, 0.75f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 4, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 36, 0.80f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 11, 1.2f, pathIndex = 1)
        )),
        // Wave 3: Southwest (Demon Hound x5, Demon Lord x2, Demon Spawn x24) | Southeast (Worg x8, Dark Slayer x2, Dark Knight x24)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 5, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 2, 3.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 24, 0.85f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 8, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 2, 3.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 24, 0.95f, pathIndex = 1)
        )),
        // Wave 4: Southwest (Necromancer x4) | Southeast (Necromancer x4)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.NECROMANCER, 4, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.NECROMANCER, 4, 2.5f, pathIndex = 1)
        )),
        // Wave 5: Southwest & Southeast (Gargoyle x16, Demon Imp x6, Magma Elemental x3, Worg x20)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 16, 0.85f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 6, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 3, 4.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 20, 0.90f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 16, 0.85f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 6, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 3, 4.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 20, 0.90f, pathIndex = 1)
        )),
        // Wave 6: Southwest (Demon Lord x3, Demon Spawn x35, Necromancer x3) | Southeast (Dark Slayer x5, Dark Knight x35, Necromancer x3)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_LORD, 3, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 35, 0.75f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.NECROMANCER, 3, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 5, 2.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 35, 0.80f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 3, 2.5f, pathIndex = 1)
        ))
    )

    // =========================================================================
    // 1 WAVE IRON CHALLENGE MÀN 11 CHUẨN WIKI (2600 GOLD - MAGE & BARRACKS ONLY)
    // =========================================================================
    val ironWaves = listOf(
        WaveData(listOf(
            // Southwest (pathIndex = 0)
            WaveSpawnInfo(EnemyType.BANDIT, 60, 0.65f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 20, 1.1f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 60, 0.65f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 80, 0.75f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 22, 2.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 18, 2.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 28, 1.2f, pathIndex = 0),

            // Southeast (pathIndex = 1)
            WaveSpawnInfo(EnemyType.BANDIT, 60, 0.65f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GARGOYLE, 20, 1.1f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WULF, 60, 0.65f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 80, 0.75f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 22, 2.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 18, 2.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 28, 1.2f, pathIndex = 1)
        ))
    )

    val data = LevelData(
        levelNumber = 11,
        levelName = "Forsaken Valley",
        startingGold = campaignStartingGold,
        startingLives = 20,
        paths = allLevelPaths,
        towerSpots = towerSpotsList,
        waves = campaignWaves,
        maxTowerLevel = 4
    )
}

package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level12TheDarkTower {

    const val campaignStartingGold = 1300
    const val heroicStartingGold = 2500
    const val ironStartingGold = 2800

    // 1. ĐƯỜNG ĐỎ (TÂY BẮC - NORTHWEST: pathIndex = 0)
    val pathNorthwest = listOf(
        Vector2(450f, 620f),
        Vector2(320f, 590f),
        Vector2(240f, 535f),
        Vector2(320f, 505f),
        Vector2(480f, 505f),
        Vector2(640f, 505f),
        Vector2(750f, 505f),
        Vector2(830f, 460f),
        Vector2(840f, 380f),
        Vector2(780f, 335f),
        Vector2(640f, 335f),
        Vector2(480f, 335f),
        Vector2(370f, 335f),
        Vector2(290f, 260f),
        Vector2(320f, 185f),
        Vector2(400f, 165f),
        Vector2(520f, 165f),
        Vector2(640f, 165f),
        Vector2(760f, 165f),
        Vector2(860f, 150f),
        Vector2(890f, 80f),
        Vector2(890f, -40f)
    )

    // 2. ĐƯỜNG VÀNG (ĐÔNG BẮC - NORTHEAST: pathIndex = 1)
    val pathNortheast = listOf(
        Vector2(830f, 620f),
        Vector2(950f, 600f),
        Vector2(1020f, 520f),
        Vector2(1020f, 430f),
        Vector2(970f, 350f),
        Vector2(880f, 310f),
        Vector2(760f, 335f),
        Vector2(640f, 335f),
        Vector2(480f, 335f),
        Vector2(370f, 335f),
        Vector2(290f, 260f),
        Vector2(320f, 185f),
        Vector2(400f, 165f),
        Vector2(520f, 165f),
        Vector2(640f, 165f),
        Vector2(760f, 165f),
        Vector2(860f, 150f),
        Vector2(890f, 80f),
        Vector2(890f, -40f)
    )

    // 3. ĐƯỜNG XANH DƯƠNG: BÃI THA MA (GRAVEYARD: pathIndex = 2)
    val pathGraveyard = listOf(
        Vector2(210f, 335f),
        Vector2(300f, 280f),
        Vector2(330f, 220f),
        Vector2(400f, 165f),
        Vector2(520f, 165f),
        Vector2(640f, 165f),
        Vector2(760f, 165f),
        Vector2(860f, 150f),
        Vector2(890f, 80f),
        Vector2(890f, -40f)
    )

    val summoningCircleTop = Vector2(640f, 505f)
    val summoningCircleCenter = Vector2(640f, 335f)
    val summoningCircleBottom = Vector2(640f, 165f)

    val graveyardPosition = Vector2(210f, 335f)
    val balconyPosition = Vector2(640f, 630f)

    val allLevelPaths = listOf(
        pathNorthwest,
        pathNortheast,
        pathGraveyard
    )

    // 13 VỊ TRÍ CỌC THÁP CHIẾN LƯỢC
    val towerSpotsList = listOf(
        Vector2(420f, 560f),
        Vector2(850f, 550f),
        Vector2(430f, 420f),
        Vector2(535f, 420f),
        Vector2(640f, 420f),
        Vector2(940f, 490f),
        Vector2(910f, 410f),
        Vector2(460f, 250f),
        Vector2(560f, 250f),
        Vector2(660f, 250f),
        Vector2(760f, 230f),
        Vector2(860f, 220f),
        Vector2(940f, 155f)
    )

    // 15 WAVES CAMPAIGN
    val campaignWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 4, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 2, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 6, 1.3f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.3f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 1, 3.5f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 5, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 1, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 5, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 1, 3.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 6, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 3, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 4.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 4, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 2, 2.0f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 4, 1.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 3.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 4, 1.6f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 8, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 2, 1.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 1, 3.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 8, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 1, 3.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 8, 1.3f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 2, 1.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 1, 3.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BANDIT, 8, 1.3f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 1, 3.5f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 8, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 2.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 6, 1.3f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 2.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.5f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 6, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 6, 2.5f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WORG, 6, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.NECROMANCER, 1, 4.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 6, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.NECROMANCER, 2, 4.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 6, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 1, 4.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 6, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 2, 4.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 12, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 4, 4.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 13, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 4, 4.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 12, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 4, 4.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 13, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 4, 4.5f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 6, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 6, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 5.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 10, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 1, 5.5f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_IMP, 10, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 10, 2.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 3, 6.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 3, 6.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 7, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 3, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 5.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 7, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 3, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 5.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 7, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 3, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 1, 5.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 7, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 3, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 1, 5.5f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 28, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 5, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 5, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 1, 4.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 5, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 5, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 2, 4.5f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 8, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 1, 6.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 1, 6.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 2, 5.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 8, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 1, 6.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 2, 5.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 1, 6.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 2, 5.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 8, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 1, 6.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 2, 5.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 1, 6.0f, pathIndex = 1)
        ))
    )

    // =========================================================================
    // 6 WAVES HEROIC CHALLENGE MÀN 12 CHUẨN WIKI (2500 GOLD - NO VEZ'NAN / NO PORTALS)
    // =========================================================================
    val heroicWaves = listOf(
        // Wave 1: Northwest (Demon Spawn x18, Demon Hound x6, Demon Lord x1) | Northeast (Brigand x16, Dark Knight x8, Marauder x3)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 18, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 6, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 4.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 16, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 8, 1.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 3, 3.0f, pathIndex = 1)
        )),
        // Wave 2: Northwest (Gargoyle x20, Demon Imp x8) | Northeast (Gargoyle x20, Rocket Rider x6)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 20, 0.9f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 8, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 20, 0.9f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 6, 1.8f, pathIndex = 1)
        )),
        // Wave 3: Northwest (Demon Spawn x24, Demon Hound x10, Demon Lord x2) | Northeast (Worg x12, Necromancer x4, Dark Knight x12, Shadow Archer x8)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 24, 0.85f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 10, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 2, 3.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 12, 1.1f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 4, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 12, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 8, 1.2f, pathIndex = 1)
        )),
        // Wave 4: Northwest (Giant Spider x24, Spider Matriarch x8, Demon Imp x8) | Northeast (Giant Spider x24, Spider Matriarch x8, Rocket Rider x6)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 24, 0.75f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 8, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 8, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 24, 0.75f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 8, 2.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 6, 1.8f, pathIndex = 1)
        )),
        // Wave 5: Northwest (Demon Spawn x30, Demon Hound x12, Demon Lord x3, Magma Elemental x2) | Northeast (Dark Knight x16, Dark Slayer x4, Shadow Archer x12, Necromancer x4)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 30, 0.80f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 12, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 3, 3.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 2, 4.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 16, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 4, 3.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 12, 1.1f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 4, 3.0f, pathIndex = 1)
        )),
        // Wave 6: Northwest (Demon Spawn x36, Demon Lord x4, Magma Elemental x4) | Northeast (Dark Knight x22, Dark Slayer x5, Necromancer x4, Magma Elemental x4)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 36, 0.75f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 4, 3.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 4, 4.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 22, 1.1f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 5, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 4, 2.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 4, 4.5f, pathIndex = 1)
        ))
    )

    // =========================================================================
    // 1 WAVE IRON CHALLENGE MÀN 12 CHUẨN WIKI (2800 GOLD - ARCHER, MAGE, BARRACKS ONLY)
    // =========================================================================
    val ironWaves = listOf(
        WaveData(listOf(
            // Northwest (pathIndex = 0): Demon Spawn x74, Demon Lord x11, Gargoyle x14, Demon Imp x5, Demon Hound x8, Magma Elemental x5
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 74, 0.70f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 11, 3.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 14, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 5, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 8, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 5, 5.0f, pathIndex = 0),

            // Northeast (pathIndex = 1): Marauder x4, Brigand x29, Necromancer x4, Dark Slayer x8, Dark Knight x10, Shadow Archer x12, Worg x10, Magma Elemental x5
            WaveSpawnInfo(EnemyType.MARAUDER, 4, 3.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 29, 0.90f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 4, 3.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 8, 3.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 10, 1.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 12, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 10, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 5, 5.0f, pathIndex = 1)
        ))
    )

    val data = LevelData(
        levelNumber = 12,
        levelName = "The Dark Tower",
        startingGold = campaignStartingGold,
        startingLives = 20,
        paths = allLevelPaths,
        towerSpots = towerSpotsList,
        waves = campaignWaves,
        maxTowerLevel = 4
    )
}

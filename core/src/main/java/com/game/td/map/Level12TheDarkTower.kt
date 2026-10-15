package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level12TheDarkTower {

    // 1. ĐƯỜNG ĐỎ (TÂY BẮC): QUA VÒNG TRÒN TRÊN RỒI QUẸO XUỐNG LUÔN TẦNG GIỮA
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

    // 2. ĐƯỜNG VÀNG (ĐÔNG BẮC): CỔNG PHẢI VÒNG XUỐNG TẦNG GIỮA QUA VÒNG TRÒN TRUNG TÂM
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

    // 3. ĐƯỜNG XANH DƯƠNG: BÃI THA MA (GRAVEYARD)
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

    // 15 WAVES CAMPAIGN - ĐÃ ĐIỀU CHỈNH KHOẢNG CÁCH VÀ CÂN BẰNG TỪNG WAVE
    val campaignWaves = listOf(
        // Wave 1
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 4, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 2, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 6, 1.3f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.3f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 1, 3.5f, pathIndex = 1)
        )),
        // Wave 2
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 5, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 1, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 5, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 1, 3.0f, pathIndex = 1)
        )),
        // Wave 3
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 6, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 3, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 4.5f, pathIndex = 0), // Demon Lord cách xa quái nhỏ
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 4, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 2, 2.0f, pathIndex = 0)
        )),
        // Wave 4: Dark Knight đi giãn cách để trụ kịp phá giáp
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 4, 1.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 3.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 4, 1.6f, pathIndex = 1)
        )),
        // Wave 5
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
        // Wave 6
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
        // Wave 7
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 6, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 6, 2.5f, pathIndex = 1)
        )),
        // Wave 8: Necromancer giãn cách lớn (4.0s)
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
        // Wave 9: Nhện mẹ Matriarch cách nhau 4.5s để không đẻ trứng quá tải
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
        // Wave 10
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 6, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 6, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 5.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 10, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 1, 5.5f, pathIndex = 1)
        )),
        // Wave 11
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_IMP, 10, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_IMP, 10, 2.0f, pathIndex = 1)
        )),
        // Wave 12: Magma Elemental giãn cách lớn (6.0s) không đi dính chùm
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 3, 6.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 3, 6.0f, pathIndex = 1)
        )),
        // Wave 13
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
        // Wave 14
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 28, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 5, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 5, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 1, 4.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 5, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 5, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.NECROMANCER, 2, 4.5f, pathIndex = 1)
        )),
        // Wave 15: CHIA LÀM 3 ĐỢT HÀNH QUÂN RÕ RỆT (Tiền phong -> Chủ lực -> Tử thủ)
        WaveData(listOf(
            // Đợt 1: Tiền phong
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 8, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 1, 6.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 1, 6.0f, pathIndex = 1),

            // Đợt 2: Chủ lực
            WaveSpawnInfo(EnemyType.DEMON_LORD, 2, 5.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 8, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 1, 6.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 2, 5.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 1, 6.0f, pathIndex = 1),

            // Đợt 3: Tử thủ
            WaveSpawnInfo(EnemyType.DEMON_LORD, 2, 5.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 8, 1.3f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 1, 6.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 2, 5.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MAGMA_ELEMENTAL, 1, 6.0f, pathIndex = 1)
        ))
    )

    val data = LevelData(
        levelNumber = 12,
        levelName = "The Dark Tower",
        startingGold = 1300,
        startingLives = 20,
        paths = allLevelPaths,
        towerSpots = towerSpotsList,
        waves = campaignWaves,
        maxTowerLevel = 4
    )
}

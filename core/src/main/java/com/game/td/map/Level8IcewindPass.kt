package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level8IcewindPass {

    const val campaignStartingGold = 800
    const val heroicStartingGold = 1100
    const val ironStartingGold = 1200

    // 1. ĐƯỜNG ĐỎ (NHÁNH CHÍNH - NORTH > PATH: pathIndex = 0)
    val pathMain = listOf(
        Vector2(1320f, 530f),
        Vector2(1100f, 530f),
        Vector2(900f, 530f),
        Vector2(700f, 520f),
        Vector2(530f, 490f), // Bắt đầu ôm khúc cua chữ S sườn Tây
        Vector2(470f, 420f),
        Vector2(490f, 355f),
        Vector2(580f, 350f),
        Vector2(700f, 350f), // Băng ngang sang phải
        Vector2(780f, 335f),
        Vector2(800f, 290f), // Cua gập xuống dốc
        Vector2(780f, 250f),
        Vector2(660f, 220f), // Nhập nhánh và rẽ về hướng Tây
        Vector2(500f, 205f),
        Vector2(320f, 185f),
        Vector2(150f, 175f),
        Vector2(-40f, 175f)  // Cổng thoát bên trái
    )

    // 2. ĐƯỜNG VÀNG (CỬA HANG MỎ - NORTH > CAVE: pathIndex = 1)
    val pathCave = listOf(
        // Đoạn vào hang:
        Vector2(1320f, 530f),
        Vector2(1100f, 530f),
        Vector2(900f, 530f),
        Vector2(780f, 550f),
        Vector2(680f, 610f),
        Vector2(620f, 660f), // Cửa hang trên Tây Bắc (Chui vào hầm)
        // Đoạn ra khỏi hang:
        Vector2(920f, 370f), // Cửa hang dưới Đông Nam (Bước ra ngoài)
        Vector2(850f, 330f),
        Vector2(780f, 250f),
        Vector2(660f, 220f), // Nhập vào đường chính
        Vector2(500f, 205f),
        Vector2(320f, 185f),
        Vector2(150f, 175f),
        Vector2(-40f, 175f)
    )

    // 3. ĐƯỜNG XANH DƯƠNG (XUẤT PHÁT ĐÔNG NAM - SOUTHEAST: pathIndex = 2)
    val pathSouthEast = listOf(
        Vector2(1320f, 130f),
        Vector2(1120f, 130f),
        Vector2(950f, 150f),
        Vector2(800f, 185f),
        Vector2(660f, 220f), // Nhập vào đường đỏ
        Vector2(500f, 205f),
        Vector2(320f, 185f),
        Vector2(150f, 175f),
        Vector2(-40f, 175f)
    )

    val allLevelPaths = listOf(
        pathMain,       // 0: North > Path
        pathCave,       // 1: North > Cave
        pathSouthEast   // 2: Southeast
    )

    // VỊ TRÍ THÁP PHÉP CẤP 2 (ADEPT TOWER) XÂY SẴN TRONG CHẾ ĐỘ IRON (HÌNH 3)
    val ironPrebuiltAdeptSpot = Vector2(625f, 425f)

    // 12 VỊ TRÍ CỌC THÁP CHIẾN LƯỢC (IRON MODE: 1 ADEPT TOWER + 11 CỌC TRỐNG = 12)
    val towerSpotsList = listOf(
        // Cụm tầng trên (dưới đường đỏ trên - 3 cọc)
        ironPrebuiltAdeptSpot, // [1] Cọc ngoài bên trái gờ trên (Iron: Adept Tower)
        Vector2(715f, 425f),   // [2] Cọc giữa gờ trên
        Vector2(805f, 425f),   // [3] Cọc phải gờ trên

        // Cụm tầng giữa (dưới khúc đường ngang giữa - 3 cọc)
        Vector2(500f, 288f),
        Vector2(590f, 288f),
        Vector2(680f, 288f),

        // Cọc lẻ sườn Tây (gần khúc rẽ thoát bên trái)
        Vector2(350f, 255f),

        // Cụm tầng dưới (giữa đường đỏ và đường xanh - 3 cọc)
        Vector2(530f, 140f),
        Vector2(635f, 155f),
        Vector2(740f, 125f),

        // Cụm chân hang Đông Nam (2 cọc)
        Vector2(878f, 237f),
        Vector2(968f, 237f)
    )

    // 18 WAVES CAMPAIGN CHUẨN WIKI
    val campaignWaves = listOf(
        // Wave 1
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 8, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 2, 2.5f, pathIndex = 2)
        )),
        // Wave 2
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 9, 1.3f, pathIndex = 0)
        )),
        // Wave 3
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 5, 2.0f, pathIndex = 0)
        )),
        // Wave 4
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 4, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 2, 1.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 2, 1.8f, pathIndex = 0)
        )),
        // Wave 5
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 9, 1.0f, pathIndex = 0)
        )),
        // Wave 6
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 6, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHAMPION, 2, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 6, 1.0f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 2, 2.5f, pathIndex = 2)
        )),
        // Wave 7
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 3, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 2, 1.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 5, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 3, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 4, 1.6f, pathIndex = 0)
        )),
        // Wave 8
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 18, 0.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 2, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 1, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WULF, 9, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 4, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 2, 1.8f, pathIndex = 0)
        )),
        // Wave 9
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 8, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHAMPION, 4, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 10, 0.9f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 7, 2.2f, pathIndex = 2)
        )),
        // Wave 10
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.YETI, 2, 3.5f, pathIndex = 0)
        )),
        // Wave 11
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 24, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 16, 0.8f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 6, 2.2f, pathIndex = 2)
        )),
        // Wave 12
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 14, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.4f, pathIndex = 0)
        )),
        // Wave 13
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 12, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHAMPION, 4, 2.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHIEFTAIN, 2, 4.0f, pathIndex = 0)
        )),
        // Wave 14
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 8, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.TROLL, 12, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHAMPION, 4, 2.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHIEFTAIN, 2, 4.0f, pathIndex = 0)
        )),
        // Wave 15
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 6, 0.9f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 6, 0.9f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.YETI, 5, 3.0f, pathIndex = 0)
        )),
        // Wave 16
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 36, 0.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 30, 0.6f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 12, 1.8f, pathIndex = 2)
        )),
        // Wave 17
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 10, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.TROLL_CHIEFTAIN, 1, 4.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.TROLL, 20, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHAMPION, 6, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHIEFTAIN, 2, 3.5f, pathIndex = 0)
        )),
        // Wave 18
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 6, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.YETI, 9, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 40, 0.5f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 30, 1.2f, pathIndex = 2)
        ))
    )

    // =========================================================================
    // HEROIC CHALLENGE MÀN 8 CHUẨN WIKI (6 WAVES - 1100 GOLD - MAX UPGRADE LVL 4)
    // =========================================================================
    val heroicWaves = listOf(
        // Wave 1: North>Cave: Wulf x16 | North>Path: Troll x7 | Southeast: Giant Spider x12
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 16, 0.75f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.TROLL, 7, 1.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 12, 1.0f, pathIndex = 2)
        )),
        // Wave 2: North>Path: Troll x15 | Southeast: Gargoyle x11
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 15, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 11, 1.1f, pathIndex = 2)
        )),
        // Wave 3: North>Cave: Wulf x12 | North>Path: Worg x6, Winter Wolf x4, Yeti x1 | Southeast: Spider Matriarch x6
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 12, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 6, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 4, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.YETI, 1, 4.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 6, 2.2f, pathIndex = 2)
        )),
        // Wave 4: North>Path: Troll Chieftain x1, Troll Champion x4, Troll x19
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 19, 1.1f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHAMPION, 4, 2.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHIEFTAIN, 1, 4.0f, pathIndex = 0)
        )),
        // Wave 5: North>Path: Troll Champion x6, Troll Chieftain x1, Troll x12 | Southeast: Giant Spider x24, Spider Matriarch x9
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 12, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHAMPION, 6, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHIEFTAIN, 1, 4.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 24, 0.75f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 9, 2.0f, pathIndex = 2)
        )),
        // Wave 6: North>Path: Winter Wolf x12, Yeti x6 | Southeast: Gargoyle x47
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 12, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.YETI, 6, 2.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 47, 0.55f, pathIndex = 2)
        ))
    )

    // =========================================================================
    // IRON CHALLENGE MÀN 8 CHUẨN WIKI (1 WAVE - 1200 GOLD - 1 ADEPT TOWER - ARTILLERY & BARRACKS ONLY)
    // =========================================================================
    val ironWaves = listOf(
        WaveData(listOf(
            // North > Cave (pathIndex = 1): Wulf x81, Worg x2
            WaveSpawnInfo(EnemyType.WULF, 81, 0.55f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 2, 3.0f, pathIndex = 1),

            // North > Path (pathIndex = 0): Gargoyle x125
            WaveSpawnInfo(EnemyType.GARGOYLE, 125, 0.45f, pathIndex = 0),

            // Southeast (pathIndex = 2): Brigand x114, Marauder x10, Bandit x44
            WaveSpawnInfo(EnemyType.BANDIT, 44, 0.65f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.BRIGAND, 114, 0.60f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.MARAUDER, 10, 2.8f, pathIndex = 2)
        ))
    )

    val data = LevelData(
        levelNumber = 8,
        levelName = "Icewind Pass",
        startingGold = campaignStartingGold,
        startingLives = 20,
        paths = allLevelPaths,
        towerSpots = towerSpotsList,
        waves = campaignWaves,
        maxTowerLevel = 4
    )
}

package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level8IcewindPass {

    // 1. ĐƯỜNG ĐỎ (NHÁNH CHÍNH - UỐN THEO NÉT VẼ TAY CỦA BẠN)
    val pathMain = listOf(
        Vector2(1320f, 530f),
        Vector2(1100f, 530f),
        Vector2(900f, 530f),
        Vector2(700f, 520f),
        Vector2(530f, 490f), // Bắt đầu ôm khúc cua chữ S sườn Tây
        Vector2(470f, 420f),
        Vector2(490f, 355f),
        Vector2(580f, 350f),
        Vector2(700f, 350f), // Băng ngang sang phải[cite: 89]
        Vector2(780f, 335f),
        Vector2(800f, 290f), // Cua gập xuống dốc[cite: 89]
        Vector2(780f, 250f),
        Vector2(660f, 220f), // Nhập nhánh và rẽ về hướng Tây[cite: 89]
        Vector2(500f, 205f),
        Vector2(320f, 185f),
        Vector2(150f, 175f),
        Vector2(-40f, 175f)  // Cổng thoát bên trái[cite: 89]
    )

    // 2. ĐƯỜNG VÀNG (CỬA HANG MỎ)[cite: 89]
    val pathCave = listOf(
        // Đoạn vào hang:
        Vector2(1320f, 530f),
        Vector2(1100f, 530f),
        Vector2(900f, 530f),
        Vector2(780f, 550f),
        Vector2(680f, 610f),
        Vector2(620f, 660f), // Cửa hang trên Tây Bắc (Chui vào hầm)[cite: 89]
        // Đoạn ra khỏi hang:
        Vector2(920f, 370f), // Cửa hang dưới Đông Nam (Bước ra ngoài)[cite: 89]
        Vector2(850f, 330f),
        Vector2(780f, 250f),
        Vector2(660f, 220f), // Nhập vào đường chính[cite: 89]
        Vector2(500f, 205f),
        Vector2(320f, 185f),
        Vector2(150f, 175f),
        Vector2(-40f, 175f)
    )

    // 3. ĐƯỜNG XANH DƯƠNG (XUẤT PHÁT ĐÔNG NAM)[cite: 89]
    val pathSouthEast = listOf(
        Vector2(1320f, 130f),
        Vector2(1120f, 130f),
        Vector2(950f, 150f),
        Vector2(800f, 185f),
        Vector2(660f, 220f), // Nhập vào đường đỏ[cite: 89]
        Vector2(500f, 205f),
        Vector2(320f, 185f),
        Vector2(150f, 175f),
        Vector2(-40f, 175f)
    )

    val allLevelPaths = listOf(
        pathMain,       // 0: Đường Đỏ[cite: 89]
        pathCave,       // 1: Đường Vàng[cite: 89]
        pathSouthEast   // 2: Đường Xanh Dương[cite: 89]
    )

    // 12 VỊ TRÍ CỌC THÁP KHỚP HOÀN TOÀN THEO CÁC VÒNG TÍM BẠN ĐÃ KHOANH[cite: 79]
    val towerSpotsList = listOf(
        // Cụm tầng trên (dưới đường đỏ trên - 3 cọc)[cite: 79]
        Vector2(625f, 425f),
        Vector2(715f, 425f),
        Vector2(805f, 425f),

        // Cụm tầng giữa (dưới khúc đường ngang giữa - 3 cọc)[cite: 79]
        Vector2(500f, 288f),
        Vector2(590f, 288f),
        Vector2(680f, 288f),

        // Cọc lẻ sườn Tây (gần khúc rẽ thoát bên trái)[cite: 79]
        Vector2(350f, 255f),

        // Cụm tầng dưới (giữa đường đỏ và đường xanh - 3 cọc)[cite: 79]
        Vector2(530f, 140f),
        Vector2(635f, 155f),
        Vector2(740f, 125f),

        // Cụm chân hang Đông Nam (2 cọc)[cite: 79]
        Vector2(878f, 237f),
        Vector2(968f, 237f)
    )

    // 18 WAVES CAMPAIGN CHUẨN WIKI[cite: 81, 82, 83]
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

    val data = LevelData(
        levelNumber = 8,
        levelName = "Icewind Pass",
        startingGold = 800,
        startingLives = 20,
        paths = allLevelPaths,
        towerSpots = towerSpotsList,
        waves = campaignWaves,
        maxTowerLevel = 4
    )
}

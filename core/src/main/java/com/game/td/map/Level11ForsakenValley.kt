package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level11ForsakenValley {

    // 1. ĐƯỜNG TRÁI (SOUTHWEST) - ĐƯỜNG TÍM NGẮN LẠI & VÒNG CUNG HẠ THẤP
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
        Vector2(640f, 530f), // GIAO NHAU TẠI TRỤC DỌC (ĐƯỜNG ĐỎ KÉO DÀI XUỐNG)
        Vector2(640f, 620f),
        Vector2(640f, 720f),
        Vector2(640f, 760f)  // CỔNG THÀNH BẮC
    )

    // 2. ĐƯỜNG PHẢI (SOUTHEAST) - ĐƯỜNG TÍM NGẮN LẠI & VÒNG CUNG HẠ THẤP
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
        Vector2(640f, 530f), // GIAO NHAU TẠI TRỤC DỌC (ĐƯỜNG ĐỎ KÉO DÀI XUỐNG)
        Vector2(640f, 620f),
        Vector2(640f, 720f),
        Vector2(640f, 760f)  // CỔNG THÀNH BẮC
    )

    // TÂM MIỆNG NÚI LỬA MAGMA FIELD (HẠ XUỐNG GIỮA BẢN ĐỒ)
    val magmaFieldPosition = Vector2(640f, 360f)

    // 3. ĐƯỜNG XANH DƯƠNG RẼ TRÁI (TỪ MAGMA PIT RA NHÁNH TÂY)
    val pathMagmaLeft = listOf(
        Vector2(640f, 360f), // TÂM HỒ NÚI LỬA
        Vector2(640f, 280f), // ĐI XUỐNG KHE NÚI
        Vector2(570f, 240f),
        Vector2(490f, 220f),
        Vector2(420f, 240f), // NHẬP VÀO ĐƯỜNG TÂY
        Vector2(350f, 250f),
        Vector2(290f, 330f),
        Vector2(280f, 410f),
        Vector2(320f, 480f),
        Vector2(410f, 520f),
        Vector2(520f, 530f),
        Vector2(640f, 530f),
        Vector2(640f, 760f)
    )

    // 4. ĐƯỜNG XANH DƯƠNG RẼ PHẢI (TỪ MAGMA PIT RA NHÁNH ĐÔNG)
    val pathMagmaRight = listOf(
        Vector2(640f, 360f), // TÂM HỒ NÚI LỬA
        Vector2(640f, 280f), // ĐI XUỐNG KHE NÚI
        Vector2(710f, 240f),
        Vector2(790f, 220f),
        Vector2(860f, 240f), // NHẬP VÀO ĐƯỜNG ĐÔNG
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
        pathSouthwest,   // 0: Nhánh Tây Nam
        pathSoutheast,   // 1: Nhánh Đông Nam
        pathMagmaRight,  // 2: Nhánh Magma rẽ phải
        pathMagmaLeft    // 3: Nhánh Magma rẽ trái
    )

    // 18 CỌC XÂY THÁP - ĐÃ CÂN ĐỐI TỌA ĐỘ ĐỂ WHEEL MENU KHÔNG BỊ TRÀN VIỀN MÀN HÌNH
    val towerSpotsList = listOf(
        // 2 cọc hai bên đường đỏ cổng thành Bắc (y=570f: đủ khoảng trống 150px lên mép trên 720px)
        Vector2(560f, 600f),
        Vector2(720f, 600f),

        // 4 cọc nằm phía trên miệng núi lửa (trong lòng vòng cung Bắc)
        Vector2(460f, 460f),
        Vector2(540f, 465f),
        Vector2(740f, 465f),
        Vector2(820f, 460f),

        // 2 cọc sườn trên hai bên
        Vector2(390f, 440f),
        Vector2(890f, 440f),

        // 2 cọc sườn giữa hai bên
        Vector2(370f, 360f),
        Vector2(910f, 360f),

        // 2 cọc nằm hai bên lối thoát nham thạch dưới miệng núi lửa
        Vector2(500f, 300f),
        Vector2(780f, 300f),

        // 2 cọc sườn ngoài dưới hai bên
        Vector2(420f, 310f),
        Vector2(870f, 310f),

        // 2 cọc tháp nằm giữa 2 lối vào phía Nam
        Vector2(530f, 150f),
        Vector2(750f, 150f),

        // 2 cọc ngoài cùng mép trái và mép phải phía Nam
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

    val data = LevelData(
        levelNumber = 11,
        levelName = "Forsaken Valley",
        startingGold = 1300,
        startingLives = 20,
        paths = allLevelPaths,
        towerSpots = towerSpotsList,
        waves = campaignWaves,
        maxTowerLevel = 4
    )
}

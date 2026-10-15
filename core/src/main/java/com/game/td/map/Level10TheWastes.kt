package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level10TheWastes {

    // 1. ĐƯỜNG ĐỎ (BÊN TRÁI) - CUA S RỘNG, THÂN CHỮ J ĐÂM VUÔNG GÓC LÊN NGÃ 3
    val pathSouthwest = listOf(
        Vector2(-40f, 160f),
        Vector2(140f, 160f),
        Vector2(240f, 160f),
        Vector2(310f, 165f),
        Vector2(355f, 195f),
        Vector2(375f, 250f), // EO THẮT LƯNG DƯỚI (GIỮ NGUYÊN KHOẢNG CÁCH VỚI ĐƯỜNG PHẢI)
        Vector2(350f, 295f),
        Vector2(280f, 335f),
        Vector2(200f, 375f),
        Vector2(145f, 430f), // KHÚC CUA TO RỘNG PHÍA TÂY
        Vector2(145f, 490f),
        Vector2(185f, 530f), // NHẬP ĐƯỜNG XƯƠNG TỪ NGHĨA TRANG TÍM
        Vector2(255f, 510f),
        Vector2(340f, 490f), // CHẠY NGANG DƯỚI NGHĨA TRANG
        Vector2(430f, 490f), // ĐÁY MÓC CHỮ J
        // THÂN CHỮ J ĐÂM THẲNG ĐỨNG LÊN (x = 430f)
        Vector2(430f, 570f),
        Vector2(430f, 670f), // ĐÂM VUÔNG GÓC 90° VÀO ĐƯỜNG NGANG y = 670f
        Vector2(370f, 670f), // RẼ VUÔNG GÓC SANG TRÁI (x = 370f)
        Vector2(370f, 760f)  // CỔNG THÀNH PHÍA BẮC (ĐÍCH ĐẾN)
    )

    // 2. ĐƯỜNG BÊN PHẢI - CUA TRÒN MỞ RỘNG VÀ ĐOẠN ĐƯỜNG THẲNG NGANG BẰNG PHẲNG
    val pathSoutheast = listOf(
        Vector2(1320f, 160f),
        Vector2(1150f, 160f),
        Vector2(950f, 160f),
        Vector2(800f, 160f),
        Vector2(680f, 160f),
        Vector2(590f, 175f),
        Vector2(540f, 205f),
        Vector2(515f, 250f), // ĐỐI DIỆN EO ĐƯỜNG ĐỎ (KHOẢNG CÁCH CHUẨN XÁC THEO VẠCH XANH)
        Vector2(505f, 310f),
        Vector2(505f, 380f),
        Vector2(520f, 440f),
        // KHÚC CUA BÊN PHẢI MỞ RỘNG TO VÀ BO TRÒN MỀM MẠI
        Vector2(560f, 490f),
        Vector2(630f, 530f),
        Vector2(710f, 560f),
        Vector2(770f, 595f),
        Vector2(785f, 635f), // ĐỈNH CUA VÒNG TRÒN RỘNG SANG PHÍA ĐÔNG
        Vector2(765f, 665f),
        Vector2(720f, 670f),
        // ĐOẠN ĐƯỜNG THẲNG BĂNG HOÀN TOÀN TỪ PHẢI QUA TRÁI (y = 670f)
        Vector2(600f, 670f),
        Vector2(500f, 670f),
        Vector2(430f, 670f), // NHẬP VUÔNG GÓC 90° VÀO THÂN CHỮ J TẠI NGÃ 3
        Vector2(370f, 670f), // RẼ VUÔNG GÓC LÊN CỔNG BẮC
        Vector2(370f, 760f)  // CÙNG TIẾN VÀO CỔNG NHÀ
    )

    // 3. ĐƯỜNG NGHĨA ĐỊA - BỘ XƯƠNG TỪ VÙNG KHOANH TÍM BÒ RA NHẬP VÀO ĐƯỜNG ĐỎ
    val pathSkeleton = listOf(
        Vector2(130f, 560f), // TÂM NGHĨA TRANG TẠI VÙNG KHOANH TÍM
        Vector2(160f, 530f),
        Vector2(185f, 530f), // NHẬP VÀO ĐƯỜNG ĐỎ
        Vector2(255f, 510f),
        Vector2(340f, 490f),
        Vector2(430f, 490f),
        Vector2(430f, 570f),
        Vector2(430f, 670f),
        Vector2(370f, 670f),
        Vector2(370f, 760f)
    )

    val allLevelPaths = listOf(pathSouthwest, pathSoutheast, pathSkeleton)

    // VỊ TRÍ NGHĨA TRANG TẠI VÙNG KHOANH TÍM GÓC TÂY BẮC
    val graveyardPosition = Vector2(130f, 560f)

    // 14 CỌC XÂY THÁP ĐẶT CHUẨN XÁC TRÊN GỜ ĐẤT (KHÔNG BỊ LÒNG ĐƯỜNG ĐÈ LÊN)
    val towerSpotsList = listOf(
        // Cụm gờ đất dưới bên trái
        Vector2(210f, 240f),
        Vector2(290f, 240f),

        // Cụm gờ đất khúc cua chữ S bên trái
        Vector2(240f, 440f),
        Vector2(320f, 400f),

        // Cọc nằm bên trong móc chữ J
        Vector2(360f, 570f),

        // Cọc nằm bên trái cổng thành
        Vector2(275f, 585f),

        // Cụm dải trung tâm giữa 2 làn đường
        Vector2(445f, 240f), // Nằm giữa khoảng hở của vạch xanh dương
        Vector2(430f, 400f),
        Vector2(500f, 560f), // Nằm bên phải thân chữ J

        // Cụm sườn trung tâm bên phải
        Vector2(600f, 275f),
        Vector2(430f, 320f),
        Vector2(600f, 400f),

        // Cụm phía trên bên phải (dưới đoạn cua tròn đường vàng)
        Vector2(580f, 600f),
        Vector2(670f, 610f)
    )

    // 19 WAVES CAMPAIGN CHUẨN WIKI
    val campaignWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.4f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 3, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 2, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 8, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 6, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 3, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 3, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 3, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 8, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 2, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 8, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 6, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 3, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 3, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 4, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 2, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 1, 2.5f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 7, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 7, 1.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 6, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 3, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 6, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 15, 0.8f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 3.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 4, 1.5f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 6, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 4, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 2, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 4, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 12, 0.9f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 20, 0.7f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 10, 1.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 25, 0.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 8, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 5, 1.5f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 6, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 3, 1.5f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 1, 3.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 7, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 3, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 8, 1.0f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 4, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 4, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 10, 0.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 6, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 3, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 10, 0.9f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 6, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 2, 2.5f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 25, 0.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 12, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 5, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 10, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 9, 1.1f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BANDIT, 30, 0.6f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_LORD, 2, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 16, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 2, 2.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 15, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 12, 1.0f, pathIndex = 1)
        ))
    )

    val data = LevelData(
        levelNumber = 10,
        levelName = "The Wastes",
        startingGold = 800,
        startingLives = 20,
        paths = allLevelPaths,
        towerSpots = towerSpotsList,
        waves = campaignWaves,
        maxTowerLevel = 4
    )
}

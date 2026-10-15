package com.game.td.map

import com.badlogic.gdx.math.CatmullRomSpline
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level2Farmlands {

    // 1. TỌA ĐỘ ĐIỀU KHIỂN UỐN LƯỢN CHỮ S CHUẨN NGUYÊN BẢN KINGDOM RUSH
    private val controlPoints = arrayOf(
        Vector2(1360f, 470f), // Điểm mỏ neo ngoài biên
        Vector2(1330f, 470f), // [CỔNG XUẤT PHÁT ĐÔNG]
        Vector2(1100f, 470f),
        Vector2(950f, 470f),
        Vector2(850f, 495f),  // Bắt đầu dốc ngược lên đồi Bắc
        Vector2(780f, 560f),
        Vector2(710f, 620f),  // Đỉnh vòm bên phải
        Vector2(640f, 630f),  // Đỉnh cao nhất phía Bắc
        Vector2(575f, 575f),  // UỐN CONG XUỐNG NHƯ CHỮ S (ĐI VÀO GIỮA 2 CỌC THÁP)
        Vector2(540f, 485f),  // Hạ dốc xuống dải đất trung tâm
        Vector2(480f, 430f),  // Uốn lượn vòng dưới chân cọc tháp trên trái
        Vector2(395f, 415f),  // Mở cua rộng sang sườn Tây
        Vector2(335f, 355f),  // Đỉnh bụng ngoài khúc cua Tây
        Vector2(365f, 280f),
        Vector2(450f, 240f),  // Vắt chéo qua dải đất ngang
        Vector2(570f, 240f),
        Vector2(680f, 245f),  // Vòng sang khúc cua Đông
        Vector2(755f, 205f),  // Đỉnh bụng ngoài khúc cua Đông
        Vector2(740f, 135f),  // Đổ dốc thẳng xuống cổng thoát
        Vector2(655f, 85f),
        Vector2(575f, 60f),
        Vector2(575f, -40f),  // [CỔNG THOÁT NAM]
        Vector2(575f, -70f)   // Điểm mỏ neo ngoài biên
    )

    private fun generateSmoothPath(controls: Array<Vector2>, samples: Int = 120): List<Vector2> {
        val spline = CatmullRomSpline(controls, false)
        val points = mutableListOf<Vector2>()
        for (i in 0..samples) {
            val out = Vector2()
            spline.valueAt(out, i.toFloat() / samples)
            points.add(out)
        }
        return points
    }

    val waypointsList = generateSmoothPath(controlPoints)

    // 2. TỌA ĐỘ 7 CỌC THÁP CHUẨN XÁC NẰM GỌN TRONG CÁC KHÚC CUA CHỮ S
    // Toàn bộ cọc đều nằm trong vùng an toàn y in [115f, 535f] không bị che khuất
    val towerSpotsList = listOf(
        Vector2(460f, 530f), // 1. Bờ ngoài khúc cua Bắc (trái)
        Vector2(670f, 545f), // 2. Bờ trong khúc cua Bắc (phải)
        Vector2(595f, 415f), // 3. Dải đất giữa 2 tầng đường trên
        Vector2(415f, 340f), // 4. Bụng trong khúc cua Tây
        Vector2(480f, 165f), // 5. Sườn trái dải đất dưới (ngay trên đồng ruộng)
        Vector2(650f, 175f), // 6. Bụng trong khúc cua Đông
        Vector2(825f, 125f)  // 7. Bờ Đông trước cổng thoát Nam
    )

    val campaignWaves = listOf(
        WaveData(listOf(WaveSpawnInfo(EnemyType.GOBLIN, 20, 1.2f))),
        WaveData(listOf(WaveSpawnInfo(EnemyType.GOBLIN, 24, 1.0f))),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 12, 0.9f),
            WaveSpawnInfo(EnemyType.ORC, 4, 1.8f)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 6, 1.1f)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.8f),
            WaveSpawnInfo(EnemyType.ORC, 6, 1.5f)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 10, 0.7f),
            WaveSpawnInfo(EnemyType.ORC, 6, 1.4f),
            WaveSpawnInfo(EnemyType.WULF, 6, 1.0f)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.6f),
            WaveSpawnInfo(EnemyType.ORC, 10, 1.3f),
            WaveSpawnInfo(EnemyType.WULF, 10, 0.9f)
        ))
    )

    val heroicWaves = listOf(
        WaveData(listOf(WaveSpawnInfo(EnemyType.WULF, 12, 0.7f))),
        WaveData(listOf(WaveSpawnInfo(EnemyType.GOBLIN, 18, 0.7f))),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 16, 0.7f),
            WaveSpawnInfo(EnemyType.ORC, 6, 1.6f)
        )),
        WaveData(listOf(WaveSpawnInfo(EnemyType.WULF, 24, 0.55f))),
        WaveData(listOf(WaveSpawnInfo(EnemyType.ORC, 12, 1.3f))),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 24, 0.6f),
            WaveSpawnInfo(EnemyType.WULF, 24, 0.6f)
        ))
    )

    val ironWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 9, 0.6f),
            WaveSpawnInfo(EnemyType.GOBLIN, 100, 0.45f),
            WaveSpawnInfo(EnemyType.ORC, 35, 1.2f)
        ))
    )

    val data = LevelData(
        levelNumber = 2,
        levelName = "The Farmlands",
        startingGold = 220,
        startingLives = 20,
        maxTowerLevel = 2,
        waypoints = waypointsList,
        towerSpots = towerSpotsList,
        waves = campaignWaves
    )
}

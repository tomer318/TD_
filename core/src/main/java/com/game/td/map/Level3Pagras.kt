package com.game.td.map

import com.badlogic.gdx.math.CatmullRomSpline
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level3Pagras {

    // 1. NHÁNH TRÊN (NORTHEAST ENTRANCE) - THU GỌN VÀO VÙNG AN TOÀN
    private val topControlPoints = arrayOf(
        Vector2(1360f, 490f),
        Vector2(1330f, 490f), // Cổng Đông Bắc
        Vector2(1120f, 490f),
        Vector2(940f, 485f),
        Vector2(790f, 480f),
        Vector2(640f, 470f),
        Vector2(480f, 435f),  // Uốn quanh mũi dải đồi trung tâm
        Vector2(400f, 370f),  // Khúc cua vòng sang sườn Tây
        Vector2(430f, 310f),
        Vector2(490f, 260f),  // HỢP LƯU VỚI NHÁNH DƯỚI
        Vector2(600f, 235f),
        Vector2(705f, 215f),
        Vector2(775f, 175f),  // Khúc cua chữ S phía Đông
        Vector2(745f, 120f),
        Vector2(650f, 95f),   // Đáy vòng cua chữ S phía Nam
        Vector2(530f, 110f),
        Vector2(380f, 160f),  // Đâm thẳng sang Tây ra cổng thoát
        Vector2(200f, 205f),  // Đi qua chốt chặn Linirea (Alleria)
        Vector2(60f, 225f),
        Vector2(-40f, 225f),  // Cổng thoát Linirea phía Tây
        Vector2(-80f, 225f)
    )

    // 2. NHÁNH DƯỚI (EAST ENTRANCE)
    private val bottomControlPoints = arrayOf(
        Vector2(1360f, 350f),
        Vector2(1330f, 350f), // Cổng Đông giữa
        Vector2(1120f, 350f),
        Vector2(940f, 345f),
        Vector2(790f, 340f),
        Vector2(640f, 330f),
        Vector2(530f, 305f),
        Vector2(460f, 280f),
        Vector2(490f, 260f),  // HỢP LƯU VÀO ĐƯỜNG CHÍNH (ĐỒNG NHẤT TUYỆT ĐỐI)
        Vector2(600f, 235f),
        Vector2(705f, 215f),
        Vector2(775f, 175f),
        Vector2(745f, 120f),
        Vector2(650f, 95f),
        Vector2(530f, 110f),
        Vector2(380f, 160f),
        Vector2(200f, 205f),
        Vector2(60f, 225f),
        Vector2(-40f, 225f),
        Vector2(-80f, 225f)
    )

    private fun generateSmoothPath(controls: Array<Vector2>, samples: Int = 115): List<Vector2> {
        val spline = CatmullRomSpline(controls, false)
        val points = mutableListOf<Vector2>()
        for (i in 0..samples) {
            val out = Vector2()
            spline.valueAt(out, i.toFloat() / samples)
            points.add(out)
        }
        return points
    }

    val topWaypoints = generateSmoothPath(topControlPoints)
    val bottomWaypoints = generateSmoothPath(bottomControlPoints)

    // 3. TOÀN BỘ 12 CỌC THÁP NẰM GỌN TRONG SAFE ZONE y in [150f, 535f]
    // Khoảng cách tối thiểu giữa 2 cọc là 105px, không bao giờ bị đè HUD hay rớt màn hình
    val towerSpotsList = listOf(
        Vector2(470f, 510f), // 1. Bờ Bắc ngoài (gần doanh trại lều)
        Vector2(620f, 540f), // 2. Bờ Bắc ngoài (ở giữa)
        Vector2(560f, 390f), // 3. Mũi Tây dải đồi trung tâm
        Vector2(670f, 405f), // 4. Giữa dải đồi trung tâm
        Vector2(780f, 415f), // 5. Phía Đông dải đồi trung tâm
        Vector2(820f, 270f), // 6. Khúc giữa vòng lượn S trên
        Vector2(480f, 360f), // 7. Sườn trong khúc cua hợp lưu
        Vector2(720f, 275f), // 8. Giữa sườn dốc chữ S
        Vector2(655f, 160f), // 9. Bụng trong khúc cua chữ S phía Đông
        Vector2(850f, 150f), // 10. Góc Đông Nam (gần cụm rừng dưới)
        Vector2(550f, 175f), // 11. Bờ Nam (gần nhà tranh & đàn cừu)
        Vector2(430f, 210f)  // 12. Bờ Nam Tây (giữa đường thoát và hồ nước)
    )

    val campaignWaves = listOf(
        WaveData(listOf(WaveSpawnInfo(EnemyType.GOBLIN, 24, 1.0f, pathIndex = 0))),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 5, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 5, 2.0f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 6, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 6, 1.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.8f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 10, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 25, 0.7f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ORC, 5, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 5, 1.8f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ORC, 9, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.7f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ORC, 12, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 25, 0.6f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 30, 0.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 6, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WULF, 10, 0.8f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 6, 1.3f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 10, 0.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 8, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 10, 0.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.ORC, 8, 1.2f, pathIndex = 1)
        ))
    )

    val heroicWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 5, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 5, 0.8f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 25, 0.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 2, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 1, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 25, 0.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.ORC, 2, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHAMAN, 1, 2.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 18, 0.65f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 8, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 18, 0.65f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.ORC, 8, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ORC, 3, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.OGRE, 1, 3.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 3, 1.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.OGRE, 1, 3.5f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 30, 0.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 4, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.OGRE, 2, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GOBLIN, 30, 0.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.ORC, 4, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.OGRE, 2, 3.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.OGRE, 5, 2.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 15, 0.85f, pathIndex = 1)
        ))
    )

    val ironWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 80, 0.75f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 6, 2.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.OGRE, 2, 6.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 45, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.OGRE, 2, 6.5f, pathIndex = 1)
        ))
    )

    val data = LevelData(
        levelNumber = 3,
        levelName = "Pagras",
        startingGold = 300,
        startingLives = 20,
        maxTowerLevel = 3,
        waypoints = topWaypoints,
        secondaryWaypoints = bottomWaypoints,
        towerSpots = towerSpotsList,
        waves = campaignWaves
    )
}

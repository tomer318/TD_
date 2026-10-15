package com.game.td.map

import com.badlogic.gdx.math.CatmullRomSpline
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level4TwinRivers {

    // 1. NHÁNH TÂY (WEST LANE)
    private val westControlPoints = arrayOf(
        Vector2(620f, -40f),
        Vector2(620f, 0f),    // [CỔNG XUẤT PHÁT NAM]
        Vector2(620f, 100f),
        Vector2(540f, 165f),
        Vector2(440f, 235f),  // Qua cầu Tây Nam
        Vector2(340f, 320f),
        Vector2(320f, 430f),  // Bụng mạn Tây
        Vector2(375f, 545f),
        Vector2(480f, 620f),
        Vector2(620f, 650f),  // Hợp lưu vào cổng Bắc
        Vector2(620f, 720f),
        Vector2(620f, 760f)
    )

    // 2. NHÁNH ĐÔNG (EAST LANE): MỞ RỘNG TỐI ĐA BỤNG ĐÔNG NAM ĐỂ DẢI ĐẤT THẬT RỘNG
    private val eastControlPoints = arrayOf(
        Vector2(620f, -40f),
        Vector2(620f, 0f),    // [CỔNG XUẤT PHÁT NAM]
        Vector2(620f, 90f),
        Vector2(720f, 140f),  // Dạt nhanh sang Đông Nam
        Vector2(845f, 205f),  // Vòng rộng ở đáy Nam
        Vector2(935f, 300f),  // MỞ RỘNG BỤNG ĐÔNG
        Vector2(955f, 420f),  // Đỉnh ngoài sườn Đông
        Vector2(885f, 540f),
        Vector2(780f, 605f),  // Qua cầu Đông Bắc
        Vector2(620f, 650f),  // Hợp lưu vào cổng Bắc
        Vector2(620f, 720f),
        Vector2(620f, 760f)
    )

    private fun generateSmoothPath(controls: Array<Vector2>, samples: Int = 110): List<Vector2> {
        val spline = CatmullRomSpline(controls, false)
        val points = mutableListOf<Vector2>()
        for (i in 0..samples) {
            val out = Vector2()
            spline.valueAt(out, i.toFloat() / samples)
            points.add(out)
        }
        return points
    }

    val data = LevelData(
        levelNumber = 4,
        levelName = "Twin Rivers",
        startingGold = 520,
        startingLives = 20,
        maxTowerLevel = 3,

        paths = listOf(
            generateSmoothPath(westControlPoints),
            generateSmoothPath(eastControlPoints)
        ),

        // 15 VỊ TRÍ ĐẶT TRỤ: CĂN VỪA KHÍT TỪNG VÒNG ĐỎ TRONG Ô XANH LÁ
        towerSpots = listOf(
            // === CỤM NỬA TRÊN ĐẢO (BỜ TÂY BẮC SÔNG - 6 CỌC) ===
            Vector2(445f, 500f), // [Tháp 1]: Bờ Tây Bắc đảo
            Vector2(505f, 550f), // [Tháp 2]: Bờ Bắc đảo
            Vector2(590f, 560f), // [Tháp 3]: Đỉnh Bắc đảo
            Vector2(395f, 420f), // [Tháp 4]: Bờ Tây đảo
            Vector2(420f, 360f), // [Tháp 5]: Bờ Tây đảo (đầu cầu dưới)
            Vector2(695f, 550f), // [Tháp 6]: Bờ trên dòng sông (đầu cầu trên)

            // === CỤM 6 CỌC NỬA DƯỚI ĐẢO: NẰM GỌN TRỌN VẸN TRONG KHOANH XANH LÁ ===
            Vector2(870f, 430f), // [Tháp 7 - VÒNG ĐỎ 1]: Đón sát chân cầu Đông Bắc
            Vector2(868f, 347f), // [Tháp 8 - VÒNG ĐỎ 2]: Sườn Đông trên, sát mép đường mở rộng
            Vector2(815f, 275f), // [Tháp 9 - VÒNG ĐỎ 3]: Sườn Đông dưới
            Vector2(735f, 235f), // [Tháp 10 - VÒNG ĐỎ 4]: Bờ Nam đảo (bên phải)
            Vector2(645f, 200f), // [Tháp 11 - VÒNG ĐỎ 5]: Bờ Nam đảo (chính giữa)
            Vector2(565f, 253f), // [Tháp 12 - VÒNG ĐỎ 6]: Bờ Nam đảo (bên trái gần cầu Tây Nam)

            // === 3 CỌC VÒNG NGOÀI ===
            Vector2(300f, 250f), // [Tháp 13]: Bờ ngoài phía Tây (giữa đường và rừng)
            Vector2(725f, 65f), // [Tháp 14]: Bờ ngoài Đông Nam (cọc trái cổng Nam)
            Vector2(845f, 120f)  // [Tháp 15]: Bờ ngoài Đông Nam (cọc phải)
        ),

        waves = listOf(
            WaveData(listOf(
                WaveSpawnInfo(EnemyType.GOBLIN, 10, 1.2f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.ORC, 3, 2.5f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.GOBLIN, 10, 1.2f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.ORC, 3, 2.5f, pathIndex = 1)
            )),
            WaveData(listOf(
                WaveSpawnInfo(EnemyType.GOBLIN, 10, 1.0f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.ORC, 4, 2.0f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.GOBLIN, 10, 1.0f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.ORC, 4, 2.0f, pathIndex = 1)
            )),
            WaveData(listOf(
                WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.8f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.SHAMAN, 1, 3.0f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.ORC, 5, 1.8f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.SHAMAN, 1, 3.0f, pathIndex = 1)
            )),
            WaveData(listOf(
                WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.7f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.GOBLIN, 30, 0.6f, pathIndex = 1)
            )),
            WaveData(listOf(
                WaveSpawnInfo(EnemyType.GOBLIN, 20, 0.7f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.ORC, 9, 1.5f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.5f, pathIndex = 1)
            )),
            WaveData(listOf(
                WaveSpawnInfo(EnemyType.BANDIT, 4, 1.5f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.BANDIT, 5, 1.5f, pathIndex = 1)
            )),
            WaveData(listOf(
                WaveSpawnInfo(EnemyType.WULF, 9, 0.8f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.BANDIT, 4, 1.4f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.WULF, 9, 0.8f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.BANDIT, 4, 1.4f, pathIndex = 1)
            )),
            WaveData(listOf(
                WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.0f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.ORC, 4, 1.5f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.OGRE, 1, 4.0f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.0f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.ORC, 4, 1.5f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.OGRE, 1, 4.0f, pathIndex = 1)
            )),
            WaveData(listOf(
                WaveSpawnInfo(EnemyType.ORC, 7, 1.3f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.BANDIT, 8, 1.0f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.ORC, 7, 1.3f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.BANDIT, 8, 1.0f, pathIndex = 1)
            )),
            WaveData(listOf(
                WaveSpawnInfo(EnemyType.ORC, 4, 1.4f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.SHAMAN, 3, 2.0f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.BANDIT, 6, 1.0f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.ORC, 4, 1.4f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.SHAMAN, 3, 2.0f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.BANDIT, 6, 1.0f, pathIndex = 1)
            )),
            WaveData(listOf(
                WaveSpawnInfo(EnemyType.BRIGAND, 4, 2.0f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.BRIGAND, 4, 2.0f, pathIndex = 1)
            )),
            WaveData(listOf(
                WaveSpawnInfo(EnemyType.SHAMAN, 3, 1.8f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.BANDIT, 8, 0.9f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.BRIGAND, 4, 1.6f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.SHAMAN, 3, 1.8f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.BANDIT, 8, 0.9f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.BRIGAND, 4, 1.6f, pathIndex = 1)
            )),
            WaveData(listOf(
                WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.0f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.BANDIT, 8, 0.8f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.BRIGAND, 7, 1.4f, pathIndex = 0),
                WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.0f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.BANDIT, 8, 0.8f, pathIndex = 1),
                WaveSpawnInfo(EnemyType.BRIGAND, 7, 1.4f, pathIndex = 1)
            ))
        )
    )
}

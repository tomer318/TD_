package com.game.td.map

import com.badlogic.gdx.math.CatmullRomSpline
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level4TwinRivers {

    const val campaignStartingGold = 520
    const val heroicStartingGold = 600
    const val ironStartingGold = 1200

    // 1. NHÁNH TÂY (WEST LANE: CỔNG NAM -> CẦU TÂY NAM -> VÒNG TÂY -> CỔNG BẮC)
    private val westControlPoints = arrayOf(
        Vector2(590f, -70f), // Mỏ neo
        Vector2(590f, -40f),
        Vector2(590f, 0f),   // [CỔNG XUẤT PHÁT NAM]
        Vector2(590f, 150f), // Ngã ba tách đường Nam
        Vector2(499f, 170f),
        Vector2(420f, 232f), // QUA CẦU TÂY NAM
        Vector2(375f, 365f), // Đỉnh bụng mạn Tây
        Vector2(414f, 488f),
        Vector2(499f, 560f),
        Vector2(590f, 580f), // Ngã ba hợp lưu Bắc
        Vector2(590f, 660f), // Điểm cờ chiến thắng Linirea
        Vector2(590f, 720f), // [CỔNG THOÁT BẮC]
        Vector2(590f, 760f),
        Vector2(590f, 790f)  // Mỏ neo
    )

    // 2. NHÁNH ĐÔNG (EAST LANE: CỔNG NAM -> VÒNG ĐÔNG -> CẦU ĐÔNG BẮC -> CỔNG BẮC)
    private val eastControlPoints = arrayOf(
        Vector2(590f, -70f),
        Vector2(590f, -40f),
        Vector2(590f, 0f),   // [CỔNG XUẤT PHÁT NAM]
        Vector2(590f, 150f), // Ngã ba tách đường Nam
        Vector2(681f, 170f),
        Vector2(776f, 257f),
        Vector2(805f, 365f), // Đỉnh bụng mạn Đông
        Vector2(760f, 498f), // QUA CẦU ĐÔNG BẮC
        Vector2(681f, 560f),
        Vector2(590f, 580f), // Ngã ba hợp lưu Bắc
        Vector2(590f, 660f), // Điểm cờ chiến thắng Linirea
        Vector2(590f, 720f), // [CỔNG THOÁT BẮC]
        Vector2(590f, 760f),
        Vector2(590f, 790f)
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

    val westWaypoints = generateSmoothPath(westControlPoints)
    val eastWaypoints = generateSmoothPath(eastControlPoints)

    // 3. TOÀN BỘ 15 CỌC THÁP CHUẨN XÁC NGUYÊN BẢN THEO ẢNH GỐC
    val towerSpotsList = listOf(
        // Cụm nửa trên đảo Tây Bắc (quanh túp lều & giàn cá - 6 cọc)
        Vector2(560f, 500f), // 1. Bờ Tây Bắc đảo (gần ngã ba Bắc)
        Vector2(640f, 500f), // 2. Đỉnh Bắc đảo
        Vector2(490f, 470f), // 3. Bờ Tây đảo (trên)
        Vector2(455f, 415f), // 4. Bờ Tây đảo (giữa)
        Vector2(445f, 350f), // 5. Bờ Tây Nam đảo (đầu cầu dưới)
        Vector2(770f, 595f), // 6. Tâm đảo Tây Bắc (sát lều ngư dân)

        // Cụm nửa dưới đảo Đông Nam (quanh đụn rơm & thùng gỗ - 6 cọc)
        Vector2(860f, 445f), // 7. Bờ Đông đảo (đầu cầu trên)
        Vector2(735f, 365f), // 8. Bờ Đông đảo (giữa)
        Vector2(720f, 290f), // 9. Bờ Đông đảo (dưới)
        Vector2(665f, 235f), // 10. Bờ Nam đảo (phía Đông)
        Vector2(600f, 225f), // 11. Bờ Nam đảo (gần ngã ba Nam)
        Vector2(535f, 230f), // 12. Tâm đảo Đông Nam (gần đụn rơm)

        // 3 Cọc vòng ngoài
        Vector2(325f, 250f), // 13. Bờ ngoài phía Tây (bên kia cầu Tây Nam)
        Vector2(760f, 140f), // 14. Bờ ngoài phía Nam (bên phải đường vào)
        Vector2(815f, 200f)  // 15. Bờ ngoài góc Đông Nam
    )

    // =========================================================================
    // 4. DANH SÁCH WAVES CHO 3 CHẾ ĐỘ: CAMPAIGN, HEROIC, IRON (0 = West, 1 = East)
    // =========================================================================
    val campaignWaves = listOf(
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

    val waves = campaignWaves

    // HEROIC CHALLENGE MÀN 4 CHUẨN WIKI (6 WAVES - 600 GOLD)
    val heroicWaves = listOf(
        // Wave 1: West: Goblin x30 | East: Wulf x18
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 30, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 18, 0.9f, pathIndex = 1)
        )),
        // Wave 2: West: Orc x12 | East: Bandit x9
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ORC, 12, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 9, 1.4f, pathIndex = 1)
        )),
        // Wave 3: East: Bandit x4, Brigand x10
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 4, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 10, 1.5f, pathIndex = 1)
        )),
        // Wave 4: West: Goblin x15, Orc x18, Shaman x2, Ogre x1
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 15, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 18, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 2, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.OGRE, 1, 4.0f, pathIndex = 0)
        )),
        // Wave 5: West: Goblin x16, Orc x5, Shaman x3, Ogre x1 | East: Wulf x12, Bandit x6, Brigand x8
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 16, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ORC, 5, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.SHAMAN, 3, 2.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.OGRE, 1, 4.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 12, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BANDIT, 6, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 8, 1.5f, pathIndex = 1)
        )),
        // Wave 6: West: Orc x15, Ogre x3 | East: Brigand x15, Ogre x3
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ORC, 15, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.OGRE, 3, 3.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 15, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.OGRE, 3, 3.8f, pathIndex = 1)
        ))
    )

    // IRON CHALLENGE MÀN 4 CHUẨN WIKI (1 WAVE - 1200 GOLD - CHỈ ARCHER & BARRACKS)
    val ironWaves = listOf(
        // Wave 1: West: Goblin x150 | East: Wulf x150
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GOBLIN, 150, 0.45f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 150, 0.45f, pathIndex = 1)
        ))
    )

    val data = LevelData(
        levelNumber = 4,
        levelName = "Twin Rivers",
        startingGold = campaignStartingGold,
        startingLives = 20,
        maxTowerLevel = 3,
        paths = listOf(westWaypoints, eastWaypoints),
        towerSpots = towerSpotsList,
        waves = campaignWaves
    )
}

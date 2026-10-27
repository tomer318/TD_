package com.game.td.map

import com.badlogic.gdx.math.CatmullRomSpline
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level5Silveroak {

    const val campaignStartingGold = 700
    const val heroicStartingGold = 1000
    const val ironStartingGold = 1500

    // =========================================================================
    // 1. BA NHÁNH ĐƯỜNG ĐI (3 PATHS: 0 = South>West, 1 = South>North, 2 = East)
    // =========================================================================

    // NHÁNH 0: CỔNG NAM -> MẶT NGOÀI DẢI ĐẤT (QUA NHÀ ELF HALL) -> CỔNG BẮC
    private val southToWestControls = arrayOf(
        Vector2(520f, -60f),
        Vector2(520f, 0f),    // [CỔNG VÀO 1: NAM]
        Vector2(520f, 100f),
        Vector2(460f, 155f),  // Ngã ba tách đường Nam
        Vector2(370f, 195f),
        Vector2(285f, 245f),  // Ôm quanh đuôi dưới dải đồi
        Vector2(240f, 330f),  // Bụng cong sườn Tây
        Vector2(245f, 425f),
        Vector2(295f, 510f),  // Chạy qua trước nhà Sylvan Elf Hall
        Vector2(380f, 555f),  // Ôm mặt trên dải đồi
        Vector2(480f, 570f),
        Vector2(560f, 560f),
        Vector2(605f, 545f),  // [NGÃ BA TRÊN: HỢP LƯU VỚI NHÁNH GIỮA]
        Vector2(625f, 630f),
        Vector2(625f, 720f),  // [CỔNG THOÁT BẮC]
        Vector2(625f, 760f)
    )

    // NHÁNH 1: CỔNG NAM -> MẶT TRONG DẢI ĐẤT (LUỒN DƯỚI THÁP CUNG) -> CỔNG BẮC
    private val southToMiddleControls = arrayOf(
        Vector2(520f, -60f),
        Vector2(520f, 0f),    // [CỔNG VÀO 1: NAM]
        Vector2(520f, 100f),
        Vector2(460f, 155f),  // Ngã ba tách đường Nam
        Vector2(415f, 220f),  // Luồn vào khe hẹp
        Vector2(430f, 290f),
        Vector2(465f, 360f),  // Ôm mặt dưới dải đồi
        Vector2(515f, 405f),  // Chạy sát chân Tháp Cung cấp 3
        Vector2(570f, 425f),
        Vector2(600f, 435f),  // [NGÃ BA DƯỚI: HỢP LƯU VỚI ĐƯỜNG RỪNG NHỆN]
        Vector2(605f, 490f),  // Đoạn nối thẳng chạy bên phải Tháp Cung
        Vector2(605f, 545f),  // [NGÃ BA TRÊN: HỢP LƯU VỚI NHÁNH TÂY]
        Vector2(625f, 630f),
        Vector2(625f, 720f),  // [CỔNG THOÁT BẮC]
        Vector2(625f, 760f)
    )

    // NHÁNH 2: TỪ RỪNG NHỆN ĐÔNG NAM -> VÒNG QUA GÒ ĐỒI -> NGÃ BA DƯỚI -> CỔNG BẮC
    private val spiderCaveControls = arrayOf(
        Vector2(1170f, 190f),
        Vector2(1120f, 190f), // [CỔNG VÀO 2: RỪNG NHỆN]
        Vector2(1000f, 190f),
        Vector2(880f, 205f),
        Vector2(780f, 235f),  // Lượn quanh sườn Đông gò đồi có cây
        Vector2(700f, 300f),
        Vector2(650f, 370f),
        Vector2(600f, 435f),  // [NGÃ BA DƯỚI: HỢP LƯU VỚI NHÁNH GIỮA]
        Vector2(605f, 490f),
        Vector2(605f, 545f),  // [NGÃ BA TRÊN]
        Vector2(625f, 630f),
        Vector2(625f, 720f),  // [CỔNG THOÁT BẮC]
        Vector2(625f, 760f)
    )

    private fun generatePath(controls: Array<Vector2>, samples: Int = 115): List<Vector2> {
        val spline = CatmullRomSpline(controls, false)
        val points = mutableListOf<Vector2>()
        for (i in 0..samples) {
            val out = Vector2()
            spline.valueAt(out, i.toFloat() / samples)
            points.add(out)
        }
        return points
    }

    val pathWest = generatePath(southToWestControls)
    val pathMiddle = generatePath(southToMiddleControls)
    val pathSpider = generatePath(spiderCaveControls)

    // =========================================================================
    // 2. VỊ TRÍ CÁC CÔNG TRÌNH XÂY SẴN CHO CAMPAIGN / HEROIC / IRON
    // =========================================================================
    val prebuiltArcherSpot = Vector2(535f, 490f)           // Tháp Cung cấp 3 #1 (đỉnh dải đồi)
    val ironPrebuiltArcherSpot2 = Vector2(410f, 430f)      // Tháp Cung cấp 3 #2 (giữa dải đồi - Iron Mode)
    val ironPrebuiltArcherSpot3 = Vector2(335f, 285f)      // Tháp Cung cấp 3 #3 (đuôi dưới dải đồi - Iron Mode)

    val sylvanElfHallSpot = Vector2(310f, 610f)            // Sylvan Elf Hall #1 (Tây Bắc cạnh bờ suối)
    val ironSecondSylvanElfHallSpot = Vector2(520f, 295f)  // Sylvan Elf Hall #2 (Trên trái gò đồi tròn - Iron Mode)

    val ironPrebuiltArcherSpots = listOf(
        prebuiltArcherSpot,
        ironPrebuiltArcherSpot2,
        ironPrebuiltArcherSpot3
    )

    val ironSylvanElfHallSpots = listOf(
        sylvanElfHallSpot,
        ironSecondSylvanElfHallSpot
    )

    // =========================================================================
    // 3. TOÀN BỘ 14 VỊ TRÍ CHIẾN LƯỢC CHUẨN NGUYÊN BẢN
    // - Campaign / Heroic: 12 cọc trống + 1 Archer Lvl 3 + 1 Elf Hall = 14
    // - Iron Challenge:    9 cọc trống + 3 Archer Lvl 3 + 2 Elf Hall = 14
    // =========================================================================
    val towerSpotsList = listOf(
        prebuiltArcherSpot,          // [1] Đỉnh dải đồi (Archer Lvl 3)
        sylvanElfHallSpot,           // [2] Bờ suối Tây Bắc (Sylvan Elf Hall #1)
        ironPrebuiltArcherSpot3,     // [3] Đuôi dưới dải đồi (Cọc trống / Iron: Archer Lvl 3 #3)
        ironPrebuiltArcherSpot2,     // [4] Giữa dải đồi (Cọc trống / Iron: Archer Lvl 3 #2)
        Vector2(210f, 510f),         // [5] Bờ ngoài Tây Bắc dưới Elf Hall
        Vector2(330f, 145f),         // [6] Bờ ngoài Tây Nam gần cổng Nam
        Vector2(450f, 635f),         // [7] Phía Bắc trên dải đồi
        ironSecondSylvanElfHallSpot, // [8] Gò đồi tròn giữa - trên trái (Cọc trống / Iron: Sylvan Elf Hall #2)
        Vector2(610f, 305f),         // [9] Gò đồi tròn giữa - trên phải
        Vector2(515f, 225f),         // [10] Gò đồi tròn giữa - giữa trái
        Vector2(665f, 225f),         // [11] Gò đồi tròn giữa - giữa phải
        Vector2(565f, 175f),         // [12] Gò đồi tròn giữa - đáy
        Vector2(695f, 615f),         // [13] Bờ Đông đường lên cổng Bắc
        Vector2(685f, 485f)          // [14] Bờ ngoài phía Đông đoạn nối giữa
    )

    // =========================================================================
    // 4. DANH SÁCH WAVES CHO 3 CHẾ ĐỘ (0 = South>West, 1 = South>North, 2 = East)
    // =========================================================================
    val campaignWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 4, 1.4f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 4, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 8, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 7, 1.3f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 8, 1.1f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 5, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 5, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 2.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 3, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 5, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 10, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 2.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 15, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 15, 0.7f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WORG, 3, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 3, 2.0f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 5, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 6, 1.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 5, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 6, 1.6f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 20, 0.6f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 3, 3.5f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 4, 3.0f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WORG, 6, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 15, 0.7f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 6, 2.8f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 12, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 12, 0.9f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 8, 1.4f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 6, 2.5f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 6, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 2, 4.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 6, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 3, 1.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 1, 4.0f, pathIndex = 1)
        ))
    )

    // HEROIC CHALLENGE MÀN 5 CHUẨN WIKI (6 WAVES - 1000 GOLD)
    val heroicWaves = listOf(
        // Wave 1: South>West: Bandit x7 | South>North: Brigand x5 | East: Giant Spiders x7
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 7, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 5, 1.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 7, 1.3f, pathIndex = 2)
        )),
        // Wave 2: South>West: Bandit x30 | East: Giant Spiders x15
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 30, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 15, 0.9f, pathIndex = 2)
        )),
        // Wave 3: South>North: Brigand x12, Marauder x2 | East: Giant Spiders x12
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 12, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 2, 4.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 12, 1.0f, pathIndex = 2)
        )),
        // Wave 4: East: Giant Spiders x18, Spider Matriarch x5
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 18, 0.8f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 5, 2.8f, pathIndex = 2)
        )),
        // Wave 5: South>West: Bandit x28 | South>North: Brigand x15 | East: Giant Spiders x24
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BANDIT, 28, 0.7f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BRIGAND, 15, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 24, 0.6f, pathIndex = 2)
        )),
        // Wave 6: South>West: Marauder x4 | South>North: Marauder x4 | East: Spider Matriarch x4
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.MARAUDER, 4, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 4, 2.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 4, 3.0f, pathIndex = 2)
        ))
    )

    // IRON CHALLENGE MÀN 5 CHUẨN WIKI (1 WAVE - 1500 GOLD - 3 ARCHER LVL3 & 2 ELF HALLS)
    val ironWaves = listOf(
        // Wave 1: South>West: Marauder x3, Bandit x55 | South>North: Marauder x4, Brigand x39 | East: Giant Spiders x25, Spider Matriarch x10
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.MARAUDER, 3, 3.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 55, 0.65f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.MARAUDER, 4, 3.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 39, 0.85f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.GIANT_SPIDER, 25, 0.75f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SPIDER_MATRIARCH, 10, 2.4f, pathIndex = 2)
        ))
    )

    val data = LevelData(
        levelNumber = 5,
        levelName = "Silveroak Forest",
        startingGold = campaignStartingGold,
        startingLives = 20,
        maxTowerLevel = 3,
        paths = listOf(pathWest, pathMiddle, pathSpider),
        towerSpots = towerSpotsList,
        waves = campaignWaves
    )
}

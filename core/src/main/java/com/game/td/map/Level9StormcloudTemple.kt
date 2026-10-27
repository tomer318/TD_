package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level9StormcloudTemple {

    const val heroicStartingGold = 2000
    const val ironStartingGold = 2400

    // 1. ĐƯỜNG ĐỎ (NORTH PATH)
    val pathNorth = listOf(
        Vector2(650f, 750f),
        Vector2(650f, 655f),
        Vector2(640f, 625f),
        Vector2(600f, 620f),
        Vector2(480f, 620f),
        Vector2(430f, 580f),
        Vector2(420f, 510f),
        Vector2(450f, 445f),
        Vector2(510f, 430f),
        Vector2(680f, 430f),
        Vector2(850f, 430f),
        Vector2(905f, 395f),
        Vector2(885f, 345f),
        Vector2(810f, 320f),
        Vector2(600f, 320f),
        Vector2(420f, 320f),
        Vector2(370f, 320f),
        Vector2(320f, 320f),
        Vector2(160f, 320f),
        Vector2(-40f, 320f)
    )

    // 2. ĐƯỜNG XANH DƯƠNG (CAVE PATH)
    val pathCave = listOf(
        Vector2(930f, 510f),
        Vector2(915f, 465f),
        Vector2(905f, 395f),
        Vector2(885f, 345f),
        Vector2(810f, 320f),
        Vector2(600f, 320f),
        Vector2(420f, 320f),
        Vector2(370f, 320f),
        Vector2(320f, 320f),
        Vector2(160f, 320f),
        Vector2(-40f, 320f)
    )

    // 3. ĐƯỜNG VÀNG (SOUTH PATH)
    val pathSouth = listOf(
        Vector2(650f, -40f),
        Vector2(650f, 35f),
        Vector2(665f, 65f),
        Vector2(730f, 75f),
        Vector2(860f, 85f),
        Vector2(915f, 120f),
        Vector2(915f, 170f),
        Vector2(870f, 200f),
        Vector2(760f, 205f),
        Vector2(560f, 205f),
        Vector2(460f, 205f),
        Vector2(390f, 225f),
        Vector2(370f, 275f),
        Vector2(370f, 320f),
        Vector2(320f, 320f),
        Vector2(160f, 320f),
        Vector2(-40f, 320f)
    )

    val allLevelPaths = listOf(pathNorth, pathCave, pathSouth)

    // 15 VỊ TRÍ CỌC THÁP
    val towerSpotsList = listOf(
        // Hàng 1 (Dải trên cùng - 3 cọc)
        Vector2(560f, 515f),
        Vector2(650f, 515f),
        Vector2(740f, 515f),

        // Hàng 2 (Dải giữa trên - 4 cọc)
        Vector2(390f, 420f),
        Vector2(520f, 375f),
        Vector2(610f, 375f),
        Vector2(710f, 375f),

        // Hàng 3 (Dải giữa dưới - 4 cọc)
        Vector2(520f, 265f),
        Vector2(610f, 265f),
        Vector2(700f, 265f),
        Vector2(790f, 265f),

        // Hàng 4 (Dải đáy Nam - 3 cọc)
        Vector2(570f, 140f),
        Vector2(660f, 140f),
        Vector2(750f, 140f),

        // Cọc lẻ sườn Đông
        Vector2(940f, 315f)
    )

    // 9 VỊ TRÍ XÂY SẴN THÁP MAGE CẤP 1 TRONG CHẾ ĐỘ IRON CHALLENGE (CHUẨN HÌNH BẢN ĐỒ WIKI)
    val ironPrebuiltMageSpots = listOf(
        // Hàng 1: 2 cọc bên trái
        Vector2(560f, 515f),
        Vector2(650f, 515f),
        // Hàng 2: 2 cọc bên phải
        Vector2(610f, 375f),
        Vector2(710f, 375f),
        // Hàng 3: 3 cọc bên trái
        Vector2(520f, 265f),
        Vector2(610f, 265f),
        Vector2(700f, 265f),
        // Hàng 4: 2 cọc bên phải
        Vector2(660f, 140f),
        Vector2(750f, 140f)
    )

    // THÁP ĐẶC BIỆT SUNRAY TOWER Ở GÓC DƯỚI ĐỀN THỜ
    val sunrayTowerPosition = Vector2(340f, 142f)

    // 19 WAVES CAMPAIGN CHUẨN WIKI
    val campaignWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 2, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 3, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.4f, pathIndex = 2)
        )),
        WaveData(listOf(WaveSpawnInfo(EnemyType.TROLL, 4, 1.8f, pathIndex = 0))),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 4, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 15, 0.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.2f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.GARGOYLE, 4, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.GARGOYLE, 4, 1.2f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.BRIGAND, 8, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 4, 1.4f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 18, 0.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 4, 1.2f, pathIndex = 1)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 6, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHAMPION, 3, 2.2f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 6, 1.5f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.2f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 1.5f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 1, 3.0f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 9, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 20, 0.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 6, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 1, 2.0f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 3, 1.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 3, 1.5f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 4, 1.5f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 6, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 2, 2.8f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 12, 1.1f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHIEFTAIN, 1, 4.0f, pathIndex = 0)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 15, 0.6f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 6, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 2, 1.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WULF, 15, 0.6f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.YETI, 1, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.YETI, 1, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.YETI, 1, 3.0f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 5, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 5, 1.2f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 8, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHAMPION, 4, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHIEFTAIN, 2, 3.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 8, 1.4f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 8, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 2, 2.5f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.YETI, 3, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.YETI, 3, 2.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.YETI, 3, 2.5f, pathIndex = 2)
        )),
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.JT, 1, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 6, 1.5f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.YETI, 3, 4.0f, pathIndex = 2)
        ))
    )

    // 6 WAVES HEROIC CHALLENGE CHUẨN WIKI
    val heroicWaves = listOf(
        // Wave 1: North (Troll x10), Cave (Wulf x12), South (Dark Knight x10)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 10, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 12, 0.7f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 10, 1.2f, pathIndex = 2)
        )),
        // Wave 2: Cave (Wulf x24), South (Bandit x20, Brigand x16, Dark Knight x10, Dark Slayer x1)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 24, 0.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BANDIT, 20, 0.7f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.BRIGAND, 16, 0.9f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 10, 1.2f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 1, 3.0f, pathIndex = 2)
        )),
        // Wave 3: North (Troll x18, Troll Chieftain x1, Troll Champion x3), Cave (Wulf x36)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 18, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHIEFTAIN, 1, 3.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHAMPION, 3, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 36, 0.5f, pathIndex = 1)
        )),
        // Wave 4: North (Winter Wolf x4, Yeti x2), Cave (Wulf x44, Worg x12), South (Winter Wolf x4, Yeti x2)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 4, 1.4f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.YETI, 2, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WULF, 44, 0.45f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WORG, 12, 0.9f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 4, 1.4f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.YETI, 2, 3.0f, pathIndex = 2)
        )),
        // Wave 5: North (Rocket Rider x7), South (Rocket Rider x7)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 7, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.ROCKET_RIDER, 7, 1.2f, pathIndex = 2)
        )),
        // Wave 6: North (Troll x18, Troll Chieftain x1, Troll Champion x2), Cave (Winter Wolf x8, Yeti x2), South (Dark Knight x10, Dark Slayer x3, Shadow Archer x8)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 18, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHIEFTAIN, 1, 3.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHAMPION, 2, 2.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WINTER_WOLF, 8, 1.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.YETI, 2, 3.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 10, 1.1f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 3, 2.5f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.SHADOW_ARCHER, 8, 1.1f, pathIndex = 2)
        ))
    )

    // 1 WAVE IRON CHALLENGE CHUẨN WIKI
    // North: Troll x183, Troll Chieftain x1, Troll Champion x4, Yeti x9
    // South: Bandit x40, Brigand x75, Dark Knight x110, Dark Slayer x15
    val ironWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.TROLL, 183, 0.45f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHAMPION, 4, 2.5f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.TROLL_CHIEFTAIN, 1, 4.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.YETI, 9, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 40, 0.55f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.BRIGAND, 75, 0.65f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 110, 0.60f, pathIndex = 2),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 15, 2.5f, pathIndex = 2)
        ))
    )

    val data = LevelData(
        levelNumber = 9,
        levelName = "Stormcloud Temple",
        startingGold = 800,
        startingLives = 20,
        paths = allLevelPaths,
        towerSpots = towerSpotsList,
        waves = campaignWaves,
        maxTowerLevel = 4
    )
}

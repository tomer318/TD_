package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level10TheWastes {

    const val heroicStartingGold = 1800
    const val ironStartingGold = 2200

    // 1. ĐƯỜNG ĐỎ (SOUTHWEST PATH - BÊN TRÁI)
    val pathSouthwest = listOf(
        Vector2(-40f, 160f),
        Vector2(140f, 160f),
        Vector2(240f, 160f),
        Vector2(310f, 165f),
        Vector2(355f, 195f),
        Vector2(375f, 250f),
        Vector2(350f, 295f),
        Vector2(280f, 335f),
        Vector2(200f, 375f),
        Vector2(145f, 430f),
        Vector2(145f, 490f),
        Vector2(185f, 530f),
        Vector2(255f, 510f),
        Vector2(340f, 490f),
        Vector2(430f, 490f),
        Vector2(430f, 570f),
        Vector2(430f, 670f),
        Vector2(370f, 670f),
        Vector2(370f, 760f)
    )

    // 2. ĐƯỜNG VÀNG (SOUTHEAST PATH - BÊN PHẢI)
    val pathSoutheast = listOf(
        Vector2(1320f, 160f),
        Vector2(1150f, 160f),
        Vector2(950f, 160f),
        Vector2(800f, 160f),
        Vector2(680f, 160f),
        Vector2(590f, 175f),
        Vector2(540f, 205f),
        Vector2(515f, 250f),
        Vector2(505f, 310f),
        Vector2(505f, 380f),
        Vector2(520f, 440f),
        Vector2(560f, 490f),
        Vector2(630f, 530f),
        Vector2(710f, 560f),
        Vector2(770f, 595f),
        Vector2(785f, 635f),
        Vector2(765f, 665f),
        Vector2(720f, 670f),
        Vector2(600f, 670f),
        Vector2(500f, 670f),
        Vector2(430f, 670f),
        Vector2(370f, 670f),
        Vector2(370f, 760f)
    )

    // 3. ĐƯỜNG NGHĨA ĐỊA (SKELETON PATH)
    val pathSkeleton = listOf(
        Vector2(130f, 560f),
        Vector2(160f, 530f),
        Vector2(185f, 530f),
        Vector2(255f, 510f),
        Vector2(340f, 490f),
        Vector2(430f, 490f),
        Vector2(430f, 570f),
        Vector2(430f, 670f),
        Vector2(370f, 670f),
        Vector2(370f, 760f)
    )

    val allLevelPaths = listOf(pathSouthwest, pathSoutheast, pathSkeleton)

    val graveyardPosition = Vector2(130f, 560f)

    // 14 CỌC XÂY THÁP
    val towerSpotsList = listOf(
        Vector2(210f, 240f),
        Vector2(290f, 240f),
        Vector2(240f, 440f),
        Vector2(320f, 400f),
        Vector2(360f, 570f),
        Vector2(275f, 585f),
        Vector2(445f, 240f),
        Vector2(430f, 400f),
        Vector2(500f, 560f),
        Vector2(600f, 275f),
        Vector2(430f, 320f),
        Vector2(600f, 400f),
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

    // 6 WAVES HEROIC CHALLENGE CHUẨN WIKI
    val heroicWaves = listOf(
        // Wave 1: Southwest (Demon Spawn x10), Southeast (Bandit x10, Brigand x10)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 10, 1.1f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 10, 0.9f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 10, 1.1f, pathIndex = 1)
        )),
        // Wave 2: Southwest (Demon Spawn x12), Southeast (Bandit x15, Brigand x16, Dark Knight x3)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 12, 1.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 15, 0.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 16, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 3, 1.8f, pathIndex = 1)
        )),
        // Wave 3: Southwest (Wulf x85), Southeast (Bandit x60)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 85, 0.40f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 60, 0.45f, pathIndex = 1)
        )),
        // Wave 4: Southwest (Demon Lord x1, Demon Spawn x14), Southeast (Dark Slayer x2, Dark Knight x14)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_LORD, 1, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 14, 0.9f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 2, 2.8f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 14, 1.0f, pathIndex = 1)
        )),
        // Wave 5: Southwest (Demon Lord x2, Demon Hound x10, Demon Spawn x20), Southeast (Dark Slayer x8)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_LORD, 2, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 10, 1.1f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 20, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 8, 2.2f, pathIndex = 1)
        )),
        // Wave 6: Southwest (Demon Hound x8, Demon Lord x2, Demon Spawn x20), Southeast (Bandit x25, Brigand x15, Dark Knight x14, Dark Slayer x4)
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 8, 1.1f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 2, 3.0f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 20, 0.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 25, 0.7f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 15, 0.9f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 14, 1.0f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 4, 2.5f, pathIndex = 1)
        ))
    )

    // 1 WAVE IRON CHALLENGE CHUẨN WIKI
    // Southwest: Wulf x110, Demon Spawn x55, Worg x12, Demon Lord x4, Demon Hound x3
    // Southeast: Bandit x80, Brigand x57, Marauder x8, Dark Knight x8, Dark Slayer x5
    val ironWaves = listOf(
        WaveData(listOf(
            WaveSpawnInfo(EnemyType.WULF, 110, 0.42f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_SPAWN, 55, 0.65f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.WORG, 12, 1.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_LORD, 4, 3.2f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.DEMON_HOUND, 3, 1.8f, pathIndex = 0),
            WaveSpawnInfo(EnemyType.BANDIT, 80, 0.45f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.BRIGAND, 57, 0.65f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.MARAUDER, 8, 2.2f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_KNIGHT, 8, 1.6f, pathIndex = 1),
            WaveSpawnInfo(EnemyType.DARK_SLAYER, 5, 2.8f, pathIndex = 1)
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

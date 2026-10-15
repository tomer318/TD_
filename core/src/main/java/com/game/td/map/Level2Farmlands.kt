package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object Level2Farmlands {
    val waypointsList = listOf(
        Vector2(1330f, 480f),
        Vector2(1100f, 480f),
        Vector2(970f, 450f),
        Vector2(860f, 420f),
        Vector2(770f, 460f),
        Vector2(695f, 545f),
        Vector2(650f, 625f),
        Vector2(590f, 650f),
        Vector2(530f, 630f),
        Vector2(495f, 550f),
        Vector2(480f, 470f),
        Vector2(430f, 420f),
        Vector2(330f, 385f),
        Vector2(245f, 340f),
        Vector2(245f, 275f),
        Vector2(310f, 235f),
        Vector2(420f, 230f),
        Vector2(540f, 250f),
        Vector2(645f, 255f),
        Vector2(710f, 205f),
        Vector2(690f, 135f),
        Vector2(590f, 105f),
        Vector2(550f, 40f),
        Vector2(540f, -40f)
    )

    val towerSpotsList = listOf(
        Vector2(405f, 610f),
        Vector2(595f, 575f),
        Vector2(610f, 475f),
        Vector2(385f, 320f),
        Vector2(475f, 125f),
        Vector2(595f, 179f),
        Vector2(685f, 45f)
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

    // Heroic Challenge Màn 2: 6 Waves chuẩn Wiki
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

    // Iron Challenge Màn 2: 1 Wave 9 Wulf, 100 Goblin, 35 Orc
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

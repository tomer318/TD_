package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

object LevelSandboxTest {
    // Tạo sẵn 100 Wave vô tận: Mỗi wave gồm 3 Goblin và 3 Orc
    private val endlessWaves: List<WaveData> = List(100) { waveIndex ->
        WaveData(
            spawns = listOf(
                WaveSpawnInfo(EnemyType.DEMON_SPAWN, 3, interval = 1.0f),
                WaveSpawnInfo(EnemyType.DEMON_LORD, 3, interval = 1.4f),
                WaveSpawnInfo(EnemyType.SKELETON_KNIGHT, 3, interval = 1.4f),
                WaveSpawnInfo(EnemyType.SKELETON, 3, interval = 1.4f)
            )
        )
    }

    val data = LevelData(
        levelNumber = 999,
        levelName = "Testing Grounds (Endless)",
        startingGold = 10000, // 10,000 Gold để tự do test trụ và nâng cấp
        startingLives = 999,   // Mạng khổng lồ không sợ thua
        waypoints = listOf(
            Vector2(100f, 360f),
            Vector2(450f, 360f),
            Vector2(450f, 540f),
            Vector2(850f, 540f),
            Vector2(850f, 220f),
            Vector2(1200f, 220f)
        ),
        towerSpots = listOf(
            Vector2(250f, 440f),
            Vector2(380f, 440f),
            Vector2(530f, 460f),
            Vector2(680f, 460f),
            Vector2(780f, 460f),
            Vector2(770f, 310f),
            Vector2(930f, 310f),
            Vector2(1050f, 310f)
        ),
        waves = endlessWaves
    )
}

package com.game.td.map

import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.EnemyType

data class WaveSpawnInfo(
    val enemyType: EnemyType,
    val count: Int,
    val interval: Float = 1.2f,
    val pathIndex: Int = 0 // Chỉ số nhánh đường quái đi (0, 1, 2, 3,...)
)

data class WaveData(
    val spawns: List<WaveSpawnInfo>,
    val delayBeforeNextWave: Float = 4.0f
)

data class LevelData(
    val levelNumber: Int,
    val levelName: String,
    val startingGold: Int,
    val startingLives: Int,
    // Danh sách toàn bộ các nhánh đường quái đi trong màn chơi (hỗ trợ 1, 2, 3, 4... nhánh)
    val paths: List<List<Vector2>> = emptyList(),
    val towerSpots: List<Vector2>,
    val waves: List<WaveData>,
    val maxTowerLevel: Int = 4,

    // Tương thích ngược: nếu truyền waypoints đơn lẻ thì tự động đóng gói vào paths
    private val legacyWaypoints: List<Vector2> = emptyList(),
    private val legacySecondaryWaypoints: List<Vector2> = emptyList()
) {
    // Thuộc tính lấy danh sách hợp nhất tất cả các đường
    val allPaths: List<List<Vector2>> = when {
        paths.isNotEmpty() -> paths
        legacySecondaryWaypoints.isNotEmpty() -> listOf(legacyWaypoints, legacySecondaryWaypoints)
        legacyWaypoints.isNotEmpty() -> listOf(legacyWaypoints)
        else -> emptyList()
    }

    // Các accessor tương thích với code cũ
    val waypoints: List<Vector2> get() = if (allPaths.isNotEmpty()) allPaths[0] else emptyList()
    val secondaryWaypoints: List<Vector2> get() = if (allPaths.size > 1) allPaths[1] else emptyList()

    // Constructor tiện lợi tương thích với cách gọi trước đây
    constructor(
        levelNumber: Int,
        levelName: String,
        startingGold: Int,
        startingLives: Int,
        waypoints: List<Vector2>,
        secondaryWaypoints: List<Vector2> = emptyList(),
        towerSpots: List<Vector2>,
        waves: List<WaveData>,
        maxTowerLevel: Int = 4
    ) : this(
        levelNumber = levelNumber,
        levelName = levelName,
        startingGold = startingGold,
        startingLives = startingLives,
        paths = if (secondaryWaypoints.isNotEmpty()) listOf(waypoints, secondaryWaypoints) else listOf(waypoints),
        towerSpots = towerSpots,
        waves = waves,
        maxTowerLevel = maxTowerLevel
    )
}

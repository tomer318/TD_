package com.game.td.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.*
import com.game.td.map.*

class WaveManager(
    private val engine: Engine,
    private val levelData: LevelData,
    var difficulty: GameDifficulty = GameDifficulty.NORMAL
) {
    var currentWaveIndex: Int = -1
    val totalWaves: Int = levelData.waves.size
    val currentWaveDisplay: Int get() = (currentWaveIndex + 1).coerceAtMost(totalWaves)

    var waveCountdownTimer: Float = 0f
    var waveMaxCountdown: Float = 26f
    val isCountdownActive: Boolean get() = waveCountdownTimer > 0f

    private data class LaneSpawner(
        var timer: Float = 0f,
        val queue: MutableList<Pair<EnemyType, Float>> = mutableListOf()
    )

    private data class BatchInfo(
        val spawns: List<WaveSpawnInfo>,
        val totalCount: Int
    )

    private val activeSpawners = mutableMapOf<Int, LaneSpawner>()
    private val laneToggles = mutableMapOf<Int, Boolean>()
    private var isNextWaveCountdownTriggered: Boolean = false

    private var totalEnemiesInCurrentWave: Int = 0

    // CƠ CHẾ CHIA ĐỢT (SUB-WAVE) THÔNG MINH
    private val pendingWaveBatches = mutableListOf<BatchInfo>()
    private var currentBatchTotalEnemies: Int = 0
    private var currentBatchDeadCount: Int = 0
    private var thresholdToNextBatch: Int = 0
    private var batchDelayTimer: Float = 0f // Khoảng nghỉ đệm giữa 2 đợt lính

    val isLevelCompleted: Boolean
        get() = currentWaveIndex >= totalWaves - 1 &&
            pendingWaveBatches.isEmpty() &&
            activeSpawners.values.all { it.queue.isEmpty() } &&
            engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get()).size() == 0

    fun update(delta: Float) {
        if (currentWaveIndex == -1) return

        // Xử lý khoảng nghỉ đệm giữa 2 đợt lính
        if (batchDelayTimer > 0f) {
            batchDelayTimer -= delta
            if (batchDelayTimer <= 0f && pendingWaveBatches.isNotEmpty()) {
                val nextBatch = pendingWaveBatches.removeAt(0)
                loadBatchIntoSpawners(nextBatch.spawns)
                currentBatchTotalEnemies = nextBatch.totalCount
                currentBatchDeadCount = 0
                thresholdToNextBatch = (currentBatchTotalEnemies * 0.70f).toInt().coerceAtLeast(1)
            }
        }

        var hasActiveSpawns = false
        for ((pathIndex, spawner) in activeSpawners) {
            if (spawner.queue.isNotEmpty()) {
                hasActiveSpawns = true
                spawner.timer -= delta
                if (spawner.timer <= 0f) {
                    val (enemyType, interval) = spawner.queue.removeAt(0)
                    spawnEnemy(enemyType, pathIndex)
                    spawner.timer = interval
                }
            }
        }

        // Tự động kích hoạt đợt kế tiếp nếu đợt hiện tại đã spawn hết và số quái trên sân còn ít
        if (!hasActiveSpawns && pendingWaveBatches.isNotEmpty() && batchDelayTimer <= 0f) {
            val aliveInLane = engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get()).size()
            if (aliveInLane <= 3) {
                batchDelayTimer = 2.0f
            }
        }

        if (!hasActiveSpawns && pendingWaveBatches.isEmpty() && !isNextWaveCountdownTriggered && currentWaveIndex < totalWaves - 1) {
            val aliveEnemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get()).size()
            val deadEnemies = (totalEnemiesInCurrentWave - aliveEnemies).coerceAtLeast(0)
            val halfThreshold = (totalEnemiesInCurrentWave * 0.60f).toInt().coerceAtLeast(1)

            if (deadEnemies >= halfThreshold) {
                isNextWaveCountdownTriggered = true
                val nextDuration = calculateDynamicCountdown(currentWaveIndex + 1)
                waveMaxCountdown = nextDuration
                waveCountdownTimer = nextDuration
            }
        }

        if (waveCountdownTimer > 0f) {
            waveCountdownTimer -= delta
            if (waveCountdownTimer <= 0f) {
                waveCountdownTimer = 0f
                if (currentWaveIndex < totalWaves - 1) {
                    startNextWave()
                }
            }
        }
    }

    // ĐƯỢC GỌI MỖI KHI 1 QUÁI VẬT BỊ TIÊU DIỆT HOẶC QUA CỔNG
    fun onEnemyDefeated() {
        if (pendingWaveBatches.isNotEmpty() && batchDelayTimer <= 0f) {
            currentBatchDeadCount++
            // NÂNG NGƯỠNG LÊN 70% (PHẢI DIỆT GẦN HẾT ĐỢT HIỆN TẠI MỚI THẢ TIẾP)
            if (currentBatchDeadCount >= thresholdToNextBatch) {
                batchDelayTimer = 3.0f // Nghỉ 3 giây trước khi đợt sau xuất quân
            }
        }
    }

    private fun calculateDynamicCountdown(nextWaveIndex: Int): Float {
        if (nextWaveIndex !in 0 until totalWaves) return 26f

        val wave = levelData.waves[nextWaveIndex]
        var totalCount = 0
        var hasHeavyEnemy = false
        var hasBoss = false

        for (spawn in wave.spawns) {
            totalCount += spawn.count
            when (spawn.enemyType) {
                EnemyType.JUGGERNAUT, EnemyType.JT, EnemyType.VEZNAN -> hasBoss = true
                EnemyType.OGRE,
                EnemyType.DARK_KNIGHT,
                EnemyType.DARK_SLAYER,
                EnemyType.MAGMA_ELEMENTAL,
                EnemyType.DEMON_LORD,
                EnemyType.MARAUDER,
                EnemyType.SPIDER_MATRIARCH -> hasHeavyEnemy = true
                else -> {}
            }
        }

        return when {
            hasBoss -> 38f
            hasHeavyEnemy || totalCount >= 40 -> 32f
            totalCount >= 20 -> 28f
            else -> 24f
        }
    }

    fun callEarlyWave(): Pair<Int, Float> {
        val savedTime = if (waveCountdownTimer > 0f) waveCountdownTimer else 0f
        val bonus = if (savedTime > 0f) (savedTime * 1.5f).toInt().coerceAtLeast(5) else 0
        waveCountdownTimer = 0f
        startNextWave()
        return Pair(bonus, savedTime)
    }

    private fun startNextWave() {
        currentWaveIndex++
        isNextWaveCountdownTriggered = false
        activeSpawners.clear()
        pendingWaveBatches.clear()
        batchDelayTimer = 0f

        if (currentWaveIndex < totalWaves) {
            val wave = levelData.waves[currentWaveIndex]
            totalEnemiesInCurrentWave = wave.spawns.sumOf { it.count }

            val hasHeavy = wave.spawns.any {
                it.enemyType in listOf(
                    EnemyType.DEMON_LORD, EnemyType.MAGMA_ELEMENTAL,
                    EnemyType.DARK_SLAYER, EnemyType.DARK_KNIGHT,
                    EnemyType.SPIDER_MATRIARCH, EnemyType.NECROMANCER,
                    EnemyType.OGRE, EnemyType.MARAUDER
                )
            }

            // TỰ ĐỘNG CHIA 2 HOẶC 3 ĐỢT LÍNH NẾU WAVE CÓ NHIỀU QUÁI HOẶC CÓ QUÁI HẠNG NẶNG
            if (totalEnemiesInCurrentWave >= 16 || (hasHeavy && totalEnemiesInCurrentWave >= 10)) {
                val numBatches = if (totalEnemiesInCurrentWave <= 26) 2 else 3
                val batchLists: List<MutableList<WaveSpawnInfo>> = List(numBatches) { mutableListOf() }

                for (spawn in wave.spawns) {
                    val basePerBatch = spawn.count / numBatches
                    val remainder = spawn.count % numBatches

                    for (b in 0 until numBatches) {
                        val countThisBatch = basePerBatch + (if (b < remainder) 1 else 0)
                        if (countThisBatch > 0) {
                            batchLists[b].add(WaveSpawnInfo(spawn.enemyType, countThisBatch, spawn.interval, spawn.pathIndex))
                        }
                    }
                }

                // Nạp đợt đầu tiên vào bàn chơi
                val batch0Spawns = batchLists[0]
                loadBatchIntoSpawners(batch0Spawns)
                currentBatchTotalEnemies = batch0Spawns.sumOf { it.count }
                currentBatchDeadCount = 0
                thresholdToNextBatch = (currentBatchTotalEnemies * 0.70f).toInt().coerceAtLeast(1)

                // Lưu các đợt còn lại vào hàng đợi chờ
                for (b in 1 until numBatches) {
                    val bSpawns = batchLists[b]
                    pendingWaveBatches.add(BatchInfo(bSpawns, bSpawns.sumOf { it.count }))
                }
            } else {
                loadBatchIntoSpawners(wave.spawns)
            }
        }
    }

    private fun loadBatchIntoSpawners(spawns: List<WaveSpawnInfo>) {
        for (spawnInfo in spawns) {
            val spawner = activeSpawners.getOrPut(spawnInfo.pathIndex) { LaneSpawner(timer = 0.3f) }
            for (i in 0 until spawnInfo.count) {
                spawner.queue.add(Pair(spawnInfo.enemyType, spawnInfo.interval))
            }
        }
    }

    fun getActiveSpawningPathsForNextWave(): List<Int> {
        val nextIdx = currentWaveIndex + 1
        if (nextIdx !in 0 until totalWaves) return emptyList()

        val nextWave = levelData.waves[nextIdx]
        val activePaths = mutableListOf<Int>()
        val maxAvailablePathIndex = levelData.allPaths.size - 1

        for (spawn in nextWave.spawns) {
            val validIndex = spawn.pathIndex.coerceIn(0, maxAvailablePathIndex.coerceAtLeast(0))
            if (!activePaths.contains(validIndex)) {
                activePaths.add(validIndex)
            }
        }
        return activePaths
    }

    private fun spawnEnemy(type: EnemyType, pathIndex: Int) {
        val maxHp = type.getMaxHp(difficulty)
        val allPaths = levelData.allPaths

        val targetPath = if (allPaths.isNotEmpty() && pathIndex in allPaths.indices) {
            allPaths[pathIndex]
        } else {
            allPaths.getOrNull(0) ?: emptyList()
        }

        if (targetPath.isEmpty()) return

        val isDualSouthLane = (levelData.levelNumber == 6 && (pathIndex == 1 || pathIndex == 2))
        val isCaveLaneLevel9 = (levelData.levelNumber == 9 && pathIndex == 1)

        val laneOffset = if (isDualSouthLane || isCaveLaneLevel9) {
            0f
        } else {
            val currentToggle = !(laneToggles[pathIndex] ?: false)
            laneToggles[pathIndex] = currentToggle
            if (currentToggle) 12f else -12f
        }

        val enemy = Entity().apply {
            val startPos = Vector2(targetPath.first()).add(0f, laneOffset)
            add(TransformComponent(position = startPos))
            add(HealthComponent(maxHealth = maxHp, currentHealth = maxHp))

            val shiftedWaypoints = if (isDualSouthLane || isCaveLaneLevel9) {
                targetPath.map { Vector2(it) }.toMutableList()
            } else {
                targetPath.map { Vector2(it.x, it.y + laneOffset) }.toMutableList()
            }

            add(MovementComponent(
                speed = type.speed,
                waypoints = shiftedWaypoints,
                isInsideTunnel = isCaveLaneLevel9,
                tunnelTimer = if (isCaveLaneLevel9) 2.0f else 0f
            ))
            add(ArmorComponent(armorType = type.armorType, magicResistType = type.magicResist))
            add(EnemyComponent(enemyType = type))
        }
        engine.addEntity(enemy)
    }
}

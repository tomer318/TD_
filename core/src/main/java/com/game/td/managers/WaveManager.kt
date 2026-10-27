package com.game.td.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.game.td.ecs.components.*
import com.game.td.map.*

class WaveManager(
    private val engine: Engine,
    private val levelData: LevelData,
    var difficulty: GameDifficulty = GameDifficulty.NORMAL,
    var gameMode: GameMode = GameMode.CAMPAIGN
) {
    var currentWaveIndex: Int = -1
    val totalWaves: Int = levelData.waves.size
    val currentWaveDisplay: Int
        get() = if (currentWaveIndex < 0) 0 else if (gameMode == GameMode.IRON) 1 else (currentWaveIndex + 1).coerceAtMost(totalWaves)

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
    private var totalSubWavesInCurrentWave: Int = 1

    // HÀNG ĐỢI SUB-WAVES NGẦM (SCRIPTED TIME-SPANNING & GROUPING)
    private val pendingWaveBatches = mutableListOf<BatchInfo>()
    private var currentBatchTotalEnemies: Int = 0
    private var currentBatchDeadCount: Int = 0
    private var thresholdToNextBatch: Int = 0
    private var batchDelayTimer: Float = 0f

    val isLevelCompleted: Boolean
        get() = currentWaveIndex >= totalWaves - 1 &&
            pendingWaveBatches.isEmpty() &&
            activeSpawners.values.all { it.queue.isEmpty() } &&
            engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get()).size() == 0

    // PHÂN LOẠI CẤP ĐỘ QUÁI ĐỂ ĐIỀU TIẾT MẬT ĐỘ TRONG IRON & HEROIC
    private fun isHeavyTank(type: EnemyType): Boolean {
        return type in listOf(
            EnemyType.OGRE, EnemyType.MAGMA_ELEMENTAL, EnemyType.YETI,
            EnemyType.MARAUDER, EnemyType.DARK_SLAYER, EnemyType.TROLL_CHIEFTAIN,
            EnemyType.TROLL_CHAMPION, EnemyType.SPIDER_MATRIARCH, EnemyType.DEMON_LORD,
            EnemyType.JUGGERNAUT, EnemyType.JT, EnemyType.VEZNAN, EnemyType.VEZNAN_DEMON
        ) || type.hpNormal >= 480f
    }

    private fun isEliteThreat(type: EnemyType): Boolean {
        if (isHeavyTank(type)) return false
        return type in listOf(
            EnemyType.TROLL, EnemyType.DARK_KNIGHT, EnemyType.SHAMAN,
            EnemyType.WORG, EnemyType.WINTER_WOLF, EnemyType.SHADOW_ARCHER,
            EnemyType.DEMON_SPAWN, EnemyType.DEMON_HOUND, EnemyType.NECROMANCER,
            EnemyType.SKELETON_KNIGHT
        )
    }

    private fun isFastRunner(type: EnemyType): Boolean {
        return type.speed >= 75f || type in listOf(
            EnemyType.WULF, EnemyType.WORG, EnemyType.WINTER_WOLF,
            EnemyType.DEMON_HOUND, EnemyType.SPIDER_HATCHLING, EnemyType.ROCKET_RIDER
        )
    }

    private fun isTinySwarm(type: EnemyType): Boolean {
        return type in listOf(
            EnemyType.GOBLIN, EnemyType.WULF, EnemyType.SPIDER_HATCHLING, EnemyType.SKELETON
        )
    }

    fun update(delta: Float) {
        if (currentWaveIndex == -1) return

        // ĐẾM SỐ LƯỢNG QUÁI ĐANG SỐNG TRÊN ĐƯỜNG ĐI (REAL-TIME PRESSURE GOVERNOR)
        val aliveEntities = engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get())
        val aliveTotal = aliveEntities.size()
        var aliveTanks = 0
        var aliveElites = 0
        for (i in 0 until aliveTotal) {
            val ec = aliveEntities.get(i).getComponent(EnemyComponent::class.java) ?: continue
            if (isHeavyTank(ec.enemyType)) aliveTanks++
            else if (isEliteThreat(ec.enemyType)) aliveElites++
        }

        // 1. CHUYỂN TIẾP GIỮA CÁC SUB-WAVE NGẦM (EVENT-DRIVEN SPAWNING)
        if (batchDelayTimer > 0f) {
            val maxTanksBeforeNextBatch = if (gameMode == GameMode.IRON) 1 else 2
            val maxTotalBeforeNextBatch = if (gameMode == GameMode.IRON) 10 else 16

            if (aliveTanks <= maxTanksBeforeNextBatch && aliveTotal <= maxTotalBeforeNextBatch) {
                batchDelayTimer -= delta
                if (batchDelayTimer <= 0f && pendingWaveBatches.isNotEmpty()) {
                    val nextBatch = pendingWaveBatches.removeAt(0)
                    loadBatchIntoSpawners(nextBatch.spawns)
                    currentBatchTotalEnemies = nextBatch.totalCount
                    currentBatchDeadCount = 0
                    thresholdToNextBatch = (currentBatchTotalEnemies * 0.80f).toInt().coerceAtLeast(1)
                }
            }
        }

        // 2. GIỚI HẠN MẬT ĐỘ QUÁI TỐI ĐA TRÊN MÀN HÌNH ĐỂ KHÔNG BỊ TRÀN BẤT KHẢ CHIẾN BẠI
        val completedSubWaves = (totalSubWavesInCurrentWave - pendingWaveBatches.size).coerceAtLeast(1)
        val maxConcurrentTotal = if (gameMode == GameMode.IRON) {
            // Đầu trận tối đa 9 quái trên đường, tăng dần lên tối đa 18 quái ở cuối trận
            (8 + completedSubWaves).coerceIn(9, 18)
        } else {
            24
        }
        val maxConcurrentElites = if (gameMode == GameMode.IRON) {
            // Đầu trận tối đa 4 Troll / Dark Knight cùng lúc trên đường, cuối trận tối đa 8
            (3 + (completedSubWaves / 2)).coerceIn(4, 8)
        } else {
            12
        }
        val maxConcurrentTanks = if (gameMode == GameMode.IRON) {
            if (completedSubWaves <= 5) 1 else 2
        } else {
            3
        }

        var hasActiveSpawns = false
        for ((pathIndex, spawner) in activeSpawners) {
            if (spawner.queue.isNotEmpty()) {
                hasActiveSpawns = true
                spawner.timer -= delta
                if (spawner.timer <= 0f) {
                    val nextType = spawner.queue[0].first
                    val nextIsTank = isHeavyTank(nextType)
                    val nextIsElite = isEliteThreat(nextType)

                    // BỘ HÃM THÔNG MINH: Nếu đường đang quá đông quái, tạm dừng 0.5s chờ người chơi bắn chết bớt
                    val isScreenCrowded = aliveTotal >= maxConcurrentTotal ||
                        (nextIsTank && aliveTanks >= maxConcurrentTanks) ||
                        (nextIsElite && aliveElites >= maxConcurrentElites)

                    if (isScreenCrowded && aliveTotal > 2) {
                        spawner.timer = 0.5f
                        continue
                    }

                    val (enemyType, interval) = spawner.queue.removeAt(0)

                    // Chỉ cho phép quái nhỏ máu giấy (Goblin, Wulf, Nhện con) đi song song
                    val canSpawnParallel = isTinySwarm(enemyType) &&
                        aliveTotal + 2 <= maxConcurrentTotal &&
                        spawner.queue.isNotEmpty() &&
                        spawner.queue[0].first == enemyType &&
                        MathUtils.randomBoolean(0.30f)

                    if (canSpawnParallel) {
                        val nextSpawn = spawner.queue.removeAt(0)
                        spawnSingleEnemyWithOffset(enemyType, pathIndex, -14f)
                        spawnSingleEnemyWithOffset(nextSpawn.first, pathIndex, 14f)
                        spawner.timer = interval * 1.45f
                    } else {
                        val offset = if (isHeavyTank(enemyType)) {
                            0f
                        } else {
                            val currentToggle = !(laneToggles[pathIndex] ?: false)
                            laneToggles[pathIndex] = currentToggle
                            if (currentToggle) 12f else -12f
                        }
                        spawnSingleEnemyWithOffset(enemyType, pathIndex, offset)
                        spawner.timer = interval
                    }
                }
            }
        }

        // Kích hoạt đợt ngầm kế tiếp ngay khi trên bản đồ vơi bớt quái (Event-driven)
        if (!hasActiveSpawns && pendingWaveBatches.isNotEmpty() && batchDelayTimer <= 0f) {
            val triggerAliveLimit = if (gameMode == GameMode.IRON) 4 else 5
            if (aliveTotal <= triggerAliveLimit) {
                batchDelayTimer = if (gameMode == GameMode.IRON) 3.2f else 2.0f
            }
        }

        if (!hasActiveSpawns && pendingWaveBatches.isEmpty() && !isNextWaveCountdownTriggered && currentWaveIndex < totalWaves - 1) {
            val deadEnemies = (totalEnemiesInCurrentWave - aliveTotal).coerceAtLeast(0)
            val halfThreshold = (totalEnemiesInCurrentWave * 0.65f).toInt().coerceAtLeast(1)

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

    fun onEnemyDefeated() {
        if (pendingWaveBatches.isNotEmpty() && batchDelayTimer <= 0f) {
            currentBatchDeadCount++
            if (currentBatchDeadCount >= thresholdToNextBatch) {
                val nextBatchHasTank = pendingWaveBatches.firstOrNull()?.spawns?.any { isHeavyTank(it.enemyType) } ?: false
                batchDelayTimer = if (gameMode == GameMode.IRON) {
                    if (nextBatchHasTank) 4.2f else 3.2f
                } else {
                    if (nextBatchHasTank) 4.0f else 3.0f
                }
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
                else -> if (isHeavyTank(spawn.enemyType)) hasHeavyEnemy = true
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
        // Trong Iron Mode chỉ được bấm START ở đầu trận, không có gọi sớm giữa trận
        if (gameMode == GameMode.IRON && currentWaveIndex >= 0) {
            return Pair(0, 0f)
        }
        val savedTime = if (waveCountdownTimer > 0f) waveCountdownTimer else 0f
        val bonus = if (savedTime > 0f) (savedTime * 1.5f).toInt().coerceAtLeast(5) else 0
        waveCountdownTimer = 0f
        startNextWave()
        return Pair(bonus, savedTime)
    }

    // CHUẨN HÓA SỐ LƯỢNG VÀ GIÃN CÁCH SPAWN CHO CHẾ ĐỘ IRON ĐỂ VỪA KỊCH TÍNH VỪA CÓ THỂ CHIẾN THẮNG
    private fun normalizeIronSpawn(sp: WaveSpawnInfo): WaveSpawnInfo {
        val type = sp.enemyType
        val balancedCount = when {
            isHeavyTank(type) -> when {
                sp.count > 10 -> (sp.count * 0.45f).toInt().coerceAtLeast(5)
                sp.count > 5 -> (sp.count * 0.60f).toInt().coerceAtLeast(4)
                else -> sp.count
            }
            type.isFlying -> when {
                sp.count > 60 -> (sp.count * 0.40f).toInt().coerceAtLeast(28)
                sp.count > 30 -> (sp.count * 0.55f).toInt().coerceAtLeast(20)
                else -> sp.count
            }
            isEliteThreat(type) -> when {
                sp.count > 100 -> (sp.count * 0.36f).toInt().coerceAtLeast(38)
                sp.count > 50 -> (sp.count * 0.45f).toInt().coerceAtLeast(25)
                sp.count > 25 -> (sp.count * 0.60f).toInt().coerceAtLeast(16)
                else -> sp.count
            }
            else -> when {
                sp.count > 100 -> (sp.count * 0.45f).toInt().coerceAtLeast(45)
                sp.count > 50 -> (sp.count * 0.55f).toInt().coerceAtLeast(30)
                else -> sp.count
            }
        }

        val safeInterval = when {
            isHeavyTank(type) -> maxOf(sp.interval, 5.0f)
            isEliteThreat(type) -> maxOf(sp.interval, 2.0f)
            type.isFlying -> maxOf(sp.interval, 1.45f)
            isTinySwarm(type) -> maxOf(sp.interval, 1.0f)
            else -> maxOf(sp.interval, 1.50f)
        }

        return WaveSpawnInfo(type, balancedCount, safeInterval, sp.pathIndex)
    }

    private fun startNextWave() {
        currentWaveIndex++
        isNextWaveCountdownTriggered = false
        activeSpawners.clear()
        pendingWaveBatches.clear()
        batchDelayTimer = 0f

        if (currentWaveIndex < totalWaves) {
            val rawWave = levelData.waves[currentWaveIndex]

            if (gameMode == GameMode.IRON) {
                // =============================================================
                // KỊCH BẢN 12 SUB-WAVES NGẦM CHO IRON CHALLENGE (CHUẨN WIKI)
                // =============================================================
                val normalizedSpawns = rawWave.spawns.map { normalizeIronSpawn(it) }
                totalEnemiesInCurrentWave = normalizedSpawns.sumOf { it.count }

                val numSubWaves = 12
                totalSubWavesInCurrentWave = numSubWaves
                val subWaves = List(numSubWaves) { mutableListOf<WaveSpawnInfo>() }

                fun distributeToWindow(sp: WaveSpawnInfo, startSub: Int, endSub: Int, slowStartMultiplier: Float = 1.0f) {
                    val windowSize = (endSub - startSub + 1).coerceAtLeast(1)
                    val basePerSub = sp.count / windowSize
                    val rem = sp.count % windowSize
                    for (i in 0 until windowSize) {
                        val subIdx = startSub + i
                        val cnt = basePerSub + (if (i < rem) 1 else 0)
                        if (cnt > 0) {
                            // 3 Sub-wave đầu tiên giãn cách rộng hơn để người chơi kịp tích vàng nâng tháp
                            val intervalMult = if (subIdx <= 2) slowStartMultiplier else 1.0f
                            subWaves[subIdx].add(WaveSpawnInfo(sp.enemyType, cnt, sp.interval * intervalMult, sp.pathIndex))
                        }
                    }
                }

                for (sp in normalizedSpawns) {
                    when {
                        // 1. Quái bay (Gargoyle, Rocket Rider...): Chỉ xuất hiện từ Sub-wave 4 -> 11
                        //    để người chơi có thời gian hạ bộ binh kiếm vàng xây tháp phòng không trước!
                        sp.enemyType.isFlying -> {
                            distributeToWindow(sp, startSub = 3, endSub = 10, slowStartMultiplier = 1.15f)
                        }
                        // 2. Quái Tanker hạng nặng (Yeti, Dark Slayer, Ogre, Marauder...): Rải ở nửa sau (Sub-wave 4 -> 11)
                        isHeavyTank(sp.enemyType) -> {
                            distributeToWindow(sp, startSub = 4, endSub = 11, slowStartMultiplier = 1.25f)
                        }
                        // 3. Quái Tinh nhuệ (Troll, Dark Knight, Shaman, Demon Spawn...):
                        //    20% nhử ở Sub-wave 1-3 với tốc độ chậm để lấy tiền, 80% rải đều từ Sub-wave 4 -> 11
                        isEliteThreat(sp.enemyType) -> {
                            if (sp.count >= 10) {
                                val earlyCount = (sp.count * 0.20f).toInt().coerceAtLeast(2)
                                val mainCount = sp.count - earlyCount
                                distributeToWindow(sp.copy(count = earlyCount), startSub = 0, endSub = 2, slowStartMultiplier = 1.40f)
                                distributeToWindow(sp.copy(count = mainCount), startSub = 3, endSub = 11, slowStartMultiplier = 1.10f)
                            } else {
                                distributeToWindow(sp, startSub = 2, endSub = 10, slowStartMultiplier = 1.25f)
                            }
                        }
                        // 4. Quái bộ binh cơ bản (Goblin, Bandit, Brigand, Orc, Wulf...): Rải từ Sub-wave 1 -> 9 làm nguồn thu vàng
                        else -> {
                            distributeToWindow(sp, startSub = 0, endSub = 8, slowStartMultiplier = 1.30f)
                        }
                    }
                }

                val nonEmptySubWaves = subWaves.filter { it.isNotEmpty() }
                totalSubWavesInCurrentWave = nonEmptySubWaves.size.coerceAtLeast(1)

                if (nonEmptySubWaves.isNotEmpty()) {
                    val firstSub = nonEmptySubWaves[0]
                    loadBatchIntoSpawners(firstSub)
                    currentBatchTotalEnemies = firstSub.sumOf { it.count }
                    currentBatchDeadCount = 0
                    thresholdToNextBatch = (currentBatchTotalEnemies * 0.80f).toInt().coerceAtLeast(1)

                    for (idx in 1 until nonEmptySubWaves.size) {
                        val sw = nonEmptySubWaves[idx]
                        pendingWaveBatches.add(BatchInfo(sw, sw.sumOf { it.count }))
                    }
                }
            } else {
                // CHẾ ĐỘ CAMPAIGN & HEROIC
                totalEnemiesInCurrentWave = rawWave.spawns.sumOf { it.count }

                if (totalEnemiesInCurrentWave >= 16) {
                    val numBatches = when {
                        totalEnemiesInCurrentWave >= 50 -> 5
                        totalEnemiesInCurrentWave >= 32 -> 4
                        totalEnemiesInCurrentWave >= 22 -> 3
                        else -> 2
                    }
                    totalSubWavesInCurrentWave = numBatches
                    val batchLists: List<MutableList<WaveSpawnInfo>> = List(numBatches) { mutableListOf() }

                    for (spawn in rawWave.spawns) {
                        val safeInterval = if (gameMode == GameMode.HEROIC) {
                            when {
                                isHeavyTank(spawn.enemyType) -> maxOf(spawn.interval, 3.8f)
                                isEliteThreat(spawn.enemyType) -> maxOf(spawn.interval, 1.4f)
                                else -> maxOf(spawn.interval, 0.85f)
                            }
                        } else spawn.interval

                        val basePerBatch = spawn.count / numBatches
                        val remainder = spawn.count % numBatches
                        for (b in 0 until numBatches) {
                            val countThisBatch = basePerBatch + (if (b < remainder) 1 else 0)
                            if (countThisBatch > 0) {
                                batchLists[b].add(WaveSpawnInfo(spawn.enemyType, countThisBatch, safeInterval, spawn.pathIndex))
                            }
                        }
                    }

                    val nonEmptyBatches = batchLists.filter { it.isNotEmpty() }
                    if (nonEmptyBatches.isNotEmpty()) {
                        val b0 = nonEmptyBatches[0]
                        loadBatchIntoSpawners(b0)
                        currentBatchTotalEnemies = b0.sumOf { it.count }
                        currentBatchDeadCount = 0
                        thresholdToNextBatch = (currentBatchTotalEnemies * 0.75f).toInt().coerceAtLeast(1)

                        for (b in 1 until nonEmptyBatches.size) {
                            val bSpawns = nonEmptyBatches[b]
                            pendingWaveBatches.add(BatchInfo(bSpawns, bSpawns.sumOf { it.count }))
                        }
                    }
                } else {
                    totalSubWavesInCurrentWave = 1
                    loadBatchIntoSpawners(rawWave.spawns)
                }
            }
        }
    }

    private fun loadBatchIntoSpawners(spawns: List<WaveSpawnInfo>) {
        for (spawnInfo in spawns) {
            val spawner = activeSpawners.getOrPut(spawnInfo.pathIndex) { LaneSpawner(timer = 0.4f) }
            for (i in 0 until spawnInfo.count) {
                spawner.queue.add(Pair(spawnInfo.enemyType, spawnInfo.interval))
            }
        }
    }

    fun getActiveSpawningPathsForNextWave(): List<Int> {
        if (gameMode == GameMode.IRON && currentWaveIndex >= 0) return emptyList()
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

    private fun offsetPath(path: List<Vector2>, offsetDist: Float): MutableList<Vector2> {
        if (path.isEmpty()) return mutableListOf()
        if (path.size == 1) return mutableListOf(Vector2(path[0]))

        val result = mutableListOf<Vector2>()
        for (i in path.indices) {
            val normal = Vector2()
            if (i == 0) {
                var nextIdx = 1
                while (nextIdx < path.size && path[nextIdx].dst2(path[0]) < 0.001f) {
                    nextIdx++
                }
                val nextP = if (nextIdx < path.size) path[nextIdx] else path[0]
                val dir = Vector2(nextP).sub(path[0])
                if (!dir.isZero) dir.nor() else dir.set(0f, -1f)
                normal.set(-dir.y, dir.x)
            } else if (i == path.size - 1) {
                var prevIdx = i - 1
                while (prevIdx >= 0 && path[prevIdx].dst2(path[i]) < 0.001f) {
                    prevIdx--
                }
                val prevP = if (prevIdx >= 0) path[prevIdx] else path[i]
                val dir = Vector2(path[i]).sub(prevP)
                if (!dir.isZero) dir.nor() else dir.set(0f, -1f)
                normal.set(-dir.y, dir.x)
            } else {
                val dir1 = Vector2(path[i]).sub(path[i - 1])
                if (!dir1.isZero) dir1.nor() else dir1.set(0f, -1f)
                val dir2 = Vector2(path[i + 1]).sub(path[i])
                if (!dir2.isZero) dir2.nor() else dir2.set(0f, -1f)

                val avgDir = Vector2(dir1).add(dir2)
                if (avgDir.isZero) {
                    normal.set(-dir1.y, dir1.x)
                } else {
                    avgDir.nor()
                    normal.set(-avgDir.y, avgDir.x)
                }
            }
            result.add(Vector2(path[i].x + normal.x * offsetDist, path[i].y + normal.y * offsetDist))
        }
        return result
    }

    private fun spawnSingleEnemyWithOffset(type: EnemyType, pathIndex: Int, laneOffset: Float) {
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

        val finalOffset = if (isDualSouthLane || isCaveLaneLevel9) 0f else laneOffset

        val shiftedWaypoints = if (finalOffset == 0f) {
            targetPath.map { Vector2(it) }.toMutableList()
        } else {
            offsetPath(targetPath, finalOffset)
        }

        val enemy = Entity().apply {
            val startPos = Vector2(shiftedWaypoints.first())
            add(TransformComponent(position = startPos))
            add(HealthComponent(maxHealth = maxHp, currentHealth = maxHp))

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

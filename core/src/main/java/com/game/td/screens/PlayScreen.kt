package com.game.td.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.game.td.Config
import com.game.td.ecs.components.*
import com.game.td.ecs.systems.*
import com.game.td.effects.*
import com.game.td.managers.CheatManager
import com.game.td.managers.EconomyManager
import com.game.td.managers.GameMode
import com.game.td.managers.SaveManager
import com.game.td.managers.UpgradeManager
import com.game.td.managers.WaveManager
import com.game.td.map.*
import com.game.td.renderers.*
import com.game.td.renderers.maps.Level12MapRenderer
import com.game.td.renderers.GameUIRenderer.drawOutlinedText

enum class ActiveSpell {
    NONE,
    RAIN_OF_FIRE,
    REINFORCEMENTS
}

class PlayScreen(
    private val currentLevel: LevelData = Level1Southport.data,
    private val difficulty: GameDifficulty = GameDifficulty.NORMAL,
    private val gameMode: GameMode = GameMode.CAMPAIGN,
    private val onLevelVictory: () -> Unit = {},
    private val onReturnToMap: () -> Unit = {}
) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(Config.WORLD_WIDTH, Config.WORLD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val font = BitmapFont().apply { data.setScale(1.35f) }
    private val smallFont = BitmapFont().apply { data.setScale(0.95f) }

    private val engine = Engine()
    private lateinit var waveManager: WaveManager
    private val effectRenderer = EffectRenderer()

    private var selectedSpot: Entity? = null
    private var selectedTower: Entity? = null
    private var selectedEnemy: Entity? = null
    private var selectedSoldier: Entity? = null
    private var isSettingRallyPoint: Boolean = false

    private var previewBuildType: TowerType? = null
    private var previewTowerAction: String? = null

    private var isGamePaused: Boolean = false

    private var selectedSpell: ActiveSpell = ActiveSpell.NONE
    private var rainOfFireCooldown = 0f
    private val maxRainOfFireCooldown: Float
        get() = when (UpgradeManager.getEffectiveRainLevel()) {
            3 -> 70f
            4, 5 -> 60f
            else -> 80f
        }

    private var reinforcementCooldown = 0f
    private val maxReinforcementCooldown = 10f

    private var isAimingSunrayTarget: Boolean = false

    // CƠ CHẾ ĐẶC BIỆT CỦA MÀN 11 THEO NOTE WIKI
    private var magmaFieldTotalTime = 0f
    private var magmaFieldSpawnTimer = 0f

    // CƠ CHẾ ĐẶC BIỆT CỦA MÀN 12: THE DARK TOWER
    private data class LockedTowerState(
        var initialTimer: Float = 5.0f,
        var penaltyTimer: Float = 5.0f,
        var isUndispellable: Boolean = false
    )
    private val lockedTowersMap = mutableMapOf<Entity, LockedTowerState>()
    private val warningTowersMap = mutableMapOf<Entity, Float>() // Giai đoạn cảnh báo 2s (chặn wheel, vẫn bắn được)
    private var veznanTowerLockCooldown = 16.0f

    private var veznanSummonTimer = 10.0f
    private var veznanSummonCountThisWave = 0
    private var lastRecordedWaveForSummon = -1

    // Vòng tròn ma thuật đang phát sáng nạp năng lượng (Vị trí -> Thời gian còn lại)
    private val glowingCircles = mutableListOf<Pair<Vector2, Float>>()

    // Hàng đợi xuất quỷ từng con một từ vòng tròn ma thuật
    private data class QueuedPortalSpawn(
        val circlePos: Vector2,
        val type: EnemyType,
        var delay: Float
    )
    private val portalSpawnQueue = mutableListOf<QueuedPortalSpawn>()

    // HÀNG ĐỢI XUẤT XƯƠNG TỪ BÃI THA MA (GIÃN CÁCH, TRÁNH TRÀN Ở WAVE 15)
    private val graveyardQueue = mutableListOf<EnemyType>()
    private var graveyardCooldown = 0f
    private var activeGraveyardSkeletons = 0

    private var veznanBossSteppedDown = false

    private val maxRallyRadius: Float
        get() = if (UpgradeManager.getEffectiveBarracksLevel() >= 3) 138f else 115f

    private val allowedTowers: List<TowerType>
        get() = if (gameMode == GameMode.IRON) {
            when (currentLevel.levelNumber) {
                1 -> listOf(TowerType.BARRACKS, TowerType.ARTILLERY)
                2 -> listOf(TowerType.ARCHER, TowerType.MAGE, TowerType.ARTILLERY)
                3 -> listOf(TowerType.ARCHER, TowerType.BARRACKS, TowerType.MAGE)
                7 -> listOf(TowerType.ARCHER, TowerType.MAGE)
                else -> TowerType.values().toList()
            }
        } else {
            TowerType.values().toList()
        }

    private val buildOptions = listOf(
        Pair(TowerType.ARCHER, Vector2(0f, 75f)),
        Pair(TowerType.BARRACKS, Vector2(-75f, 0f)),
        Pair(TowerType.MAGE, Vector2(75f, 0f)),
        Pair(TowerType.ARTILLERY, Vector2(0f, -75f))
    )

    private val touchPoint = Vector3()

    override fun show() {
        UpgradeManager.maxUpgradeCap = when (gameMode) {
            GameMode.CAMPAIGN -> 5
            GameMode.HEROIC, GameMode.IRON -> when (currentLevel.levelNumber) {
                1 -> 1
                2, 3 -> 2
                else -> 5
            }
        }

        startNewGame()

        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                touchPoint.set(screenX.toFloat(), screenY.toFloat(), 0f)
                viewport.unproject(touchPoint)
                handleTouch(touchPoint.x, touchPoint.y)
                return true
            }
        }
    }

    private fun startNewGame() {
        engine.removeAllEntities()
        engine.removeAllSystems()

        isGamePaused = false
        EconomyManager.gold = currentLevel.startingGold
        EconomyManager.lives = currentLevel.startingLives

        engine.addSystem(MovementSystem(
            onHealingTriggered = { pos -> effectRenderer.healingAuras.add(HealingAuraEffect(Vector2(pos))) },
            getLevelNumber = { currentLevel.levelNumber }
        ))
        engine.addSystem(SpiderEggSystem(difficulty))
        engine.addSystem(CombatSystem(
            onDeathRayTriggered = { from, to -> effectRenderer.deathRayBeams.add(DeathRayBeam(Vector2(from), Vector2(to))) },
            onTeleportTriggered = { pos -> effectRenderer.magicSparks.add(MagicSparkEffect(Vector2(pos))) },
            onSniperShotTriggered = { from, to -> effectRenderer.sniperTrails.add(SniperTrailEffect(Vector2(from), Vector2(to))) },
            onShrapnelExploded = { pos -> effectRenderer.shrapnelExplosions.add(ShrapnelExplosionEffect(Vector2(pos))) },
            onForestWrathTriggered = { pos -> effectRenderer.forestWrathVines.add(ForestWrathEffect(Vector2(pos))) },
            onTeslaBoltTriggered = { from, to -> effectRenderer.teslaBolts.add(TeslaBoltEffect(Vector2(from), Vector2(to))) },
            onOverchargeTriggered = { pos, radius -> effectRenderer.overchargeEffects.add(TeslaOverchargeEffect(Vector2(pos), radius)) },
            onClusterBombsLaunched = { towerPos, centerPos, subPositions, onExplodeCallback ->
                val effect = ClusterBombEffect(
                    towerPos = Vector2(towerPos),
                    centerImpactPos = Vector2(centerPos),
                    subBombPositions = subPositions,
                    onSubBombExplode = onExplodeCallback
                )
                effectRenderer.clusterBombs.add(effect)
            },
            onEnemyKilled = { deadEnemy ->
                checkGraveyardSpawn(deadEnemy)
                checkVeznanTransformation(deadEnemy)
            }
        ))

        engine.addSystem(ProjectileSystem())
        engine.addSystem(BarracksSystem(
            onAxeThrown = { from, to -> effectRenderer.axeProjectiles.add(ThrownAxeEffect(Vector2(from), Vector2(to))) },
            onHealingLight = { pos -> effectRenderer.healingAuras.add(HealingAuraEffect(Vector2(pos))) },
            onWhirlwindTriggered = { pos -> effectRenderer.whirlwindSlashes.add(WhirlwindEffect(Vector2(pos))) },
            onHolyStrikeTriggered = { pos -> effectRenderer.holyStrikeBeams.add(HolyStrikeEffect(Vector2(pos))) }
        ))

        waveManager = WaveManager(engine, currentLevel, difficulty)

        engine.addEntityListener(Family.all(EnemyComponent::class.java, HealthComponent::class.java).get(), object : EntityListener {
            override fun entityAdded(entity: Entity) {}
            override fun entityRemoved(entity: Entity) {
                val hp = entity.getComponent(HealthComponent::class.java)
                val eComp = entity.getComponent(EnemyComponent::class.java)
                if (eComp?.enemyType == EnemyType.SKELETON || eComp?.enemyType == EnemyType.SKELETON_KNIGHT) {
                    activeGraveyardSkeletons = (activeGraveyardSkeletons - 1).coerceAtLeast(0)
                }
                if (hp != null && (hp.currentHealth <= 0f || hp.isDead)) {
                    checkGraveyardSpawn(entity)
                    checkVeznanTransformation(entity)
                }
                waveManager.onEnemyDefeated()
            }
        })

        for (pos in currentLevel.towerSpots) {
            createTowerSpot(pos)
        }

        // XÂY SẴN THÁP CUNG CẤP 3 VÀ NHÀ SLYVAN ELF HALL CHO MÀN 5
        if (currentLevel.levelNumber == 5) {
            val pArcherPos = Level5Silveroak.prebuiltArcherSpot
            val sylvanPos = Level5Silveroak.sylvanElfHallSpot

            val spots = engine.getEntitiesFor(Family.all(TowerSpotComponent::class.java, TransformComponent::class.java).get())
            for (i in 0 until spots.size()) {
                val s = spots.get(i)
                val sPos = s.getComponent(TransformComponent::class.java).position
                if (sPos.dst(pArcherPos) < 5f || sPos.dst(sylvanPos) < 5f) {
                    s.getComponent(TowerSpotComponent::class.java).isOccupied = true
                }
            }

            var pRange = 260f
            if (UpgradeManager.getEffectiveArcherLevel() >= 4) pRange *= 1.21f
            else if (UpgradeManager.getEffectiveArcherLevel() >= 2) pRange *= 1.10f

            val prebuiltArcher = Entity().apply {
                add(TransformComponent(position = Vector2(pArcherPos)))
                add(TowerComponent(
                    towerType = TowerType.ARCHER,
                    level = 3,
                    range = pRange,
                    damageMin = 10f,
                    damageMax = 16f,
                    attackRate = 0.5f,
                    totalInvestedGold = 450
                ))
            }
            engine.addEntity(prebuiltArcher)

            val sylvanHall = Entity().apply {
                add(TransformComponent(position = Vector2(sylvanPos)))
                add(TowerComponent(
                    towerType = TowerType.BARRACKS,
                    level = 4,
                    specialization = TowerSpecialization.SYLVAN_ELF_HALL,
                    range = 220f,
                    totalInvestedGold = 100
                ))
                add(BarracksComponent(rallyPoint = Vector2(sylvanPos.x + 40f, sylvanPos.y - 40f)))
            }
            engine.addEntity(sylvanHall)
        }

        // KHỞI TẠO 7 LÍNH CANH (IMPERIAL GUARDS) CHO MÀN 6
        if (currentLevel.levelNumber == 6) {
            val allGuardPositions = Level6Citadel.imperialGuardPositions
            for (pos in allGuardPositions) {
                val guard = Entity().apply {
                    add(TransformComponent(position = Vector2(pos)))
                    add(HealthComponent(maxHealth = 250f, currentHealth = 250f))
                    add(ArmorComponent(armorType = ArmorType.MEDIUM_ARMOR))
                    add(SoldierComponent(
                        attackDamageMin = 10f,
                        attackDamageMax = 30f,
                        attackRate = 1.2f,
                        regenPerSec = 25f,
                        targetPosition = Vector2(pos),
                        soldierRankName = "Imperial Guard",
                        moveSpeed = 50f
                    ))
                }
                engine.addEntity(guard)
            }
        }

        if (currentLevel.levelNumber == 9) {
            val sunray = Entity().apply {
                add(TransformComponent(position = Vector2(Level9StormcloudTemple.sunrayTowerPosition)))
                add(TowerComponent(
                    towerType = TowerType.SUNRAY,
                    level = 1,
                    specialization = TowerSpecialization.SUNRAY_TOWER,
                    range = 1200f,
                    sunraySorcerers = 0,
                    totalInvestedGold = 0,
                    isSunrayReady = false,
                    sunrayChargeTimer = 0f
                ))
            }
            engine.addEntity(sunray)
        }

        if (currentLevel.levelNumber == 7 && gameMode == GameMode.IRON) {
            val ironBarrackPositions = listOf(
                Level7ColdstepMines.towerSpotsList[4],
                Level7ColdstepMines.towerSpotsList[5],
                Level7ColdstepMines.towerSpotsList[6]
            )
            for (pos in ironBarrackPositions) {
                val spots = engine.getEntitiesFor(Family.all(TowerSpotComponent::class.java, TransformComponent::class.java).get())
                for (s in spots) {
                    if (s.getComponent(TransformComponent::class.java).position.dst(pos) < 5f) {
                        s.getComponent(TowerSpotComponent::class.java).isOccupied = true
                        break
                    }
                }
                val rally = getNearestRoadPoint(pos)
                buildPrebuiltBarracks(pos, rally, level = 2)
            }
        }

        clearSelections()
        rainOfFireCooldown = 0f
        reinforcementCooldown = 0f
        magmaFieldTotalTime = 0f
        magmaFieldSpawnTimer = 0f
        lockedTowersMap.clear()
        warningTowersMap.clear()
        veznanTowerLockCooldown = 16.0f
        veznanSummonTimer = 10.0f
        veznanSummonCountThisWave = 0
        lastRecordedWaveForSummon = -1
        glowingCircles.clear()
        portalSpawnQueue.clear()
        graveyardQueue.clear()
        graveyardCooldown = 0f
        activeGraveyardSkeletons = 0
        veznanBossSteppedDown = false
        Level12MapRenderer.isVeznanOnBalcony = true
        effectRenderer.clear()
    }

    private fun checkVeznanTransformation(deadEntity: Entity) {
        val eComp = deadEntity.getComponent(EnemyComponent::class.java) ?: return
        val trans = deadEntity.getComponent(TransformComponent::class.java) ?: return
        val move = deadEntity.getComponent(MovementComponent::class.java) ?: return

        if (eComp.enemyType == EnemyType.VEZNAN) {
            effectRenderer.shrapnelExplosions.add(ShrapnelExplosionEffect(Vector2(trans.position)))
            effectRenderer.magicSparks.add(MagicSparkEffect(Vector2(trans.position)))

            val demonVeznan = Entity().apply {
                add(TransformComponent(position = Vector2(trans.position)))
                val maxHp = EnemyType.VEZNAN_DEMON.getMaxHp(difficulty)
                add(HealthComponent(maxHealth = maxHp, currentHealth = maxHp))
                add(ArmorComponent(armorType = EnemyType.VEZNAN_DEMON.armorType, magicResistType = EnemyType.VEZNAN_DEMON.magicResist))
                add(EnemyComponent(enemyType = EnemyType.VEZNAN_DEMON))
                add(MovementComponent(
                    speed = EnemyType.VEZNAN_DEMON.speed,
                    waypoints = move.waypoints.drop(move.currentWaypointIndex).toMutableList()
                ))
            }
            engine.addEntity(demonVeznan)
        }
    }

    // CƠ CHẾ NGHĨA ĐỊA (NẠP VÀO HÀNG ĐỢI ĐỂ KHÔNG BỊ TRÀN Ở WAVE 15)
    private fun checkGraveyardSpawn(enemy: Entity) {
        if (currentLevel.levelNumber != 10 && currentLevel.levelNumber != 12) return
        val eComp = enemy.getComponent(EnemyComponent::class.java) ?: return
        val hp = enemy.getComponent(HealthComponent::class.java) ?: return

        if (eComp.hasSpawnedSkeleton) return
        if (eComp.enemyType == EnemyType.SKELETON || eComp.enemyType == EnemyType.SKELETON_KNIGHT) return

        val isEligible = eComp.enemyType in listOf(
            EnemyType.BANDIT, EnemyType.BRIGAND, EnemyType.DARK_KNIGHT,
            EnemyType.DARK_SLAYER, EnemyType.SHADOW_ARCHER,
            EnemyType.DEMON_SPAWN, EnemyType.DEMON_LORD, EnemyType.DEMON_HOUND,
            EnemyType.NECROMANCER, EnemyType.MAGMA_ELEMENTAL
        )

        if (isEligible) {
            eComp.hasSpawnedSkeleton = true
            val sType = if (eComp.enemyType == EnemyType.MAGMA_ELEMENTAL || hp.maxHealth >= 300f) {
                EnemyType.SKELETON_KNIGHT
            } else {
                EnemyType.SKELETON
            }
            graveyardQueue.add(sType)
        }
    }

    private fun spawnSingleSkeletonFromGraveyard(sType: EnemyType) {
        val gPos = if (currentLevel.levelNumber == 12) Level12TheDarkTower.graveyardPosition else Level10TheWastes.graveyardPosition
        val skelWps = if (currentLevel.levelNumber == 12) Level12TheDarkTower.pathGraveyard else (currentLevel.allPaths.getOrNull(2) ?: emptyList())

        val skeleton = Entity().apply {
            val offX = MathUtils.random(-14f, 14f)
            val offY = MathUtils.random(-14f, 14f)
            add(TransformComponent(position = Vector2(gPos.x + offX, gPos.y + offY)))
            val sMax = sType.getMaxHp(difficulty)
            add(HealthComponent(maxHealth = sMax, currentHealth = sMax))
            add(ArmorComponent(armorType = sType.armorType, magicResistType = sType.magicResist))
            add(EnemyComponent(enemyType = sType, isSummonedMinion = false))
            add(MovementComponent(
                speed = sType.speed,
                waypoints = skelWps.toMutableList(),
                isInsideTunnel = true,
                tunnelTimer = 1.2f
            ))
        }
        engine.addEntity(skeleton)
        activeGraveyardSkeletons++
        effectRenderer.magicSparks.add(MagicSparkEffect(Vector2(gPos)))
    }

    private fun clearSelections() {
        selectedSpot = null
        selectedTower = null
        selectedEnemy = null
        selectedSoldier = null
        previewBuildType = null
        previewTowerAction = null
        isSettingRallyPoint = false
        isAimingSunrayTarget = false
        selectedSpell = ActiveSpell.NONE
    }

    private fun isPointOnRoad(point: Vector2, tolerance: Float = 38f): Boolean {
        for (wps in currentLevel.allPaths) {
            for (i in 0 until wps.size - 1) {
                if (distanceToSegment(point, wps[i], wps[i + 1]) <= tolerance) return true
            }
        }
        return false
    }

    private fun distanceToSegment(p: Vector2, a: Vector2, b: Vector2): Float {
        val abX = b.x - a.x; val abY = b.y - a.y
        val apX = p.x - a.x; val apY = p.y - a.y
        val abLenSq = abX * abX + abY * abY
        if (abLenSq == 0f) return p.dst(a)
        val t = ((apX * abX + apY * abY) / abLenSq).coerceIn(0f, 1f)
        return Vector2.dst(p.x, p.y, a.x + t * abX, a.y + t * abY)
    }

    private fun handleTouch(x: Float, y: Float) {
        val cX = Config.WORLD_WIDTH / 2f
        val cY = Config.WORLD_HEIGHT / 2f

        // 1. XỬ LÝ PAUSE MENU
        if (isGamePaused) {
            if (x in (cX - 120f)..(cX + 120f) && y in (cY + 50f)..(cY + 94f)) {
                isGamePaused = false
                return
            }
            if (x in (cX - 120f)..(cX + 120f) && y in (cY - 10f)..(cY + 34f)) {
                isGamePaused = false
                startNewGame()
                return
            }
            if (x in (cX - 120f)..(cX + 120f) && y in (cY - 70f)..(cY - 26f)) {
                isGamePaused = false
                UpgradeManager.maxUpgradeCap = 5
                onReturnToMap()
                return
            }
            if (x in (cX - 120f)..(cX + 120f) && y in (cY - 130f)..(cY - 86f)) {
                CheatManager.toggleCheat()
                return
            }
            return
        }

        // 2. XỬ LÝ NÚT VÀ MENU CHEAT
        if (CheatManager.isCheatEnabled) {
            val cheatBtn = Vector2(Config.WORLD_WIDTH - 50f, Config.WORLD_HEIGHT - 90f)

            if (CheatManager.isCheatMenuOpen) {
                val pX = Config.WORLD_WIDTH / 2f
                val pY = Config.WORLD_HEIGHT / 2f

                if (x in (pX - 120f)..(pX + 120f) && y in (pY + 50f)..(pY + 86f)) {
                    CheatManager.addGold(1000)
                    return
                }
                if (x in (pX - 120f)..(pX + 120f) && y in (pY + 5f)..(pY + 41f)) {
                    CheatManager.toggleHighDamage()
                    return
                }
                if (x in (pX - 120f)..(pX + 120f) && y in (pY - 40f)..(pY - 4f)) {
                    val enemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get())
                    val snapshot = mutableListOf<Entity>()
                    for (i in 0 until enemies.size()) snapshot.add(enemies.get(i))
                    for (e in snapshot) {
                        e.getComponent(MovementComponent::class.java)?.isBlocked = false
                        engine.removeEntity(e)
                    }
                    return
                }
                if (x in (pX - 120f)..(pX + 120f) && y in (pY - 85f)..(pY - 49f)) {
                    CheatManager.isCheatMenuOpen = false
                    EconomyManager.lives = 20
                    SaveManager.recordVictory(currentLevel.levelNumber, 20, gameMode)
                    UpgradeManager.maxUpgradeCap = 5
                    onLevelVictory()
                    onReturnToMap()
                    return
                }
                if (x in (pX - 60f)..(pX + 60f) && y in (pY - 135f)..(pY - 105f)) {
                    CheatManager.isCheatMenuOpen = false
                    return
                }
                if (x !in (pX - 150f)..(pX + 150f) || y !in (pY - 150f)..(pY + 150f)) {
                    CheatManager.isCheatMenuOpen = false
                }
                return
            }

            if (cheatBtn.dst(x, y) <= 22f) {
                CheatManager.toggleCheatMenu()
                return
            }
        }

        // 3. NÚT PAUSE VÀ QUAY VỀ MAP
        if (x >= Config.WORLD_WIDTH - 85f && y >= Config.WORLD_HEIGHT - 65f) {
            clearSelections()
            isGamePaused = true
            return
        }

        if (Vector2(35f, Config.WORLD_HEIGHT - 35f).dst(x, y) <= 26f) {
            UpgradeManager.maxUpgradeCap = 5
            onReturnToMap()
            return
        }

        // 4. GAME OVER VÀ VICTORY DIALOG
        if (EconomyManager.isGameOver) {
            val replayBtn = Vector2(Config.WORLD_WIDTH / 2f, Config.WORLD_HEIGHT / 2f - 70f)
            if (replayBtn.dst(x, y) <= 60f) startNewGame()
            return
        }

        if (waveManager.isLevelCompleted && (!veznanBossSteppedDown || engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get()).size() == 0)) {
            val recordedLives = if (CheatManager.isCheatEnabled) 20 else EconomyManager.lives
            if (x in (cX - 120f)..(cX + 120f) && y in (cY - 115f)..(cY - 69f)) {
                SaveManager.recordVictory(currentLevel.levelNumber, recordedLives, gameMode)
                UpgradeManager.maxUpgradeCap = 5
                onLevelVictory()
                onReturnToMap()
                return
            }
            return
        }

        // 5. GIẢI PHÙ PHÉP CHO THÁP BỊ KHÓA
        val allTowers = engine.getEntitiesFor(Family.all(TowerComponent::class.java, TransformComponent::class.java).get())
        for (i in 0 until allTowers.size()) {
            val t = allTowers.get(i)
            val tComp = t.getComponent(TowerComponent::class.java)
            val tPos = t.getComponent(TransformComponent::class.java).position
            if (tComp != null && tComp.isFrozen && tPos.dst(x, y) <= 40f) {
                val lockState = lockedTowersMap[t]
                if (lockState == null || !lockState.isUndispellable) {
                    tComp.freezeHitsRemaining--
                    effectRenderer.magicSparks.add(MagicSparkEffect(Vector2(tPos)))
                    if (tComp.freezeHitsRemaining <= 0) {
                        tComp.isFrozen = false
                        lockedTowersMap.remove(t)
                    }
                    return
                }
            }
        }

        // 6. KHAI HỎA SUNRAY TOWER
        if (isAimingSunrayTarget) {
            val sunray = allTowers.firstOrNull {
                it.getComponent(TowerComponent::class.java)?.specialization == TowerSpecialization.SUNRAY_TOWER
            }
            val sComp = sunray?.getComponent(TowerComponent::class.java)
            val sPos = sunray?.getComponent(TransformComponent::class.java)?.position

            if (sComp != null && sPos != null && sComp.isSunrayReady && sComp.sunraySorcerers > 0) {
                val enemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java, TransformComponent::class.java, HealthComponent::class.java).get())
                var hitEnemy: Entity? = null

                for (i in 0 until enemies.size()) {
                    val e = enemies.get(i)
                    val ePos = e.getComponent(TransformComponent::class.java).position
                    val eHp = e.getComponent(HealthComponent::class.java)
                    if (ePos.dst(x, y) <= 38f && eHp != null && !eHp.isDead) {
                        hitEnemy = e
                        break
                    }
                }

                if (hitEnemy != null) {
                    val eTrans = hitEnemy.getComponent(TransformComponent::class.java)
                    val eHp = hitEnemy.getComponent(HealthComponent::class.java)
                    val eComp = hitEnemy.getComponent(EnemyComponent::class.java)

                    effectRenderer.sunrayBeams.add(SunrayBeamEffect(Vector2(sPos.x, sPos.y + 44f), Vector2(eTrans.position)))

                    val dmg = when (sComp.sunraySorcerers) {
                        1 -> MathUtils.random(75f, 125f)
                        2 -> MathUtils.random(125f, 175f)
                        3 -> MathUtils.random(175f, 225f)
                        else -> MathUtils.random(225f, 275f)
                    }

                    eHp.currentHealth -= dmg
                    if (eHp.currentHealth <= 0f) {
                        if (eComp != null && !eComp.isSummonedMinion) EconomyManager.addGold(eComp.enemyType.rewardGold)
                        hitEnemy.getComponent(MovementComponent::class.java)?.isBlocked = false
                        engine.removeEntity(hitEnemy)
                    }

                    sComp.isSunrayReady = false
                    sComp.sunrayChargeTimer = when (sComp.sunraySorcerers) {
                        1 -> 19.0f; 2 -> 16.0f; 3 -> 13.0f; else -> 10.0f
                    }
                    isAimingSunrayTarget = false
                    clearSelections()
                    return
                }
            }
            isAimingSunrayTarget = false
            return
        }

        // 7. NÚT CHỌN PHÉP
        val clickPos = Vector2(x, y)
        val fireBtnPos = Vector2(45f, 45f)
        val reinfBtnPos = Vector2(115f, 45f)

        if (fireBtnPos.dst(x, y) <= 28f) {
            if (selectedSpell == ActiveSpell.RAIN_OF_FIRE) {
                selectedSpell = ActiveSpell.NONE
            } else if (rainOfFireCooldown <= 0f) {
                clearSelections()
                selectedSpell = ActiveSpell.RAIN_OF_FIRE
            }
            return
        }

        if (reinfBtnPos.dst(x, y) <= 28f) {
            if (selectedSpell == ActiveSpell.REINFORCEMENTS) {
                selectedSpell = ActiveSpell.NONE
            } else if (reinforcementCooldown <= 0f) {
                clearSelections()
                selectedSpell = ActiveSpell.REINFORCEMENTS
            }
            return
        }

        // 8. THẢ PHÉP TRÊN ĐƯỜNG
        if (selectedSpell != ActiveSpell.NONE) {
            if (isPointOnRoad(clickPos, 45f)) {
                when (selectedSpell) {
                    ActiveSpell.RAIN_OF_FIRE -> {
                        triggerRainOfFire(x, y)
                        rainOfFireCooldown = maxRainOfFireCooldown
                        selectedSpell = ActiveSpell.NONE
                    }
                    ActiveSpell.REINFORCEMENTS -> {
                        spawnReinforcements(x, y)
                        reinforcementCooldown = maxReinforcementCooldown
                        selectedSpell = ActiveSpell.NONE
                    }
                    else -> {}
                }
                return
            }
            selectedSpell = ActiveSpell.NONE
        }

        // 9. NÚT GO! GỌI WAVE SỚM
        val canCallWave = waveManager.currentWaveIndex == -1 || waveManager.isCountdownActive
        if (canCallWave && waveManager.currentWaveDisplay < waveManager.totalWaves) {
            val activePaths = waveManager.getActiveSpawningPathsForNextWave()
            val candidatePositions = mutableListOf<Vector2>()

            for (pIdx in activePaths) {
                if (pIdx in currentLevel.allPaths.indices && currentLevel.allPaths[pIdx].isNotEmpty()) {
                    val p = currentLevel.allPaths[pIdx][0]
                    candidatePositions.add(Vector2(p.x.coerceIn(60f, Config.WORLD_WIDTH - 60f), p.y.coerceIn(60f, Config.WORLD_HEIGHT - 60f)))
                }
            }

            val clickedAnyBtn = candidatePositions.any { it.dst(x, y) <= 42f }
            if (clickedAnyBtn) {
                val (goldBonus, secondsSaved) = waveManager.callEarlyWave()
                if (goldBonus > 0) EconomyManager.addGold(goldBonus)
                if (secondsSaved > 0f) {
                    rainOfFireCooldown = (rainOfFireCooldown - secondsSaved).coerceAtLeast(0f)
                    reinforcementCooldown = (reinforcementCooldown - secondsSaved).coerceAtLeast(0f)
                } else if (waveManager.currentWaveIndex == 0) {
                    rainOfFireCooldown = 0f
                    reinforcementCooldown = 0f
                }
                clearSelections()
                return
            }
        }

        // 10. ĐẶT ĐIỂM RALLY
        if (isSettingRallyPoint && selectedTower != null) {
            val tPos = selectedTower!!.getComponent(TransformComponent::class.java).position
            val tComp = selectedTower!!.getComponent(TowerComponent::class.java)

            if (tPos.dst(x, y) <= maxRallyRadius && isPointOnRoad(clickPos, 55f)) {
                val bComp = selectedTower!!.getComponent(BarracksComponent::class.java)
                if (bComp != null) {
                    bComp.rallyPoint.set(x, y)
                    val offsets = listOf(Vector2(-14f, 0f), Vector2(14f, 8f), Vector2(0f, -12f), Vector2(0f, 14f))
                    for (i in 0 until bComp.soldiers.size) {
                        val sol = bComp.soldiers[i]
                        val sComp = sol.getComponent(SoldierComponent::class.java)
                        sComp?.engagedEnemy?.getComponent(MovementComponent::class.java)?.isBlocked = false
                        sComp?.engagedEnemy = null
                        sComp?.targetPosition?.set(x + offsets.getOrElse(i) { Vector2() }.x, y + offsets.getOrElse(i) { Vector2() }.y)
                    }
                }

                if (tComp.specialization == TowerSpecialization.SORCERER_MAGE) {
                    tComp.golemRallyPoint.set(x, y)
                    tComp.summonedGolem?.let { golem ->
                        val sComp = golem.getComponent(SoldierComponent::class.java)
                        sComp?.engagedEnemy?.getComponent(MovementComponent::class.java)?.isBlocked = false
                        sComp?.engagedEnemy = null
                        sComp?.targetPosition?.set(x, y)
                    }
                }

                isSettingRallyPoint = false
                selectedTower = null
                return
            }
            isSettingRallyPoint = false
        }

        // 11. CLICK NÚT TRÊN CỌC ĐẤT ĐANG CHỌN (BUILD MENU - XÁC NHẬN 2 BƯỚC)
        if (selectedSpot != null) {
            val spot = selectedSpot!!
            val spotPos = spot.getComponent(TransformComponent::class.java).position
            val isHigh = spotPos.y > 540f

            var clickedAnyBuildBtn = false
            for (opt in buildOptions) {
                val offset = if (isHigh) Vector2(opt.second.x, -opt.second.y) else opt.second
                val btnPos = Vector2(spotPos).add(offset)
                if (btnPos.dst(x, y) <= 36f) {
                    clickedAnyBuildBtn = true
                    val clickedType = opt.first
                    if (!allowedTowers.contains(clickedType)) return

                    var finalCost = clickedType.cost
                    if (clickedType == TowerType.BARRACKS && UpgradeManager.getEffectiveBarracksLevel() >= 2) finalCost = 60
                    if (clickedType == TowerType.MAGE && UpgradeManager.getEffectiveMageLevel() >= 3) finalCost = 90
                    if (clickedType == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 3) {
                        finalCost = (finalCost * 0.9f).toInt()
                    }

                    // BƯỚC 2: NẾU ĐÃ CHỌN TRƯỚC ĐÓ THÌ CHẠM LẦN NỮA ĐỂ XÁC NHẬN XÂY
                    if (previewBuildType == clickedType) {
                        if (EconomyManager.spendGold(finalCost)) {
                            spot.getComponent(TowerSpotComponent::class.java).isOccupied = true
                            if (clickedType == TowerType.BARRACKS) {
                                val rally = getNearestRoadPoint(spotPos)
                                buildBarracks(spotPos, rally, finalCost)
                            } else {
                                buildTower(spotPos, clickedType, finalCost)
                            }
                            clearSelections()
                            return
                        }
                    } else {
                        // BƯỚC 1: CHỌN ĐỂ XEM THÔNG TIN (PREVIEW TOOLTIP)
                        previewBuildType = clickedType
                    }
                    return
                }
            }
            if (!clickedAnyBuildBtn) {
                selectedSpot = null
                previewBuildType = null
            }
        }

        // 12. CLICK NÚT TRÊN THÁP ĐANG CHỌN (XÁC NHẬN 2 BƯỚC: UPGRADE, BRANCH, SKILL, SELL)
        if (selectedTower != null) {
            val tower = selectedTower!!
            val tPos = tower.getComponent(TransformComponent::class.java).position
            val tComp = tower.getComponent(TowerComponent::class.java)
            val isHigh = tPos.y > 540f

            // A. THÁP SUNRAY TOWER
            if (tComp.specialization == TowerSpecialization.SUNRAY_TOWER) {
                val upgradeBtnX = if (tComp.isSunrayReady && tComp.sunraySorcerers > 0) tPos.x - 55f else tPos.x
                val upgradeBtnY = tPos.y + 60f
                val fireBtnX = if (tComp.sunraySorcerers < 4) tPos.x + 55f else tPos.x
                val fireBtnY = tPos.y + 60f

                if (tComp.sunraySorcerers < 4 && Vector2(upgradeBtnX, upgradeBtnY).dst(x, y) <= 32f) {
                    if (previewTowerAction == "UPGRADE_SUNRAY") {
                        if (EconomyManager.spendGold(100)) {
                            tComp.sunraySorcerers++
                            tComp.isSunrayReady = false
                            tComp.sunrayChargeTimer = when (tComp.sunraySorcerers) {
                                1 -> 19.0f; 2 -> 16.0f; 3 -> 13.0f; else -> 10.0f
                            }
                            clearSelections()
                        }
                    } else {
                        previewTowerAction = "UPGRADE_SUNRAY"
                    }
                    return
                }

                if (tComp.isSunrayReady && tComp.sunraySorcerers > 0 && Vector2(fireBtnX, fireBtnY).dst(x, y) <= 32f) {
                    if (previewTowerAction == "FIRE_SUNRAY") {
                        isAimingSunrayTarget = true
                        selectedTower = null
                        previewTowerAction = null
                    } else {
                        previewTowerAction = "FIRE_SUNRAY"
                    }
                    return
                }
            } else if (tComp.specialization == TowerSpecialization.SYLVAN_ELF_HALL) {
                // B. NHÀ SYLVAN ELF HALL
                val bComp = tower.getComponent(BarracksComponent::class.java)
                val currentElves = bComp?.soldiers?.size ?: 0

                if (tComp.isRuined) {
                    if (Vector2(tPos.x, tPos.y - 60f).dst(x, y) <= 32f) {
                        if (previewTowerAction == "REPAIR") {
                            if (EconomyManager.spendGold(100)) {
                                tComp.isRuined = false
                                bComp?.respawnTimer = 2.0f
                                clearSelections()
                            }
                        } else {
                            previewTowerAction = "REPAIR"
                        }
                        return
                    }
                } else {
                    if (currentElves < 4 && Vector2(tPos.x + 55f, tPos.y).dst(x, y) <= 32f) {
                        if (previewTowerAction == "HIRE_ELF") {
                            if (EconomyManager.spendGold(100)) {
                                val sPos = getNearestRoadPoint(tPos)
                                val elf = Entity().apply {
                                    add(TransformComponent(position = Vector2(sPos)))
                                    add(HealthComponent(maxHealth = 180f, currentHealth = 180f))
                                    add(ArmorComponent(armorType = ArmorType.MEDIUM_ARMOR))
                                    add(SoldierComponent(
                                        attackDamageMin = 15f, attackDamageMax = 25f, attackRate = 1.0f,
                                        regenPerSec = 10f, targetPosition = Vector2(sPos), parentTower = tower,
                                        soldierRankName = "Sylvan Elf", moveSpeed = 65f
                                    ))
                                }
                                bComp?.soldiers?.add(elf)
                                engine.addEntity(elf)
                                clearSelections()
                            }
                        } else {
                            previewTowerAction = "HIRE_ELF"
                        }
                        return
                    }

                    if (Vector2(tPos.x - 55f, tPos.y).dst(x, y) <= 30f) {
                        isSettingRallyPoint = true
                        previewTowerAction = null
                        return
                    }
                }
            } else {
                // C. CÁC THÁP THƯỜNG (LEVEL 1 -> 4)
                val upgradeY = if (isHigh) tPos.y - 65f else tPos.y + 65f
                val sellY = if (isHigh) tPos.y + 65f else tPos.y - 65f
                val branchY = if (isHigh) tPos.y - 60f else tPos.y + 60f

                val maxLevel = when {
                    currentLevel.levelNumber == 0 -> 4
                    currentLevel.levelNumber == 5 -> if (waveManager.currentWaveDisplay >= 5) 4 else 3
                    currentLevel.levelNumber >= 6 -> 4
                    else -> currentLevel.maxTowerLevel
                }

                // 1. NÂNG CẤP CẤP ĐỘ 1 -> 2, 2 -> 3
                if (tComp.level < 3 && tComp.level < maxLevel && Vector2(tPos.x, upgradeY).dst(x, y) <= 32f) {
                    var upCost = tComp.upgradeCost
                    if (tComp.towerType == TowerType.MAGE && UpgradeManager.getEffectiveMageLevel() >= 3) upCost = (upCost * 0.9f).toInt()
                    if (tComp.towerType == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 3) upCost = (upCost * 0.9f).toInt()

                    if (previewTowerAction == "UPGRADE") {
                        if (EconomyManager.spendGold(upCost)) {
                            upgradeTower(tower, 0)
                            clearSelections()
                        }
                    } else {
                        previewTowerAction = "UPGRADE"
                    }
                    return
                }

                val canUpgradeToSpec = if (tComp.level == 3) {
                    when (currentLevel.levelNumber) {
                        0 -> true
                        5 -> tComp.towerType == TowerType.ARCHER && maxLevel >= 4
                        6 -> (tComp.towerType == TowerType.ARCHER || tComp.towerType == TowerType.MAGE || tComp.towerType == TowerType.BARRACKS)
                        7 -> (tComp.towerType == TowerType.ARCHER || tComp.towerType == TowerType.MAGE || tComp.towerType == TowerType.BARRACKS)
                        8, 9, 10, 11, 12 -> true
                        else -> maxLevel >= 4
                    }
                } else false

                // 2. NÂNG CẤP CHUYÊN HÓA NHÁNH (LEVEL 3 -> LEVEL 4)
                if (canUpgradeToSpec) {
                    val (spec1, spec2) = getBranchSpecs(tComp.towerType)
                    val isSingleBranch = (currentLevel.levelNumber in listOf(5, 6))

                    if (isSingleBranch) {
                        if (Vector2(tPos.x, branchY).dst(x, y) <= 34f) {
                            if (previewTowerAction == "SPEC_1") {
                                if (EconomyManager.spendGold(spec1.branchCost)) {
                                    upgradeTower(tower, 1)
                                    clearSelections()
                                }
                            } else {
                                previewTowerAction = "SPEC_1"
                            }
                            return
                        }
                    } else {
                        if (Vector2(tPos.x - 45f, branchY).dst(x, y) <= 34f) {
                            if (previewTowerAction == "SPEC_1") {
                                if (EconomyManager.spendGold(spec1.branchCost)) {
                                    upgradeTower(tower, 1)
                                    clearSelections()
                                }
                            } else {
                                previewTowerAction = "SPEC_1"
                            }
                            return
                        }
                        if (Vector2(tPos.x + 45f, branchY).dst(x, y) <= 34f) {
                            if (previewTowerAction == "SPEC_2") {
                                if (EconomyManager.spendGold(spec2.branchCost)) {
                                    upgradeTower(tower, 2)
                                    clearSelections()
                                }
                            } else {
                                previewTowerAction = "SPEC_2"
                            }
                            return
                        }
                    }
                }

                // 3. NÂNG CẤP KỸ NĂNG CHO THÁP CẤP 4 (SKILLS 1, 2, 3)
                if (tComp.level == 4) {
                    val spec = tComp.specialization
                    val has3Skills = (tComp.towerType == TowerType.BARRACKS)
                    val skillCenterY = if (isHigh) tPos.y - 55f else tPos.y + 55f
                    val skillMiddleY = if (isHigh) tPos.y - 75f else tPos.y + 75f
                    val costDiscount = if (tComp.towerType == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 4) 0.75f else 1.0f

                    if (has3Skills) {
                        if (Vector2(tPos.x - 65f, skillCenterY).dst(x, y) <= 32f) {
                            if (tComp.skill1Level < 3) {
                                val cost = (spec.skill1Costs.getOrElse(tComp.skill1Level) { 0 } * costDiscount).toInt()
                                if (previewTowerAction == "SKILL_1") {
                                    if (EconomyManager.spendGold(cost)) {
                                        tComp.skill1Level++
                                        applySkillUpgrade(tower, 1)
                                        clearSelections()
                                    }
                                } else {
                                    previewTowerAction = "SKILL_1"
                                }
                            }
                            return
                        }
                        val maxSkill2 = if (spec == TowerSpecialization.HOLY_ORDER) 1 else 3
                        if (Vector2(tPos.x, skillMiddleY).dst(x, y) <= 32f) {
                            if (tComp.skill2Level < maxSkill2) {
                                val cost = (spec.skill2Costs.getOrElse(tComp.skill2Level) { 0 } * costDiscount).toInt()
                                if (previewTowerAction == "SKILL_2") {
                                    if (EconomyManager.spendGold(cost)) {
                                        tComp.skill2Level++
                                        applySkillUpgrade(tower, 2)
                                        clearSelections()
                                    }
                                } else {
                                    previewTowerAction = "SKILL_2"
                                }
                            }
                            return
                        }
                        if (Vector2(tPos.x + 65f, skillCenterY).dst(x, y) <= 32f) {
                            if (tComp.skill3Level < 3 && spec.skill3Costs.isNotEmpty()) {
                                val cost = (spec.skill3Costs.getOrElse(tComp.skill3Level) { 0 } * costDiscount).toInt()
                                if (previewTowerAction == "SKILL_3") {
                                    if (EconomyManager.spendGold(cost)) {
                                        tComp.skill3Level++
                                        applySkillUpgrade(tower, 3)
                                        clearSelections()
                                    }
                                } else {
                                    previewTowerAction = "SKILL_3"
                                }
                            }
                            return
                        }
                    } else {
                        if (Vector2(tPos.x - 55f, skillCenterY).dst(x, y) <= 32f) {
                            if (tComp.skill1Level < 3) {
                                val cost = (spec.skill1Costs.getOrElse(tComp.skill1Level) { 0 } * costDiscount).toInt()
                                if (previewTowerAction == "SKILL_1") {
                                    if (EconomyManager.spendGold(cost)) {
                                        tComp.skill1Level++
                                        applySkillUpgrade(tower, 1)
                                        clearSelections()
                                    }
                                } else {
                                    previewTowerAction = "SKILL_1"
                                }
                            }
                            return
                        }
                        if (Vector2(tPos.x + 55f, skillCenterY).dst(x, y) <= 32f) {
                            if (tComp.skill2Level < 3) {
                                val cost = (spec.skill2Costs.getOrElse(tComp.skill2Level) { 0 } * costDiscount).toInt()
                                if (previewTowerAction == "SKILL_2") {
                                    if (EconomyManager.spendGold(cost)) {
                                        tComp.skill2Level++
                                        applySkillUpgrade(tower, 2)
                                        clearSelections()
                                    }
                                } else {
                                    previewTowerAction = "SKILL_2"
                                }
                            }
                            return
                        }
                    }
                }

                // 4. BÁN THÁP (SELL TOWER - 2 BƯỚC)
                if (Vector2(tPos.x, sellY).dst(x, y) <= 30f) {
                    val isPreGame = (waveManager.currentWaveIndex == -1)
                    val refundRatio = when {
                        isPreGame -> 1.0f
                        UpgradeManager.getEffectiveArcherLevel() >= 1 -> 0.90f
                        else -> 0.70f
                    }
                    val refundAmount = (tComp.totalInvestedGold * refundRatio).toInt()

                    if (previewTowerAction == "SELL") {
                        EconomyManager.addGold(refundAmount)
                        freeSpotAt(tPos)
                        val bComp = tower.getComponent(BarracksComponent::class.java)
                        bComp?.soldiers?.forEach { engine.removeEntity(it) }
                        tComp.summonedGolem?.let { engine.removeEntity(it) }
                        engine.removeEntity(tower)
                        clearSelections()
                    } else {
                        previewTowerAction = "SELL"
                    }
                    return
                }

                // 5. ĐẶT ĐIỂM TẬP KẾT RALLY
                val canRally = tComp.towerType == TowerType.BARRACKS ||
                    (tComp.specialization == TowerSpecialization.SORCERER_MAGE && tComp.skill2Level > 0)
                if (canRally && Vector2(tPos.x - 65f, tPos.y).dst(x, y) <= 30f) {
                    isSettingRallyPoint = true
                    previewTowerAction = null
                    return
                }
            }

            selectedTower = null
            previewTowerAction = null
        }

        // 13. CHỌN MỤC TIÊU MỚI: QUÁI, LÍNH, THÁP HOẶC CỌC ĐẤT TRỐNG
        val enemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java, TransformComponent::class.java, HealthComponent::class.java).get())
        for (i in 0 until enemies.size()) {
            val e = enemies.get(i)
            val ePos = e.getComponent(TransformComponent::class.java).position
            if (ePos.dst(x, y) <= 28f) {
                clearSelections()
                selectedEnemy = e
                return
            }
        }

        val soldiers = engine.getEntitiesFor(Family.all(SoldierComponent::class.java, TransformComponent::class.java, HealthComponent::class.java).get())
        for (i in 0 until soldiers.size()) {
            val sol = soldiers.get(i)
            val sPos = sol.getComponent(TransformComponent::class.java).position
            if (sPos.dst(x, y) <= 26f) {
                clearSelections()
                selectedSoldier = sol
                return
            }
        }

        for (i in 0 until allTowers.size()) {
            val t = allTowers.get(i)
            val pos = t.getComponent(TransformComponent::class.java).position
            val tc = t.getComponent(TowerComponent::class.java)
            val hitRadius = if (tc?.specialization == TowerSpecialization.SUNRAY_TOWER) 50f else 34f
            if (pos.dst(x, y) <= hitRadius) {
                // NẾU THÁP ĐANG BỊ CẢNH BÁO SẮP KHÓA HOẶC ĐANG BỊ KHÓA -> CHẶN MỞ WHEEL MENU
                if (warningTowersMap.containsKey(t) || tc?.isFrozen == true) {
                    clearSelections()
                    return
                }
                clearSelections()
                selectedTower = t
                return
            }
        }

        val spots = engine.getEntitiesFor(Family.all(TowerSpotComponent::class.java, TransformComponent::class.java).get())
        for (i in 0 until spots.size()) {
            val s = spots.get(i)
            val spotComp = s.getComponent(TowerSpotComponent::class.java)
            val pos = s.getComponent(TransformComponent::class.java).position
            if (!spotComp.isOccupied && pos.dst(x, y) <= 42f) {
                clearSelections()
                selectedSpot = s
                return
            }
        }

        clearSelections()
    }

    private fun getBranchSpecs(type: TowerType): Pair<TowerSpecialization, TowerSpecialization> {
        return when (type) {
            TowerType.ARCHER -> Pair(TowerSpecialization.RANGERS_HIDEOUT, TowerSpecialization.MUSKETEER_GARRISON)
            TowerType.BARRACKS -> Pair(TowerSpecialization.HOLY_ORDER, TowerSpecialization.BARBARIAN_MEAD_HALL)
            TowerType.MAGE -> Pair(TowerSpecialization.ARCANE_WIZARD, TowerSpecialization.SORCERER_MAGE)
            TowerType.ARTILLERY -> Pair(TowerSpecialization.BIG_BERTHA, TowerSpecialization.TESLA_X104)
            TowerType.SUNRAY -> Pair(TowerSpecialization.SUNRAY_TOWER, TowerSpecialization.SUNRAY_TOWER)
        }
    }

    private fun triggerRainOfFire(targetX: Float, targetY: Float) {
        val lvl = UpgradeManager.getEffectiveRainLevel()
        val meteorCount = when (lvl) {
            0 -> 3
            5 -> 10
            else -> 5
        }
        val (dMin, dMax) = when (lvl) {
            0 -> Pair(30f, 60f)
            1, 2 -> Pair(50f, 80f)
            3, 4 -> Pair(90f, 120f)
            else -> Pair(150f, 180f)
        }
        val blastRadius = if (lvl >= 3) 70f else 55f

        val burnDuration = when {
            lvl >= 4 -> 10f
            lvl >= 2 -> 5f
            else -> 0f
        }
        val burnDps = when {
            lvl >= 4 -> 25f
            lvl >= 2 -> 15f
            else -> 0f
        }

        for (i in 0 until meteorCount) {
            val offsetX = if (lvl == 5 && i >= 5) MathUtils.random(-350f, 350f) else MathUtils.random(-30f, 30f)
            val offsetY = if (lvl == 5 && i >= 5) MathUtils.random(-200f, 200f) else MathUtils.random(-30f, 30f)
            val delay = i * 0.15f
            val hitX = targetX + offsetX
            val hitY = targetY + offsetY

            effectRenderer.fireRainEffects.add(FireRainEffect(hitX, hitY, delay = delay) { mx, my ->
                val enemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java, TransformComponent::class.java, HealthComponent::class.java).get())
                for (e in enemies) {
                    val ePos = e.getComponent(TransformComponent::class.java).position
                    val hp = e.getComponent(HealthComponent::class.java)
                    val eComp = e.getComponent(EnemyComponent::class.java)

                    if (ePos.dst(mx, my) <= blastRadius) {
                        val dmg = MathUtils.random(dMin, dMax)
                        hp.currentHealth -= dmg
                        if (hp.currentHealth <= 0f || hp.isDead) {
                            if (eComp != null && !eComp.isSummonedMinion) EconomyManager.addGold(eComp.enemyType.rewardGold)
                            e.getComponent(MovementComponent::class.java)?.isBlocked = false
                            engine.removeEntity(e)
                        }
                    }
                }

                if (burnDuration > 0f) {
                    effectRenderer.scorchedEarthPatches.add(
                        ScorchedEarthPatch(
                            x = mx,
                            y = my,
                            duration = burnDuration,
                            maxDuration = burnDuration,
                            dps = burnDps,
                            radius = 26f
                        )
                    )
                }
            })
        }
    }

    private fun spawnReinforcements(x: Float, y: Float) {
        val lvl = UpgradeManager.getEffectiveReinfLevel()
        val (rankName, hp, dMin, dMax, armorType) = when (lvl) {
            1 -> Quintuple("Farmer", 50f, 1f, 3f, ArmorType.NONE)
            2 -> Quintuple("Conscript", 70f, 2f, 4f, ArmorType.LIGHT_ARMOR)
            3 -> Quintuple("Warrior", 90f, 3f, 6f, ArmorType.LIGHT_ARMOR)
            4 -> Quintuple("Legionnaire", 110f, 6f, 10f, ArmorType.MEDIUM_ARMOR)
            5 -> Quintuple("Legionnaire", 110f, 6f, 10f, ArmorType.MEDIUM_ARMOR)
            else -> Quintuple("Farmer", 30f, 1f, 2f, ArmorType.NONE)
        }

        val offsets = listOf(Vector2(-12f, 0f), Vector2(12f, 0f))
        for (off in offsets) {
            val pos = Vector2(x + off.x, y + off.y)
            val farmer = Entity().apply {
                add(TransformComponent(position = Vector2(pos)))
                add(HealthComponent(maxHealth = hp, currentHealth = hp))
                add(ArmorComponent(armorType = armorType))
                add(ReinforcementComponent(lifeTime = 20f))
                add(SoldierComponent(
                    attackDamageMin = dMin,
                    attackDamageMax = dMax,
                    attackRate = 1.0f,
                    targetPosition = Vector2(pos),
                    soldierRankName = rankName,
                    moveSpeed = 80f
                ))
            }
            engine.addEntity(farmer)
        }
    }

    private fun freeSpotAt(pos: Vector2) {
        val spots = engine.getEntitiesFor(Family.all(TowerSpotComponent::class.java, TransformComponent::class.java).get())
        for (i in 0 until spots.size()) {
            val s = spots.get(i)
            if (s.getComponent(TransformComponent::class.java).position.dst(pos) < 5f) {
                s.getComponent(TowerSpotComponent::class.java).isOccupied = false
                break
            }
        }
    }

    private fun upgradeTower(tower: Entity, branchChoice: Int) {
        val tComp = tower.getComponent(TowerComponent::class.java)
        tComp.level++

        if (tComp.level <= 3) {
            var cost = tComp.upgradeCost
            if (tComp.towerType == TowerType.MAGE && UpgradeManager.getEffectiveMageLevel() >= 3) cost = (cost * 0.9f).toInt()
            if (tComp.towerType == TowerType.ARTILLERY && UpgradeManager.getEffectiveArtilleryLevel() >= 3) cost = (cost * 0.9f).toInt()
            tComp.totalInvestedGold += cost
        }

        when (tComp.towerType) {
            TowerType.ARCHER -> when (tComp.level) {
                2 -> { tComp.damageMin = 7f; tComp.damageMax = 11f; tComp.attackRate = 0.6f; tComp.range = 230f }
                3 -> { tComp.damageMin = 10f; tComp.damageMax = 16f; tComp.attackRate = 0.5f; tComp.range = 260f }
                4 -> {
                    if (branchChoice == 1) {
                        tComp.specialization = TowerSpecialization.RANGERS_HIDEOUT
                        tComp.damageMin = 13f; tComp.damageMax = 19f; tComp.attackRate = 0.4f; tComp.range = 290f
                    } else {
                        tComp.specialization = TowerSpecialization.MUSKETEER_GARRISON
                        tComp.damageMin = 35f; tComp.damageMax = 65f; tComp.attackRate = 1.5f; tComp.range = 330f
                    }
                    tComp.totalInvestedGold += tComp.specialization.branchCost
                }
            }
            TowerType.BARRACKS -> when (tComp.level) {
                2 -> { tComp.damageMin = 3f; tComp.damageMax = 4f; tComp.range = 220f }
                3 -> { tComp.damageMin = 6f; tComp.damageMax = 10f; tComp.range = 230f }
                4 -> {
                    if (branchChoice == 1) {
                        tComp.specialization = TowerSpecialization.HOLY_ORDER
                        tComp.damageMin = 12f; tComp.damageMax = 18f
                    } else {
                        tComp.specialization = TowerSpecialization.BARBARIAN_MEAD_HALL
                        tComp.damageMin = 16f; tComp.damageMax = 24f
                    }
                    tComp.range = 240f
                    tComp.totalInvestedGold += tComp.specialization.branchCost
                }
            }
            TowerType.MAGE -> when (tComp.level) {
                2 -> { tComp.damageMin = 23f; tComp.damageMax = 43f; tComp.attackRate = 1.5f; tComp.range = 230f }
                3 -> { tComp.damageMin = 40f; tComp.damageMax = 74f; tComp.attackRate = 1.5f; tComp.range = 260f }
                4 -> {
                    if (branchChoice == 1) {
                        tComp.specialization = TowerSpecialization.ARCANE_WIZARD
                        tComp.damageMin = 76f; tComp.damageMax = 140f; tComp.attackRate = 2.0f; tComp.range = 300f
                    } else {
                        tComp.specialization = TowerSpecialization.SORCERER_MAGE
                        tComp.damageMin = 42f; tComp.damageMax = 78f; tComp.attackRate = 1.5f; tComp.range = 280f
                    }
                    tComp.totalInvestedGold += tComp.specialization.branchCost
                }
            }
            TowerType.ARTILLERY -> when (tComp.level) {
                2 -> { tComp.damageMin = 20f; tComp.damageMax = 40f; tComp.attackRate = 3.0f; tComp.range = 220f }
                3 -> { tComp.damageMin = 30f; tComp.damageMax = 60f; tComp.attackRate = 3.0f; tComp.range = 260f }
                4 -> {
                    if (branchChoice == 1) {
                        tComp.specialization = TowerSpecialization.BIG_BERTHA
                        tComp.damageMin = 50f; tComp.damageMax = 100f
                        tComp.attackRate = 3.0f; tComp.range = 300f
                    } else {
                        tComp.specialization = TowerSpecialization.TESLA_X104
                        tComp.damageMin = 30f; tComp.damageMax = 55f
                        tComp.attackRate = 2.25f; tComp.range = 230f
                    }
                    tComp.totalInvestedGold += tComp.specialization.branchCost
                }
            }
            else -> {}
        }

        if (tComp.towerType == TowerType.ARCHER) {
            if (UpgradeManager.getEffectiveArcherLevel() >= 4) tComp.range *= 1.21f
            else if (UpgradeManager.getEffectiveArcherLevel() >= 2) tComp.range *= 1.10f
        }
        if (tComp.towerType == TowerType.MAGE) {
            if (UpgradeManager.getEffectiveMageLevel() >= 1) tComp.range *= 1.10f
            if (UpgradeManager.getEffectiveMageLevel() >= 4) {
                tComp.damageMin *= 1.15f
                tComp.damageMax *= 1.15f
            }
        }
        if (tComp.towerType == TowerType.ARTILLERY) {
            if (UpgradeManager.getEffectiveArtilleryLevel() >= 1) {
                tComp.damageMin *= 1.10f
                tComp.damageMax *= 1.10f
            }
            if (UpgradeManager.getEffectiveArtilleryLevel() >= 2) tComp.range *= 1.10f
        }

        if (tComp.towerType == TowerType.BARRACKS) {
            val bComp = tower.getComponent(BarracksComponent::class.java)
            if (bComp != null) {
                for (sol in bComp.soldiers) {
                    val sComp = sol.getComponent(SoldierComponent::class.java)
                    sComp?.engagedEnemy?.getComponent(MovementComponent::class.java)?.isBlocked = false
                    sComp?.engagedEnemy = null
                    engine.removeEntity(sol)
                }
                bComp.soldiers.clear()
                bComp.respawnTimer = 10f

                val offsets = listOf(Vector2(-14f, 0f), Vector2(14f, 8f), Vector2(0f, -12f))
                val hpBonus = when {
                    UpgradeManager.getEffectiveBarracksLevel() >= 4 -> 1.20f
                    UpgradeManager.getEffectiveBarracksLevel() >= 1 -> 1.10f
                    else -> 1.0f
                }

                val isArmorLevel2 = (UpgradeManager.getEffectiveBarracksLevel() >= 2)
                val isArmorLevel5 = (UpgradeManager.getEffectiveBarracksLevel() >= 5)

                val (rName, hpMax, aType, dMin, dMax, aRate, reg) = when (tComp.level) {
                    2 -> Septuple("Footman", 100f * hpBonus, if (isArmorLevel5 || isArmorLevel2) ArmorType.MEDIUM_ARMOR else ArmorType.LIGHT_ARMOR, 3f, 4f, 1.36f, 7f)
                    3 -> Septuple("Knight", 150f * hpBonus, if (isArmorLevel5 || isArmorLevel2) ArmorType.HEAVY_ARMOR else ArmorType.MEDIUM_ARMOR, 6f, 10f, 1.36f, 10f)
                    4 -> if (tComp.specialization == TowerSpecialization.HOLY_ORDER) {
                        val holyArmor = if (tComp.skill2Level > 0 || isArmorLevel2 || isArmorLevel5) ArmorType.HEAVY_ARMOR else ArmorType.MEDIUM_ARMOR
                        Septuple("Paladin", 200f * hpBonus, holyArmor, 12f, 18f, 1.47f, 25f)
                    } else {
                        val addDmg = tComp.skill3Level * 10f
                        Septuple("Barbarian", 250f * hpBonus, if (isArmorLevel5 || isArmorLevel2) ArmorType.LIGHT_ARMOR else ArmorType.NONE, 16f + addDmg, 24f + addDmg, 1.37f, 20f)
                    }
                    else -> Septuple("Militia", 50f * hpBonus, if (isArmorLevel5 || isArmorLevel2) ArmorType.LIGHT_ARMOR else ArmorType.NONE, 1f, 3f, 1.0f, 5f)
                }

                for (idx in 0 until 3) {
                    val target = Vector2(bComp.rallyPoint).add(offsets[idx])
                    val newSol = Entity().apply {
                        add(TransformComponent(position = Vector2(target)))
                        add(HealthComponent(maxHealth = hpMax, currentHealth = hpMax))
                        add(ArmorComponent(armorType = aType))
                        add(SoldierComponent(
                            attackDamageMin = dMin,
                            attackDamageMax = dMax,
                            attackRate = aRate,
                            regenPerSec = reg,
                            targetPosition = target,
                            parentTower = tower,
                            soldierRankName = rName,
                            moveSpeed = 55f
                        ))
                    }
                    bComp.soldiers.add(newSol)
                    engine.addEntity(newSol)
                }
            }
        }
    }

    private fun applySkillUpgrade(tower: Entity, skillNum: Int) {
        val tComp = tower.getComponent(TowerComponent::class.java)
        if (skillNum == 1) tComp.skill1Cooldown = 0f
        if (skillNum == 2) tComp.skill2Cooldown = 0f
        if (skillNum == 3) tComp.skill3Cooldown = 0f

        if (tComp.specialization == TowerSpecialization.HOLY_ORDER && skillNum == 2) {
            val bComp = tower.getComponent(BarracksComponent::class.java)
            bComp?.soldiers?.forEach { sol ->
                sol.getComponent(ArmorComponent::class.java)?.armorType = ArmorType.HEAVY_ARMOR
            }
        }

        if (tComp.specialization == TowerSpecialization.BARBARIAN_MEAD_HALL) {
            val bComp = tower.getComponent(BarracksComponent::class.java)
            if (skillNum == 2) {
                val chance = when (tComp.skill2Level) {
                    1 -> 0.15f; 2 -> 0.20f; else -> 0.25f
                }
                bComp?.soldiers?.forEach { sol ->
                    sol.getComponent(SoldierComponent::class.java)?.whirlwindChance = chance
                }
            }
            if (skillNum == 3) {
                val addDmg = tComp.skill3Level * 10f
                bComp?.soldiers?.forEach { sol ->
                    val sComp = sol.getComponent(SoldierComponent::class.java)
                    if (sComp != null) {
                        sComp.attackDamageMin = 16f + addDmg
                        sComp.attackDamageMax = 24f + addDmg
                    }
                }
            }
        }

        if (tComp.specialization == TowerSpecialization.SORCERER_MAGE && skillNum == 2) {
            val golemHp = 500f + tComp.skill2Level * 200f
            val golemDmgMin = 30f + tComp.skill2Level * 15f
            val golemDmgMax = 50f + tComp.skill2Level * 25f

            if (tComp.summonedGolem == null) {
                val tPos = tower.getComponent(TransformComponent::class.java).position
                val spawnPos = if (tComp.golemRallyPoint.isZero) getNearestRoadPoint(tPos) else Vector2(tComp.golemRallyPoint)
                tComp.golemRallyPoint.set(spawnPos)

                val golem = Entity().apply {
                    add(TransformComponent(position = Vector2(spawnPos)))
                    add(HealthComponent(maxHealth = golemHp, currentHealth = golemHp))
                    add(ArmorComponent(armorType = ArmorType.HEAVY_ARMOR))
                    add(SoldierComponent(
                        attackDamageMin = golemDmgMin,
                        attackDamageMax = golemDmgMax,
                        attackRate = 1.35f,
                        regenPerSec = 20f,
                        targetPosition = Vector2(spawnPos),
                        parentTower = tower,
                        soldierRankName = "Earth Elemental",
                        moveSpeed = 48f
                    ))
                }
                tComp.summonedGolem = golem
                engine.addEntity(golem)
            } else {
                tComp.summonedGolem?.let { golem ->
                    val gHp = golem.getComponent(HealthComponent::class.java)
                    val gSol = golem.getComponent(SoldierComponent::class.java)
                    if (gHp != null) {
                        gHp.maxHealth = golemHp
                        gHp.currentHealth = golemHp
                    }
                    if (gSol != null) {
                        gSol.attackDamageMin = golemDmgMin
                        gSol.attackDamageMax = golemDmgMax
                    }
                }
            }
        }
    }

    private fun queueCircleDemons(circlePos: Vector2, spawnList: List<Pair<EnemyType, Int>>) {
        var delayCounter = 0.2f
        for ((type, count) in spawnList) {
            for (c in 0 until count) {
                portalSpawnQueue.add(QueuedPortalSpawn(circlePos, type, delayCounter))
                delayCounter += 0.85f // Giãn cách xuất hiện từng con một
            }
        }
    }

    private fun spawnSinglePortalDemon(circlePos: Vector2, type: EnemyType) {
        val path = Level12TheDarkTower.pathNorthwest
        var closestIdx = 0
        var minDist = Float.MAX_VALUE
        for (idx in path.indices) {
            val d = path[idx].dst(circlePos)
            if (d < minDist) {
                minDist = d
                closestIdx = idx
            }
        }
        val remainingWps = path.drop(closestIdx).toMutableList()

        val offX = MathUtils.random(-12f, 12f)
        val offY = MathUtils.random(-12f, 12f)
        val demon = Entity().apply {
            add(TransformComponent(position = Vector2(circlePos.x + offX, circlePos.y + offY)))
            val maxHp = type.getMaxHp(difficulty)
            add(HealthComponent(maxHealth = maxHp, currentHealth = maxHp))
            add(ArmorComponent(armorType = type.armorType, magicResistType = type.magicResist))
            add(EnemyComponent(enemyType = type, isSummonedMinion = false))
            add(MovementComponent(
                speed = type.speed,
                waypoints = remainingWps.toMutableList()
            ))
        }
        engine.addEntity(demon)
        effectRenderer.magicSparks.add(MagicSparkEffect(Vector2(circlePos.x + offX, circlePos.y + offY)))
    }

    private fun createTowerSpot(pos: Vector2) {
        val spot = Entity().apply {
            add(TransformComponent(position = Vector2(pos)))
            add(TowerSpotComponent(isOccupied = false))
        }
        engine.addEntity(spot)
    }

    private fun buildTower(pos: Vector2, type: TowerType, investedCost: Int = type.cost) {
        val tower = Entity().apply {
            add(TransformComponent(position = Vector2(pos)))
            var (range, rate, dMin, dMax) = when (type) {
                TowerType.ARCHER -> Quadruple(200f, 0.8f, 4f, 6f)
                TowerType.MAGE -> Quadruple(200f, 1.5f, 9f, 17f)
                TowerType.ARTILLERY -> Quadruple(200f, 3.0f, 8f, 15f)
                else -> Quadruple(200f, 1f, 3f, 6f)
            }
            if (type == TowerType.MAGE && UpgradeManager.getEffectiveMageLevel() >= 4) {
                dMin *= 1.15f
                dMax *= 1.15f
            }
            if (type == TowerType.ARCHER) {
                if (UpgradeManager.getEffectiveArcherLevel() >= 4) range *= 1.21f
                else if (UpgradeManager.getEffectiveArcherLevel() >= 2) range *= 1.10f
            }
            if (type == TowerType.MAGE && UpgradeManager.getEffectiveMageLevel() >= 1) range *= 1.10f
            if (type == TowerType.ARTILLERY) {
                if (UpgradeManager.getEffectiveArtilleryLevel() >= 1) {
                    dMin *= 1.10f
                    dMax *= 1.10f
                }
                if (UpgradeManager.getEffectiveArtilleryLevel() >= 2) range *= 1.10f
            }

            add(TowerComponent(towerType = type, range = range, attackRate = rate, damageMin = dMin, damageMax = dMax, totalInvestedGold = investedCost))
        }
        engine.addEntity(tower)
    }

    private fun buildBarracks(towerPos: Vector2, rallyPoint: Vector2, investedCost: Int = TowerType.BARRACKS.cost) {
        buildPrebuiltBarracks(towerPos, rallyPoint, level = 1, investedCost = investedCost)
    }

    private fun buildPrebuiltBarracks(towerPos: Vector2, rallyPoint: Vector2, level: Int, investedCost: Int = TowerType.BARRACKS.cost) {
        val bComp = BarracksComponent(rallyPoint = Vector2(rallyPoint))
        val barracks = Entity().apply {
            add(TransformComponent(position = Vector2(towerPos)))
            add(TowerComponent(towerType = TowerType.BARRACKS, level = level, range = 210f, totalInvestedGold = investedCost))
            add(bComp)
        }
        engine.addEntity(barracks)

        val hpBonus = when {
            UpgradeManager.getEffectiveBarracksLevel() >= 4 -> 1.20f
            UpgradeManager.getEffectiveBarracksLevel() >= 1 -> 1.10f
            else -> 1.0f
        }

        val isArmorLevel2 = (UpgradeManager.getEffectiveBarracksLevel() >= 2)
        val isArmorLevel5 = (UpgradeManager.getEffectiveBarracksLevel() >= 5)

        val (rName, hpMax, aType, dMin, dMax, aRate, reg) = when (level) {
            2 -> Septuple("Footman", 100f * hpBonus, if (isArmorLevel5 || isArmorLevel2) ArmorType.MEDIUM_ARMOR else ArmorType.LIGHT_ARMOR, 3f, 4f, 1.36f, 7f)
            3 -> Septuple("Knight", 150f * hpBonus, if (isArmorLevel5 || isArmorLevel2) ArmorType.HEAVY_ARMOR else ArmorType.MEDIUM_ARMOR, 6f, 10f, 1.36f, 10f)
            else -> Septuple("Militia", 50f * hpBonus, if (isArmorLevel5 || isArmorLevel2) ArmorType.LIGHT_ARMOR else ArmorType.NONE, 1f, 3f, 1.0f, 5f)
        }

        val offsets = listOf(Vector2(-14f, 0f), Vector2(14f, 8f), Vector2(0f, -12f))
        for (off in offsets) {
            val sPos = Vector2(rallyPoint).add(off)
            val soldier = Entity().apply {
                add(TransformComponent(position = Vector2(sPos)))
                add(HealthComponent(maxHealth = hpMax, currentHealth = hpMax))
                add(ArmorComponent(armorType = aType))
                add(SoldierComponent(
                    attackDamageMin = dMin,
                    attackDamageMax = dMax,
                    attackRate = aRate,
                    regenPerSec = reg,
                    targetPosition = Vector2(sPos),
                    parentTower = barracks,
                    soldierRankName = rName,
                    moveSpeed = 55f
                ))
            }
            bComp.soldiers.add(soldier)
            engine.addEntity(soldier)
        }
    }

    private fun getNearestRoadPoint(pos: Vector2): Vector2 {
        var bestPoint = if (currentLevel.allPaths.isNotEmpty() && currentLevel.allPaths[0].isNotEmpty()) currentLevel.allPaths[0][0] else Vector2()
        var minDst = Float.MAX_VALUE

        for (wps in currentLevel.allPaths) {
            for (i in 0 until wps.size - 1) {
                val a = wps[i]
                val b = wps[i + 1]

                val abX = b.x - a.x; val abY = b.y - a.y
                val apX = pos.x - a.x; val apY = pos.y - a.y
                val abLenSq = abX * abX + abY * abY

                val projPoint = if (abLenSq == 0f) {
                    Vector2(a)
                } else {
                    val t = ((apX * abX + apY * abY) / abLenSq).coerceIn(0f, 1f)
                    Vector2(a.x + t * abX, a.y + t * abY)
                }

                val d = pos.dst(projPoint)
                if (d < minDst) {
                    minDst = d
                    bestPoint = projPoint
                }
            }
        }
        return bestPoint
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.36f, 0.62f, 0.22f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined

        if (!isGamePaused) {
            if (rainOfFireCooldown > 0f) rainOfFireCooldown -= delta
            if (reinforcementCooldown > 0f) reinforcementCooldown -= delta

            val allEnemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get())
            for (i in 0 until allEnemies.size()) {
                val ec = allEnemies.get(i).getComponent(EnemyComponent::class.java)
                if (ec != null && ec.burnDuration > 0f) {
                    ec.burnDuration -= delta
                }
            }

            effectRenderer.update(delta) { px, py, radius, tickDmg ->
                val enemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java, TransformComponent::class.java, HealthComponent::class.java).get())
                for (e in enemies) {
                    val ePos = e.getComponent(TransformComponent::class.java).position
                    val hp = e.getComponent(HealthComponent::class.java)
                    val eComp = e.getComponent(EnemyComponent::class.java)
                    if (hp != null && !hp.isDead && ePos.dst(px, py) <= radius) {
                        hp.currentHealth -= tickDmg
                        eComp?.burnDuration = 0.8f
                        if (hp.currentHealth <= 0f) {
                            if (eComp != null && !eComp.isSummonedMinion) EconomyManager.addGold(eComp.enemyType.rewardGold)
                            e.getComponent(MovementComponent::class.java)?.isBlocked = false
                            engine.removeEntity(e)
                        }
                    }
                }
            }

            if (!EconomyManager.isGameOver && !waveManager.isLevelCompleted) {
                waveManager.update(delta)
                engine.update(delta)

                // CƠ CHẾ NGHĨA ĐỊA: GIÃN CÁCH XUẤT XƯƠNG (1.8s/con, TỐI ĐA 5 con alive)
                if (graveyardQueue.isNotEmpty() && activeGraveyardSkeletons < 5) {
                    graveyardCooldown -= delta
                    if (graveyardCooldown <= 0f) {
                        graveyardCooldown = 1.8f
                        val nextType = graveyardQueue.removeAt(0)
                        spawnSingleSkeletonFromGraveyard(nextType)
                    }
                }

                // CƠ CHẾ MAGMA FIELD MÀN 11 CHUẨN THEO NOTE WIKI
                if (currentLevel.levelNumber == 11 && waveManager.currentWaveIndex >= 0) {
                    magmaFieldTotalTime += delta
                    if (magmaFieldTotalTime >= 400f) {
                        magmaFieldSpawnTimer -= delta
                        if (magmaFieldSpawnTimer <= 0f) {
                            magmaFieldSpawnTimer = 120f
                            val mPos = Level11ForsakenValley.magmaFieldPosition
                            val mPath = Level11ForsakenValley.pathMagmaRight

                            val titan = Entity().apply {
                                add(TransformComponent(position = Vector2(mPos)))
                                val maxHp = EnemyType.MAGMA_ELEMENTAL.getMaxHp(difficulty)
                                add(HealthComponent(maxHealth = maxHp, currentHealth = maxHp))
                                add(ArmorComponent(armorType = EnemyType.MAGMA_ELEMENTAL.armorType, magicResistType = EnemyType.MAGMA_ELEMENTAL.magicResist))
                                add(EnemyComponent(enemyType = EnemyType.MAGMA_ELEMENTAL, isSummonedMinion = false))
                                add(MovementComponent(
                                    speed = EnemyType.MAGMA_ELEMENTAL.speed,
                                    waypoints = mPath.toMutableList(),
                                    isInsideTunnel = true,
                                    tunnelTimer = 2.0f
                                ))
                            }
                            engine.addEntity(titan)
                            effectRenderer.shrapnelExplosions.add(ShrapnelExplosionEffect(Vector2(mPos)))
                        }
                    }
                }

                // CƠ CHẾ ĐẶC BIỆT MÀN 12: THE DARK TOWER
                if (currentLevel.levelNumber == 12 && waveManager.currentWaveIndex >= 0) {
                    val currentWaveNum = waveManager.currentWaveDisplay

                    val veznanEntity = engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get()).firstOrNull {
                        val ec = it.getComponent(EnemyComponent::class.java)
                        ec?.enemyType == EnemyType.VEZNAN
                    }
                    val veznanMove = veznanEntity?.getComponent(MovementComponent::class.java)

                    // 1. CẬP NHẬT CÁC THÁP ĐANG BỊ CẢNH BÁO (2s niệm phép: chặn wheel, vẫn bắn được)
                    val warnIterator = warningTowersMap.entries.iterator()
                    while (warnIterator.hasNext()) {
                        val entry = warnIterator.next()
                        val tower = entry.key
                        val newTimer = entry.value - delta
                        if (newTimer <= 0f) {
                            warnIterator.remove()
                            val tComp = tower.getComponent(TowerComponent::class.java)
                            if (tComp != null) {
                                tComp.isFrozen = true
                                tComp.freezeHitsRemaining = 4
                                lockedTowersMap[tower] = LockedTowerState()
                                effectRenderer.magicSparks.add(MagicSparkEffect(Vector2(tower.getComponent(TransformComponent::class.java).position)))
                            }
                        } else {
                            entry.setValue(newTimer)
                        }
                    }

                    // 2. CẬP NHẬT CÁC THÁP ĐÃ BỊ KHÓA HOÀN TOÀN (5s đầu cho mở -> 5s sau phạt cấm mở)
                    val lockIterator = lockedTowersMap.entries.iterator()
                    while (lockIterator.hasNext()) {
                        val entry = lockIterator.next()
                        val tower = entry.key
                        val state = entry.value
                        val tComp = tower.getComponent(TowerComponent::class.java)

                        if (tComp == null || !tComp.isFrozen) {
                            lockIterator.remove()
                            continue
                        }

                        if (!state.isUndispellable) {
                            state.initialTimer -= delta
                            if (state.initialTimer <= 0f) {
                                state.isUndispellable = true
                                tComp.freezeHitsRemaining = 0
                                effectRenderer.magicSparks.add(MagicSparkEffect(Vector2(tower.getComponent(TransformComponent::class.java).position)))
                            }
                        } else {
                            state.penaltyTimer -= delta
                            if (state.penaltyTimer <= 0f) {
                                tComp.isFrozen = false
                                lockIterator.remove()
                            }
                        }
                    }

                    // 3. VEZ'NAN TRÊN BAN CÔNG KHÓA THÁP TỪ WAVE 9 ĐỊNH KỲ MỖI 16s
                    if (currentWaveNum >= 9) {
                        veznanTowerLockCooldown -= delta
                        if (veznanTowerLockCooldown <= 0f) {
                            veznanTowerLockCooldown = 16.0f
                            val towersToDisableCount = when (currentWaveNum) {
                                9 -> 2; 10 -> 3; 11 -> 4; 12 -> 5; 13 -> 6; 14 -> 7; else -> 8
                            }

                            val availableTowers = engine.getEntitiesFor(Family.all(TowerComponent::class.java).get()).filter {
                                val tc = it.getComponent(TowerComponent::class.java)
                                tc != null && !tc.isFrozen && !warningTowersMap.containsKey(it)
                            }

                            val chosenTowers = availableTowers.shuffled().take(towersToDisableCount)
                            for (tw in chosenTowers) {
                                warningTowersMap[tw] = 2.0f // 2s niệm phép cảnh báo
                                effectRenderer.magicSparks.add(MagicSparkEffect(Vector2(tw.getComponent(TransformComponent::class.java).position)))
                            }
                        }
                    }

                    // 4. TRIỆU HỒI TẠI 3 VÒNG TRÒN MA THUẬT TỪ WAVE 6
                    val maxSummonsThisWave = if (currentWaveNum == 15) 2 else 3
                    val summonCooldownDuration = if (currentWaveNum == 15) 18.0f else 10.0f

                    if (currentWaveNum >= 6) {
                        if (lastRecordedWaveForSummon != currentWaveNum) {
                            lastRecordedWaveForSummon = currentWaveNum
                            veznanSummonCountThisWave = 0
                            veznanSummonTimer = 10.0f
                        }

                        if (veznanSummonCountThisWave < maxSummonsThisWave) {
                            veznanSummonTimer -= delta
                            if (veznanSummonTimer <= 0f) {
                                veznanSummonTimer = summonCooldownDuration
                                veznanSummonCountThisWave++

                                val cTop = Level12TheDarkTower.summoningCircleTop
                                val cCenter = Level12TheDarkTower.summoningCircleCenter
                                val cBottom = Level12TheDarkTower.summoningCircleBottom

                                when {
                                    currentWaveNum in 6..7 -> glowingCircles.add(Pair(if (MathUtils.randomBoolean()) cTop else cCenter, 2.5f))
                                    currentWaveNum in 8..11 -> { glowingCircles.add(Pair(cTop, 2.5f)); glowingCircles.add(Pair(cCenter, 2.5f)) }
                                    currentWaveNum in 12..13 -> glowingCircles.add(Pair(cBottom, 2.5f))
                                    currentWaveNum >= 14 -> { glowingCircles.add(Pair(cTop, 2.5f)); glowingCircles.add(Pair(cCenter, 2.5f)); glowingCircles.add(Pair(cBottom, 2.5f)) }
                                }
                            }
                        }
                    }

                    // BOSS DỪNG LẠI NIỆM PHÉP KHI CÓ CỔNG MA THUẬT HOẶC KHÓA THÁP
                    val isBossCastingSpell = glowingCircles.isNotEmpty() || warningTowersMap.isNotEmpty()
                    if (veznanMove != null) {
                        veznanMove.isBlocked = isBossCastingSpell
                    }

                    // CẬP NHẬT VÒNG TRÒN PHÁT SÁNG NẠP NĂNG LƯỢNG (2.5S)
                    val circleIterator = glowingCircles.listIterator()
                    while (circleIterator.hasNext()) {
                        val item = circleIterator.next()
                        val pos = item.first
                        val newTime = item.second - delta
                        if (newTime <= 0f) {
                            circleIterator.remove()
                            when (pos) {
                                Level12TheDarkTower.summoningCircleTop -> queueCircleDemons(pos, listOf(Pair(EnemyType.DEMON_SPAWN, 6), Pair(EnemyType.DEMON_HOUND, 3)))
                                Level12TheDarkTower.summoningCircleCenter -> queueCircleDemons(pos, listOf(Pair(EnemyType.DEMON_SPAWN, 3), Pair(EnemyType.DEMON_HOUND, 2)))
                                Level12TheDarkTower.summoningCircleBottom -> queueCircleDemons(pos, listOf(Pair(EnemyType.DEMON_SPAWN, 3)))
                            }
                        } else {
                            circleIterator.set(Pair(pos, newTime))
                        }
                    }

                    // XỬ LÝ HÀNG ĐỢI XUẤT QUỶ TỪNG CON MỘT CÓ GIÃN CÁCH
                    val portalIterator = portalSpawnQueue.listIterator()
                    while (portalIterator.hasNext()) {
                        val spawnItem = portalIterator.next()
                        spawnItem.delay -= delta
                        if (spawnItem.delay <= 0f) {
                            spawnSinglePortalDemon(spawnItem.circlePos, spawnItem.type)
                            portalIterator.remove()
                        }
                    }

                    // 5. TRÙM CUỐI VEZ'NAN HẠ ĐÀI BƯỚC XUỐNG THÁP SAU WAVE 15
                    if (currentWaveNum == 15 && !veznanBossSteppedDown && waveManager.currentWaveIndex >= 14) {
                        val aliveNonBoss = engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get()).filter {
                            val ec = it.getComponent(EnemyComponent::class.java)
                            ec?.enemyType != EnemyType.VEZNAN && ec?.enemyType != EnemyType.VEZNAN_DEMON
                        }
                        if (aliveNonBoss.size <= 4) {
                            veznanBossSteppedDown = true
                            Level12MapRenderer.isVeznanOnBalcony = false

                            val bPos = Level12TheDarkTower.balconyPosition
                            val startP = Level12TheDarkTower.pathNorthwest

                            effectRenderer.shrapnelExplosions.add(ShrapnelExplosionEffect(Vector2(bPos)))
                            val veznanBoss = Entity().apply {
                                add(TransformComponent(position = Vector2(startP.first())))
                                val maxHp = EnemyType.VEZNAN.getMaxHp(difficulty)
                                add(HealthComponent(maxHealth = maxHp, currentHealth = maxHp))
                                add(ArmorComponent(armorType = EnemyType.VEZNAN.armorType, magicResistType = EnemyType.VEZNAN.magicResist))
                                add(EnemyComponent(enemyType = EnemyType.VEZNAN))
                                add(MovementComponent(
                                    speed = 20f, // TỐC ĐỘ 20f CHẬM RÃI VÀ UY NGHIÊM
                                    waypoints = startP.toMutableList()
                                ))
                            }
                            engine.addEntity(veznanBoss)
                        }
                    }
                }

                val enemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java, TransformComponent::class.java).get())
                for (e in enemies) {
                    val eComp = e.getComponent(EnemyComponent::class.java)
                    if (eComp?.enemyType == EnemyType.SPIDER_MATRIARCH) {
                        eComp.eggLayTimer -= delta
                        if (eComp.eggLayTimer <= 0f) {
                            eComp.eggLayTimer = 10.0f
                            val pos = e.getComponent(TransformComponent::class.java).position
                            val move = e.getComponent(MovementComponent::class.java)

                            val egg = Entity().apply {
                                add(TransformComponent(position = Vector2(pos)))
                                add(SpiderEggComponent(
                                    hatchTimer = 8.0f,
                                    waypointsRemaining = move?.waypoints?.drop(move.currentWaypointIndex)?.toList() ?: emptyList()
                                ))
                            }
                            engine.addEntity(egg)
                        }
                    }
                }

                val sTower = engine.getEntitiesFor(Family.all(TowerComponent::class.java).get()).firstOrNull {
                    it.getComponent(TowerComponent::class.java)?.specialization == TowerSpecialization.SUNRAY_TOWER
                }
                sTower?.let { st ->
                    val sc = st.getComponent(TowerComponent::class.java)
                    if (sc.sunraySorcerers > 0 && !sc.isSunrayReady) {
                        sc.sunrayChargeTimer -= delta
                        if (sc.sunrayChargeTimer <= 0f) {
                            sc.sunrayChargeTimer = 0f
                            sc.isSunrayReady = true
                        }
                    }
                }

                // XỬ LÝ CHIÊU THỨC CỦA BOSS J.T. & VEZ'NAN TRÊN SÂN ĐẤU
                val allBosses = engine.getEntitiesFor(Family.all(EnemyComponent::class.java, TransformComponent::class.java).get())
                for (bIdx in 0 until allBosses.size()) {
                    val boss = allBosses.get(bIdx)
                    val bComp = boss.getComponent(EnemyComponent::class.java)
                    val bTrans = boss.getComponent(TransformComponent::class.java)
                    val bMove = boss.getComponent(MovementComponent::class.java)

                    // 1. BOSS J.T.
                    if (bComp?.enemyType == EnemyType.JT && bTrans != null) {
                        if (bComp.jtRestingTimer > 0f) {
                            bComp.jtRestingTimer -= delta
                            bMove?.isBlocked = true
                        } else {
                            bMove?.isBlocked = false
                        }

                        val soldiers = engine.getEntitiesFor(Family.all(SoldierComponent::class.java, TransformComponent::class.java).get())
                        for (sIdx in 0 until soldiers.size()) {
                            val sol = soldiers.get(sIdx)
                            val sTrans = sol.getComponent(TransformComponent::class.java)
                            val sHp = sol.getComponent(HealthComponent::class.java)
                            if (sTrans != null && sHp != null && !sHp.isDead && sTrans.position.dst(bTrans.position) <= 32f) {
                                sHp.currentHealth = 0f
                            }
                        }

                        bComp.jtFreezeCooldown -= delta
                        if (bComp.jtFreezeCooldown <= 0f) {
                            var frozeAny = false
                            val towers = engine.getEntitiesFor(Family.all(TowerComponent::class.java, TransformComponent::class.java).get())
                            for (tIdx in 0 until towers.size()) {
                                val tw = towers.get(tIdx)
                                val tc = tw.getComponent(TowerComponent::class.java)
                                val tp = tw.getComponent(TransformComponent::class.java)?.position
                                if (tc != null && tp != null && !tc.isFrozen && tc.specialization != TowerSpecialization.SUNRAY_TOWER && tp.dst(bTrans.position) <= 145f) {
                                    tc.isFrozen = true
                                    tc.freezeHitsRemaining = 4
                                    frozeAny = true
                                }
                            }

                            if (frozeAny) {
                                effectRenderer.shrapnelExplosions.add(ShrapnelExplosionEffect(Vector2(bTrans.position)))
                                bComp.jtRestingTimer = 3.5f
                                bComp.jtFreezeCooldown = 12f
                            }
                        }
                    }

                    // 2. BOSS VEZ'NAN DẠNG 2: ĐẠI QUỶ ĐỎ THIÊU RỤI LÍNH DIỆN RỘNG
                    if (bComp?.enemyType == EnemyType.VEZNAN_DEMON && bTrans != null) {
                        val soldiers = engine.getEntitiesFor(Family.all(SoldierComponent::class.java, TransformComponent::class.java).get())
                        for (sIdx in 0 until soldiers.size()) {
                            val sol = soldiers.get(sIdx)
                            val sTrans = sol.getComponent(TransformComponent::class.java)
                            val sHp = sol.getComponent(HealthComponent::class.java)
                            if (sTrans != null && sHp != null && !sHp.isDead && sTrans.position.dst(bTrans.position) <= 45f) {
                                sHp.currentHealth = 0f
                            }
                        }
                    }
                }

                val towers = engine.getEntitiesFor(Family.all(TowerComponent::class.java).get())
                for (t in towers) {
                    val tComp = t.getComponent(TowerComponent::class.java)
                    if (tComp?.specialization == TowerSpecialization.SORCERER_MAGE && tComp.skill2Level > 0) {
                        val golem = tComp.summonedGolem
                        val gHp = golem?.getComponent(HealthComponent::class.java)
                        if (golem == null || gHp == null || gHp.isDead || gHp.currentHealth <= 0f) {
                            if (tComp.skill2Cooldown > 0f) {
                                tComp.skill2Cooldown -= delta
                            } else {
                                val golemHp = 500f + tComp.skill2Level * 200f
                                val spawnPos = if (tComp.golemRallyPoint.isZero) getNearestRoadPoint(t.getComponent(TransformComponent::class.java).position) else Vector2(tComp.golemRallyPoint)
                                val newGolem = Entity().apply {
                                    add(TransformComponent(position = Vector2(spawnPos)))
                                    add(HealthComponent(maxHealth = golemHp, currentHealth = golemHp))
                                    add(ArmorComponent(armorType = ArmorType.HEAVY_ARMOR))
                                    add(SoldierComponent(
                                        attackDamageMin = 30f + tComp.skill2Level * 15f,
                                        attackDamageMax = 50f + tComp.skill2Level * 25f,
                                        attackRate = 1.35f,
                                        regenPerSec = 20f,
                                        targetPosition = Vector2(spawnPos),
                                        parentTower = t,
                                        soldierRankName = "Earth Elemental",
                                        moveSpeed = 48f
                                    ))
                                }
                                tComp.summonedGolem = newGolem
                                tComp.skill2Cooldown = 10f
                                engine.addEntity(newGolem)
                            }
                        }
                    }
                }
            }
        }

        MapRenderer.render(shapeRenderer, currentLevel)

        // VẼ HIỆU ỨNG VÒNG TRÒN MA THUẬT PHÁT SÁNG KHI ĐANG NẠP NĂNG LƯỢNG TRIỆU HỒI (MÀN 12)
        if (glowingCircles.isNotEmpty()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            for (circle in glowingCircles) {
                val cPos = circle.first
                shapeRenderer.color = Color(0.75f, 0.20f, 0.95f, 0.45f)
                shapeRenderer.circle(cPos.x, cPos.y, 40f)
                shapeRenderer.color = Color(1f, 0.85f, 0.20f, 0.65f)
                shapeRenderer.circle(cPos.x, cPos.y, 24f)
            }
            shapeRenderer.end()
        }

        effectRenderer.renderGroundPatches(shapeRenderer)
        renderEntities()
        effectRenderer.render(shapeRenderer)

        GameUIRenderer.renderEarlyCallWaveButtons(
            shapeRenderer, batch, font, smallFont, waveManager, currentLevel.allPaths
        )

        val maxLevel = when {
            currentLevel.levelNumber == 0 -> 4
            currentLevel.levelNumber == 5 -> if (waveManager.currentWaveDisplay >= 5) 4 else 3
            currentLevel.levelNumber >= 6 -> 4
            else -> currentLevel.maxTowerLevel
        }

        GameUIRenderer.renderWheelMenu(
            shapeRenderer, batch, smallFont,
            selectedSpot, selectedTower, previewBuildType, previewTowerAction,
            isSettingRallyPoint, maxRallyRadius, buildOptions, ::getBranchSpecs,
            maxLevelAllowed = maxLevel,
            allowedTowers = allowedTowers,
            currentLevelNumber = currentLevel.levelNumber,
            isPreGame = (waveManager.currentWaveIndex == -1)
        )

        GameUIRenderer.renderSpellUI(
            shapeRenderer, batch, font, smallFont,
            selectedSpell, rainOfFireCooldown, reinforcementCooldown
        )

        GameUIRenderer.renderBottomInfoBar(
            shapeRenderer, batch, font, smallFont,
            selectedEnemy, selectedSoldier, selectedTower,
            onClearEnemySelection = { selectedEnemy = null },
            onClearSoldierSelection = { selectedSoldier = null }
        )

        GameUIRenderer.renderHUD(
            shapeRenderer, batch, font, waveManager, isSettingRallyPoint, isGamePaused
        )

        if (isAimingSunrayTarget) {
            batch.begin()
            drawOutlinedText(
                batch, font,
                "SELECT AN ENEMY TO FIRE SOLAR BEAM!",
                Config.WORLD_WIDTH / 2f - 190f, 65f, Color.YELLOW
            )
            batch.end()
        }

        if (isGamePaused) {
            GameUIRenderer.renderPauseMenu(shapeRenderer, batch, font, smallFont)
        } else {
            if (CheatManager.isCheatEnabled) {
                GameUIRenderer.renderCheatFloatingButton(shapeRenderer, batch, font)
                if (CheatManager.isCheatMenuOpen) {
                    GameUIRenderer.renderCheatMenuPopup(shapeRenderer, batch, font, smallFont)
                }
            }
        }

        if (waveManager.isLevelCompleted && (!veznanBossSteppedDown || engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get()).size() == 0)) {
            val earnedStars = if (gameMode == GameMode.CAMPAIGN) {
                when {
                    EconomyManager.lives >= 18 -> 3
                    EconomyManager.lives >= 6 -> 2
                    EconomyManager.lives >= 1 -> 1
                    else -> 0
                }
            } else {
                1
            }
            GameUIRenderer.renderVictoryDialog(
                shapeRenderer, batch, font, smallFont, earnedStars,
                isChallengeMode = (gameMode != GameMode.CAMPAIGN)
            )
        }
    }

    private fun renderEntities() {
        val spots = engine.getEntitiesFor(Family.all(TowerSpotComponent::class.java, TransformComponent::class.java).get())
        val towers = engine.getEntitiesFor(Family.all(TowerComponent::class.java, TransformComponent::class.java).get())
        val enemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java, TransformComponent::class.java).get())
        val eggs = engine.getEntitiesFor(Family.all(SpiderEggComponent::class.java, TransformComponent::class.java).get())
        val soldiers = engine.getEntitiesFor(Family.all(SoldierComponent::class.java, TransformComponent::class.java).get())
        val projectiles = engine.getEntitiesFor(Family.all(ProjectileComponent::class.java, TransformComponent::class.java).get())

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (s in spots) {
            if (!s.getComponent(TowerSpotComponent::class.java).isOccupied) {
                val p = s.getComponent(TransformComponent::class.java).position
                shapeRenderer.color = Color(0.48f, 0.45f, 0.38f, 1f)
                shapeRenderer.circle(p.x, p.y, 25f)
                shapeRenderer.color = Color(0.62f, 0.58f, 0.48f, 1f)
                shapeRenderer.circle(p.x, p.y, 19f)
            }
        }

        for (egg in eggs) {
            EnemyRenderer.renderEgg(shapeRenderer, egg)
        }

        for (t in towers) {
            TowerRenderer.render(shapeRenderer, t)

            val tPos = t.getComponent(TransformComponent::class.java)?.position
            val tComp = t.getComponent(TowerComponent::class.java)

            // VẼ HIỆU ỨNG CẢNH BÁO SẮP BỊ KHÓA (VẪN BẮN ĐƯỢC, CHẶN WHEEL)
            if (warningTowersMap.containsKey(t) && tPos != null) {
                shapeRenderer.color = Color(0.95f, 0.35f, 1.0f, 0.35f)
                shapeRenderer.circle(tPos.x, tPos.y, 38f)
            }

            // HIỆU ỨNG PHONG ẤN THÁP CỦA VEZ'NAN KHI ĐÃ BỊ KHÓA HOÀN TOÀN
            if (tComp != null && tComp.isFrozen && tPos != null) {
                val lockState = lockedTowersMap[t]
                if (lockState != null && lockState.isUndispellable) {
                    shapeRenderer.color = Color(0.65f, 0.15f, 0.85f, 0.70f) // Tím đậm cấm mở
                } else {
                    shapeRenderer.color = Color(0.85f, 0.25f, 0.95f, 0.50f) // Tím cho phép click mở
                }
                shapeRenderer.circle(tPos.x, tPos.y, 34f)
                shapeRenderer.color = Color(1f, 0.85f, 0.25f, 0.85f)
                shapeRenderer.circle(tPos.x, tPos.y, 16f)
            }
        }

        for (sol in soldiers) {
            SoldierRenderer.render(shapeRenderer, sol)
        }

        for (e in enemies) {
            val move = e.getComponent(MovementComponent::class.java)
            if (move?.isInsideTunnel == true) continue
            val eComp = e.getComponent(EnemyComponent::class.java)
            val eTrans = e.getComponent(TransformComponent::class.java)

            // VẼ HÀO QUANG KHI BOSS VEZ'NAN ĐỨNG YÊN NIỆM CHÚ
            if (eComp?.enemyType == EnemyType.VEZNAN && eTrans != null) {
                val isBossCasting = glowingCircles.isNotEmpty() || warningTowersMap.isNotEmpty()
                if (isBossCasting) {
                    shapeRenderer.color = Color(0.85f, 0.20f, 0.95f, 0.40f)
                    shapeRenderer.circle(eTrans.position.x, eTrans.position.y, 32f)
                }
            }

            EnemyRenderer.render(shapeRenderer, e)
        }

        for (proj in projectiles) {
            val p = proj.getComponent(TransformComponent::class.java).position
            val pComp = proj.getComponent(ProjectileComponent::class.java)
            shapeRenderer.color = pComp.color
            when (pComp.type) {
                ProjectileType.ARROW -> shapeRenderer.rect(p.x - 2f, p.y - 2f, 8f, 4f)
                ProjectileType.MAGIC_BOLT -> shapeRenderer.circle(p.x, p.y, 6.5f)
                ProjectileType.BOMB -> shapeRenderer.circle(p.x, p.y, 9.5f)
            }
        }
        shapeRenderer.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        font.dispose()
        smallFont.dispose()
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
    private data class Quintuple<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)
    private data class Septuple<A, B, C, D, E, F, G>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E, val sixth: F, val seventh: G)
}

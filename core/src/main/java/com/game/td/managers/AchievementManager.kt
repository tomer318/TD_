package com.game.td.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.game.td.ecs.components.EnemyType
import com.game.td.ecs.components.TowerSpecialization
import com.game.td.ecs.components.TowerType

enum class AchievementId(
    val title: String,
    val description: String,
    val maxProgress: Int = 1,
    val page: Int = 1,
    val showProgressBar: Boolean = maxProgress > 1
) {
    // ================= TRANG 1 (14 THÀNH TỰU) =================
    FIRST_BLOOD("FIRST BLOOD", "Defeat one enemy.", 1, 1, false),
    DARING("DARING", "Call 10 early waves.", 10, 1, true),
    CONSTRUCTOR("CONSTRUCTOR", "Build 30 towers.", 30, 1, true),
    BLOODLUST("BLOODLUST", "Defeat 500 enemies.", 500, 1, true),
    ARMAGGEDON("ARMAGGEDON", "Use Rain of Fire 5 times in a single stage.", 5, 1, true),
    HOME_IMPROVEMENT("HOME IMPROVEMENT", "Upgrade all basic tower types to level 3.", 4, 1, true),
    STARRY("STARRY", "Earn 15 stars.", 15, 1, true),
    WHATS_THAT("WHATS THAT?", "Open 10 enemy information cards.", 10, 1, true),
    SUPERMARIO("SUPERMARIO", "Earn 30 stars.", 30, 1, true),
    NUTS_AND_BOLTS("NUTS AND BOLTS", "Defeat The Juggernaut.", 1, 1, false),
    ENGINEER("ENGINEER", "Build 100 towers.", 100, 1, true),
    IS_HE_DEAD_YETI("IS HE DEAD YETI?", "Defeat J.T.", 1, 1, false),
    SLAYER("SLAYER", "Defeat 2500 enemies.", 2500, 1, true),
    DEATH_FROM_ABOVE("DEATH FROM ABOVE", "Defeat 100 enemies with meteor shower.", 100, 1, true),

    // ================= TRANG 2 (14 THÀNH TỰU - ĐÃ BỎ LIKE A HENDERSON) =================
    TACTICIAN("TACTICIAN", "Change soldiers rally point 200 times.", 200, 2, true),
    SUPERSTAR("SUPERSTAR", "Earn 45 stars.", 45, 2, true),
    THE_ARCHITECT("THE ARCHITECT", "Build 150 towers.", 150, 2, true),
    THIS_IS_THE_END("THIS IS THE END!", "Defeat Vez'nan.", 1, 2, false),
    TERMINATOR("TERMINATOR", "Defeat 10.000 enemies.", 10000, 2, true),
    DIE_HARD("DIE HARD", "Have your soldiers regenerate a total of 50.000 life.", 50000, 2, true),
    GI_JOE("G.I JOE", "Train 1.000 soldiers.", 1000, 2, true),
    CANNON_FODDER("CANNON FODDER", "Send 1.000 soldiers to their deaths.", 1000, 2, true),
    FEARLESS("FEARLESS", "Call all waves early in a single mission.", 1, 2, false),
    REAL_ESTATE("REAL ESTATE", "Sell 30 Towers.", 30, 2, true),
    INDECISIVE("INDECISIVE", "Sell 5 towers in a single mission.", 5, 2, true),
    IMPATIENT("IMPATIENT", "Call an early wave within 3 seconds of the icon showing up.", 1, 2, false),
    FOREST_DIPLOMACY("FOREST DIPLOMACY", "Recruit max elves at The Silveroak Outpost.", 4, 2, true),
    SUNBURNER("SUNBURNER!", "Fire the sunray 20 times.", 20, 2, true),

    // ================= TRANG 3 (14 THÀNH TỰU) =================
    IMPERIAL_SAVIOUR("IMPERIAL SAVIOUR", "Complete The Citadel with at least 3 surviving imperial guards.", 1, 3, false),
    SPECIALIST("SPECIALIST", "Build all 8 tower specializations.", 8, 3, true),
    FIFTY_SHOTS_FIFTY_KILLS("50 SHOTS 50 KILLS", "Snipe 50 enemies.", 50, 3, true),
    TOXICITY("TOXICITY", "Defeat 50 enemies by poison damage.", 50, 3, true),
    ENTANGLED("ENTANGLED", "Hold 500 or more enemies with Wrath of the Forest.", 500, 3, true),
    DUST_TO_DUST("DUST TO DUST", "Disintegrate 50 or more enemies.", 50, 3, true),
    BEAM_ME_UP_SCOTTY("BEAM ME UP SCOTTY", "Teleport 250 or more enemies.", 250, 3, true),
    SHEPHERD("SHEPHERD", "Polymorph 50 enemies into sheeps.", 50, 3, true),
    ELEMENTALIST("ELEMENTALIST", "Summon 5 rock elementals in any one stage.", 5, 3, true),
    AXE_RAIN("AXE RAIN", "Throw 500 or more axes.", 500, 3, true),
    ARE_YOU_NOT_ENTERTAINED("ARE YOU NOT ENTERTAINED?", "Have a single barbarian defeat 10 enemies.", 10, 3, true),
    MEDIC("MEDIC", "Have your Paladins heal a total of 7.000 life.", 7000, 3, true),
    HOLY_CHORUS("HOLY CHORUS", "Have your Paladins perform 100 Holy Strikes.", 100, 3, true),
    ROCKETEER("ROCKETEER", "Shoot 100 Missiles.", 100, 3, true),

    // ================= TRANG 4 (6 THÀNH TỰU) =================
    CLUSTERED("CLUSTERED", "Drop 1.000 or more bomblets with the cluster bomb.", 1000, 4, true),
    ENERGY_NETWORK("ENERGY NETWORK", "Build 4 Tesla towers in any stage.", 4, 4, true),
    AC_DC("AC/DC", "Defeat 300 enemies with electricity.", 300, 4, true),
    GREAT_DEFENDER("GREAT DEFENDER", "Complete all campaign stages in Normal difficulty.", 12, 4, true),
    HEROIC_DEFENDER("HEROIC DEFENDER", "Complete all Heroic stages in Normal difficulty.", 12, 4, true),
    IRON_DEFENDER("IRON DEFENDER", "Complete all Iron stages in Normal difficulty.", 12, 4, true)
}

object AchievementManager {

    private const val KEY_UNLOCKED = "ach_unlocked_set"
    private const val KEY_SPECS_BUILT = "ach_specs_built_set"
    private const val KEY_LVL3_BUILT = "ach_lvl3_built_set"

    private var loadedSlot: Int = -1
    private val unlockedSet = mutableSetOf<String>()
    private val progressMap = mutableMapOf<AchievementId, Int>()
    private val builtSpecsSet = mutableSetOf<String>()
    private val builtLvl3TypesSet = mutableSetOf<String>()

    private var stageRainOfFireCount = 0
    private var stageTowersSoldCount = 0
    private var stageEarlyWavesCalled = 0
    private var stageElementalsSummoned = 0
    private var regenAccumulator = 0f

    private val toastQueue = mutableListOf<AchievementId>()
    var activeToast: AchievementId? = null
        private set
    var toastTimer: Float = 0f
        private set
    const val TOAST_TOTAL_DURATION = 3.4f

    private fun getSlotPrefs(): Preferences {
        return Gdx.app.getPreferences("kr_save_slot_${SaveManager.activeSlot}")
    }

    private fun ensureSlotSynced() {
        if (loadedSlot != SaveManager.activeSlot) {
            loadForCurrentSlot()
        }
    }

    fun loadForCurrentSlot() {
        loadedSlot = SaveManager.activeSlot
        unlockedSet.clear()
        progressMap.clear()
        builtSpecsSet.clear()
        builtLvl3TypesSet.clear()
        toastQueue.clear()
        activeToast = null
        toastTimer = 0f

        val prefs = getSlotPrefs()
        val savedUnlocked = prefs.getString(KEY_UNLOCKED, "")
        if (savedUnlocked.isNotBlank()) {
            unlockedSet.addAll(savedUnlocked.split(",").filter { it.isNotBlank() })
        }

        val savedSpecs = prefs.getString(KEY_SPECS_BUILT, "")
        if (savedSpecs.isNotBlank()) {
            builtSpecsSet.addAll(savedSpecs.split(",").filter { it.isNotBlank() })
        }

        val savedLvl3 = prefs.getString(KEY_LVL3_BUILT, "")
        if (savedLvl3.isNotBlank()) {
            builtLvl3TypesSet.addAll(savedLvl3.split(",").filter { it.isNotBlank() })
        }

        for (ach in AchievementId.values()) {
            val savedProg = prefs.getInteger("ach_prog_${ach.name}", 0)
            progressMap[ach] = if (unlockedSet.contains(ach.name)) ach.maxProgress else savedProg
        }

        syncWithSaveProgress(triggerToast = false)
    }

    fun resetForCurrentSlot() {
        loadedSlot = SaveManager.activeSlot
        unlockedSet.clear()
        progressMap.clear()
        builtSpecsSet.clear()
        builtLvl3TypesSet.clear()
        toastQueue.clear()
        activeToast = null
        toastTimer = 0f
        save()
    }

    private fun save() {
        val prefs = getSlotPrefs()
        prefs.putString(KEY_UNLOCKED, unlockedSet.joinToString(","))
        prefs.putString(KEY_SPECS_BUILT, builtSpecsSet.joinToString(","))
        prefs.putString(KEY_LVL3_BUILT, builtLvl3TypesSet.joinToString(","))
        for ((ach, prog) in progressMap) {
            prefs.putInteger("ach_prog_${ach.name}", prog)
        }
        prefs.flush()
    }

    fun isUnlocked(id: AchievementId): Boolean {
        ensureSlotSynced()
        return unlockedSet.contains(id.name)
    }

    fun getProgress(id: AchievementId): Int {
        ensureSlotSynced()
        if (unlockedSet.contains(id.name)) return id.maxProgress
        return (progressMap[id] ?: 0).coerceIn(0, id.maxProgress)
    }

    fun getAchievementsForPage(page: Int): List<AchievementId> {
        return AchievementId.values().filter { it.page == page }
    }

    private fun unlock(id: AchievementId, triggerToast: Boolean = true) {
        ensureSlotSynced()
        if (unlockedSet.add(id.name)) {
            progressMap[id] = id.maxProgress
            save()
            if (triggerToast) {
                toastQueue.add(id)
                if (activeToast == null) {
                    activeToast = toastQueue.removeAt(0)
                    toastTimer = TOAST_TOTAL_DURATION
                    SoundManager.playTowerUpgrade()
                }
            }
        }
    }

    private fun addProgress(id: AchievementId, amount: Int = 1, triggerToast: Boolean = true) {
        ensureSlotSynced()
        if (unlockedSet.contains(id.name)) return
        val next = ((progressMap[id] ?: 0) + amount).coerceAtMost(id.maxProgress)
        progressMap[id] = next
        if (next >= id.maxProgress) {
            unlock(id, triggerToast)
        } else {
            save()
        }
    }

    private fun setMaxProgress(id: AchievementId, value: Int, triggerToast: Boolean = true) {
        ensureSlotSynced()
        if (unlockedSet.contains(id.name)) return
        val current = progressMap[id] ?: 0
        if (value > current) {
            val clamped = value.coerceAtMost(id.maxProgress)
            progressMap[id] = clamped
            if (clamped >= id.maxProgress) {
                unlock(id, triggerToast)
            } else {
                save()
            }
        }
    }

    fun onStageStart() {
        ensureSlotSynced()
        stageRainOfFireCount = 0
        stageTowersSoldCount = 0
        stageEarlyWavesCalled = 0
        stageElementalsSummoned = 0
        regenAccumulator = 0f
    }

    fun updateToast(delta: Float) {
        if (activeToast != null) {
            toastTimer -= delta
            if (toastTimer <= 0f) {
                if (toastQueue.isNotEmpty()) {
                    activeToast = toastQueue.removeAt(0)
                    toastTimer = TOAST_TOTAL_DURATION
                    SoundManager.playTowerUpgrade()
                } else {
                    activeToast = null
                    toastTimer = 0f
                }
            }
        } else if (toastQueue.isNotEmpty()) {
            activeToast = toastQueue.removeAt(0)
            toastTimer = TOAST_TOTAL_DURATION
            SoundManager.playTowerUpgrade()
        }
    }

    fun syncWithSaveProgress(triggerToast: Boolean = true) {
        ensureSlotSynced()
        val totalStars = SaveManager.getTotalStars()
        setMaxProgress(AchievementId.STARRY, totalStars, triggerToast)
        setMaxProgress(AchievementId.SUPERMARIO, totalStars, triggerToast)
        setMaxProgress(AchievementId.SUPERSTAR, totalStars, triggerToast)

        var campCompleted = 0
        var heroicCompleted = 0
        var ironCompleted = 0
        for (lvl in 1..12) {
            if (SaveManager.getLevelStars(lvl) > 0) campCompleted++
            if (SaveManager.isHeroicCompleted(lvl)) heroicCompleted++
            if (SaveManager.isIronCompleted(lvl)) ironCompleted++
        }
        setMaxProgress(AchievementId.GREAT_DEFENDER, campCompleted, triggerToast)
        setMaxProgress(AchievementId.HEROIC_DEFENDER, heroicCompleted, triggerToast)
        setMaxProgress(AchievementId.IRON_DEFENDER, ironCompleted, triggerToast)
    }

    fun onEnemyKilled(enemyType: EnemyType) {
        unlock(AchievementId.FIRST_BLOOD)
        addProgress(AchievementId.BLOODLUST, 1)
        addProgress(AchievementId.SLAYER, 1)
        addProgress(AchievementId.TERMINATOR, 1)

        when (enemyType) {
            EnemyType.JUGGERNAUT -> unlock(AchievementId.NUTS_AND_BOLTS)
            EnemyType.JT -> unlock(AchievementId.IS_HE_DEAD_YETI)
            EnemyType.VEZNAN_DEMON -> unlock(AchievementId.THIS_IS_THE_END)
            else -> {}
        }
    }

    fun onEarlyWaveCalled(secondsSaved: Float, maxCountdownSeconds: Float, totalWaves: Int) {
        addProgress(AchievementId.DARING, 1)
        stageEarlyWavesCalled++

        if (secondsSaved > 0f && (maxCountdownSeconds - secondsSaved) <= 3.2f) {
            unlock(AchievementId.IMPATIENT)
        }

        if (totalWaves > 1 && stageEarlyWavesCalled >= totalWaves - 1) {
            unlock(AchievementId.FEARLESS)
        }
    }

    fun onTowerBuilt() {
        addProgress(AchievementId.CONSTRUCTOR, 1)
        addProgress(AchievementId.ENGINEER, 1)
        addProgress(AchievementId.THE_ARCHITECT, 1)
    }

    fun onTowerUpgraded(
        towerType: TowerType,
        newLevel: Int,
        specialization: TowerSpecialization,
        activeTeslaCountInStage: Int
    ) {
        if (newLevel == 3 && towerType in listOf(TowerType.ARCHER, TowerType.BARRACKS, TowerType.MAGE, TowerType.ARTILLERY)) {
            if (builtLvl3TypesSet.add(towerType.name)) {
                setMaxProgress(AchievementId.HOME_IMPROVEMENT, builtLvl3TypesSet.size)
            }
        }

        if (newLevel == 4 && specialization in listOf(
                TowerSpecialization.RANGERS_HIDEOUT,
                TowerSpecialization.MUSKETEER_GARRISON,
                TowerSpecialization.HOLY_ORDER,
                TowerSpecialization.BARBARIAN_MEAD_HALL,
                TowerSpecialization.ARCANE_WIZARD,
                TowerSpecialization.SORCERER_MAGE,
                TowerSpecialization.BIG_BERTHA,
                TowerSpecialization.TESLA_X104
            )
        ) {
            if (builtSpecsSet.add(specialization.name)) {
                setMaxProgress(AchievementId.SPECIALIST, builtSpecsSet.size)
            }
        }

        if (activeTeslaCountInStage > 0) {
            setMaxProgress(AchievementId.ENERGY_NETWORK, activeTeslaCountInStage)
        }
    }

    fun onTowerSold() {
        addProgress(AchievementId.REAL_ESTATE, 1)
        stageTowersSoldCount++
        setMaxProgress(AchievementId.INDECISIVE, stageTowersSoldCount)
    }

    fun onRainOfFireUsed() {
        stageRainOfFireCount++
        setMaxProgress(AchievementId.ARMAGGEDON, stageRainOfFireCount)
    }

    fun onMeteorKill() {
        addProgress(AchievementId.DEATH_FROM_ABOVE, 1)
    }

    fun onEnemyCardOpened() {
        addProgress(AchievementId.WHATS_THAT, 1)
    }

    fun onRallyPointChanged() {
        addProgress(AchievementId.TACTICIAN, 1)
    }

    fun onSoldierTrained() {
        addProgress(AchievementId.GI_JOE, 1)
    }

    fun onSoldierDied() {
        addProgress(AchievementId.CANNON_FODDER, 1)
    }

    fun onSoldierRegenerated(hpAmount: Float) {
        if (hpAmount <= 0f) return
        regenAccumulator += hpAmount
        val wholePoints = regenAccumulator.toInt()
        if (wholePoints >= 5) {
            regenAccumulator -= wholePoints
            addProgress(AchievementId.DIE_HARD, wholePoints)
        }
    }

    fun onSylvanElfCountChanged(currentElfCount: Int) {
        setMaxProgress(AchievementId.FOREST_DIPLOMACY, currentElfCount)
    }

    fun onSunrayFired() {
        addProgress(AchievementId.SUNBURNER, 1)
    }

    fun onCitadelCompleted(survivingImperialGuards: Int) {
        if (survivingImperialGuards >= 3) {
            unlock(AchievementId.IMPERIAL_SAVIOUR)
        }
    }

    fun onSniperKill() {
        addProgress(AchievementId.FIFTY_SHOTS_FIFTY_KILLS, 1)
    }

    fun onPoisonKill() {
        addProgress(AchievementId.TOXICITY, 1)
    }

    fun onWrathOfTheForestTrapped(trappedCount: Int) {
        if (trappedCount > 0) {
            addProgress(AchievementId.ENTANGLED, trappedCount)
        }
    }

    fun onDeathRayKill() {
        addProgress(AchievementId.DUST_TO_DUST, 1)
    }

    fun onEnemiesTeleported(count: Int) {
        if (count > 0) {
            addProgress(AchievementId.BEAM_ME_UP_SCOTTY, count)
        }
    }

    fun onEnemyPolymorphed() {
        addProgress(AchievementId.SHEPHERD, 1)
    }

    fun onRockElementalSummoned() {
        stageElementalsSummoned++
        setMaxProgress(AchievementId.ELEMENTALIST, stageElementalsSummoned)
    }

    fun onAxeThrown() {
        addProgress(AchievementId.AXE_RAIN, 1)
    }

    fun onBarbarianKillCountUpdated(killsByThisBarbarian: Int) {
        setMaxProgress(AchievementId.ARE_YOU_NOT_ENTERTAINED, killsByThisBarbarian)
    }

    fun onPaladinHeal(healAmount: Int) {
        if (healAmount > 0) {
            addProgress(AchievementId.MEDIC, healAmount)
        }
    }

    fun onHolyStrike() {
        addProgress(AchievementId.HOLY_CHORUS, 1)
    }

    fun onMissileLaunched() {
        addProgress(AchievementId.ROCKETEER, 1)
    }

    fun onClusterBombletsDropped(bombletCount: Int) {
        if (bombletCount > 0) {
            addProgress(AchievementId.CLUSTERED, bombletCount)
        }
    }

    fun onElectricityKill() {
        addProgress(AchievementId.AC_DC, 1)
    }
}

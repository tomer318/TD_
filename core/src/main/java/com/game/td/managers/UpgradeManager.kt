package com.game.td.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

object UpgradeManager {
    private const val LEGACY_PREF_NAME = "kingdom_rush_upgrades"

    private fun getSlotPrefs(): Preferences {
        return Gdx.app.getPreferences("kr_save_slot_${SaveManager.activeSlot}")
    }

    val ARCHER_COSTS = listOf(1, 1, 2, 2, 3)
    val BARRACKS_COSTS = listOf(1, 1, 2, 2, 3)
    val MAGE_COSTS = listOf(1, 1, 2, 2, 3)
    val ARTILLERY_COSTS = listOf(1, 1, 2, 3, 3)
    val RAIN_COSTS = listOf(2, 2, 3, 3, 3)
    val REINF_COSTS = listOf(2, 3, 3, 3, 4)

    private var loadedSlot: Int = -1

    var archerLevel = 0
    var barracksLevel = 0
    var mageLevel = 0
    var artilleryLevel = 0
    var rainLevel = 0
    var reinfLevel = 0

    // Giới hạn cấp upgrade hiệu dụng của trận đấu hiện tại (5: không giới hạn, 1..4 tùy challenge)
    var maxUpgradeCap: Int = 5

    private fun ensureSlotSynced() {
        if (loadedSlot != SaveManager.activeSlot) {
            loadUpgrades()
        }
    }

    fun getEffectiveArcherLevel(): Int {
        ensureSlotSynced()
        return archerLevel.coerceAtMost(maxUpgradeCap)
    }

    fun getEffectiveBarracksLevel(): Int {
        ensureSlotSynced()
        return barracksLevel.coerceAtMost(maxUpgradeCap)
    }

    fun getEffectiveMageLevel(): Int {
        ensureSlotSynced()
        return mageLevel.coerceAtMost(maxUpgradeCap)
    }

    fun getEffectiveArtilleryLevel(): Int {
        ensureSlotSynced()
        return artilleryLevel.coerceAtMost(maxUpgradeCap)
    }

    fun getEffectiveRainLevel(): Int {
        ensureSlotSynced()
        return rainLevel.coerceAtMost(maxUpgradeCap)
    }

    fun getEffectiveReinfLevel(): Int {
        ensureSlotSynced()
        return reinfLevel.coerceAtMost(maxUpgradeCap)
    }

    init {
        loadUpgrades()
    }

    private fun calculateUsedStars(
        arch: Int,
        barr: Int,
        mage: Int,
        art: Int,
        rain: Int,
        reinf: Int
    ): Int {
        var used = 0
        for (i in 0 until arch) used += ARCHER_COSTS[i]
        for (i in 0 until barr) used += BARRACKS_COSTS[i]
        for (i in 0 until mage) used += MAGE_COSTS[i]
        for (i in 0 until art) used += ARTILLERY_COSTS[i]
        for (i in 0 until rain) used += RAIN_COSTS[i]
        for (i in 0 until reinf) used += REINF_COSTS[i]
        return used
    }

    fun getUsedStars(): Int {
        ensureSlotSynced()
        return calculateUsedStars(archerLevel, barracksLevel, mageLevel, artilleryLevel, rainLevel, reinfLevel)
    }

    fun getAvailableStars(): Int {
        ensureSlotSynced()
        return (SaveManager.getTotalStars() - getUsedStars()).coerceAtLeast(0)
    }

    fun canUpgrade(branchIndex: Int): Boolean {
        ensureSlotSynced()
        val (lvl, costs) = getBranchInfo(branchIndex)
        if (lvl >= 5) return false
        val cost = costs[lvl]
        return getAvailableStars() >= cost
    }

    fun upgradeBranch(branchIndex: Int): Boolean {
        ensureSlotSynced()
        if (!canUpgrade(branchIndex)) return false
        when (branchIndex) {
            0 -> archerLevel++
            1 -> barracksLevel++
            2 -> mageLevel++
            3 -> artilleryLevel++
            4 -> rainLevel++
            5 -> reinfLevel++
        }
        saveUpgrades()
        return true
    }

    fun resetAllUpgrades() {
        ensureSlotSynced()
        archerLevel = 0
        barracksLevel = 0
        mageLevel = 0
        artilleryLevel = 0
        rainLevel = 0
        reinfLevel = 0
        saveUpgrades()
    }

    fun getBranchInfo(branchIndex: Int): Pair<Int, List<Int>> {
        ensureSlotSynced()
        return when (branchIndex) {
            0 -> Pair(archerLevel, ARCHER_COSTS)
            1 -> Pair(barracksLevel, BARRACKS_COSTS)
            2 -> Pair(mageLevel, MAGE_COSTS)
            3 -> Pair(artilleryLevel, ARTILLERY_COSTS)
            4 -> Pair(rainLevel, RAIN_COSTS)
            else -> Pair(reinfLevel, REINF_COSTS)
        }
    }

    fun saveUpgrades() {
        val prefs = getSlotPrefs()
        prefs.putBoolean("upg_initialized", true)
        prefs.putInteger("upg_archer", archerLevel)
        prefs.putInteger("upg_barracks", barracksLevel)
        prefs.putInteger("upg_mage", mageLevel)
        prefs.putInteger("upg_artillery", artilleryLevel)
        prefs.putInteger("upg_rain", rainLevel)
        prefs.putInteger("upg_reinf", reinfLevel)
        prefs.flush()
    }

    fun loadUpgrades() {
        loadedSlot = SaveManager.activeSlot
        val prefs = getSlotPrefs()
        val totalStarsInSlot = SaveManager.getTotalStars()

        if (!prefs.getBoolean("upg_initialized", false)) {
            // Nếu là Slot cũ đã có sao từ trước, chuyển dữ liệu nâng cấp cũ sang Slot này
            if (totalStarsInSlot > 0) {
                val legacyPrefs = Gdx.app.getPreferences(LEGACY_PREF_NAME)
                val lArch = legacyPrefs.getInteger("archer", 0)
                val lBarr = legacyPrefs.getInteger("barracks", 0)
                val lMage = legacyPrefs.getInteger("mage", 0)
                val lArt = legacyPrefs.getInteger("artillery", 0)
                val lRain = legacyPrefs.getInteger("rain", 0)
                val lReinf = legacyPrefs.getInteger("reinf", 0)

                val legacyUsed = calculateUsedStars(lArch, lBarr, lMage, lArt, lRain, lReinf)
                if (legacyUsed <= totalStarsInSlot) {
                    archerLevel = lArch
                    barracksLevel = lBarr
                    mageLevel = lMage
                    artilleryLevel = lArt
                    rainLevel = lRain
                    reinfLevel = lReinf
                } else {
                    archerLevel = 0; barracksLevel = 0; mageLevel = 0
                    artilleryLevel = 0; rainLevel = 0; reinfLevel = 0
                }
            } else {
                // Nếu là Slot mới (0 sao), bắt đầu hoàn toàn từ 0
                archerLevel = 0; barracksLevel = 0; mageLevel = 0
                artilleryLevel = 0; rainLevel = 0; reinfLevel = 0
            }
            saveUpgrades()
            return
        }

        archerLevel = prefs.getInteger("upg_archer", 0)
        barracksLevel = prefs.getInteger("upg_barracks", 0)
        mageLevel = prefs.getInteger("upg_mage", 0)
        artilleryLevel = prefs.getInteger("upg_artillery", 0)
        rainLevel = prefs.getInteger("upg_rain", 0)
        reinfLevel = prefs.getInteger("upg_reinf", 0)

        // Đảm bảo số sao đã dùng không bao giờ vượt quá tổng số sao thực tế của Slot
        if (calculateUsedStars(archerLevel, barracksLevel, mageLevel, artilleryLevel, rainLevel, reinfLevel) > totalStarsInSlot) {
            archerLevel = 0; barracksLevel = 0; mageLevel = 0
            artilleryLevel = 0; rainLevel = 0; reinfLevel = 0
            saveUpgrades()
        }
    }
}

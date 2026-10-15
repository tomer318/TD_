package com.game.td.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

object UpgradeManager {
    private const val PREF_NAME = "kingdom_rush_upgrades"
    private val prefs: Preferences by lazy { Gdx.app.getPreferences(PREF_NAME) }

    val ARCHER_COSTS = listOf(1, 1, 2, 2, 3)
    val BARRACKS_COSTS = listOf(1, 1, 2, 2, 3)
    val MAGE_COSTS = listOf(1, 1, 2, 2, 3)
    val ARTILLERY_COSTS = listOf(1, 1, 2, 3, 3)
    val RAIN_COSTS = listOf(2, 2, 3, 3, 3)
    val REINF_COSTS = listOf(2, 3, 3, 3, 4)

    var archerLevel = 0
    var barracksLevel = 0
    var mageLevel = 0
    var artilleryLevel = 0
    var rainLevel = 0
    var reinfLevel = 0

    // Giới hạn cấp upgrade hiệu dụng của trận đấu hiện tại (5: không giới hạn, 1 hoặc 2 tùy challenge)
    var maxUpgradeCap: Int = 5

    fun getEffectiveArcherLevel(): Int = archerLevel.coerceAtMost(maxUpgradeCap)
    fun getEffectiveBarracksLevel(): Int = barracksLevel.coerceAtMost(maxUpgradeCap)
    fun getEffectiveMageLevel(): Int = mageLevel.coerceAtMost(maxUpgradeCap)
    fun getEffectiveArtilleryLevel(): Int = artilleryLevel.coerceAtMost(maxUpgradeCap)
    fun getEffectiveRainLevel(): Int = rainLevel.coerceAtMost(maxUpgradeCap)
    fun getEffectiveReinfLevel(): Int = reinfLevel.coerceAtMost(maxUpgradeCap)

    init {
        loadUpgrades()
    }

    fun getUsedStars(): Int {
        var used = 0
        for (i in 0 until archerLevel) used += ARCHER_COSTS[i]
        for (i in 0 until barracksLevel) used += BARRACKS_COSTS[i]
        for (i in 0 until mageLevel) used += MAGE_COSTS[i]
        for (i in 0 until artilleryLevel) used += ARTILLERY_COSTS[i]
        for (i in 0 until rainLevel) used += RAIN_COSTS[i]
        for (i in 0 until reinfLevel) used += REINF_COSTS[i]
        return used
    }

    fun getAvailableStars(): Int {
        return (SaveManager.getTotalStars() - getUsedStars()).coerceAtLeast(0)
    }

    fun canUpgrade(branchIndex: Int): Boolean {
        val (lvl, costs) = getBranchInfo(branchIndex)
        if (lvl >= 5) return false
        val cost = costs[lvl]
        return getAvailableStars() >= cost
    }

    fun upgradeBranch(branchIndex: Int): Boolean {
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
        archerLevel = 0
        barracksLevel = 0
        mageLevel = 0
        artilleryLevel = 0
        rainLevel = 0
        reinfLevel = 0
        saveUpgrades()
    }

    fun getBranchInfo(branchIndex: Int): Pair<Int, List<Int>> {
        return when (branchIndex) {
            0 -> Pair(archerLevel, ARCHER_COSTS)
            1 -> Pair(barracksLevel, BARRACKS_COSTS)
            2 -> Pair(mageLevel, MAGE_COSTS)
            3 -> Pair(artilleryLevel, ARTILLERY_COSTS)
            4 -> Pair(rainLevel, RAIN_COSTS)
            else -> Pair(reinfLevel, REINF_COSTS)
        }
    }

    private fun saveUpgrades() {
        prefs.putInteger("archer", archerLevel)
        prefs.putInteger("barracks", barracksLevel)
        prefs.putInteger("mage", mageLevel)
        prefs.putInteger("artillery", artilleryLevel)
        prefs.putInteger("rain", rainLevel)
        prefs.putInteger("reinf", reinfLevel)
        prefs.flush()
    }

    private fun loadUpgrades() {
        archerLevel = prefs.getInteger("archer", 0)
        barracksLevel = prefs.getInteger("barracks", 0)
        mageLevel = prefs.getInteger("mage", 0)
        artilleryLevel = prefs.getInteger("artillery", 0)
        rainLevel = prefs.getInteger("rain", 0)
        reinfLevel = prefs.getInteger("reinf", 0)
    }
}

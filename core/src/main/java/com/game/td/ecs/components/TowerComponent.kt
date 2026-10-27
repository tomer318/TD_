package com.game.td.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

enum class TowerType(val cost: Int, val title: String, val color: Color) {
    ARCHER(70, "ARCHER", Color(0.2f, 0.75f, 0.3f, 1f)),
    BARRACKS(70, "BARRACK", Color(0.2f, 0.5f, 0.9f, 1f)),
    MAGE(100, "MAGE", Color(0.7f, 0.2f, 0.9f, 1f)),
    ARTILLERY(125, "BOMB", Color(0.9f, 0.45f, 0.1f, 1f)),
    SUNRAY(100, "SUNRAY", Color(1f, 0.85f, 0.2f, 1f))
}

enum class TowerSpecialization(
    val displayName: String,
    val branchCost: Int,
    val skill1Name: String,
    val skill1Costs: List<Int>,
    val skill2Name: String,
    val skill2Costs: List<Int>,
    val skill3Name: String = "",
    val skill3Costs: List<Int> = listOf()
) {
    NONE("None", 0, "", listOf(), "", listOf()),
    RANGERS_HIDEOUT("Rangers", 230, "Poison Arrows", listOf(250, 250, 250), "Forest Wrath", listOf(300, 150, 150)),
    MUSKETEER_GARRISON("Musketeers", 230, "Sniper Shot", listOf(250, 250, 250), "Shrapnel Shot", listOf(300, 300, 300)),
    HOLY_ORDER("Holy Order", 230, "Healing Light", listOf(150, 150, 150), "Shield Valor", listOf(250), "Holy Strike", listOf(220, 150, 150)),
    BARBARIAN_MEAD_HALL("Barbarians", 230, "Throwing Axes", listOf(200, 100, 100), "Whirlwind", listOf(150, 100, 100), "More Axes", listOf(250, 100, 100)),
    ARCANE_WIZARD("Arcane Wizard", 300, "Death Ray", listOf(350, 200, 200), "Teleport", listOf(300, 100, 100)),
    SORCERER_MAGE("Sorcerer Mage", 300, "Polymorph", listOf(300, 150, 150), "Elemental", listOf(350, 150, 150)),
    BIG_BERTHA("Big Bertha", 400, "Dragon Missile", listOf(250, 100, 100), "Cluster Bombs", listOf(250, 150, 150)),
    TESLA_X104("Tesla x104", 375, "Supercharged Bolt", listOf(250, 250), "Overcharge", listOf(250, 125, 125)),
    SYLVAN_ELF_HALL("Elf Hall", 100, "", listOf(), "", listOf()),
    SUNRAY_TOWER("Sunray", 100, "", listOf(), "", listOf())
}

class TowerComponent(
    val towerType: TowerType,
    var level: Int = 1,
    var specialization: TowerSpecialization = TowerSpecialization.NONE,
    var range: Float = 210f,
    var damageMin: Float = 4f,
    var damageMax: Float = 6f,
    var attackRate: Float = 0.6f,
    var attackCooldown: Float = 0f,
    var totalInvestedGold: Int = towerType.cost,
    var currentTarget: Entity? = null,

    var skill1Level: Int = 0,
    var skill1Cooldown: Float = 0f,
    var skill2Level: Int = 0,
    var skill2Cooldown: Float = 0f,
    var skill3Level: Int = 0,
    var skill3Cooldown: Float = 0f,

    var summonedGolem: Entity? = null,
    val golemRallyPoint: Vector2 = Vector2(),
    var isRuined: Boolean = false,

    // CƠ CHẾ ĐÓNG BĂNG BỞI BOSS J.T.
    var isFrozen: Boolean = false,
    var freezeHitsRemaining: Int = 0,

    // CƠ CHẾ THÁP SUNRAY TOWER
    var sunraySorcerers: Int = 0,
    var sunrayChargeTimer: Float = 0f,
    var isSunrayReady: Boolean = false
) : Component {

    val fireRateText: String
        get() = when {
            attackRate <= 0.45f -> "Very Fast"
            attackRate <= 0.7f -> "Fast"
            attackRate <= 1.35f -> "Average"
            attackRate <= 1.8f -> "Slow"
            else -> "Very Slow"
        }

    val rangeText: String
        get() = when {
            range <= 190f -> "Short"
            range <= 235f -> "Medium"
            range <= 280f -> "Far"
            else -> "Extreme"
        }

    val upgradeCost: Int
        get() = when (towerType) {
            TowerType.ARCHER -> when (level) { 1 -> 110; 2 -> 160; 3 -> 230; else -> 0 }
            TowerType.BARRACKS -> when (level) { 1 -> 110; 2 -> 160; 3 -> 230; else -> 0 }
            TowerType.MAGE -> when (level) { 1 -> 160; 2 -> 240; 3 -> 300; else -> 0 }
            TowerType.ARTILLERY -> when (level) { 1 -> 220; 2 -> 320; 3 -> 375; else -> 0 }
            TowerType.SUNRAY -> 100
        }

    val sellRefund: Int
        get() = (totalInvestedGold * 0.70f).toInt()
}

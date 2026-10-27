package com.game.td.ecs.components

import com.badlogic.ashley.core.Component

enum class GameDifficulty {
    CASUAL,
    NORMAL,
    VETERAN
}

enum class EnemyType(
    val title: String,
    val hpCasual: Float,
    val hpNormal: Float,
    val hpVeteran: Float,
    val speed: Float,
    val damageMin: Float,
    val damageMax: Float,
    val armorType: ArmorType,
    val magicResist: MagicResistType,
    val rewardGold: Int,
    val livesTaken: Int = 1,
    val dodgeChance: Float = 0f,
    val isFlying: Boolean = false,
    val canRangedAttack: Boolean = false,
    val rangedRange: Float = 0f,
    val rangedDmgMin: Float = 0f,
    val rangedDmgMax: Float = 0f,
    val isAreaAttack: Boolean = false,
    val isImmuneToPoison: Boolean = false
) {
    GOBLIN("Goblin", 16f, 20f, 24f, 54f, 1f, 4f, ArmorType.NONE, MagicResistType.NONE, 3, 1),
    ORC("Orc", 64f, 80f, 96f, 36f, 4f, 8f, ArmorType.LIGHT_ARMOR, MagicResistType.NONE, 9, 1),
    WULF("Wulf", 28f, 35f, 42f, 98f, 1f, 3f, ArmorType.NONE, MagicResistType.NONE, 5, 1, 0.30f),
    WORG("Worg", 96f, 120f, 144f, 78f, 12f, 18f, ArmorType.NONE, MagicResistType.MEDIUM, 12, 1, 0.50f),
    SHAMAN("Shaman", 80f, 100f, 120f, 46f, 3f, 5f, ArmorType.NONE, MagicResistType.HIGH, 10, 1),
    BANDIT("Bandit", 56f, 70f, 84f, 52f, 20f, 30f, ArmorType.NONE, MagicResistType.NONE, 8, 1),
    BRIGAND("Brigand", 128f, 160f, 192f, 40f, 6f, 10f, ArmorType.MEDIUM_ARMOR, MagicResistType.NONE, 15, 1),
    MARAUDER("Marauder", 480f, 600f, 720f, 34f, 16f, 24f, ArmorType.HEAVY_ARMOR, MagicResistType.NONE, 40, 3),
    OGRE("Ogre", 640f, 800f, 960f, 28f, 40f, 60f, ArmorType.NONE, MagicResistType.NONE, 50, 3),
    GIANT_SPIDER("Giant Spider", 48f, 60f, 72f, 74f, 10f, 18f, ArmorType.NONE, MagicResistType.HIGH, 6, 1),
    SPIDER_MATRIARCH("Spider Matriarch", 200f, 250f, 300f, 42f, 15f, 25f, ArmorType.NONE, MagicResistType.HIGH, 20, 2),
    SPIDER_HATCHLING("Spider Hatchling", 8f, 10f, 12f, 92f, 1f, 5f, ArmorType.NONE, MagicResistType.MEDIUM, 0, 1),
    DARK_KNIGHT("Dark Knight", 240f, 300f, 360f, 34f, 15f, 25f, ArmorType.HEAVY_ARMOR, MagicResistType.NONE, 25, 1),
    GARGOYLE("Gargoyle", 72f, 90f, 108f, 45f, 0f, 0f, ArmorType.NONE, MagicResistType.NONE, 12, 1, isFlying = true),
    SHADOW_ARCHER("Shadow Archer", 144f, 180f, 216f, 52f, 20f, 30f, ArmorType.NONE, MagicResistType.LOW, 16, 1, canRangedAttack = true, rangedRange = 145f, rangedDmgMin = 10f, rangedDmgMax = 20f),
    GOLEM_HEAD("Golem Head", 100f, 120f, 150f, 30f, 10f, 20f, ArmorType.MEDIUM_ARMOR, MagicResistType.NONE, 10, 1),
    JUGGERNAUT("The Juggernaut", 8000f, 10000f, 12000f, 22f, 150f, 250f, ArmorType.NONE, MagicResistType.NONE, 250, 20, isAreaAttack = true),
    TROLL("Troll", 224f, 280f, 336f, 40f, 20f, 40f, ArmorType.NONE, MagicResistType.NONE, 25, 1),
    WINTER_WOLF("Winter Wolf", 280f, 350f, 420f, 88f, 20f, 40f, ArmorType.NONE, MagicResistType.MEDIUM, 35, 1, 0.30f),
    TROLL_CHAMPION("Troll Champion", 480f, 600f, 720f, 46f, 30f, 50f, ArmorType.NONE, MagicResistType.NONE, 50, 3, canRangedAttack = true, rangedRange = 155f, rangedDmgMin = 40f, rangedDmgMax = 80f),
    YETI("Yeti", 1600f, 2000f, 2400f, 34f, 50f, 150f, ArmorType.NONE, MagicResistType.NONE, 120, 5, isAreaAttack = true),
    TROLL_CHIEFTAIN("Troll Chieftain", 960f, 1200f, 1440f, 30f, 10f, 30f, ArmorType.NONE, MagicResistType.NONE, 70, 6),
    ROCKET_RIDER("Rocket Rider", 272f, 340f, 408f, 60f, 0f, 0f, ArmorType.NONE, MagicResistType.NONE, 30, 1, isFlying = true),
    DARK_SLAYER("Dark Slayer", 960f, 1200f, 1440f, 34f, 24f, 76f, ArmorType.GREAT_ARMOR, MagicResistType.NONE, 100, 3),
    JT("J.T.", 8800f, 11000f, 13200f, 20f, 150f, 200f, ArmorType.NONE, MagicResistType.NONE, 350, 20, isAreaAttack = true),

    SKELETON(
        title = "Skeleton",
        hpCasual = 96f, hpNormal = 120f, hpVeteran = 144f,
        speed = 36f, damageMin = 10f, damageMax = 20f,
        armorType = ArmorType.NONE, magicResist = MagicResistType.NONE,
        rewardGold = 2, livesTaken = 1,
        isImmuneToPoison = true
    ),
    SKELETON_KNIGHT(
        title = "Skeleton Knight",
        hpCasual = 320f, hpNormal = 400f, hpVeteran = 480f,
        speed = 36f, damageMin = 20f, damageMax = 40f,
        armorType = ArmorType.LIGHT_ARMOR, magicResist = MagicResistType.NONE,
        rewardGold = 10, livesTaken = 1,
        isImmuneToPoison = true
    ),
    DEMON_SPAWN(
        title = "Demon Spawn",
        hpCasual = 200f, hpNormal = 250f, hpVeteran = 300f,
        speed = 48f, damageMin = 10f, damageMax = 30f,
        armorType = ArmorType.NONE, magicResist = MagicResistType.MEDIUM,
        rewardGold = 20, livesTaken = 1
    ),
    DEMON_LORD(
        title = "Demon Lord",
        hpCasual = 800f, hpNormal = 1000f, hpVeteran = 1200f,
        speed = 36f, damageMin = 15f, damageMax = 75f,
        armorType = ArmorType.NONE, magicResist = MagicResistType.MEDIUM,
        rewardGold = 60, livesTaken = 5
    ),
    DEMON_HOUND(
        title = "Demon Hound",
        hpCasual = 280f, hpNormal = 350f, hpVeteran = 420f,
        speed = 90f, damageMin = 20f, damageMax = 40f,
        armorType = ArmorType.NONE, magicResist = MagicResistType.MEDIUM,
        rewardGold = 25, livesTaken = 1,
        dodgeChance = 0.50f
    ),
    DEMON_IMP(
        title = "Demon Imp",
        hpCasual = 280f, hpNormal = 350f, hpVeteran = 420f,
        speed = 60f, damageMin = 0f, damageMax = 0f,
        armorType = ArmorType.NONE, magicResist = MagicResistType.NONE,
        rewardGold = 25, livesTaken = 1,
        isFlying = true
    ),
    MAGMA_ELEMENTAL(
        title = "Magma Elemental",
        hpCasual = 2000f, hpNormal = 2500f, hpVeteran = 3000f,
        speed = 28f, damageMin = 50f, damageMax = 150f,
        armorType = ArmorType.NONE, magicResist = MagicResistType.NONE,
        rewardGold = 100, livesTaken = 5,
        isAreaAttack = true,
        isImmuneToPoison = true
    ),
    NECROMANCER(
        title = "Necromancer",
        hpCasual = 560f, hpNormal = 700f, hpVeteran = 840f,
        speed = 36f, damageMin = 20f, damageMax = 40f,
        armorType = ArmorType.NONE, magicResist = MagicResistType.NONE,
        rewardGold = 50, livesTaken = 3,
        canRangedAttack = true, rangedRange = 155f, rangedDmgMin = 20f, rangedDmgMax = 40f
    ),

    // BOSS CUỐI - DẠNG 1: VEZ'NAN PHÁP SƯ [Wiki]
    VEZNAN(
        title = "Vez'nan",
        hpCasual = 5333f, hpNormal = 6666f, hpVeteran = 7999f,
        speed = 24f, damageMin = 666f, damageMax = 999f,
        armorType = ArmorType.NONE, magicResist = MagicResistType.NONE,
        rewardGold = 500, livesTaken = 20,
        canRangedAttack = true, rangedRange = 180f, rangedDmgMin = 666f, rangedDmgMax = 999f,
        isImmuneToPoison = true
    ),

    // BOSS CUỐI - DẠNG 2: ĐẠI QUỶ ĐỎ THIÊU RỤI [Wiki]
    VEZNAN_DEMON(
        title = "Vez'nan Demon",
        hpCasual = 5333f, hpNormal = 6666f, hpVeteran = 7999f,
        speed = 36f, damageMin = 666f, damageMax = 999f,
        armorType = ArmorType.NONE, magicResist = MagicResistType.NONE,
        rewardGold = 1000, livesTaken = 20,
        isAreaAttack = true,
        isImmuneToPoison = true
    );

    fun getMaxHp(difficulty: GameDifficulty): Float = when (difficulty) {
        GameDifficulty.CASUAL -> hpCasual
        GameDifficulty.NORMAL -> hpNormal
        GameDifficulty.VETERAN -> hpVeteran
    }
}

class EnemyComponent(
    val enemyType: EnemyType,
    var isSheep: Boolean = false,
    var attackCooldown: Float = 0f,
    var attackRate: Float = when (enemyType) {
        EnemyType.YETI -> 2.5f
        EnemyType.MAGMA_ELEMENTAL -> 2.5f
        EnemyType.DEMON_LORD -> 2.0f
        EnemyType.VEZNAN, EnemyType.VEZNAN_DEMON -> 2.5f
        else -> 1.0f
    },
    var eggLayTimer: Float = 10.0f,
    var rangedCooldown: Float = 0f,
    var rangedRate: Float = when (enemyType) {
        EnemyType.TROLL_CHAMPION -> 1.6f
        EnemyType.NECROMANCER -> 1.3f
        EnemyType.VEZNAN -> 2.5f
        else -> 1.45f
    },
    var bossSummonTimer: Float = 3.5f,
    var bossMissileTimer: Float = 10.0f,

    val regenPerSec: Float = when (enemyType) {
        EnemyType.TROLL -> 5f
        EnemyType.TROLL_CHAMPION -> 10f
        EnemyType.TROLL_CHIEFTAIN -> 20f
        else -> 0f
    },

    var drumTimer: Float = 7.0f,
    var enrageDuration: Float = 0f,
    var shamanHealTimer: Float = 8.0f,
    var shamanCastDuration: Float = 0f,

    var hasTriggeredTurbo: Boolean = false,
    var turboDuration: Float = 0f,

    // Kỹ năng đóng băng/khóa tháp của boss J.T. & Vez'nan
    var jtFreezeCooldown: Float = 10f,
    var jtRestingTimer: Float = 0f,

    // Kỹ năng đặc biệt của Vez'nan
    var veznanLockTowerCooldown: Float = 12f,
    var veznanSummonCooldown: Float = 15f,
    var veznanFlameBreathTimer: Float = 0f,

    var burnDuration: Float = 0f,

    var isCursed: Boolean = false,
    var curseTimer: Float = 0f,
    var curseTickTimer: Float = 0f,

    var isSummonedMinion: Boolean = false,
    var infernalShieldHitsRemaining: Int = 0,
    var demonLordShieldTimer: Float = 7.0f,
    var necromancerSummonTimer: Float = 8.0f,
    var hasSpawnedSkeleton: Boolean = false,
    var isRangedAttacking: Boolean = false
) : Component

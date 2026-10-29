package com.game.td.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.game.td.ecs.components.*

enum class EncyclopediaTowerId(
    val title: String,
    val towerType: TowerType,
    val level: Int,
    val specialization: TowerSpecialization = TowerSpecialization.NONE,
    val description: String,
    val damageText: String,
    val rateOrArmorLabel: String,
    val rangeOrRespawnLabel: String,
    val specialsText: String,
    val isAdvanced: Boolean = false
) {
    // HÀNG 1 BASIC: LEVEL 1
    ARCHER_1(
        "ARCHER TOWER", TowerType.ARCHER, 1, TowerSpecialization.NONE,
        "Archers ready to strike at your enemies from a distance.",
        "4-6", "AVERAGE", "SHORT", "NONE"
    ),
    BARRACKS_1(
        "MILITIA BARRACKS", TowerType.BARRACKS, 1, TowerSpecialization.NONE,
        "Trains militia, tough soldiers that block and damage your enemies.",
        "1-3", "NONE", "10S", "HP: 50"
    ),
    MAGE_1(
        "MAGES GUILD", TowerType.MAGE, 1, TowerSpecialization.NONE,
        "Mages cast armor piercing bolts ignoring any physical protection.",
        "9-17", "SLOW", "SHORT", "NONE"
    ),
    ARTILLERY_1(
        "DWARVEN BOMBARD", TowerType.ARTILLERY, 1, TowerSpecialization.NONE,
        "Bombards ground enemies dealing area damage.",
        "8-15", "VERY SLOW", "AVERAGE", "AREA DAMAGE"
    ),

    // HÀNG 2 BASIC: LEVEL 2
    ARCHER_2(
        "MARKSMEN TOWER", TowerType.ARCHER, 2, TowerSpecialization.NONE,
        "Marksmen shoot broadhead arrows, dealing more damage with longer range.",
        "7-11", "FAST", "AVERAGE", "NONE"
    ),
    BARRACKS_2(
        "FOOTMEN BARRACKS", TowerType.BARRACKS, 2, TowerSpecialization.NONE,
        "Footmen are better trained and equipped than simple militia.",
        "3-4", "LOW", "10S", "HP: 100"
    ),
    MAGE_2(
        "ADEPT TOWER", TowerType.MAGE, 2, TowerSpecialization.NONE,
        "Adepts cast enhanced bolts that tear through armor, flesh and bone.",
        "23-43", "SLOW", "AVERAGE", "NONE"
    ),
    ARTILLERY_2(
        "DWARVEN ARTILLERY", TowerType.ARTILLERY, 2, TowerSpecialization.NONE,
        "Enhanced dwarven ordnance blasting larger areas with heavier shells.",
        "20-40", "VERY SLOW", "AVERAGE", "AREA DAMAGE"
    ),

    // HÀNG 3 BASIC: LEVEL 3 (MỞ KHÓA Ở MÀN 4)
    ARCHER_3(
        "SHARPSHOOTER", TowerType.ARCHER, 3, TowerSpecialization.NONE,
        "Sharpshooters are elite archers with deadly accuracy and long bows.",
        "10-16", "FAST", "LONG", "NONE"
    ),
    BARRACKS_3(
        "KNIGHTS BARRACKS", TowerType.BARRACKS, 3, TowerSpecialization.NONE,
        "Professional soldiers with heavy armor blocking the enemy advance.",
        "6-10", "MEDIUM", "10S", "HP: 150"
    ),
    MAGE_3(
        "WIZARD TOWER", TowerType.MAGE, 3, TowerSpecialization.NONE,
        "Wizards channel high-energy arcane bolts with extended reach.",
        "40-74", "SLOW", "LONG", "NONE"
    ),
    ARTILLERY_3(
        "DWARVEN HOWITZER", TowerType.ARTILLERY, 3, TowerSpecialization.NONE,
        "Heavy howitzers that rain fiery destruction over wide battlefields.",
        "30-60", "VERY SLOW", "LONG", "AREA DAMAGE"
    ),

    // HÀNG 1 ADVANCED: NHÁNH CHUYÊN HÓA 1 (SPEC 1)
    RANGERS_HIDEOUT(
        "RANGERS HIDEOUT", TowerType.ARCHER, 4, TowerSpecialization.RANGERS_HIDEOUT,
        "Masters of the bow, firing a hail of arrows with unmatched speed and range.",
        "13-19", "VERY FAST", "GREAT", "POISON ARROWS, WRATH OF THE FOREST", isAdvanced = true
    ),
    HOLY_ORDER(
        "HOLY ORDER", TowerType.BARRACKS, 4, TowerSpecialization.HOLY_ORDER,
        "Trains Paladins, an order of holy warriors with divine protection.",
        "12-18", "HIGH", "10S", "HEALING LIGHT, SHIELD OF VALOR, HOLY STRIKE", isAdvanced = true
    ),
    ARCANE_WIZARD(
        "ARCANE WIZARD", TowerType.MAGE, 4, TowerSpecialization.ARCANE_WIZARD,
        "Focuses on pure arcane power to disintegrate and warp enemies.",
        "76-140", "VERY SLOW", "GREAT", "DEATH RAY, TELEPORT", isAdvanced = true
    ),
    BIG_BERTHA(
        "500MM BIG BERTHA", TowerType.ARTILLERY, 4, TowerSpecialization.BIG_BERTHA,
        "The biggest piece of artillery ever built, dealing colossal area damage.",
        "50-100", "VERY SLOW", "LONG", "DRAGONBREATH LAUNCHER, CLUSTER LAUNCHER", isAdvanced = true
    ),

    // HÀNG 2 ADVANCED: NHÁNH CHUYÊN HÓA 2 (SPEC 2)
    MUSKETEER_GARRISON(
        "MUSKETEER GARRISON", TowerType.ARCHER, 4, TowerSpecialization.MUSKETEER_GARRISON,
        "Patient, careful, and deadly accurate marksmen with advanced weaponry.",
        "35-65", "SLOW", "EXTREME", "SNIPER SHOT, SHRAPNEL SHOT", isAdvanced = true
    ),
    BARBARIAN_MEAD_HALL(
        "BARBARIAN HALL", TowerType.BARRACKS, 4, TowerSpecialization.BARBARIAN_MEAD_HALL,
        "Barbarians are savage warriors that can quickly clear most threats.",
        "16-24", "NONE", "10S", "THROWING AXES, WHIRLWIND ATTACK, MORE AXES", isAdvanced = true
    ),
    SORCERER_MAGE(
        "SORCERER MAGE", TowerType.MAGE, 4, TowerSpecialization.SORCERER_MAGE,
        "Curses enemies, temporarily reducing their armor and damaging them.",
        "42-78", "SLOW", "LONG", "POLYMORPH, SUMMON ELEMENTAL", isAdvanced = true
    ),
    TESLA_X104(
        "TESLA X104", TowerType.ARTILLERY, 4, TowerSpecialization.TESLA_X104,
        "Dwarven engineering at its finest, harnessing the power of a thousand storms.",
        "60-110", "SLOW", "AVERAGE", "SUPERCHARGED BOLT, OVERCHARGE", isAdvanced = true
    )
}

data class EnemyEncyclopediaInfo(
    val description: String,
    val specials: String,
    val armorLabel: String,
    val magicResistLabel: String,
    val speedLabel: String
)

object EncyclopediaManager {

    private const val LEGACY_PREFS_NAME = "td_encyclopedia_v1"
    private const val KEY_INIT = "enc_initialized"
    private const val KEY_TOWERS = "enc_unlocked_towers"
    private const val KEY_ENEMIES = "enc_unlocked_enemies"

    private fun getSlotPrefs(): Preferences {
        return Gdx.app.getPreferences("kr_save_slot_${SaveManager.activeSlot}")
    }

    private val unlockedTowers = mutableSetOf<String>()
    private val unlockedEnemies = mutableSetOf<String>()
    private var loadedSlot: Int = -1

    val basicTowers = listOf(
        EncyclopediaTowerId.ARCHER_1, EncyclopediaTowerId.BARRACKS_1, EncyclopediaTowerId.MAGE_1, EncyclopediaTowerId.ARTILLERY_1,
        EncyclopediaTowerId.ARCHER_2, EncyclopediaTowerId.BARRACKS_2, EncyclopediaTowerId.MAGE_2, EncyclopediaTowerId.ARTILLERY_2,
        EncyclopediaTowerId.ARCHER_3, EncyclopediaTowerId.BARRACKS_3, EncyclopediaTowerId.MAGE_3, EncyclopediaTowerId.ARTILLERY_3
    )

    val advancedTowers = listOf(
        EncyclopediaTowerId.RANGERS_HIDEOUT, EncyclopediaTowerId.HOLY_ORDER, EncyclopediaTowerId.ARCANE_WIZARD, EncyclopediaTowerId.BIG_BERTHA,
        EncyclopediaTowerId.MUSKETEER_GARRISON, EncyclopediaTowerId.BARBARIAN_MEAD_HALL, EncyclopediaTowerId.SORCERER_MAGE, EncyclopediaTowerId.TESLA_X104
    )

    val allEnemiesOrdered = listOf(
        EnemyType.GOBLIN, EnemyType.ORC, EnemyType.SHAMAN, EnemyType.OGRE, EnemyType.BANDIT, EnemyType.BRIGAND,
        EnemyType.MARAUDER, EnemyType.GIANT_SPIDER, EnemyType.SPIDER_MATRIARCH, EnemyType.SPIDER_HATCHLING, EnemyType.GARGOYLE, EnemyType.SHADOW_ARCHER,
        EnemyType.DARK_KNIGHT, EnemyType.WULF, EnemyType.WORG, EnemyType.GOLEM_HEAD, EnemyType.JUGGERNAUT, EnemyType.TROLL,
        EnemyType.TROLL_CHAMPION, EnemyType.TROLL_CHIEFTAIN, EnemyType.WINTER_WOLF, EnemyType.YETI, EnemyType.ROCKET_RIDER, EnemyType.DARK_SLAYER,
        EnemyType.JT, EnemyType.SKELETON, EnemyType.SKELETON_KNIGHT, EnemyType.NECROMANCER, EnemyType.DEMON_SPAWN, EnemyType.DEMON_HOUND,
        EnemyType.DEMON_IMP, EnemyType.DEMON_LORD, EnemyType.MAGMA_ELEMENTAL, EnemyType.VEZNAN, EnemyType.VEZNAN_DEMON
    )

    private fun ensureSlotSynced() {
        if (loadedSlot != SaveManager.activeSlot) {
            loadForCurrentSlot()
        }
    }

    fun loadForCurrentSlot() {
        loadedSlot = SaveManager.activeSlot
        unlockedTowers.clear()
        unlockedEnemies.clear()

        val prefs = getSlotPrefs()
        val totalStarsInSlot = SaveManager.getTotalStars()
        val highestLevel = SaveManager.unlockedLevel

        if (!prefs.getBoolean(KEY_INIT, false)) {
            // Luôn mở khóa 4 tháp cấp 1 cơ bản
            addTowersUpToLevel(if (totalStarsInSlot > 0) highestLevel else 1)

            // Nếu là Slot cũ đã từng chơi (có sao), chuyển danh sách quái từ dữ liệu cũ sang Slot này
            if (totalStarsInSlot > 0) {
                val legacyPrefs = Gdx.app.getPreferences(LEGACY_PREFS_NAME)
                val legacyEnemies = legacyPrefs.getString("unlocked_enemies", "")
                if (legacyEnemies.isNotBlank()) {
                    unlockedEnemies.addAll(legacyEnemies.split(",").filter { it.isNotBlank() })
                }
                unlockedEnemies.add(EnemyType.GOBLIN.name)
            }
            save()
            return
        }

        val savedTowers = prefs.getString(KEY_TOWERS, "")
        if (savedTowers.isNotBlank()) {
            unlockedTowers.addAll(savedTowers.split(",").filter { it.isNotBlank() })
        }
        // Đảm bảo 4 tháp cấp 1 luôn mở
        unlockedTowers.add(EncyclopediaTowerId.ARCHER_1.name)
        unlockedTowers.add(EncyclopediaTowerId.BARRACKS_1.name)
        unlockedTowers.add(EncyclopediaTowerId.MAGE_1.name)
        unlockedTowers.add(EncyclopediaTowerId.ARTILLERY_1.name)

        val savedEnemies = prefs.getString(KEY_ENEMIES, "")
        if (savedEnemies.isNotBlank()) {
            unlockedEnemies.addAll(savedEnemies.split(",").filter { it.isNotBlank() })
        }
    }

    fun resetForCurrentSlot() {
        loadedSlot = SaveManager.activeSlot
        unlockedTowers.clear()
        unlockedEnemies.clear()
        unlockedTowers.add(EncyclopediaTowerId.ARCHER_1.name)
        unlockedTowers.add(EncyclopediaTowerId.BARRACKS_1.name)
        unlockedTowers.add(EncyclopediaTowerId.MAGE_1.name)
        unlockedTowers.add(EncyclopediaTowerId.ARTILLERY_1.name)
        save()
    }

    private fun save() {
        val prefs = getSlotPrefs()
        prefs.putBoolean(KEY_INIT, true)
        prefs.putString(KEY_TOWERS, unlockedTowers.joinToString(","))
        prefs.putString(KEY_ENEMIES, unlockedEnemies.joinToString(","))
        prefs.flush()
    }

    private fun addTowersUpToLevel(levelNumber: Int) {
        if (levelNumber >= 1) {
            unlockedTowers.add(EncyclopediaTowerId.ARCHER_1.name)
            unlockedTowers.add(EncyclopediaTowerId.BARRACKS_1.name)
            unlockedTowers.add(EncyclopediaTowerId.MAGE_1.name)
            unlockedTowers.add(EncyclopediaTowerId.ARTILLERY_1.name)
        }
        // Màn 2 (The Farmlands) mở khóa Tháp Cấp 2
        if (levelNumber >= 2) {
            unlockedTowers.add(EncyclopediaTowerId.ARCHER_2.name)
            unlockedTowers.add(EncyclopediaTowerId.BARRACKS_2.name)
            unlockedTowers.add(EncyclopediaTowerId.MAGE_2.name)
            unlockedTowers.add(EncyclopediaTowerId.ARTILLERY_2.name)
        }
        // Màn 4 (Twin Rivers Pass) mở khóa Tháp Cấp 3 chuẩn game gốc
        if (levelNumber >= 4) {
            unlockedTowers.add(EncyclopediaTowerId.ARCHER_3.name)
            unlockedTowers.add(EncyclopediaTowerId.BARRACKS_3.name)
            unlockedTowers.add(EncyclopediaTowerId.MAGE_3.name)
            unlockedTowers.add(EncyclopediaTowerId.ARTILLERY_3.name)
        }
        // Màn 5+ mở khóa các nhánh Tháp Cấp 4 chuyên biệt
        if (levelNumber >= 5) {
            unlockedTowers.add(EncyclopediaTowerId.RANGERS_HIDEOUT.name)
        }
        if (levelNumber >= 6) {
            unlockedTowers.add(EncyclopediaTowerId.HOLY_ORDER.name)
            unlockedTowers.add(EncyclopediaTowerId.ARCANE_WIZARD.name)
        }
        if (levelNumber >= 7) {
            unlockedTowers.add(EncyclopediaTowerId.MUSKETEER_GARRISON.name)
            unlockedTowers.add(EncyclopediaTowerId.BARBARIAN_MEAD_HALL.name)
            unlockedTowers.add(EncyclopediaTowerId.SORCERER_MAGE.name)
        }
        if (levelNumber >= 8) {
            unlockedTowers.add(EncyclopediaTowerId.BIG_BERTHA.name)
        }
        if (levelNumber >= 9) {
            unlockedTowers.add(EncyclopediaTowerId.TESLA_X104.name)
        }
    }

    // GỌI KHI NGƯỜI CHƠI BƯỚC VÀO MỘT MÀN CHƠI
    fun onEnterLevel(levelNumber: Int) {
        ensureSlotSynced()
        if (levelNumber <= 0) return // Không tính màn Sandbox 0
        addTowersUpToLevel(levelNumber)
        save()
    }

    // GỌI NGAY KHI QUÁI XUẤT HIỆN TRÊN BẢN ĐỒ
    fun unlockEnemy(type: EnemyType) {
        ensureSlotSynced()
        if (unlockedEnemies.add(type.name)) {
            save()
        }
    }

    fun isTowerUnlocked(towerId: EncyclopediaTowerId): Boolean {
        ensureSlotSynced()
        return unlockedTowers.contains(towerId.name)
    }

    fun isEnemyUnlocked(type: EnemyType): Boolean {
        ensureSlotSynced()
        return unlockedEnemies.contains(type.name)
    }

    fun getFirstUnlockedEnemy(): EnemyType? {
        ensureSlotSynced()
        return allEnemiesOrdered.firstOrNull { unlockedEnemies.contains(it.name) }
    }

    fun getEnemyInfo(type: EnemyType): EnemyEncyclopediaInfo {
        val armorStr = when (type.armorType) {
            ArmorType.NONE -> "NONE"
            ArmorType.LIGHT_ARMOR -> "LOW"
            ArmorType.MEDIUM_ARMOR -> "MEDIUM"
            ArmorType.HEAVY_ARMOR -> "HIGH"
            ArmorType.GREAT_ARMOR -> "GREAT"
        }
        val magicStr = when (type.magicResist) {
            MagicResistType.NONE -> "NONE"
            MagicResistType.LOW -> "LOW"
            MagicResistType.MEDIUM -> "MEDIUM"
            MagicResistType.HIGH -> "HIGH"
        }
        val speedStr = when {
            type.speed >= 70f -> "FAST"
            type.speed <= 35f -> "SLOW"
            else -> "MEDIUM"
        }

        val (desc, specials) = when (type) {
            EnemyType.GOBLIN -> Pair(
                "Small evil humanoids with no outstanding abilities.",
                "None"
            )
            EnemyType.ORC -> Pair(
                "Tough savages with light armor that serve as the backbone of the horde.",
                "Light Armor"
            )
            EnemyType.SHAMAN -> Pair(
                "Tribal healers resistant to magic attacks that mend wounded allies.",
                "Area Heal, High Magic Resistance"
            )
            EnemyType.OGRE -> Pair(
                "Big, ugly and tough humanoids. They obliterate everything in their path.",
                "Very High Health, Heavy Melee Damage"
            )
            EnemyType.BANDIT -> Pair(
                "Swift & agile opponents that can dispatch their enemies very quickly.",
                "Fast Movement, High Melee Attack"
            )
            EnemyType.BRIGAND -> Pair(
                "Armored outlaws that can withstand a lot of punishment.",
                "Medium Physical Armor"
            )
            EnemyType.MARAUDER -> Pair(
                "Hulking, well-trained armored outlaws, often leading brigands and bandits.",
                "Heavy Armor, Takes 3 Lives"
            )
            EnemyType.GIANT_SPIDER -> Pair(
                "Quick, vicious enemies resistant to magic that can overwhelm defenders.",
                "High Magic Resistance, Immune to Poison"
            )
            EnemyType.SPIDER_MATRIARCH -> Pair(
                "Beastly tough spiders that can spawn several spider hatchlings from eggs.",
                "Spawns Egg Sacs, High Magic Resistance"
            )
            EnemyType.SPIDER_HATCHLING -> Pair(
                "Tiny, fast-moving spiders hatched from Matriarch eggs.",
                "Very Fast, Medium Magic Resistance"
            )
            EnemyType.GARGOYLE -> Pair(
                "Vile winged creatures that can fly past barracks and artillery.",
                "Flying Unit"
            )
            EnemyType.SHADOW_ARCHER -> Pair(
                "Deadly accurate archers that will shoot at nearby soldiers in their path.",
                "Ranged Bow Attack, Low Magic Resistance"
            )
            EnemyType.DARK_KNIGHT -> Pair(
                "Full plated corrupted knights, almost impervious to physical damage.",
                "Heavy Physical Armor"
            )
            EnemyType.WULF -> Pair(
                "Very fast vicious creatures that can dodge melee attacks.",
                "Can Dodge Melee Attacks (30%), Very Fast"
            )
            EnemyType.WORG -> Pair(
                "Cunning fast beasts that can dodge melee attacks and resist magic.",
                "Dodge Melee (50%), Medium Magic Resistance"
            )
            EnemyType.GOLEM_HEAD -> Pair(
                "Mechanical dark-magic construct spawned by The Juggernaut.",
                "Medium Physical Armor"
            )
            EnemyType.JUGGERNAUT -> Pair(
                "A mindless unstoppable machine of destruction. Crushes any army.",
                "Boss, Launches Golem Missiles"
            )
            EnemyType.TROLL -> Pair(
                "Very tough warriors that can regenerate their wounds quickly.",
                "Health Regeneration"
            )
            EnemyType.TROLL_CHAMPION -> Pair(
                "Elite tribal warriors that throw vicious axes piercing through armor.",
                "Ranged True-Damage Axes, Regeneration"
            )
            EnemyType.TROLL_CHIEFTAIN -> Pair(
                "This chieftain can enrage his brethren, improving their combat prowess!",
                "War Drum Aura (Heals & Enrages Trolls)"
            )
            EnemyType.WINTER_WOLF -> Pair(
                "Cunning & deadly beasts that can defeat all but the best warriors.",
                "Fast, Dodge (30%), Medium Magic Resist"
            )
            EnemyType.YETI -> Pair(
                "Giant ape-man like creature that can obliterate scores of soldiers.",
                "Area Melee Slam, Very High Health"
            )
            EnemyType.ROCKET_RIDER -> Pair(
                "Rocket riders fly over the battlefield and turbo-boost when hit!",
                "Flying, Turbo Speed Boost"
            )
            EnemyType.DARK_SLAYER -> Pair(
                "Given to darkness, Slayers are the bane of anyone standing in their way.",
                "Great Physical Armor (95% Reduction)"
            )
            EnemyType.JT -> Pair(
                "An abominable legendary snow beast from the coldest reaches of the north.",
                "Boss, Freezes Towers, Crushes Soldiers"
            )
            EnemyType.SKELETON -> Pair(
                "Mindless automatons that obey the orders of their evil masters.",
                "Spawns from Graveyards, Immune to Poison"
            )
            EnemyType.SKELETON_KNIGHT -> Pair(
                "Corrupted bones of once great warriors animated by dark magic.",
                "Light Armor, Immune to Poison"
            )
            EnemyType.NECROMANCER -> Pair(
                "Masters of the dark arts, Necromancers summon undead minions.",
                "Summons Skeletons, Ranged Dark Bolt"
            )
            EnemyType.DEMON_SPAWN -> Pair(
                "Terrible infernal creatures that explode in hellfire when they die.",
                "Infernal Combustion on Death, Magic Resist"
            )
            EnemyType.DEMON_HOUND -> Pair(
                "Vicious fast hell-beasts that dodge attacks and explode upon death.",
                "Fast, Dodge (50%), Explodes on Death"
            )
            EnemyType.DEMON_IMP -> Pair(
                "Flying mischievous gargoyle-like devils that soar over defenses.",
                "Flying Unit"
            )
            EnemyType.DEMON_LORD -> Pair(
                "Leaders of the fiendish armies, shielding their minions with hellfire.",
                "Grants Infernal Shields, Explodes on Death"
            )
            EnemyType.MAGMA_ELEMENTAL -> Pair(
                "Made of rock and fire, these titans leave a trail of destruction.",
                "Area Melee Attack, Spawns from Lava"
            )
            EnemyType.VEZNAN -> Pair(
                "The evilest being you will ever meet. Master of the Dark Tower!",
                "Final Boss, Locks Towers, Summons Demons"
            )
            EnemyType.VEZNAN_DEMON -> Pair(
                "Vez'nan's true colossal demonic form unleashed in fury!",
                "Final Boss Phase 2, Area Hellfire"
            )
        }

        return EnemyEncyclopediaInfo(
            description = desc,
            specials = specials,
            armorLabel = armorStr,
            magicResistLabel = magicStr,
            speedLabel = speedStr
        )
    }
}

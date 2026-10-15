package com.game.td.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

enum class GameMode {
    CAMPAIGN,
    HEROIC,
    IRON
}

object SaveManager {
    var activeSlot: Int = 1

    private fun getPrefs(): Preferences {
        return Gdx.app.getPreferences("kr_save_slot_$activeSlot")
    }

    fun hasData(slot: Int): Boolean {
        return try {
            val p = Gdx.app.getPreferences("kr_save_slot_$slot")
            p.contains("unlocked_level")
        } catch (e: Exception) {
            false
        }
    }

    fun deleteSlot(slot: Int) {
        try {
            val p = Gdx.app.getPreferences("kr_save_slot_$slot")
            p.clear()
            p.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resetSlot(slot: Int) {
        try {
            val p = Gdx.app.getPreferences("kr_save_slot_$slot")
            p.clear()
            p.putInteger("unlocked_level", 1)
            for (i in 1..12) {
                p.putInteger("stars_lvl_$i", 0)
                p.putBoolean("heroic_lvl_$i", false)
                p.putBoolean("iron_lvl_$i", false)
            }
            p.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val unlockedLevel: Int
        get() {
            return try {
                getPrefs().getInteger("unlocked_level", 1)
            } catch (e: Exception) {
                1
            }
        }

    // Sao Campaign của màn (0..3)
    fun getLevelStars(levelNum: Int): Int {
        return try {
            getPrefs().getInteger("stars_lvl_$levelNum", 0)
        } catch (e: Exception) {
            0
        }
    }

    fun isHeroicCompleted(levelNum: Int): Boolean {
        return try {
            getPrefs().getBoolean("heroic_lvl_$levelNum", false)
        } catch (e: Exception) {
            false
        }
    }

    fun isIronCompleted(levelNum: Int): Boolean {
        return try {
            getPrefs().getBoolean("iron_lvl_$levelNum", false)
        } catch (e: Exception) {
            false
        }
    }

    // Tổng sao Campaign của slot hiện tại (tối đa 36 sao cho 12 màn)
    fun getCampaignStars(): Int {
        var total = 0
        for (i in 1..12) total += getLevelStars(i)
        return total
    }

    // Tổng sao thử thách (Heroic + Iron)
    fun getChallengeStars(): Int {
        var total = 0
        for (i in 1..12) {
            if (isHeroicCompleted(i)) total += 1
            if (isIronCompleted(i)) total += 1
        }
        return total
    }

    // Tổng tất cả các loại sao dùng để nâng cấp Upgrades
    fun getTotalStars(): Int {
        return getCampaignStars() + getChallengeStars()
    }

    // Thống kê cho từng slot tại Menu chính
    fun getSlotStats(slot: Int): Triple<Int, Int, Int> {
        return try {
            val p = Gdx.app.getPreferences("kr_save_slot_$slot")
            var campStars = 0
            var chalStars = 0
            for (i in 1..12) {
                campStars += p.getInteger("stars_lvl_$i", 0)
                if (p.getBoolean("heroic_lvl_$i", false)) chalStars++
                if (p.getBoolean("iron_lvl_$i", false)) chalStars++
            }
            Triple(campStars, chalStars, campStars + chalStars)
        } catch (e: Exception) {
            Triple(0, 0, 0)
        }
    }

    // Ghi nhận chiến thắng theo từng chế độ
    fun recordVictory(levelNum: Int, remainingLives: Int, mode: GameMode = GameMode.CAMPAIGN): Int {
        val p = getPrefs()
        var starsEarned = 0

        when (mode) {
            GameMode.CAMPAIGN -> {
                starsEarned = when {
                    remainingLives >= 18 -> 3
                    remainingLives >= 6 -> 2
                    remainingLives >= 1 -> 1
                    else -> 0
                }
                val oldStars = getLevelStars(levelNum)
                if (starsEarned > oldStars) {
                    p.putInteger("stars_lvl_$levelNum", starsEarned)
                }
                val currentUnlocked = unlockedLevel
                if (levelNum >= currentUnlocked && starsEarned > 0 && currentUnlocked < 12) {
                    p.putInteger("unlocked_level", levelNum + 1)
                }
            }
            GameMode.HEROIC -> {
                if (remainingLives >= 1) {
                    starsEarned = 1
                    p.putBoolean("heroic_lvl_$levelNum", true)
                }
            }
            GameMode.IRON -> {
                if (remainingLives >= 1) {
                    starsEarned = 1
                    p.putBoolean("iron_lvl_$levelNum", true)
                }
            }
        }
        p.flush()
        return starsEarned
    }
}

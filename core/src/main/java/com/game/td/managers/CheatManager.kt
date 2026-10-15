package com.game.td.managers

object CheatManager {
    var isCheatEnabled: Boolean = false
    var isCheatMenuOpen: Boolean = false
    var isHighDamageEnabled: Boolean = false

    fun toggleCheat() {
        isCheatEnabled = !isCheatEnabled
        if (!isCheatEnabled) {
            isCheatMenuOpen = false
            isHighDamageEnabled = false
        }
    }

    fun toggleCheatMenu() {
        isCheatMenuOpen = !isCheatMenuOpen
    }

    fun toggleHighDamage() {
        isHighDamageEnabled = !isHighDamageEnabled
    }

    fun addGold(amount: Int = 1000) {
        EconomyManager.addGold(amount)
    }
}

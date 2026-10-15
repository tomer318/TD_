package com.game.td.managers

object EconomyManager {
    var gold: Int = 150
    var lives: Int = 20

    fun reset() {
        gold = 150
        lives = 20
    }

    fun addGold(amount: Int) {
        gold += amount
    }

    fun spendGold(amount: Int): Boolean {
        if (gold >= amount) {
            gold -= amount
            return true
        }
        return false
    }

    fun takeDamage(amount: Int = 1) {
        lives = (lives - amount).coerceAtLeast(0)
    }

    val isGameOver: Boolean
        get() = lives <= 0
}

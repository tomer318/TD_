package com.game.td.ecs.components

import com.badlogic.ashley.core.Component

enum class ArmorType {
    NONE,
    LIGHT_ARMOR,   // Giảm 25% sát thương vật lý
    MEDIUM_ARMOR,  // Giảm 50% sát thương vật lý
    HEAVY_ARMOR,   // Giảm 75% sát thương vật lý
    GREAT_ARMOR    // Giảm 95% sát thương vật lý (Dark Slayer)
}

enum class MagicResistType {
    NONE,
    LOW,     // Giảm 25% sát thương phép
    MEDIUM,  // Giảm 50% sát thương phép
    HIGH     // Giảm 75% sát thương phép
}

enum class DamageType {
    PHYSICAL,
    MAGICAL,
    TRUE_DAMAGE
}

class ArmorComponent(
    var armorType: ArmorType = ArmorType.NONE,
    var magicResistType: MagicResistType = MagicResistType.NONE
) : Component {

    fun calculateDamage(incomingDamage: Float, damageType: DamageType): Float {
        if (damageType == DamageType.TRUE_DAMAGE) return incomingDamage

        var multiplier = 1.0f

        when (armorType) {
            ArmorType.NONE -> {}
            ArmorType.LIGHT_ARMOR -> {
                if (damageType == DamageType.PHYSICAL) multiplier *= 0.75f
            }
            ArmorType.MEDIUM_ARMOR -> {
                if (damageType == DamageType.PHYSICAL) multiplier *= 0.50f
            }
            ArmorType.HEAVY_ARMOR -> {
                if (damageType == DamageType.PHYSICAL) multiplier *= 0.25f
            }
            ArmorType.GREAT_ARMOR -> {
                if (damageType == DamageType.PHYSICAL) multiplier *= 0.05f // Kháng 95% vật lý
            }
        }

        when (magicResistType) {
            MagicResistType.NONE -> {}
            MagicResistType.LOW -> {
                if (damageType == DamageType.MAGICAL) multiplier *= 0.75f
            }
            MagicResistType.MEDIUM -> {
                if (damageType == DamageType.MAGICAL) multiplier *= 0.50f
            }
            MagicResistType.HIGH -> {
                if (damageType == DamageType.MAGICAL) multiplier *= 0.25f
            }
        }

        return incomingDamage * multiplier
    }
}

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
    PHYSICAL,    // Sát thương vật lý (bị cản bởi Physical Armor)
    MAGICAL,     // Sát thương phép thuật (bị cản bởi Magic Resistance)
    EXPLOSIVE,   // Sát thương nổ (xuyên 50% giáp vật lý, bỏ qua 100% kháng phép)
    TRUE,        // Sát thương chuẩn (bỏ qua mọi loại giáp)
    TRUE_DAMAGE  // Tương thích ngược
}

class ArmorComponent(
    var armorType: ArmorType = ArmorType.NONE,
    var magicResistType: MagicResistType = MagicResistType.NONE
) : Component {

    fun calculateDamage(incomingDamage: Float, damageType: DamageType): Float {
        if (damageType == DamageType.TRUE || damageType == DamageType.TRUE_DAMAGE) return incomingDamage

        var multiplier = 1.0f

        // 1. TÍNH TOÁN GIẢM TRỪ THEO GIÁP VẬT LÝ
        when (armorType) {
            ArmorType.NONE -> {}
            ArmorType.LIGHT_ARMOR -> {
                if (damageType == DamageType.PHYSICAL) multiplier *= 0.75f
                else if (damageType == DamageType.EXPLOSIVE) multiplier *= 0.875f // Bỏ qua 50% giáp (chỉ cản 12.5%)
            }
            ArmorType.MEDIUM_ARMOR -> {
                if (damageType == DamageType.PHYSICAL) multiplier *= 0.50f
                else if (damageType == DamageType.EXPLOSIVE) multiplier *= 0.75f  // Bỏ qua 50% giáp (chỉ cản 25%)
            }
            ArmorType.HEAVY_ARMOR -> {
                if (damageType == DamageType.PHYSICAL) multiplier *= 0.25f
                else if (damageType == DamageType.EXPLOSIVE) multiplier *= 0.625f // Bỏ qua 50% giáp (chỉ cản 37.5%)
            }
            ArmorType.GREAT_ARMOR -> {
                if (damageType == DamageType.PHYSICAL) multiplier *= 0.05f       // Kháng 95% vật lý
                else if (damageType == DamageType.EXPLOSIVE) multiplier *= 0.525f // Bỏ qua 50% giáp (chỉ cản 47.5%)
            }
        }

        // 2. TÍNH TOÁN GIẢM TRỪ THEO KHÁNG PHÉP (SÁT THƯƠNG NỔ BỎ QUA 100% KHÁNG PHÉP)
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

package com.middara.helper.data.model

enum class CardType(val displayName: String, val maxInHand: Int) {
    HERO_SHEET("Планшет героя", 1),
    WEAPON("Оружие", 2),
    WEAPON_UPGRADE("Улучшение оружия", 2),
    ARMOR("Броня", 1),
    ARMOR_UPGRADE("Улучшение брони", 1),
    TRINKET("Диковинка", 1),
    RELIC("Реликвия", 3),
    ACCESSORY("Аксессуар", 1),
    ITEM_UPGRADE("Улучшение предмета", 6),
    BACKPACK("Предмет рюкзака", 3),
    DISCIPLINE("Карта дисциплины", 8),
    CONSUMABLE("Одноразовый предмет", 10),
    FAMILIAR("Фамильяр", 4),
    COMPANION("Компаньон", 4)
}

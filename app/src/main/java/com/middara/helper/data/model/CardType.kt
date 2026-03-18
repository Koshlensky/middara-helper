package com.middara.helper.data.model

enum class CardType(val displayName: String, val maxInHand: Int) {
    HERO_SHEET("Планшет героя", 1),
    WEAPON("Оружие", 2),
    ARMOR("Броня", 1),
    TRINKET("Диковинка", 1),
    RELIC("Реликвия", 4),
    ITEM_UPGRADE("Улучшение предмета", 6),
    BACKPACK("Предмет рюкзака", 3),
    DISCIPLINE("Карта дисциплины", 8)
}

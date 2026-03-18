package com.middara.helper.data.model

import java.util.UUID

enum class WeaponMode { ONE_HANDED, TWO_HANDED }

data class HeroHand(
    val id: String = UUID.randomUUID().toString(),
    val heroName: String = "Новый герой",
    val heroSheet: EquippedCard? = null,
    val healthPoints: Int = 0,
    val damageTokens: Int = 0,
    val endurancePoints: Int = 0,
    val weaponMode: WeaponMode = WeaponMode.ONE_HANDED,
    val weapons: List<EquippedCard> = emptyList(),
    val weaponUpgrade1: EquippedCard? = null,
    val weaponUpgrade2: EquippedCard? = null,
    val armor: EquippedCard? = null,
    val armorUpgrade: EquippedCard? = null,
    val trinket: EquippedCard? = null,
    val relics: List<EquippedCard> = emptyList(),
    val accessory: EquippedCard? = null,
    val backpack: List<EquippedCard> = emptyList(),
    val disciplineCards: List<EquippedCard> = emptyList(),
    val consumables: List<EquippedCard> = emptyList(),
    val companions: List<EquippedCard> = emptyList()
)

fun HeroHand.totalBonusStats(): CardStats {
    val equippedCards = listOfNotNull(
        weaponUpgrade1, weaponUpgrade2,
        armor, armorUpgrade, trinket, accessory
    ) + weapons + relics + disciplineCards + consumables + companions
    return equippedCards.fold(CardStats()) { acc, ec ->
        CardStats(
            hp = acc.hp + ec.card.stats.hp,
            attack = acc.attack + ec.card.stats.attack,
            defense = acc.defense + ec.card.stats.defense,
            speed = acc.speed + ec.card.stats.speed,
            slots = acc.slots + ec.card.stats.slots
        )
    }
}

fun HeroHand.findEquippedCard(instanceId: String): EquippedCard? {
    if (heroSheet?.instanceId == instanceId) return heroSheet
    if (armor?.instanceId == instanceId) return armor
    if (armorUpgrade?.instanceId == instanceId) return armorUpgrade
    if (trinket?.instanceId == instanceId) return trinket
    if (accessory?.instanceId == instanceId) return accessory
    if (weaponUpgrade1?.instanceId == instanceId) return weaponUpgrade1
    if (weaponUpgrade2?.instanceId == instanceId) return weaponUpgrade2
    return (weapons + relics + backpack + disciplineCards + consumables + companions)
        .find { it.instanceId == instanceId }
}

fun HeroHand.updateEquippedCard(updated: EquippedCard): HeroHand {
    fun List<EquippedCard>.replaceById() = map { if (it.instanceId == updated.instanceId) updated else it }
    return copy(
        heroSheet = if (heroSheet?.instanceId == updated.instanceId) updated else heroSheet,
        armor = if (armor?.instanceId == updated.instanceId) updated else armor,
        armorUpgrade = if (armorUpgrade?.instanceId == updated.instanceId) updated else armorUpgrade,
        trinket = if (trinket?.instanceId == updated.instanceId) updated else trinket,
        accessory = if (accessory?.instanceId == updated.instanceId) updated else accessory,
        weaponUpgrade1 = if (weaponUpgrade1?.instanceId == updated.instanceId) updated else weaponUpgrade1,
        weaponUpgrade2 = if (weaponUpgrade2?.instanceId == updated.instanceId) updated else weaponUpgrade2,
        weapons = weapons.replaceById(),
        relics = relics.replaceById(),
        backpack = backpack.replaceById(),
        disciplineCards = disciplineCards.replaceById(),
        consumables = consumables.replaceById(),
        companions = companions.replaceById()
    )
}

fun HeroHand.removeEquippedCard(instanceId: String): HeroHand {
    fun List<EquippedCard>.removeById() = filter { it.instanceId != instanceId }
    return copy(
        heroSheet = if (heroSheet?.instanceId == instanceId) null else heroSheet,
        armor = if (armor?.instanceId == instanceId) null else armor,
        armorUpgrade = if (armorUpgrade?.instanceId == instanceId) null else armorUpgrade,
        trinket = if (trinket?.instanceId == instanceId) null else trinket,
        accessory = if (accessory?.instanceId == instanceId) null else accessory,
        weaponUpgrade1 = if (weaponUpgrade1?.instanceId == instanceId) null else weaponUpgrade1,
        weaponUpgrade2 = if (weaponUpgrade2?.instanceId == instanceId) null else weaponUpgrade2,
        weapons = weapons.removeById(),
        relics = relics.removeById(),
        backpack = backpack.removeById(),
        disciplineCards = disciplineCards.removeById(),
        consumables = consumables.removeById(),
        companions = companions.removeById()
    )
}

fun HeroHand.resetAllDischargedCards(): HeroHand {
    fun EquippedCard?.resetRotation() = this?.copy(rotationDegrees = 0f)
    fun List<EquippedCard>.resetRotation() = map { it.copy(rotationDegrees = 0f) }
    return copy(
        heroSheet = heroSheet.resetRotation(),
        armor = armor.resetRotation(),
        armorUpgrade = armorUpgrade.resetRotation(),
        trinket = trinket.resetRotation(),
        accessory = accessory.resetRotation(),
        weaponUpgrade1 = weaponUpgrade1.resetRotation(),
        weaponUpgrade2 = weaponUpgrade2.resetRotation(),
        weapons = weapons.resetRotation(),
        relics = relics.resetRotation(),
        backpack = backpack.resetRotation(),
        disciplineCards = disciplineCards.resetRotation(),
        consumables = consumables.resetRotation(),
        companions = companions.resetRotation()
    )
}

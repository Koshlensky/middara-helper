package com.middara.helper.data.model

import java.util.UUID

data class HeroHand(
    val id: String = UUID.randomUUID().toString(),
    val heroName: String = "Новый герой",
    val heroSheet: EquippedCard? = null,
    val damageTokens: Int = 0,
    val hpTokens: Int = 0,
    val weapons: List<EquippedCard> = emptyList(),
    val armor: EquippedCard? = null,
    val trinket: EquippedCard? = null,
    val relics: List<EquippedCard> = emptyList(),
    val backpack: List<EquippedCard> = emptyList(),
    val disciplineCards: List<EquippedCard> = emptyList()
)

fun HeroHand.findEquippedCard(instanceId: String): EquippedCard? {
    if (heroSheet?.instanceId == instanceId) return heroSheet
    if (armor?.instanceId == instanceId) return armor
    if (trinket?.instanceId == instanceId) return trinket
    return (weapons + relics + backpack + disciplineCards).find { it.instanceId == instanceId }
}

fun HeroHand.updateEquippedCard(updated: EquippedCard): HeroHand {
    fun List<EquippedCard>.replaceById() = map { if (it.instanceId == updated.instanceId) updated else it }
    return copy(
        heroSheet = if (heroSheet?.instanceId == updated.instanceId) updated else heroSheet,
        armor = if (armor?.instanceId == updated.instanceId) updated else armor,
        trinket = if (trinket?.instanceId == updated.instanceId) updated else trinket,
        weapons = weapons.replaceById(),
        relics = relics.replaceById(),
        backpack = backpack.replaceById(),
        disciplineCards = disciplineCards.replaceById()
    )
}

fun HeroHand.removeEquippedCard(instanceId: String): HeroHand {
    fun List<EquippedCard>.removeById() = filter { it.instanceId != instanceId }
    return copy(
        heroSheet = if (heroSheet?.instanceId == instanceId) null else heroSheet,
        armor = if (armor?.instanceId == instanceId) null else armor,
        trinket = if (trinket?.instanceId == instanceId) null else trinket,
        weapons = weapons.removeById(),
        relics = relics.removeById(),
        backpack = backpack.removeById(),
        disciplineCards = disciplineCards.removeById()
    )
}

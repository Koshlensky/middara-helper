package com.middara.helper.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.middara.helper.data.database.AppDatabase
import com.middara.helper.data.model.*
import com.middara.helper.data.repository.CardRepository
import com.middara.helper.data.repository.HeroHandRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HandViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val heroId: String = checkNotNull(savedStateHandle["heroId"])

    private val handRepository = HeroHandRepository(
        AppDatabase.getInstance(application).heroHandDao()
    )
    val cardRepository = CardRepository(application)

    private val _hand = MutableStateFlow(HeroHand(id = heroId))
    val hand: StateFlow<HeroHand> = _hand.asStateFlow()

    init {
        viewModelScope.launch {
            handRepository.getHandFlow(heroId)
                .catch { }
                .collect { _hand.value = it }
        }
    }

    private fun update(newHand: HeroHand) {
        _hand.value = newHand
        viewModelScope.launch { handRepository.save(newHand) }
    }

    fun updateHeroName(name: String) =
        update(_hand.value.copy(heroName = name.trim().ifBlank { "Герой" }))

    fun setHeroSheet(card: Card) =
        update(_hand.value.copy(heroSheet = EquippedCard(card = card)))

    fun setWeaponMode(mode: WeaponMode) {
        val hand = _hand.value
        val updated = when (mode) {
            WeaponMode.TWO_HANDED -> hand.copy(
                weaponMode = mode,
                weapons = hand.weapons.take(1),
                weaponUpgrade2 = null
            )
            WeaponMode.ONE_HANDED -> hand.copy(weaponMode = mode)
        }
        update(updated)
    }

    fun setWeapon(index: Int, card: Card) {
        val current = _hand.value.weapons.toMutableList()
        val newCard = EquippedCard(card = card)
        when {
            index < current.size -> current[index] = newCard
            else -> repeat(index - current.size + 1) { current.add(newCard) }
        }
        update(_hand.value.copy(weapons = current.toList()))
    }

    fun setWeaponUpgrade(index: Int, card: Card) {
        val equipped = EquippedCard(card = card)
        update(
            if (index == 0) _hand.value.copy(weaponUpgrade1 = equipped)
            else _hand.value.copy(weaponUpgrade2 = equipped)
        )
    }

    fun setArmor(card: Card) =
        update(_hand.value.copy(armor = EquippedCard(card = card)))

    fun setArmorUpgrade(card: Card) =
        update(_hand.value.copy(armorUpgrade = EquippedCard(card = card)))

    fun setTrinket(card: Card) =
        update(_hand.value.copy(trinket = EquippedCard(card = card)))

    fun addRelic(card: Card) {
        if (_hand.value.relics.size < CardType.RELIC.maxInHand)
            update(_hand.value.copy(relics = _hand.value.relics + EquippedCard(card = card)))
    }

    fun setAccessory(card: Card) =
        update(_hand.value.copy(accessory = EquippedCard(card = card)))

    fun addToBackpack(card: Card) {
        val backpack = _hand.value.backpack
        if (backpack.size < CardType.BACKPACK.maxInHand)
            update(_hand.value.copy(backpack = backpack + EquippedCard(card = card)))
    }

    fun addDisciplineCard(card: Card) {
        if (_hand.value.disciplineCards.size < CardType.DISCIPLINE.maxInHand)
            update(_hand.value.copy(disciplineCards = _hand.value.disciplineCards + EquippedCard(card = card)))
    }

    fun addConsumable(card: Card) =
        update(_hand.value.copy(consumables = _hand.value.consumables + EquippedCard(card = card)))

    fun addCompanion(card: Card) =
        update(_hand.value.copy(companions = _hand.value.companions + EquippedCard(card = card)))

    fun dischargeCard(instanceId: String) {
        val equipped = _hand.value.findEquippedCard(instanceId) ?: return
        val newRotation = if (equipped.rotationDegrees != 0f) 0f else 90f
        update(_hand.value.updateEquippedCard(equipped.copy(rotationDegrees = newRotation)))
    }

    fun flipCard(instanceId: String) {
        val equipped = _hand.value.findEquippedCard(instanceId) ?: return
        update(_hand.value.updateEquippedCard(equipped.toggleFlip()))
    }

    fun removeCard(instanceId: String) =
        update(_hand.value.removeEquippedCard(instanceId))

    fun endTurn() =
        update(_hand.value.resetAllDischargedCards())

    fun adjustHealth(delta: Int) =
        update(_hand.value.copy(healthPoints = (_hand.value.healthPoints + delta).coerceAtLeast(0)))

    fun adjustDamage(delta: Int) =
        update(_hand.value.copy(damageTokens = (_hand.value.damageTokens + delta).coerceAtLeast(0)))

    fun setEndurance(value: Int) =
        update(_hand.value.copy(endurancePoints = value.coerceIn(0, 5)))

    fun addCardByType(card: Card) {
        when (card.type) {
            CardType.HERO_SHEET -> setHeroSheet(card)
            CardType.WEAPON -> setWeapon(0, card)
            CardType.WEAPON_UPGRADE -> setWeaponUpgrade(0, card)
            CardType.ARMOR -> setArmor(card)
            CardType.ARMOR_UPGRADE -> setArmorUpgrade(card)
            CardType.TRINKET -> setTrinket(card)
            CardType.RELIC -> addRelic(card)
            CardType.ACCESSORY -> setAccessory(card)
            CardType.ITEM_UPGRADE -> addRelic(card)
            CardType.BACKPACK -> addToBackpack(card)
            CardType.DISCIPLINE -> addDisciplineCard(card)
            CardType.CONSUMABLE -> addConsumable(card)
            CardType.FAMILIAR, CardType.COMPANION -> addCompanion(card)
        }
    }
}

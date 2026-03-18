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

    fun addWeapon(card: Card) {
        val weapons = _hand.value.weapons
        if (weapons.size < CardType.WEAPON.maxInHand)
            update(_hand.value.copy(weapons = weapons + EquippedCard(card = card)))
    }

    fun setArmor(card: Card) =
        update(_hand.value.copy(armor = EquippedCard(card = card)))

    fun setTrinket(card: Card) =
        update(_hand.value.copy(trinket = EquippedCard(card = card)))

    fun addRelic(card: Card) =
        update(_hand.value.copy(relics = _hand.value.relics + EquippedCard(card = card)))

    fun addToBackpack(card: Card) {
        val backpack = _hand.value.backpack
        if (backpack.size < CardType.BACKPACK.maxInHand)
            update(_hand.value.copy(backpack = backpack + EquippedCard(card = card)))
    }

    fun addDisciplineCard(card: Card) =
        update(_hand.value.copy(disciplineCards = _hand.value.disciplineCards + EquippedCard(card = card)))

    fun rotateCard(instanceId: String) {
        val equipped = _hand.value.findEquippedCard(instanceId) ?: return
        update(_hand.value.updateEquippedCard(equipped.nextRotation()))
    }

    fun flipCard(instanceId: String) {
        val equipped = _hand.value.findEquippedCard(instanceId) ?: return
        update(_hand.value.updateEquippedCard(equipped.toggleFlip()))
    }

    fun removeCard(instanceId: String) =
        update(_hand.value.removeEquippedCard(instanceId))

    fun adjustDamage(delta: Int) =
        update(_hand.value.copy(damageTokens = (_hand.value.damageTokens + delta).coerceAtLeast(0)))

    fun adjustHp(delta: Int) =
        update(_hand.value.copy(hpTokens = (_hand.value.hpTokens + delta).coerceAtLeast(0)))

    fun addCardByType(card: Card) {
        when (card.type) {
            CardType.HERO_SHEET -> setHeroSheet(card)
            CardType.WEAPON -> addWeapon(card)
            CardType.ARMOR -> setArmor(card)
            CardType.TRINKET -> setTrinket(card)
            CardType.RELIC -> addRelic(card)
            CardType.ITEM_UPGRADE -> addRelic(card)
            CardType.BACKPACK -> addToBackpack(card)
            CardType.DISCIPLINE -> addDisciplineCard(card)
        }
    }
}

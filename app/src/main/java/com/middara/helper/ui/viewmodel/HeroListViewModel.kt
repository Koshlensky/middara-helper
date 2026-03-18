package com.middara.helper.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.middara.helper.data.database.AppDatabase
import com.middara.helper.data.model.HeroHand
import com.middara.helper.data.repository.HeroHandRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HeroListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HeroHandRepository(
        AppDatabase.getInstance(application).heroHandDao()
    )

    val heroes: StateFlow<List<HeroHand>> = repository.getAllHands()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createHero(name: String) {
        val newHand = HeroHand(heroName = name.trim().ifBlank { "Новый герой" })
        viewModelScope.launch { repository.save(newHand) }
    }

    fun deleteHero(heroId: String) {
        viewModelScope.launch { repository.delete(heroId) }
    }
}

package com.middara.helper.data.repository

import com.google.gson.Gson
import com.middara.helper.data.database.dao.HeroHandDao
import com.middara.helper.data.database.entity.HeroHandEntity
import com.middara.helper.data.model.HeroHand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class HeroHandRepository(private val dao: HeroHandDao) {

    private val gson = Gson()

    fun getAllHands(): Flow<List<HeroHand>> =
        dao.getAllHands().map { list ->
            list.map { gson.fromJson(it.handData, HeroHand::class.java) }
        }

    fun getHandFlow(id: String): Flow<HeroHand> =
        dao.getHandById(id)
            .filterNotNull()
            .map { gson.fromJson(it.handData, HeroHand::class.java) }

    suspend fun save(hand: HeroHand) {
        dao.upsertHand(
            HeroHandEntity(
                id = hand.id,
                heroName = hand.heroName,
                handData = gson.toJson(hand)
            )
        )
    }

    suspend fun delete(id: String) = dao.deleteHandById(id)
}

package com.middara.helper.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.middara.helper.data.model.Card
import com.middara.helper.data.model.CardType

class CardRepository(context: Context) {

    private val gson = Gson()

    val allCards: List<Card> by lazy {
        try {
            val json = context.assets.open("data/cards.json")
                .bufferedReader()
                .use { it.readText() }
            val type = object : TypeToken<List<Card>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getByType(cardType: CardType): List<Card> =
        allCards.filter { it.type == cardType }

    fun search(query: String): List<Card> {
        if (query.isBlank()) return allCards
        return allCards.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
    }

    fun searchByType(query: String, cardType: CardType?): List<Card> {
        val base = if (cardType != null) getByType(cardType) else allCards
        if (query.isBlank()) return base
        return base.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
    }
}

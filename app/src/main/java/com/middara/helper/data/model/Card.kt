package com.middara.helper.data.model

data class CardStats(
    val hp: Int = 0,
    val attack: Int = 0,
    val defense: Int = 0,
    val speed: Int = 0,
    val slots: Int = 0
)

data class Card(
    val id: String,
    val name: String,
    val type: CardType,
    val imageAsset: String = "",
    val description: String = "",
    val stats: CardStats = CardStats()
)

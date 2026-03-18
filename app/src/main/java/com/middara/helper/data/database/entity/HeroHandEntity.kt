package com.middara.helper.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hero_hands")
data class HeroHandEntity(
    @PrimaryKey val id: String,
    val heroName: String,
    val handData: String
)

package com.middara.helper.data.database.dao

import androidx.room.*
import com.middara.helper.data.database.entity.HeroHandEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HeroHandDao {

    @Query("SELECT * FROM hero_hands ORDER BY heroName ASC")
    fun getAllHands(): Flow<List<HeroHandEntity>>

    @Query("SELECT * FROM hero_hands WHERE id = :id")
    fun getHandById(id: String): Flow<HeroHandEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHand(hand: HeroHandEntity)

    @Query("DELETE FROM hero_hands WHERE id = :id")
    suspend fun deleteHandById(id: String)
}

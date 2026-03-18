package com.middara.helper.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.middara.helper.data.database.dao.HeroHandDao
import com.middara.helper.data.database.entity.HeroHandEntity

@Database(entities = [HeroHandEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun heroHandDao(): HeroHandDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "middara_helper.db"
                ).build().also { INSTANCE = it }
            }
    }
}

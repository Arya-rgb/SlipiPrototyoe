package com.slipi.slipiprototype.core.data.source.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.slipi.slipiprototype.core.data.source.local.entity.UserDataEntity

@Database(entities = [UserDataEntity::class], version = 1, exportSchema = false)
abstract class SlipiDatabase : RoomDatabase() {

    abstract fun slipiDao(): SlipiDao

    companion object {
        @Volatile
        private var INSTANCE: SlipiDatabase? = null

        fun getInstance(context: Context): SlipiDatabase =
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SlipiDatabase::class.java,
                    "slipi.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
    }

}
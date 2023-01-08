package com.slipi.slipiprototype.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.slipi.slipiprototype.core.data.source.local.entity.UserDataEntity

@Dao
interface SlipiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserData(user: UserDataEntity)
}
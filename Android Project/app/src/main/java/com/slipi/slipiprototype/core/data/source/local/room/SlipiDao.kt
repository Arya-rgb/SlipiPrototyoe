package com.slipi.slipiprototype.core.data.source.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.firebase.firestore.auth.User
import com.slipi.slipiprototype.core.data.source.local.entity.UserDataEntity

@Dao
interface SlipiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserData(user: UserDataEntity)

    @Query("SELECT * FROM user_data")
    fun getUserData(): LiveData<UserDataEntity>

}
package com.slipi.slipiprototype.core.data.source.local

import androidx.lifecycle.LiveData
import com.slipi.slipiprototype.core.data.source.local.entity.UserDataEntity
import com.slipi.slipiprototype.core.data.source.local.room.SlipiDao

class LocalDataSource private constructor(private val slipiDao: SlipiDao) {


    fun getUserDataFromLocalDB(): LiveData<UserDataEntity> = slipiDao.getUserData()
    fun insertData(userDataEntity: UserDataEntity) = slipiDao.insertUserData(userDataEntity)


    companion object {
        private var instance: LocalDataSource? = null

        fun getInstance(slipiDao: SlipiDao): LocalDataSource =
            instance ?: synchronized(this) {
                instance ?: LocalDataSource(slipiDao)
            }
    }


}
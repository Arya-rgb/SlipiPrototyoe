package com.slipi.slipiprototype.core.data.source.local

import com.slipi.slipiprototype.core.data.source.local.room.SlipiDao

class LocalDataSource private constructor(private val slipiDao: SlipiDao) {




    companion object {
        private var instance: LocalDataSource? = null

        fun getInstance(slipiDao: SlipiDao): LocalDataSource =
            instance ?: synchronized(this) {
                instance ?: LocalDataSource(slipiDao)
            }
    }


}
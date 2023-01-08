package com.slipi.slipiprototype.core.di

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.slipi.slipiprototype.core.data.SlipiRepository
import com.slipi.slipiprototype.core.data.source.local.LocalDataSource
import com.slipi.slipiprototype.core.data.source.local.room.SlipiDatabase
import com.slipi.slipiprototype.core.data.source.remote.RemoteDataSource
import com.slipi.slipiprototype.core.domain.repository.ISlipiRepository
import com.slipi.slipiprototype.core.domain.usecase.SlipiInteractor
import com.slipi.slipiprototype.core.domain.usecase.SlipiUseCase
import com.slipi.slipiprototype.core.utils.AppExecutors

object Injection {

    private fun provideRepository(context: Context): ISlipiRepository {
        val database = SlipiDatabase.getInstance(context)

        val remoteDataSource = RemoteDataSource.getInstance(Firebase)
        val localDataSource = LocalDataSource.getInstance(database.slipiDao())
        val appExecutors = AppExecutors()

        return SlipiRepository.getInstance(remoteDataSource, localDataSource, appExecutors)
    }

    fun provieSlipiUseCase(context: Context): SlipiUseCase {
        val repository = provideRepository(context)
        return SlipiInteractor(repository)
    }

}
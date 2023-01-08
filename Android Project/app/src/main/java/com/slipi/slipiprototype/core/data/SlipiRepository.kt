package com.slipi.slipiprototype.core.data

import com.slipi.slipiprototype.core.data.source.local.LocalDataSource
import com.slipi.slipiprototype.core.data.source.remote.RemoteDataSource
import com.slipi.slipiprototype.core.data.source.remote.response.DataUserResponse
import com.slipi.slipiprototype.core.domain.model.DataUserDomain
import com.slipi.slipiprototype.core.domain.model.UserData
import com.slipi.slipiprototype.core.domain.repository.ISlipiRepository
import com.slipi.slipiprototype.core.utils.AppExecutors
import com.slipi.slipiprototype.core.utils.DataMapper

class SlipiRepository private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
) : ISlipiRepository {

    override fun setAccountDataToFirestore(Userdata: DataUserDomain) {
        val dataResponse = DataMapper.mapDomainToResponse(Userdata)
        appExecutors.networkIO().execute { remoteDataSource.saveUserData(dataResponse) }
    }


    override fun setDataUserToDatabase(userdata: UserData) {
    }

    companion object {
        @Volatile
        private var instance: SlipiRepository? = null

        fun getInstance(
            remoteData: RemoteDataSource,
            localData: LocalDataSource,
            appExecutors: AppExecutors
        ): SlipiRepository =
            instance ?: synchronized(this) {
                instance ?: SlipiRepository(remoteData, localData, appExecutors)
            }
    }

}
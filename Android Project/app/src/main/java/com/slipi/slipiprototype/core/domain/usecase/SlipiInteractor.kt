package com.slipi.slipiprototype.core.domain.usecase

import androidx.lifecycle.LiveData
import com.slipi.slipiprototype.core.data.Resource
import com.slipi.slipiprototype.core.data.source.remote.response.DataUserResponse
import com.slipi.slipiprototype.core.domain.model.DataUserDomain
import com.slipi.slipiprototype.core.domain.model.UserData
import com.slipi.slipiprototype.core.domain.repository.ISlipiRepository

class SlipiInteractor(private val slipiRepository: ISlipiRepository) : SlipiUseCase {

    override fun setAccountDataToFirestore(userData: DataUserDomain) = slipiRepository.setAccountDataToFirestore(userData)

    override fun setDataUserToDatabase(userdata: UserData) = slipiRepository.setDataUserToDatabase(userdata)

    override fun getDataUser(): LiveData<Resource<DataUserDomain>> = slipiRepository.getDataUser()
}
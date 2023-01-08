package com.slipi.slipiprototype.core.domain.repository

import androidx.lifecycle.LiveData
import com.slipi.slipiprototype.core.data.Resource
import com.slipi.slipiprototype.core.data.source.remote.response.DataUserResponse
import com.slipi.slipiprototype.core.domain.model.DataUserDomain
import com.slipi.slipiprototype.core.domain.model.UserData

interface ISlipiRepository {
    fun setAccountDataToFirestore(Userdata: DataUserDomain)
    fun setDataUserToDatabase(userdata: UserData)
    fun getDataUser(): LiveData<Resource<DataUserDomain>>
}
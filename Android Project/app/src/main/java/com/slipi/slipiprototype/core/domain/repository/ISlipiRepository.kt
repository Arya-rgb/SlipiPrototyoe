package com.slipi.slipiprototype.core.domain.repository

import com.slipi.slipiprototype.core.data.source.remote.response.DataUserResponse
import com.slipi.slipiprototype.core.domain.model.DataUserDomain
import com.slipi.slipiprototype.core.domain.model.UserData

interface ISlipiRepository {
    fun setAccountDataToFirestore(Userdata: DataUserDomain)
    fun setDataUserToDatabase(userdata: UserData)
}
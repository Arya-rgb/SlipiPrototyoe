package com.slipi.slipiprototype.core.domain.usecase

import com.slipi.slipiprototype.core.data.source.remote.response.DataUserResponse
import com.slipi.slipiprototype.core.domain.model.DataUserDomain
import com.slipi.slipiprototype.core.domain.model.UserData

interface SlipiUseCase {

    fun setAccountDataToFirestore(userData: DataUserDomain)
    fun setDataUserToDatabase(userdata: UserData)

}
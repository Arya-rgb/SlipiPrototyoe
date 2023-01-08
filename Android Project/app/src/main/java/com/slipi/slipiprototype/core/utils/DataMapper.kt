package com.slipi.slipiprototype.core.utils

import com.slipi.slipiprototype.core.data.source.remote.network.ApiResponse
import com.slipi.slipiprototype.core.data.source.remote.response.DataUserResponse
import com.slipi.slipiprototype.core.domain.model.DataUserDomain

object DataMapper {

    fun mapDomainToResponse(input: DataUserDomain) = DataUserResponse(
        input.username,
        input.password,
        input.role,
        input.create_date,
        input.update_date
    )

    fun mapResponseToDomain(input: DataUserResponse) = DataUserDomain(
        input.username,
        input.password,
        input.role,
        input.create_date,
        input.update_date
    )

}
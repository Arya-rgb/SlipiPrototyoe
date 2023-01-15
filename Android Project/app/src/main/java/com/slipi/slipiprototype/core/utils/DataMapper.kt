package com.slipi.slipiprototype.core.utils

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.slipi.slipiprototype.core.data.source.local.entity.UserDataEntity
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

    fun mapResponseToEntity(input: DataUserResponse) = UserDataEntity(
        input.username,
        input.password,
        input.role,
        "created_date",
        "update_date"
    )


    fun mapEntitiesToDomain(input: UserDataEntity): DataUserDomain =
        DataUserDomain(
            input.username,
            input.password,
            input.role,
            Timestamp.now(),
            Timestamp.now()
        )
}
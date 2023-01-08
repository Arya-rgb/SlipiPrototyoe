package com.slipi.slipiprototype.core.data.source.remote.response

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.security.Timestamp

@IgnoreExtraProperties
data class DataUserResponse(
    var username: String? = null,
    var password: String? = null,
    var role: String? = null,
    var create_date: com.google.firebase.Timestamp,
    var update_date: com.google.firebase.Timestamp
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "username" to username,
            "password" to password,
            "role" to role,
            "create_date" to create_date,
            "update_date" to update_date,
        )
    }
}
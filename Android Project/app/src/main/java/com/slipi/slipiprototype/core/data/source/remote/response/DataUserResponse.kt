package com.slipi.slipiprototype.core.data.source.remote.response

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.security.Timestamp

@IgnoreExtraProperties
data class DataUserResponse(
    var username: String,
    var password: String,
    var role: String,
    var client: String,
    var create_date: com.google.firebase.Timestamp,
    var update_date: com.google.firebase.Timestamp
) {
    @Exclude
    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(
            "username" to username,
            "password" to password,
            "role" to role,
            "client" to client,
            "create_date" to create_date,
            "update_date" to update_date,
        )
    }
}
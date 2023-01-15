package com.slipi.slipiprototype.core.domain.model


data class DataUserDomain(
    var username: String,
    var password: String,
    var role: String,
    var client: String,
    var create_date: com.google.firebase.Timestamp,
    var update_date: com.google.firebase.Timestamp
)
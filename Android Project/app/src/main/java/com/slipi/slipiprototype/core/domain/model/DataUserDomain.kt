package com.slipi.slipiprototype.core.domain.model

import java.security.Timestamp

data class DataUserDomain(
    var username: String,
    var password: String,
    var role: String,
    var create_date: com.google.firebase.Timestamp,
    var update_date: com.google.firebase.Timestamp
)
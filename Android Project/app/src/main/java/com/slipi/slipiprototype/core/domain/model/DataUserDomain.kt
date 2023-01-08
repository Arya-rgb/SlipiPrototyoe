package com.slipi.slipiprototype.core.domain.model

import java.security.Timestamp

data class DataUserDomain(
    var username: String? = null,
    var password: String? = null,
    var role: String? = null,
    var create_date: com.google.firebase.Timestamp,
    var update_date: com.google.firebase.Timestamp
)
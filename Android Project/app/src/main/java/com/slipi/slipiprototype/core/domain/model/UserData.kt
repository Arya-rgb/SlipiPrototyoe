package com.slipi.slipiprototype.core.domain.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class UserData(
    var email: String,
    var role: String,
)
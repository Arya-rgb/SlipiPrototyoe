package com.slipi.slipiprototype.core.data.source.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
data class UserDataEntity(
    @PrimaryKey
    @ColumnInfo(name = "userEmail")
    var email: String,

    @ColumnInfo(name = "role")
    var role: String,
)
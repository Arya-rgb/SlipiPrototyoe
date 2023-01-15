package com.slipi.slipiprototype.core.data.source.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
data class UserDataEntity(
    @PrimaryKey
    @ColumnInfo(name = "username")
    var username: String,

    @ColumnInfo(name = "password")
    var password: String,

    @ColumnInfo(name = "role")
    var role: String,

    @ColumnInfo(name = "create_date")
    var create_date: String,

    @ColumnInfo(name = "update_date")
    var update_date: String,
)
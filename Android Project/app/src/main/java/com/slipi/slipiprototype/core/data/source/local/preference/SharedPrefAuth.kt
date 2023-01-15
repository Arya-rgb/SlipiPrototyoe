package com.slipi.slipiprototype.core.data.source.local.preference

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class SharedPrefAuth(context: Context) {

    private var userSharedPref: SharedPreferences =
        context.getSharedPreferences("data_user_local", Context.MODE_PRIVATE)

    fun saveState() {
        val edit : SharedPreferences.Editor = userSharedPref.edit()
        edit.putBoolean("login", true)
        edit.apply()
    }

    fun getState() : Boolean {
        return userSharedPref.getBoolean("login", false)
    }

}
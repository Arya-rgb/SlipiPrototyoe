package com.slipi.slipiprototype.core.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.slipi.slipiprototype.auth.register.RegisterViewModel
import com.slipi.slipiprototype.core.di.Injection
import com.slipi.slipiprototype.core.domain.usecase.SlipiUseCase
import com.slipi.slipiprototype.home.ui.home.HomeViewModel

class ViewModelFactory private constructor(private val slipiUseCase: SlipiUseCase) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(slipiUseCase) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provieSlipiUseCase(context)
                )
            }
    }
}
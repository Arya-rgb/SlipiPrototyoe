package com.slipi.slipiprototype.auth.register

import androidx.lifecycle.ViewModel
import com.slipi.slipiprototype.core.domain.model.DataUserDomain
import com.slipi.slipiprototype.core.domain.usecase.SlipiUseCase

class RegisterViewModel(private val slipiUseCase: SlipiUseCase) : ViewModel() {

    fun setDataToFireStore(userData: DataUserDomain) = slipiUseCase.setAccountDataToFirestore(userData)

}
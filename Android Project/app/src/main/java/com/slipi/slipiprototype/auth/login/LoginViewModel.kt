package com.slipi.slipiprototype.auth.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.slipi.slipiprototype.core.data.source.remote.response.DataUserResponse
import com.slipi.slipiprototype.core.domain.usecase.SlipiUseCase
import com.slipi.slipiprototype.core.utils.Constant
import kotlinx.coroutines.processNextEventInCurrentThread

class LoginViewModel(slipiUseCase: SlipiUseCase) : ViewModel() {

    val dataUser = slipiUseCase.getDataUser()

    private var db = Firebase.firestore

    private var id = FirebaseAuth.getInstance().currentUser

    val createLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val updateLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val getLiveData: MutableLiveData<DataUserResponse> by lazy {
        MutableLiveData<DataUserResponse>()
    }

    val deleteLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun getData() {
        val docRef = db.collection(Constant.userDb).document(id!!.uid)
        docRef.get().addOnSuccessListener {
                val product = DataUserResponse(
                    username = it.data?.get("username") as String,
                    password = "none",
                    role = it.data?.get("role") as String,
                    create_date = it.data?.get("create_date") as Timestamp,
                    update_date = it.data?.get("update_date") as Timestamp
                )
                getLiveData.postValue(product)

        }.addOnFailureListener {
            Log.d("get", it.localizedMessage!!)
            getLiveData.postValue(null)
        }
    }

}
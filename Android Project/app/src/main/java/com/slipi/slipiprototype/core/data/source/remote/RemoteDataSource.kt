package com.slipi.slipiprototype.core.data.source.remote

import android.provider.ContactsContract.Data
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.slipi.slipiprototype.core.data.source.remote.network.ApiResponse
import com.slipi.slipiprototype.core.data.source.remote.response.DataUserResponse
import com.slipi.slipiprototype.core.utils.Constant

class RemoteDataSource private constructor(private var db: Firebase) {

    fun saveUserData(userData: DataUserResponse) {

        val resultData = MutableLiveData<ApiResponse<DataUserResponse>>()

        val docRef = db.firestore.collection(Constant.userDb)
        docRef.add(userData.toMap()).addOnSuccessListener {
            resultData.value = ApiResponse.Success(userData)

        }.addOnFailureListener { error ->
            Log.d("error_create_data", error.localizedMessage!!)
            resultData.value = ApiResponse.Error(error.toString())
        }

    }

    fun getUserData(): LiveData<ApiResponse<DataUserResponse>> {

        val resultData = MutableLiveData<ApiResponse<DataUserResponse>>()

        val docref = db.firestore.collection(Constant.userDb)
        docref.get().addOnSuccessListener {
            for (item in it.documents) {
                val dataModelResponse = DataUserResponse(
                    item.data!!["username"] as String?,
                    item.data!!["password"] as String?,
                    item.data!!["role"] as String?,
                    item.data!!["create_date"] as Timestamp,
                    item.data!!["update_date"] as Timestamp
                )
                resultData.value = ApiResponse.Success(dataModelResponse)
            }

        }.addOnFailureListener { error ->
            resultData.value = ApiResponse.Error(error.toString())
        }

        if (resultData.value == null) {
            resultData.value = ApiResponse.Empty
        }

        return resultData
    }

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null
        fun getInstance(db: Firebase): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource(db)
            }
    }

}


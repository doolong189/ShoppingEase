package com.hoanglong180903.shoppingease.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hoanglong180903.shoppingease.app.MyApplication
import com.hoanglong180903.shoppingease.model.Product
import com.hoanglong180903.shoppingease.model.User
import com.hoanglong180903.shoppingease.repository.SignUpRepository
import com.hoanglong180903.shoppingease.utils.Resource
import com.hoanglong180903.shoppingease.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpViewModel(private val application: Application)  : AndroidViewModel(application) {
    private var repository : SignUpRepository = SignUpRepository()
    private var handlejob : Job? = null
    private var mUser = MutableLiveData<User>()
    var isSuccess = MutableLiveData<Boolean>()
    var errormsg = MutableLiveData<String>()
    var loading = MutableLiveData<Boolean>()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val isSuccessful = MutableLiveData<Boolean>()
    private var exceptionHandle = CoroutineExceptionHandler { _, throwable ->
        "Exception : ${throwable.localizedMessage}"
    }
    private fun onError(msg : String){
        errormsg.value = msg
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        handlejob?.cancel()
    }
//    fun register(user : User){
//        handlejob = CoroutineScope(Dispatchers.IO+exceptionHandle).launch {
//            val response = repository.register(user)
//            withContext(Dispatchers.Main){
//                if (response.isSuccessful){
//                    mUser.postValue(response.body())
//                    Log.e("input_user",mUser.toString())
//                    loading.value = false
//                    isSuccess.value = false
//                }else{
//                    onError("Error : ${response.message()}")
//                }
//            }
//        }
//    }

    fun register(user : User) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())){
                emit(Resource.success(repository.register(user)))
            }else{
                emit(Resource.error(null,"Not internet"))
            }
        }catch (ex : Exception){
            emit(Resource.error(null,ex.message ?: "Error"))
        }
    }

}
package com.hoanglong180903.shoppingease.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.hoanglong180903.shoppingease.model.LoginResponse
import com.hoanglong180903.shoppingease.repository.SignInRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.hoanglong180903.shoppingease.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInViewModel()  : ViewModel() {
    private var repository : SignInRepository = SignInRepository()

    private var handlejob : Job? = null
    var mUserLogin = MutableLiveData<LoginResponse>()
    var isSuccess = MutableLiveData<Boolean>()
    var errormsg = MutableLiveData<String>()
    var loading = MutableLiveData<Boolean>()
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
    fun login(email: String , password :String){
        handlejob = CoroutineScope(Dispatchers.IO+exceptionHandle).launch {
            val response = repository.login(email,password)
            withContext(Dispatchers.Main){
                if (response.isSuccessful){
                    mUserLogin.postValue(response.body())
                    loading.value = false
                    isSuccess.value = false
                }else{
                    onError("Error : ${response.message()}")
                    loading.value = true
                    isSuccess.value = true
                }
            }
        }
    }
}
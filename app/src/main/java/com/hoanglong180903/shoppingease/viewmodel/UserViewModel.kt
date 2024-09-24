package com.hoanglong180903.shoppingease.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.hoanglong180903.shoppingease.model.LoginResponse
import com.hoanglong180903.shoppingease.repository.SignInRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.hoanglong180903.shoppingease.R
import com.hoanglong180903.shoppingease.app.MyApplication
import com.hoanglong180903.shoppingease.model.User
import com.hoanglong180903.shoppingease.repository.UserRepository
import com.hoanglong180903.shoppingease.utils.Resource
import com.hoanglong180903.shoppingease.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val application: Application)  : AndroidViewModel(application) {
    private var repository : UserRepository = UserRepository()

    private var handlejob : Job? = null
    var mUser = MutableLiveData<List<User>>()
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
    fun getUsers(id : String){
        handlejob = CoroutineScope(Dispatchers.IO+exceptionHandle).launch {
            val response = repository.getUsers(id)
            withContext(Dispatchers.Main){
                if (response.isSuccessful){
                    mUser.postValue(response.body())
                }else{
                    onError("Error : ${response.message()}")
                }
            }
        }
    }

    fun updateLocation(id : String , loc: List<Double>) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                emit(Resource.success(repository.updateLocation(id,loc)))
            }else{
                emit(Resource.error(null,"Not internet"))
            }
        }catch (ex : Exception){
            emit(Resource.error(null,ex.message ?: "Error"))
        }
    }

    class UserViewModelFactory(val application : Application) : ViewModelProvider.Factory{

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(UserViewModel::class.java)){
                UserViewModel(application) as T
            }else{
                throw IllegalArgumentException("viewmodel not found")
            }
        }
    }
}


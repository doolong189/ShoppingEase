package com.hoanglong180903.shoppingease.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hoanglong180903.shoppingease.model.LoginResponse
import com.hoanglong180903.shoppingease.model.User
import com.hoanglong180903.shoppingease.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class SignInRepository {
    suspend fun login(email: String , password : String) = RetrofitInstance.api.requestLoginUser(email,password)

//    suspend fun login(email:String , password : String) : Response<LoginResponse>{
//        val response = RetrofitInstance.api.requestLoginUser(email,password)
//        if (response.isSuccessful){
//            Log.d("login" , "Success Login")
//            Log.e("login",response.body().toString())
//        }else{
//            Log.d("login" , "Success Failed")
//            Log.e("login",response.body().toString())
//        }
//        return response
//    }
}



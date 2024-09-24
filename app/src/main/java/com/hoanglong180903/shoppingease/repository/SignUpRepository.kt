package com.hoanglong180903.shoppingease.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hoanglong180903.shoppingease.model.User
import com.hoanglong180903.shoppingease.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class SignUpRepository {

    suspend fun register(user: User) = RetrofitInstance.api.requestRegisterUser(user)
}



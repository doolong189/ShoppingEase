package com.hoanglong180903.shoppingease.repository


import com.google.firebase.database.FirebaseDatabase
import com.hoanglong180903.shoppingease.network.RetrofitInstance


class UserRepository {
    private val database = FirebaseDatabase.getInstance()
    suspend fun getUsers(id : String) = RetrofitInstance.api.requestGetAllUser(id)
    suspend fun updateLocation(id : String, loc : List<Double>) = RetrofitInstance.api.requestUpdateLocation(id,loc)
}



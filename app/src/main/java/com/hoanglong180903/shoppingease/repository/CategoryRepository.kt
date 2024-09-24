package com.hoanglong180903.shoppingease.repository

import android.app.Application
import com.hoanglong180903.shoppingease.network.RetrofitInstance

class CategoryRepository(application: Application) {

    suspend fun getCategory() = RetrofitInstance.api.requestGetBrand()

}
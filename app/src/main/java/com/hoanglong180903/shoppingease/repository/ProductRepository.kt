package com.hoanglong180903.shoppingease.repository

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.hoanglong180903.shoppingease.database.DAO
import com.hoanglong180903.shoppingease.database.DBHelper
import com.hoanglong180903.shoppingease.model.LocationResponse
import com.hoanglong180903.shoppingease.model.Product
import com.hoanglong180903.shoppingease.network.RetrofitInstance
import java.util.Date

class ProductRepository(app : Application) {
    private val dao : DAO

    init {
        val dbHelper : DBHelper = DBHelper.getDatabase(app)
        dao = dbHelper.getDao()
    }
    suspend fun addProduct(product: Product) = RetrofitInstance.api.requestAddProduct(product)
    suspend fun getProduct(id : String) = RetrofitInstance.api.requestGetProduct(id)
    suspend fun getDetailProduct(id : String) = RetrofitInstance.api.requestGetDetailProduct(id)
    suspend fun foundLocationProduct(locationResponse: LocationResponse) = RetrofitInstance.api.requestFoundLocationProducts(locationResponse)
    suspend fun getProductWithCategory(id : String , idBrand: String) = RetrofitInstance.api.requestGetProductWithCategory(id,idBrand)
    suspend fun insertAllProductToDatabase(product : List<Product>) = dao.insertAllProduct(product)

    suspend fun deleteAllProductToDatabase() = dao.deleteAllProduct()

    fun getAllProductFromDatabase() : LiveData<List<Product>> = dao.getAllProduct()

}
package com.hoanglong180903.shoppingease.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hoanglong180903.shoppingease.model.Product

@Dao
interface DAO {

    @Insert
    fun insertAllProduct(product: List<Product>)

    @Query("Select * from product_table")
    fun getAllProduct() : LiveData<List<Product>>

    @Query("Delete from product_table")
    fun deleteAllProduct()
}
package com.hoanglong180903.shoppingease.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "product_table")
data class Product(
    @PrimaryKey(autoGenerate = false)
    val _id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val description: String = "",
    val image: String = "",
    @Embedded(prefix = "user_")
    val idUser: User? = null,
    @Embedded(prefix = "brand_")
    val idBrand: Brand? = null,
)
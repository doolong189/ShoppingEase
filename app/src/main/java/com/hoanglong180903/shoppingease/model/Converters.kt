package com.hoanglong180903.shoppingease.model

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromUser(user: User?): String {
        return Gson().toJson(user)
    }
    @TypeConverter
    fun toUser(userString: String?): User? {
        return if (userString == null) null else Gson().fromJson(userString, User::class.java)
    }


    @TypeConverter
    fun fromBrand(brand: Brand?): String {
        return Gson().toJson(brand)
    }

    @TypeConverter
    fun toBrand(brandString: String?): Brand? {
        return if (brandString == null) null else Gson().fromJson(brandString, Brand::class.java)
    }

    @TypeConverter
    fun fromListToString(list: List<Double>?): String? {
        return list?.joinToString(separator = ",")
    }

    @TypeConverter
    fun fromStringToList(data: String?): List<Double>? {
        return data?.split(",")?.map { it.toDouble() }
    }
}
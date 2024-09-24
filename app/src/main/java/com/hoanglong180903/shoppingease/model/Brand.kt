package com.hoanglong180903.shoppingease.model

data class Brand(
    val _id : String = "",
    val name : String = "",
    val image: String = ""
){
    override fun toString(): String {
        return name
    }
}
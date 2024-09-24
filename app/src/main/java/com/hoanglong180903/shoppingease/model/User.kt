package com.hoanglong180903.shoppingease.model

data class User (
    var _id:String = "",
    var name:String = "",
    var address : String = "",
    var password:String = "",
    var email:String = "",
    var phone:String = "",
    var image: String = "",
    val loc: List<Double>? = null
)
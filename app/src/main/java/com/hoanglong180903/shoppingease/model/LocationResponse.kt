package com.hoanglong180903.shoppingease.model

data class LocationResponse (
    val loc: List<Double>, // Longitude, Latitude
    val radius: Int,
    val userId: String
)
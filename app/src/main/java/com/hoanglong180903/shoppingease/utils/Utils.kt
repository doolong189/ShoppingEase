package com.hoanglong180903.shoppingease.utils

import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import com.hoanglong180903.shoppingease.app.MyApplication
import java.text.DecimalFormat

object Utils {
    fun hasInternetConnection(application: MyApplication): Boolean {
        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    fun formatPrice(price: Double): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(price)
    }

    fun calculateDistance(LA_LOCATION: LatLng,LA_LOCATION1: LatLng ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            LA_LOCATION.latitude,
            LA_LOCATION.longitude,
            LA_LOCATION1.latitude,
            LA_LOCATION1.longitude,
            results
        )
        return results[0] // Khoảng cách trả về tính bằng mét
    }
}
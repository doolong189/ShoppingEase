package com.hoanglong180903.shoppingease.utils

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.ContactsContract
import android.util.Log

class Functions{
    companion object{
        fun hasInternetConnection(application: Application):Boolean{
            val connectivityManager=application.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                Log.d(ContentValues.TAG,"SDK_M")
                val activeNetwork=connectivityManager.activeNetwork?:return false
                val capabilities=connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
                return when{
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
                    else ->false
                }
            }else {
                Log.d(ContentValues.TAG,"SDK_N")
                connectivityManager.activeNetworkInfo?.run {
                    return when(type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ContactsContract.CommonDataKinds.Email.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
            return false
        }
    }
}
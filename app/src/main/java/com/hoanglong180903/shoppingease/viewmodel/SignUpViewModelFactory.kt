package com.hoanglong180903.shoppingease.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hoanglong180903.shoppingease.repository.SignUpRepository

class SignUpViewModelFactory(private val application: Application) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SignUpViewModel::class.java)){
            SignUpViewModel(application) as T
        }else{
            throw IllegalArgumentException("viewmodel not found")
        }
    }
}
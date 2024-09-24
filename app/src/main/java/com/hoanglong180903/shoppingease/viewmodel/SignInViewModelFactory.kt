package com.hoanglong180903.shoppingease.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hoanglong180903.shoppingease.repository.SignUpRepository

class SignInViewModelFactory() : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SignInViewModel::class.java)){
            SignInViewModel() as T
        }else{
            throw IllegalArgumentException("viewmodel not found")
        }
    }
}
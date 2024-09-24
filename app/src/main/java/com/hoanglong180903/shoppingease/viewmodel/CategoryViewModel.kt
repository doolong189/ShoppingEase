package com.hoanglong180903.shoppingease.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.hoanglong180903.shoppingease.app.MyApplication
import com.hoanglong180903.shoppingease.repository.CategoryRepository
import com.hoanglong180903.shoppingease.utils.Resource
import com.hoanglong180903.shoppingease.utils.Utils
import kotlinx.coroutines.Dispatchers

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : CategoryRepository = CategoryRepository(application)

    fun getCategory() = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())){
                emit(Resource.success(repository.getCategory()))
            }else{
                emit(Resource.error(null,"Not internet"))
            }
        }catch (ex : Exception){
            emit(Resource.error(null,ex.message ?: "Error data"))
        }
    }

    class CategoryViewModelFactory(private val application: Application) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
             if (modelClass.isAssignableFrom(CategoryViewModel::class.java)){
                 return CategoryViewModel(application) as T
            }else{
                throw IllegalArgumentException("Viewmodel note found ")
            }
        }
    }
}
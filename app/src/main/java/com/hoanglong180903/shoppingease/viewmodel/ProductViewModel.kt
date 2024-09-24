package com.hoanglong180903.shoppingease.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.hoanglong180903.shoppingease.R
import com.hoanglong180903.shoppingease.app.MyApplication
import com.hoanglong180903.shoppingease.model.LocationResponse
import com.hoanglong180903.shoppingease.model.Product
import com.hoanglong180903.shoppingease.repository.ProductRepository
import com.hoanglong180903.shoppingease.utils.Resource
import com.hoanglong180903.shoppingease.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) :AndroidViewModel(application){

    private val repository : ProductRepository = ProductRepository(application)
    var loading = MutableLiveData<Boolean>()
    val productData: MutableLiveData<Resource<List<Product>>> = MutableLiveData()
    fun getProduct(id : String) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                emit(Resource.success(repository.getProduct(id)))
            }else{
                emit(Resource.error(null,"Not internet"))
            }
        }catch (ex : Exception){
            emit(Resource.error(null,ex.message ?: "Error"))
        }
    }

    fun getDetailProduct(id : String) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())){
                emit(Resource.success(repository.getDetailProduct(id)))
            }else{
                emit(Resource.error(null,"Not internet"))
            }
        }catch (ex : Exception){
            emit(Resource.error(null,ex.message ?: "Error"))
        }
    }

    fun foundLocationProduct(locationResponse: LocationResponse) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())){
                emit(Resource.success(repository.foundLocationProduct(locationResponse)))
            }else{
                emit(Resource.error(null,"Not internet"))
            }
        }catch (ex : Exception){
            emit(Resource.error(null,ex.message ?: "Error"))

        }
    }

    fun getProductWithCategory(id:String , idBrand : String) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try{
            if (Utils.hasInternetConnection(getApplication<MyApplication>())){
                emit(Resource.success(repository.getProductWithCategory(id,idBrand)))
            }else{
                emit(Resource.error(null,"Not internet"))
            }
        }catch (ex:Exception){
            emit(Resource.error(null,ex.message ?: "Error"))
        }
    }

    fun addProduct(product : Product) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                emit(Resource.success(repository.addProduct(product)))
            }else{
                emit(Resource.error(null,"Not internet"))
            }
        }catch (ex : Exception){
            emit(Resource.error(null,ex.message ?: "Error"))
        }
    }

    fun insertAllProductToDatabase(product : List<Product>) = viewModelScope.launch {
        repository.insertAllProductToDatabase(product)
    }

    fun deleteAllProductToDatabase() = viewModelScope.launch {
        repository.deleteAllProductToDatabase()
    }

    fun getAllProduct() : LiveData<List<Product>> = repository.getAllProductFromDatabase()


    class ProductViewModelFactory(private val application : Application) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProductViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return ProductViewModel(application) as T
            }else{
                throw IllegalArgumentException("viewmodel not found")
            }

        }
    }
}
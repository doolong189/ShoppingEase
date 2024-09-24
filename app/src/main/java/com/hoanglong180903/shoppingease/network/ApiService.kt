package com.hoanglong180903.shoppingease.network

import com.hoanglong180903.shoppingease.model.Brand
import com.hoanglong180903.shoppingease.model.LocationResponse
import com.hoanglong180903.shoppingease.model.LoginResponse
import com.hoanglong180903.shoppingease.model.Product
import com.hoanglong180903.shoppingease.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("/user/register")
    suspend fun requestRegisterUser(@Body user: User) : User

    @FormUrlEncoded
    @POST("/user/login")
    suspend fun requestLoginUser(
        @Field("email") email:String,
        @Field("password") password: String) : Response<LoginResponse>

    @GET("/user/getUsers/{id}")
    suspend fun requestGetAllUser(@Path("id") id: String) : Response<List<User>>

    @POST("/user/update-location/{id}")
    suspend fun requestUpdateLocation(
        @Path("id") id:String,
        @Body loc : List<Double>) : LoginResponse

    @POST("/product/addProduct")
    suspend fun requestAddProduct(@Body product: Product) : Product

    @GET("/product/getProduct/{id}")
    suspend fun requestGetProduct(@Path("id") id : String): List<Product>

    @GET("/product/getDetailProduct/{id}")
    suspend fun requestGetDetailProduct(@Path("id") id: String): Product

    @GET("/brand/getBrand")
    suspend fun requestGetBrand() : List<Brand>

    @POST("/product/searchLocationProduct")
    suspend fun requestFoundLocationProducts(@Body location : LocationResponse) : List<Product>

    @GET("/product/getProductWithCategory/{id}")
    suspend fun requestGetProductWithCategory(@Path("id") id:String,
                                              @Query("idBrand") idBrand : String) : List<Product>
}
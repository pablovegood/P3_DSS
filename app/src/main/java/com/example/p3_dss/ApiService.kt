package com.example.p3_dss

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("api/productos")
    fun getProductos(@Query("query") query: String? = null): Call<List<Product>>

    @GET("api/cart")
    fun getCart(): Call<CartResponse>

    @POST("api/cart/add/{id}")
    fun addToCart(@Path("id") id: Long): Call<CartResponse>

    @POST("api/cart/remove/{id}")
    fun removeFromCart(@Path("id") id: Long): Call<CartResponse>

    @POST("api/cart/clear")
    fun clearCart(): Call<CartResponse>

    @POST("api/cart/checkout")
    fun checkout(): Call<CartResponse>

    @POST("api/productos")
    fun adminAddProducto(
        @Header("Authorization") auth: String,
        @Body req: ProductCreateRequest
    ): Call<Product>

    @DELETE("api/productos/{id}")
    fun adminDeleteProducto(
        @Header("Authorization") auth: String,
        @Path("id") id: Long
    ): Call<ResponseModel>

    @GET("api/auth/me")
    fun authMe(@Header("Authorization") auth: String): Call<Map<String, Any>>
}

package com.example.p3_dss

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("productoId")
    val productoId: Long,

    @SerializedName("productoNombre")
    val productoNombre: String,

    @SerializedName("price")
    val productoPrecio: Double
)

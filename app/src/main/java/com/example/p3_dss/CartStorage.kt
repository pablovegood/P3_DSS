package com.example.p3_dss

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartStorage {
    private const val PREF = "cart_prefs"
    private const val KEY = "cart_json"
    private val gson = Gson()

    // Cargar carrito
    fun load(context: Context): MutableList<Product> {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val json = sp.getString(KEY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Product>>() {}.type
        return gson.fromJson<MutableList<Product>>(json, type) ?: mutableListOf()
    }

    // Guardar carrito
    private fun save(context: Context, items: List<Product>) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val json = gson.toJson(items)
        sp.edit()
            .putString(KEY, json)
            .apply()                    // <-- IMPORTANTE
    }

    // Añadir producto
    fun add(context: Context, product: Product) {
        val items = load(context)
        items.add(product)
        save(context, items)
    }

    // Eliminar por id
    fun removeById(context: Context, productoId: Long) {
        val items = load(context)
        val newItems = items.filterNot { it.productoId == productoId }
        save(context, newItems)
    }

    // Vaciar
    fun clear(context: Context) {
        save(context, emptyList())
    }

    // Total
    fun total(items: List<Product>): Double {
        return items.sumOf { it.productoPrecio }    // usa aquí el campo de precio que tengas
    }
}

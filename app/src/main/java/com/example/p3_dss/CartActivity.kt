package com.example.p3_dss

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartActivity : ComponentActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var tvItemsCount: TextView
    private lateinit var adapter: CartAdapter

    private var items: MutableList<Product> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        rv = findViewById(R.id.rvCart)
        tvTotal = findViewById(R.id.tvTotal)
        tvItemsCount = findViewById(R.id.tvItemsCount)

        rv.layoutManager = LinearLayoutManager(this)

        items = CartStorage.load(this)
        Log.d("CartActivity", "Carrito cargado: ${items.size} elementos")

        adapter = CartAdapter(items, onRemove = { product ->
            CartStorage.removeById(this, product.productoId)
            recargarCarrito()
            Toast.makeText(this, "Eliminado: ${product.productoNombre}", Toast.LENGTH_SHORT).show()
        }, showRemove = true)

        rv.adapter = adapter

        findViewById<Button>(R.id.btnVaciar).setOnClickListener {
            CartStorage.clear(this)
            recargarCarrito()
            Toast.makeText(this, "Carrito vaciado", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnPagar).setOnClickListener {
            if (items.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, CheckoutActivity::class.java))
        }

        // Bottom nav (ajusta ids según tu bottom_menu)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_cart
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_products -> { startActivity(Intent(this, MainActivity::class.java)); true }
                R.id.nav_cart -> true
                R.id.nav_map -> { startActivity(Intent(this, MapActivity::class.java)); true }
                else -> false
            }
        }

        pintarTotales()
    }

    override fun onResume() {
        super.onResume()
        recargarCarrito()
    }

    private fun recargarCarrito() {
        items = CartStorage.load(this)
        adapter.setData(items)
        pintarTotales()
    }

    private fun pintarTotales() {
        val total = CartStorage.total(items)
        tvTotal.text = String.format("Total: %.2f €", total)
        tvItemsCount.text = "Elementos: ${items.size}"
    }
}

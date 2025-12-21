package com.example.p3_dss

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProductsActivity : ComponentActivity() {

    private lateinit var adapter: AdminProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_products)

        if (!AdminSession.isLoggedIn(this)) {
            Toast.makeText(this, "No autorizado. Inicia sesión.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarAdmin)
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel)
        toolbar.setNavigationOnClickListener { finish() }

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etPrecio = findViewById<EditText>(R.id.etPrecio)
        val btnAdd = findViewById<Button>(R.id.btnAdd)

        val rv = findViewById<RecyclerView>(R.id.rvAdminProductos)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = AdminProductAdapter(mutableListOf()) { product ->
            borrarProducto(product.productoId)
        }
        rv.adapter = adapter

        btnAdd.setOnClickListener {
            val nombre = etNombre.text?.toString()?.trim().orEmpty()
            val precioStr = etPrecio.text?.toString()?.trim().orEmpty()

            if (nombre.isBlank()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val precio = precioStr.toDoubleOrNull()
            if (precio == null || precio <= 0.0) {
                Toast.makeText(this, "El precio debe ser un número > 0", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            crearProducto(nombre, precio) {
                etNombre.setText("")
                etPrecio.setText("")
            }
        }

        cargarProductos()
    }

    private fun authOrFinish(): String {
        val auth = AdminSession.authHeader(this)
        if (auth == null) {
            Toast.makeText(this, "Sesión no válida. Vuelve a iniciar sesión.", Toast.LENGTH_LONG).show()
            finish()
            return ""
        }
        return auth
    }

    private fun cargarProductos() {
        ApiClient.api.getProductos().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@AdminProductsActivity, "HTTP ${response.code()}", Toast.LENGTH_LONG).show()
                    return
                }
                adapter.setData(response.body().orEmpty())
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@AdminProductsActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun crearProducto(nombre: String, precio: Double, onOk: () -> Unit) {
        val auth = authOrFinish()
        if (auth.isBlank()) return

        val req = ProductCreateRequest(productoNombre = nombre, price = precio)

        ApiClient.api.adminAddProducto(auth, req).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.code() == 401 || response.code() == 403) {
                    Toast.makeText(this@AdminProductsActivity, "No autorizado (HTTP ${response.code()})", Toast.LENGTH_LONG).show()
                    AdminSession.clear(this@AdminProductsActivity)
                    finish()
                    return
                }
                if (!response.isSuccessful) {
                    Toast.makeText(this@AdminProductsActivity, "Error al añadir (HTTP ${response.code()})", Toast.LENGTH_LONG).show()
                    return
                }

                Toast.makeText(this@AdminProductsActivity, "Producto añadido ✅", Toast.LENGTH_SHORT).show()
                onOk()
                cargarProductos()
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                Toast.makeText(this@AdminProductsActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun borrarProducto(id: Long) {
        val auth = authOrFinish()
        if (auth.isBlank()) return

        ApiClient.api.adminDeleteProducto(auth, id).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                if (response.code() == 401 || response.code() == 403) {
                    Toast.makeText(this@AdminProductsActivity, "No autorizado (HTTP ${response.code()})", Toast.LENGTH_LONG).show()
                    AdminSession.clear(this@AdminProductsActivity)
                    finish()
                    return
                }
                if (!response.isSuccessful) {
                    Toast.makeText(this@AdminProductsActivity, "Error al borrar (HTTP ${response.code()})", Toast.LENGTH_LONG).show()
                    return
                }

                Toast.makeText(this@AdminProductsActivity, "Producto eliminado 🗑️", Toast.LENGTH_SHORT).show()
                cargarProductos()
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                Toast.makeText(this@AdminProductsActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}

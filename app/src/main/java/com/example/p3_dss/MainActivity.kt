package com.example.p3_dss

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Base64
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    private lateinit var adapter: ProductAdapter

    private val searchHandler = Handler(Looper.getMainLooper())
    private var pendingSearch: Runnable? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.menu_products)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_admin -> {
                    pedirLoginAdmin()
                    true
                }
                else -> false
            }
        }

        val rv = findViewById<RecyclerView>(R.id.rvProductos)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = ProductAdapter(mutableListOf()) { product ->
            CartStorage.add(this, product)
            Toast.makeText(this, "Añadido: ${product.productoNombre}", Toast.LENGTH_SHORT).show()
        }
        rv.adapter = adapter

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_products
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_products -> true
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                R.id.nav_map -> {
                    startActivity(Intent(this, MapActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val etSearch = findViewById<TextInputEditText>(R.id.etSearch)
        etSearch.doAfterTextChanged { text ->
            val q = text?.toString()?.trim().orEmpty()

            pendingSearch?.let { searchHandler.removeCallbacks(it) }
            pendingSearch = Runnable {

                cargarProductos(q.takeIf { it.isNotBlank() })
            }
            searchHandler.postDelayed(pendingSearch!!, 250)
        }

        cargarProductos(null)
    }

    private fun cargarProductos(query: String?) {
        ApiClient.api.getProductos(query).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "HTTP ${response.code()}", Toast.LENGTH_LONG).show()
                    return
                }
                adapter.setData(response.body().orEmpty())
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun pedirLoginAdmin() {
        val userEt = EditText(this).apply {
            hint = "Usuario"
            inputType = InputType.TYPE_CLASS_TEXT
        }
        val passEt = EditText(this).apply {
            hint = "Contraseña"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val pad = (16 * resources.displayMetrics.density).toInt()
            setPadding(pad, pad, pad, pad)
            addView(userEt)
            addView(passEt)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Acceso restringido")
            .setMessage("Introduce credenciales de administrador")
            .setView(container)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Entrar") { _, _ ->
                val u = userEt.text?.toString()?.trim().orEmpty()
                val p = passEt.text?.toString()?.trim().orEmpty()
                intentarLoginAdmin(u, p)
            }
            .show()
    }

    private fun intentarLoginAdmin(user: String, pass: String) {
        if (user.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Rellena usuario y contraseña", Toast.LENGTH_LONG).show()
            return
        }

        val token = Base64.encodeToString(
            "$user:$pass".toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )
        val authHeader = "Basic $token"

        ApiClient.api.authMe(authHeader).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    AdminSession.save(this@MainActivity, user, pass)
                    startActivity(Intent(this@MainActivity, AdminProductsActivity::class.java))
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "No autorizado (HTTP ${response.code()})",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "Error de red: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}

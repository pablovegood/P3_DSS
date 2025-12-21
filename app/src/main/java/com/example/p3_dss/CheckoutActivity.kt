package com.example.p3_dss

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CheckoutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val tvResumen = findViewById<TextView>(R.id.tvResumen)
        val tvItemsCount = findViewById<TextView>(R.id.tvItemsCount)
        val rv = findViewById<RecyclerView>(R.id.rvCheckoutItems)

        val etCardNumber = findViewById<EditText>(R.id.etCardNumber)
        val etExpiry = findViewById<EditText>(R.id.etExpiry)
        val etCvv = findViewById<EditText>(R.id.etCvv)
        val etCardHolder = findViewById<EditText>(R.id.etCardHolder)

        val btnPagar = findViewById<Button>(R.id.btnConfirmarCompra)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        val items = CartStorage.load(this)
        val total = CartStorage.total(items)

        tvResumen.text = String.format("Total: %.2f €", total)
        tvItemsCount.text = "Elementos: ${items.size}"

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = CheckoutItemsAdapter(items)

        btnCancelar.setOnClickListener {
            // Vuelve al carrito (la activity anterior)
            finish()
        }

        btnPagar.setOnClickListener {
            if (items.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cardNumber = (etCardNumber.text?.toString() ?: "").replace(" ", "").trim()
            val expiry = (etExpiry.text?.toString() ?: "").trim()
            val cvv = (etCvv.text?.toString() ?: "").trim()
            val holder = (etCardHolder.text?.toString() ?: "").trim()

            val cardOk = cardNumber.length in 13..19 && cardNumber.all { it.isDigit() }
            val expiryOk = Regex("^(0[1-9]|1[0-2])/[0-9]{2}$").matches(expiry)
            val cvvOk = cvv.length in 3..4 && cvv.all { it.isDigit() }
            val holderOk = holder.isNotBlank()

            if (!cardOk || !expiryOk || !cvvOk || !holderOk) {
                Toast.makeText(this, "Revisa los datos de la tarjeta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mock: pago aprobado
            CartStorage.clear(this)
            Toast.makeText(this, "Pago aprobado ✅", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}

package com.example.p3_dss

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CheckoutItemsAdapter(
    private val items: List<Product>
) : RecyclerView.Adapter<CheckoutItemsAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvCartName)
        val tvPrice: TextView = v.findViewById(R.id.tvCartPrice)
        val btnRemove: Button = v.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.tvName.text = p.productoNombre
        holder.tvPrice.text = String.format("%.2f €", p.productoPrecio)
        holder.btnRemove.visibility = View.GONE
        holder.btnRemove.setOnClickListener(null)
    }
}

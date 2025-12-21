package com.example.p3_dss

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val items: MutableList<Product>,
    private val onAdd: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView = v.findViewById(R.id.tvNombre)
        val tvPrecio: TextView = v.findViewById(R.id.tvPrecio)
        val btnAdd: Button = v.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.tvNombre.text = p.productoNombre
        holder.tvPrecio.text = String.format("%.2f €", p.productoPrecio)
        holder.btnAdd.setOnClickListener { onAdd(p) }
    }

    override fun getItemCount(): Int = items.size

    fun setData(newItems: List<Product>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

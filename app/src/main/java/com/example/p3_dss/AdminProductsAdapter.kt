package com.example.p3_dss

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminProductAdapter(
    private val items: MutableList<Product>,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView = v.findViewById(R.id.tvAdminNombre)
        val tvPrecio: TextView = v.findViewById(R.id.tvAdminPrecio)
        val btnDelete: Button = v.findViewById(R.id.btnAdminDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.admin_product_item, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.tvNombre.text = p.productoNombre
        holder.tvPrecio.text = String.format("%.2f €", p.productoPrecio)
        holder.btnDelete.setOnClickListener { onDelete(p) }
    }

    override fun getItemCount(): Int = items.size

    fun setData(newItems: List<Product>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

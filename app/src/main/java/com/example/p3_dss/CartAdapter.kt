package com.example.p3_dss

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val items: MutableList<Product>,
    private val onRemove: (Product) -> Unit,
    private val showRemove: Boolean = true
) : RecyclerView.Adapter<CartAdapter.VH>() {

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

        if (showRemove) {
            holder.btnRemove.visibility = View.VISIBLE
            holder.btnRemove.setOnClickListener { onRemove(p) }
        } else {
            holder.btnRemove.visibility = View.GONE
            holder.btnRemove.setOnClickListener(null)
        }
    }

    fun setData(newItems: List<Product>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

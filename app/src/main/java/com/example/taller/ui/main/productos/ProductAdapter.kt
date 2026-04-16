package com.example.taller.ui.main.productos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taller.databinding.ItemProductoBinding

class ProductAdapter(private val lista: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val producto = lista[position]

        holder.binding.txtNombre.text = producto.nombre
        holder.binding.txtPrecio.text = producto.precio
        holder.binding.imgProducto.setImageResource(producto.imagen)
    }

    override fun getItemCount(): Int = lista.size
}
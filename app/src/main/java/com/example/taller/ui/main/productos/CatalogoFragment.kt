package com.example.taller.ui.main.productos

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.taller.R
import com.example.taller.databinding.FragmentCatalogoBinding

class   CatalogoFragment : Fragment(R.layout.fragment_catalogo) {

    private var _binding: FragmentCatalogoBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCatalogoBinding.bind(view)

        val lista = listOf(
            Product("Camisa Casual", "$25.000", R.drawable.camisa_casual),
            Product("Pantalón Jean", "$40.000", R.drawable.pantalon_jean),
            Product("Zapatos Negros", "$60.000", R.drawable.zapatos_negros),
            Product("Chaqueta Cuero", "$80.000", R.drawable.chaqueta_cuero)
        )

        binding.recyclerProductos.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerProductos.adapter = ProductAdapter(lista)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
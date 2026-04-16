package com.example.taller.ui.main.perfil

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.taller.R
import com.example.taller.ui.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var etRePassword: EditText
    private lateinit var btnGuardar: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val client = SupabaseClient.client

        etNombres = view.findViewById(R.id.etNombres)
        etApellidos = view.findViewById(R.id.etApellidos)
        etCorreo = view.findViewById(R.id.etCorreo)
        etPassword = view.findViewById(R.id.etPassword)
        etRePassword = view.findViewById(R.id.etRePassword)
        btnGuardar = view.findViewById(R.id.btnGuardar)

        btnGuardar.setOnClickListener {

            val nombres = etNombres.text.toString()
            val apellidos = etApellidos.text.toString()
            val correo = etCorreo.text.toString()
            val password = etPassword.text.toString()
            val repassword = etRePassword.text.toString()

            if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty()) {
                Toast.makeText(requireContext(), "Completa los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isNotEmpty() && password != repassword) {
                Toast.makeText(requireContext(), "Contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {

                    val userId = client.auth.currentUserOrNull()?.id ?: ""

                    // UPDATE EN SUPABASE
                    client.from("usuarios").update(
                        mapOf(
                            "nombres" to nombres,
                            "apellidos" to apellidos,
                            "correo" to correo
                        )
                    ) {
                        filter {
                            eq("id", userId)
                        }
                    }

                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
package com.example.taller.ui.main.perfil

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.example.taller.R
import com.example.taller.data.UsuarioRepository
import kotlinx.coroutines.launch

class EditarPerfilFragment : Fragment(R.layout.fragment_editar_perfil) {

    //  Guardar la imagen seleccionada
    private var imagenSeleccionada: Uri? = null

    // Abrir galería
    private val seleccionarImagen =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imagenSeleccionada = uri
                view?.findViewById<ImageView>(R.id.iv_editar_foto)
                    ?.setImageURI(uri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNombres = view.findViewById<EditText>(R.id.et_editar_nombres)
        val etApellidos = view.findViewById<EditText>(R.id.et_editar_apellidos)
        val etCorreo = view.findViewById<EditText>(R.id.et_editar_correo)
        val etPassword = view.findViewById<EditText>(R.id.et_editar_contrasena)
        val etRePassword = view.findViewById<EditText>(R.id.et_editar_recontrasena)
        val btnGuardar = view.findViewById<Button>(R.id.btn_guardar_perfil)
        val ivCamara = view.findViewById<ImageView>(R.id.iv_camara_icon)
        val ivFoto = view.findViewById<ImageView>(R.id.iv_editar_foto)

        //  Abrir galería al tocar ícono
        ivCamara.setOnClickListener {
            seleccionarImagen.launch("image/*")
        }

        //  Cargar datos actuales
        lifecycleScope.launch {
            val usuario = UsuarioRepository.obtenerUsuarioActual()
            if (usuario != null) {
                etNombres.setText(usuario.nombres)
                etApellidos.setText(usuario.apellidos)
                etCorreo.setText(usuario.correo ?: "")

                if (!usuario.foto_url.isNullOrEmpty()) {
                    ivFoto.load(usuario.foto_url) {
                        transformations(CircleCropTransformation())
                        placeholder(R.mipmap.ic_launcher_round)
                        error(R.mipmap.ic_launcher_round)
                    }
                }
            }
        }

        //  Guardar cambios
        btnGuardar.setOnClickListener {

            val nombres = etNombres.text.toString().trim()
            val apellidos = etApellidos.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
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
                    var fotoUrl: String? = null

                    //  Subir imagen si el usuario eligió una
                    if (imagenSeleccionada != null) {
                        fotoUrl = UsuarioRepository.subirFotoPerfil(
                            requireContext(),
                            imagenSeleccionada!!
                        )
                    }

                    //  Actualizar perfil completo
                    UsuarioRepository.actualizarPerfil(
                        nombres,
                        apellidos,
                        correo,
                        fotoUrl
                    )

                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()

                        //  Volver al perfil
                        parentFragmentManager.popBackStack()
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
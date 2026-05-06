package com.example.taller.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.taller.R
import com.example.taller.data.UsuarioRepository

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import com.example.taller.ui.SupabaseClient
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class RegistroActivity : AppCompatActivity() {

    private lateinit var etnombres: EditText
    private lateinit var etapellidos: EditText
    private lateinit var etcorreo: EditText
    private lateinit var etpassword: EditText
    private lateinit var etrepassword: EditText
    private lateinit var terminos: CheckBox
    private lateinit var btnRegistrar: Button
    private lateinit var irLogin: TextView

    lateinit var client: io.github.jan.supabase.SupabaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)


        client = SupabaseClient.client

        // Referencias
        etnombres = findViewById(R.id.nombres)
        etapellidos = findViewById(R.id.apellidos)
        etcorreo = findViewById(R.id.correo)
        etpassword = findViewById(R.id.password)
        etrepassword = findViewById(R.id.repassword)
        terminos = findViewById(R.id.terminos)
        btnRegistrar = findViewById(R.id.btn_registrar)
        irLogin = findViewById(R.id.ir_login)

        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }

        irLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registrarUsuario() {

        val nombres = etnombres.text.toString().trim()
        val apellidos = etapellidos.text.toString().trim()
        val correo = etcorreo.text.toString().trim()
        val password = etpassword.text.toString().trim()
        val repassword = etrepassword.text.toString().trim()

        // VALIDACIONES
        if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty()
            || password.isEmpty() || repassword.isEmpty()
        ) {
            Toast.makeText(this, "Completa toda la información", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != repassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        if (!terminos.isChecked) {
            Toast.makeText(this, "Debes aceptar los términos", Toast.LENGTH_SHORT).show()
            return
        }

        // SUPABASE
        lifecycleScope.launch {
            try {

                // 1. AUTH
                client.auth.signUpWith(io.github.jan.supabase.auth.providers.builtin.Email) {
                    email = correo
                    this.password = password
                    data = buildJsonObject {
                        put("nombres", nombres)
                        put("apellidos", apellidos)
                    }
                }

                // 2. ID USUARIO
                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id?:""
                UsuarioRepository.insertarUsuario(userId,nombres,apellidos,correo)

                // 3. INSERTAR
                //client.from("usuarios").insert(
                 //   mapOf(
                  //      "id" to userId,
                  //      "nombres" to nombres,
                  //      "apellidos" to apellidos,
                  //      "correo" to correo
                  //  )
               // )

                // 4. RESPUESTA
                runOnUiThread {
                    Toast.makeText(
                        this@RegistroActivity,
                        "Registro exitoso",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(Intent(this@RegistroActivity, LoginActivity::class.java))
                    finish()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@RegistroActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
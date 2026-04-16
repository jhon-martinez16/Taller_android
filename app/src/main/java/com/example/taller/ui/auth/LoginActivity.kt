package com.example.taller.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.example.taller.R
import com.example.taller.ui.SupabaseClient
import com.example.taller.ui.main.MainActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var etcorreo: EditText
    private lateinit var etpassword: EditText
    private lateinit var btnbtn_ingresar: Button
    private lateinit var tvtxt_registro: TextView
    private lateinit var tvrecuperar_contrasena: TextView
    private lateinit var btn_google: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        //configuarcion de rotview del teclado
        val rootView = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = maxOf(systemBars.bottom, imeInsets.bottom)

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                bottomPadding
            )
            insets
        }

        etcorreo = findViewById(R.id.correo)
        etpassword = findViewById(R.id.password)
        btnbtn_ingresar = findViewById(R.id.btn_ingresar)
        tvtxt_registro = findViewById(R.id.txt_registro)
        tvrecuperar_contrasena = findViewById(R.id.recuperar_contrasena)
        btn_google = findViewById(R.id.btn_google)


        //Handler(Looper.getMainLooper()).postDelayed({
        //    startActivity(Intent(this, MainActivity::class.java))
        //    finish()
        //}, 3000)


        // Inicio de sesion con infromacion de supabase
        btnbtn_ingresar.setOnClickListener {

            val correo = etcorreo.text.toString().trim()
            val contrasena = etpassword.text.toString().trim()

            // Validar de los campos necesarios
            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor completa toda la información", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Validar longitud de contraseña
            if (contrasena.length < 7) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 8 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validacion - consulta supabase .)

            lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.signInWith(Email) {
                        email = correo
                        password = contrasena
                    }
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            "Inicio de sesión exitoso",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error en el inicio de sesión",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }


        // Inicio de sesion con google
        btn_google.setOnClickListener {
            iniciarSesionConGoogle()
        }
    }

    private fun iniciarSesionConGoogle() {
        lifecycleScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("1036779276775-cdvq3oevc8u6ma30gpfvmrtok99oma1i.apps.googleusercontent.com")
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val credentialManager = CredentialManager.create(this@LoginActivity)
                val result = credentialManager.getCredential(context = this@LoginActivity, request = request)
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)

                SupabaseClient.client.auth.signInWith(IDToken) {
                    idToken = googleIdTokenCredential.idToken
                    provider = Google
                }

                runOnUiThread {
                    Toast.makeText(
                        this@LoginActivity,
                        "Inicio de sesión con Google exitoso",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error en el inicio de sesión con Google: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }


            }
        }
    }
}
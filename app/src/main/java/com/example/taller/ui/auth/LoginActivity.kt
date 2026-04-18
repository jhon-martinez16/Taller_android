package com.example.taller.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
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
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.taller.data.CredencialesManager
import io.github.jan.supabase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var etcorreo: EditText
    private lateinit var etpassword: EditText
    private lateinit var btnbtn_ingresar: Button
    private lateinit var tvtxt_registro: TextView
    private lateinit var tvrecuperar_contrasena: TextView
    private lateinit var btn_google: LinearLayout

    private lateinit var tvHuella: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val rootView = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = maxOf(systemBars.bottom, imeInsets.bottom)

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }

        etcorreo = findViewById(R.id.correo)
        etpassword = findViewById(R.id.password)
        btnbtn_ingresar = findViewById(R.id.btn_ingresar)
        tvtxt_registro = findViewById(R.id.txt_registro)
        tvrecuperar_contrasena = findViewById(R.id.recuperar_contrasena)
        btn_google = findViewById(R.id.btn_google)
        tvHuella = findViewById(R.id.in_huella)


        configurarVisibilidadHuella()
        tvHuella.setOnClickListener {
            mostrarDialogoHuella()
        }

        // LOGIN NORMAL
        btnbtn_ingresar.setOnClickListener {
            val correo = etcorreo.text.toString().trim()
            val contrasena = etpassword.text.toString().trim()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.signInWith(Email) {
                        email = correo
                        password = contrasena
                    }

                    CredencialesManager.guardarCredenciales(
                        this@LoginActivity,
                        correo,
                        contrasena
                    )

                    Toast.makeText(this@LoginActivity, "Login exitoso", Toast.LENGTH_SHORT).show()
                    irAPantallaPrincipal()

                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error login", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // GOOGLE
        btn_google.setOnClickListener {
            iniciarSesionConGoogle()
        }
    }


    override fun onResume() {
        super.onResume()
        configurarVisibilidadHuella()
    }

    private fun iniciarSesionConGoogle() {
        lifecycleScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("1036779276775-7rgld97k8d296la4vgneg2m7l75queb7.apps.googleusercontent.com")
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val credentialManager = CredentialManager.create(this@LoginActivity)
                val result = credentialManager.getCredential(this@LoginActivity, request)

                val credential = GoogleIdTokenCredential.createFrom(result.credential.data)

                SupabaseClient.client.auth.signInWith(IDToken) {
                    idToken = credential.idToken
                    provider = Google
                }

                irAPantallaPrincipal()

            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Error Google: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun irAPantallaPrincipal() {
        runOnUiThread {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finishAffinity()
        }
    }


    private fun configurarVisibilidadHuella() {

        val huellaActiva = CredencialesManager.huellaActiva(this)

        val biometricManager = BiometricManager.from(this)

        val result = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        )

        val biometriaDisponible = result == BiometricManager.BIOMETRIC_SUCCESS

        tvHuella.visibility =
            if (huellaActiva && biometriaDisponible)
                android.view.View.VISIBLE
            else
                android.view.View.GONE
    }


    private fun mostrarDialogoHuella() {

        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {

                    val correo = CredencialesManager.obtenerCorreo(this@LoginActivity)
                    val contrasena = CredencialesManager.obtenerContrasena(this@LoginActivity)

                    if (correo != null && contrasena != null) {

                        lifecycleScope.launch {
                            try {
                                SupabaseClient.client.auth.signInWith(Email) {
                                    email = correo
                                    password = contrasena
                                }

                                irAPantallaPrincipal()

                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Sesion expirada",
                            Toast.LENGTH_LONG
                        ).show()

                        CredencialesManager.limpiarCredenciales(this@LoginActivity)
                        configurarVisibilidadHuella()
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error biometrico: $errString",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(
                        this@LoginActivity,
                        "Huella no reconocida",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso con huella")
            .setSubtitle("Usa tu huella")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}


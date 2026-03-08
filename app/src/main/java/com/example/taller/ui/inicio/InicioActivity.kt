package com.example.taller.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taller.R
import com.example.taller.ui.auth.RegistroActivity

class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
        // Delay de 3 segundos y redireccionamiento
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, RegistroActivity::class.java))
            finish()
        }, 3000)
    }
}
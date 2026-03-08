package com.example.taller.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.taller.R

class SplahsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splahs)

        // Delay de 3 segundos y redireccionamiento
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, InformacionActivity::class.java))
            finish()
        }, 3000)
    }
}
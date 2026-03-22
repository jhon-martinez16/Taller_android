package com.example.taller.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.taller.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, RegistroActivity::class.java))
            finish()
        }, 3000)
    }
}
package com.example.taller.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.taller.R

class SplahsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splahs)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, InformacionActivity::class.java))
            finish()
        }, 3000)
    }
}
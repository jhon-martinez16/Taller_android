package com.example.taller.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.taller.R
import com.example.taller.ui.SupabaseClient
import com.example.taller.ui.auth.LoginActivity
import com.example.taller.ui.main.admin.AdminFragment
import com.example.taller.ui.main.perfil.PerfilFragment
import com.example.taller.ui.main.productos.CarritoFragment
import com.example.taller.ui.main.productos.CatalogoFragment
import com.example.taller.ui.main.productos.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navigationView = findViewById<NavigationView>(R.id.navigationView) // 👈 ESTE ES EL TUYO

        // 🔹 Abrir menú lateral
        btnMenu.setOnClickListener {
            drawer.open()
        }

        // 🔹 Fragment inicial
        cambiarFragment(HomeFragment())

        // 🔹 Bottom Navigation
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.nav_home -> cambiarFragment(HomeFragment())
                R.id.nav_catalogo -> cambiarFragment(CatalogoFragment())
                R.id.nav_carrito -> cambiarFragment(CarritoFragment())
                R.id.nav_perfil -> cambiarFragment(PerfilFragment())
            }
            true
        }

        navigationView.setNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.nav_inicio -> cambiarFragment(HomeFragment())

                R.id.nav_productos -> cambiarFragment(CatalogoFragment())

                R.id.nav_perfil -> cambiarFragment(PerfilFragment())

                R.id.nav_admin -> cambiarFragment(AdminFragment())

                R.id.nav_logout -> cerrarSesion()
            }

            drawer.close()
            true
        }
    }

    // Cambiar fragment
    private fun cambiarFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedor, fragment)
            .commit()
    }

    //  FUNCIÓN cerrar sesion
    private fun cerrarSesion() {
        lifecycleScope.launch {
            try {
                SupabaseClient.client.auth.signOut()

                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Sesión cerrada", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
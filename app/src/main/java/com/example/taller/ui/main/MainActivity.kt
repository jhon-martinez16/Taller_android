package com.example.taller.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.taller.R
import android.view.Menu
import com.example.taller.data.UsuarioRepository
import com.example.taller.ui.SupabaseClient
import com.example.taller.ui.auth.LoginActivity
import com.example.taller.ui.main.admin.AdminFragment
import com.example.taller.ui.main.perfil.EditarPerfilFragment
import com.example.taller.ui.main.productos.CarritoFragment
import com.example.taller.ui.main.productos.CatalogoFragment
import com.example.taller.ui.main.productos.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.taller.ui.main.perfil.PerfilFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)

        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        //configurarBottomMenuPorRol(bottomNav)

        lifecycleScope.launch {

            val user = SupabaseClient.client.auth.currentUserOrNull()

            if (user == null) {
                cerrarSesion()
                return@launch
            }

            val rol = withContext(Dispatchers.IO) {
                UsuarioRepository.obtenerRolPorId(user.id)
            }.trim().lowercase()

            println("USER ID: ${user.id}")
            println("ROL REAL: $rol")

            configurarMenuPorRol(navigationView.menu, rol)
            configurarBottomMenuPorRol(bottomNav, rol)
        }

        //  Abrir menú lateral
        btnMenu.setOnClickListener {
            drawer.open()
        }

        //  Fragment inicial
        cambiarFragment(HomeFragment())

        //  Configurar rol
        //configurarMenuPorRol(navigationView.menu)

        //  Bottom Navigation
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

    // configurar menu por rol
    private fun configurarMenuPorRol(menu: Menu, rol: String) {

        println("ROL OBTENIDO: $rol")

        when (rol.trim().lowercase()) {

            "admin" -> {
                menu.findItem(R.id.nav_admin).isVisible = true
                menu.findItem(R.id.nav_perfil).isVisible = true
            }

            "vendedor" -> {
                menu.findItem(R.id.nav_admin).isVisible = false
                menu.findItem(R.id.nav_perfil).isVisible = true
            }

            else -> {
                menu.findItem(R.id.nav_admin).isVisible = false
                menu.findItem(R.id.nav_perfil).isVisible = true
            }
        }
    }

    private fun configurarBottomMenuPorRol(bottomNav: BottomNavigationView, rol: String) {

        when (rol.trim().lowercase()) {

            "admin" -> {
                bottomNav.menu.findItem(R.id.nav_admin).isVisible = true
            }

            else -> {
                bottomNav.menu.findItem(R.id.nav_admin).isVisible = false
            }
        }
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
package com.example.invytrack

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoadingScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading_screen)

        // Menyesuaikan padding untuk tampilan agar tidak tertutup system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Menampilkan loading screen, kemudian pindah ke SignIn setelah beberapa detik
        Handler().postDelayed({
            // Pindah ke SignInActivity setelah loading screen
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish() // Tutup LoadingScreenActivity
        }, 2000) // 2 detik delay
    }
}

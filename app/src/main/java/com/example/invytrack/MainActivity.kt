package com.example.invytrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Tombol SignUp
        val btnSignUp: Button = findViewById(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            // Logika untuk SignUp
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Tombol SignIn
        val btnSignIn: Button = findViewById(R.id.btnSignIn)
        btnSignIn.setOnClickListener {
            // Logika untuk SignIn
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}

package com.example.invytrack

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Inisialisasi elemen UI
        val btnSignUp: Button = findViewById(R.id.btnSignUp)
        val etFullName: EditText = findViewById(R.id.etFullName)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val cbTerms: CheckBox = findViewById(R.id.cbTerms)

        // Inisialisasi TextView untuk "Sign In" link
        val tvSignInLink: TextView = findViewById(R.id.tvSignInLink)

        // Ketika tombol Sign Up ditekan
        btnSignUp.setOnClickListener {
            val fullName = etFullName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && cbTerms.isChecked) {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()

                // Pindah ke Loading Screen
                val intent = Intent(this, LoadingScreen::class.java)
                startActivity(intent)

                // Delay sebelum masuk ke SignInActivity
                Handler().postDelayed({
                    val signInIntent = Intent(this, SignInActivity::class.java)
                    startActivity(signInIntent)
                    finish()
                }, 2000) // 2 detik delay
            } else {
                Toast.makeText(this, "Please fill all fields and agree to the terms", Toast.LENGTH_SHORT).show()
            }
        }

        // Ketika link "Sign In" diklik
        tvSignInLink.setOnClickListener {
            val signInIntent = Intent(this, SignInActivity::class.java)
            startActivity(signInIntent)
        }
    }
}

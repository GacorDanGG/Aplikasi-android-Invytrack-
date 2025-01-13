package com.example.invytrack

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.account

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.DashboardActivity -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.inventory -> {
                    startActivity(Intent(this, InventoryActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.setting -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.account -> return@setOnItemSelectedListener true
                else -> false
            }
        }

        setupProfileInfo()
    }

    private fun setupProfileInfo() {
        val profileName = findViewById<TextView>(R.id.account_name)
        val profileEmail = findViewById<TextView>(R.id.account_email)

        // Get user email from SharedPreferences
        val sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("USER_EMAIL", null)

        if (userEmail != null) {
            val userName = userEmail.split("@")[0].capitalize()
            profileName.text = userName
            profileEmail.text = userEmail
        } else {
            profileName.text = "User"
            profileEmail.text = "Email not available"
        }
    }
}

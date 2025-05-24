package com.raffy.apitask

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UserActivity : AppCompatActivity() {
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var ivBack: ImageView
    private lateinit var btnLogout: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)

        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        ivBack = findViewById(R.id.ivBack)
        btnLogout = findViewById(R.id.btnLogout)

        val userName = sharedPreferences.getString("USER_NAME", "Default Name") ?: "Default Name"
        val userEmail = sharedPreferences.getString("USER_EMAIL", "Default Email") ?: "Default Email"

        tvName.setText(userName)
        tvEmail.setText(userEmail)

        ivBack.setOnClickListener {
            finish()
        }

        btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("TOKEN") // Hapus token
        editor.apply()

        Toast.makeText(this, "Logout Successful!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
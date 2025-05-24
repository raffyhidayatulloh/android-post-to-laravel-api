package com.raffy.apitask

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isUserLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and Password must be filled in!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser (email: String, password: String) {
        // Validasi input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this@LoginActivity, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        // Panggil API untuk login
        RetrofitClient.instance.loginUser (email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    val user = loginResponse?.user

                    if (token != null && user != null) {
                        saveUserData(token, user)  // Menyimpan token dan data user
                        Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Cek kode status untuk memberikan pesan yang lebih spesifik
                    when (response.code()) {
                        401 -> Toast.makeText(this@LoginActivity, "Invalid email or password!", Toast.LENGTH_SHORT).show()
                        422 -> Toast.makeText(this@LoginActivity, "Invalid email or password!", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this@LoginActivity, "Invalid email or password!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginError", "Network error: ${t.message}")
                Toast.makeText(this@LoginActivity, "Invalid email or password!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserData(token: String, user: User) {
        val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("TOKEN", token)
        editor.putInt("USER_ID", user.id)
        editor.putString("USER_NAME", user.name)
        editor.putString("USER_EMAIL", user.email)
        editor.apply()
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        return sharedPreferences.getString("TOKEN", null) != null
    }
}
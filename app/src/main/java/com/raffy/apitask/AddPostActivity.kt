package com.raffy.apitask

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPostActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etBody: EditText
    private lateinit var btnSave: Button
    private lateinit var ivBack: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        etTitle = findViewById(R.id.etTitle)
        etBody = findViewById(R.id.etBody)
        btnSave = findViewById(R.id.btnSave)
        ivBack = findViewById(R.id.ivBack)
        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)

        checkLoginStatus()

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val body = etBody.text.toString().trim()

            if (title.isEmpty() || body.isEmpty()) {
                Toast.makeText(this, "The title and body cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addPostToApi(title, body)
        }

        ivBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun checkLoginStatus() {
        val token = getTokenFromSharedPreferences()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "You are not logged in yet, please log in first!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun getTokenFromSharedPreferences(): String? {
        return sharedPreferences.getString("TOKEN", null)
    }

    private fun addPostToApi(title: String, body: String) {
        val token = getTokenFromSharedPreferences()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token not found, please log in again!", Toast.LENGTH_SHORT).show()
            return
        }

        val authToken = "Bearer $token" // Tambahkan "Bearer" ke token
        RetrofitClient.instance.createPost(authToken, title, body).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddPostActivity, "Post added successfully!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK) // Beri tahu MainActivity bahwa ada perubahan
                    finish()
                } else {
                    Log.e("AddPost", "Failed to add post, kode: ${response.code()}")
                    Toast.makeText(this@AddPostActivity, "Failed to add post!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("AddPost", "Network error: ${t.message}")
                Toast.makeText(this@AddPostActivity, "Network error!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
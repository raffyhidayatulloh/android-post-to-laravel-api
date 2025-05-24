package com.raffy.apitask

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditPostActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var etEditTitle: EditText
    private lateinit var etEditBody: EditText
    private lateinit var ivBack: ImageView
    private lateinit var btnUpdate: Button
    private var postId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        etEditTitle = findViewById(R.id.etEditTitle)
        etEditBody = findViewById(R.id.etEditContent)
        btnUpdate = findViewById(R.id.btnUpdate)
        ivBack = findViewById(R.id.ivBack)

        // Ambil data artikel dari intent
        postId = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title") ?: ""
        val body = intent.getStringExtra("body") ?: ""

        // Set data ke input
        etEditTitle.setText(title)
        etEditBody.setText(body)

        btnUpdate.setOnClickListener {
            val updatedTitle = etEditTitle.text.toString().trim()
            val updatedBody = etEditBody.text.toString().trim()

            if (updatedTitle.isEmpty() || updatedBody.isEmpty()) {
                Toast.makeText(this, "Please fill in all columns!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updatePost(postId, updatedTitle, updatedBody)
        }

        ivBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun updatePost(id: Int, title: String, body: String) {
        val token = getTokenFromSharedPreferences()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token not found, please log in again!", Toast.LENGTH_SHORT).show()
            return
        }

        val authToken = "Bearer $token" // Tambahkan "Bearer" ke token
        RetrofitClient.instance.updatePost(authToken, id, title, body)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "Post updated!", Toast.LENGTH_SHORT).show()

                        // Set result agar MainActivity tahu ada perubahan data
                        setResult(RESULT_OK)
                        finish()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(applicationContext, "Failed to update post!", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getTokenFromSharedPreferences(): String? {
        return sharedPreferences.getString("TOKEN", null)
    }
}
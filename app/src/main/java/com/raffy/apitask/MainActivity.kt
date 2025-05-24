package com.raffy.apitask

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var ivUser: ImageView
    private lateinit var recyclerView: RecyclerView
    private val viewModel: PostViewModel by viewModels()

    // Launcher untuk menangkap hasil dari Add/Edit Article Activity
    private val postActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Jika artikel berhasil ditambahkan/diperbarui, refresh data
                viewModel.getPosts()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        ivUser = findViewById(R.id.ivUser)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val token = getTokenFromSharedPreferences()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token not found, please log in again!", Toast.LENGTH_SHORT).show()

            // Mengarahkan pengguna ke halaman login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Mengakhiri UserActivity agar pengguna tidak bisa kembali ke halaman ini
            return
        }
        val authToken = "Bearer $token" // Tambahkan "Bearer" ke token

        ivUser.setOnClickListener {
            startActivity(Intent(this@MainActivity, UserActivity::class.java))
        }

        val addButton: ImageView = findViewById(R.id.ivAdd)
        addButton.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            postActivityLauncher.launch(intent) // Memulai activity dengan launcher
        }

        viewModel.getPosts().observe(this) { posts ->
            recyclerView.adapter = PostAdapter(
                this, // Tambahkan konteks (MainActivity)
                posts,
                onViewClick = { post ->
                    val intent = Intent(this, ViewPostActivity::class.java).apply {
                        putExtra("id", post.id)
                        putExtra("title", post.title)
                        putExtra("body", post.body)
                        putExtra("updated_at", post.updated_at)
                        putExtra("created_at", post.created_at)
                        putExtra("name", post.user?.name ?: "Unknown User") // Safe call with default value
                    }
                    postActivityLauncher.launch(intent)
                },
                onEditClick = { post ->
                    val intent = Intent(this, EditPostActivity::class.java).apply {
                        putExtra("id", post.id)
                        putExtra("user_id", post.user_id)
                        putExtra("title", post.title)
                        putExtra("body", post.body)
                    }
                    postActivityLauncher.launch(intent)
                },
                onDeleteClick = { post ->
                    RetrofitClient.instance.deletePost(authToken, post.id).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                viewModel.getPosts() // Refresh daftar artikel setelah penghapusan
                                Toast.makeText(this@MainActivity, "Post deleted successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.e("MainActivity", "Failed to delete article, code: ${response.code()}")
                                Toast.makeText(this@MainActivity, "Failed to delete post!", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("MainActivity", "Network error: ${t.message}")
                            Toast.makeText(this@MainActivity, "Network error!", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            )
        }
    }

    private fun getTokenFromSharedPreferences(): String? {
        return sharedPreferences.getString("TOKEN", null)
    }
}

package com.raffy.apitask

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ViewPostActivity : AppCompatActivity() {
    private lateinit var ivBack: ImageView
    private var postId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_post)

        ivBack = findViewById(R.id.ivBack)
        // Menampilkan data pada TextView
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvCreatedDate = findViewById<TextView>(R.id.tvCreatedDate)
        val tvUpdatedDate = findViewById<TextView>(R.id.tvUpdatedDate)
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvBody = findViewById<TextView>(R.id.tvBody)

        postId = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title") ?: ""
        val updated_at = intent.getStringExtra("updated_at") ?: ""
        val created_at = intent.getStringExtra("created_at") ?: ""
        val body = intent.getStringExtra("body") ?: ""
        val name = intent.getStringExtra("name") ?: ""

        // Set text sesuai dengan data dari Post
        tvTitle.text = title
        tvCreatedDate.text = "Created: ${formatDate(created_at)}"
        tvUpdatedDate.text = "Updated: ${formatDate(updated_at)}"
        tvName.text = "Writer: $name"
        tvBody.text = body

        // Menambahkan padding jika diperlukan
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ivBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }
}
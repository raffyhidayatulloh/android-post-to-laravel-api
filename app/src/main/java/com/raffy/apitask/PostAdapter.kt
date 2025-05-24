package com.raffy.apitask

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(
    private val context: Context,
    private val posts: List<Post>,
    private val onViewClick: (Post) -> Unit,
    private val onEditClick: (Post) -> Unit,
    private val onDeleteClick: (Post) -> Unit,
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val loggedInUserId: Int = getUserIdFromPrefs()

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val body: TextView = view.findViewById(R.id.tvContent)
        val date: TextView = view.findViewById(R.id.tvDate)
        val edit: ImageView = view.findViewById(R.id.ivEdit)
        val delete: ImageView = view.findViewById(R.id.ivDelete)
        val lUser: LinearLayout = view.findViewById(R.id.llUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.title.text = post.title
        holder.body.text = post.body
        holder.date.text = "${formatDate(post.updated_at)} by ${post.user?.name}"

        // Sembunyikan lUser jika user_id postingan tidak cocok dengan USER_ID pengguna yang login
        if (post.user_id == loggedInUserId) {
            holder.lUser.visibility = View.VISIBLE
        } else {
            holder.lUser.visibility = View.GONE
        }

        holder.title.setOnClickListener { onViewClick(post) }
        holder.edit.setOnClickListener { onEditClick(post) }
        holder.delete.setOnClickListener { onDeleteClick(post) }
    }

    override fun getItemCount(): Int = posts.size

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

    private fun getUserIdFromPrefs(): Int {
        val sharedPreferences = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("USER_ID", -1)
    }
}
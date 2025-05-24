package com.raffy.apitask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostViewModel : ViewModel() {
    private val postsLiveData = MutableLiveData<List<Post>>()

    fun getPosts(): LiveData<List<Post>> {
        fetchPosts() // Panggil fungsi untuk mengambil data terbaru
        return postsLiveData
    }

    fun fetchPosts() { // Ambil data terbaru dari API
        RetrofitClient.instance.getPosts().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    postsLiveData.postValue(response.body()) // Update LiveData
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                // Handle failure
            }
        })
    }
}
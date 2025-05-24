package com.raffy.apitask

import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST
import retrofit2.http.Body

interface ApiService {
    @POST("api/login")
    @FormUrlEncoded
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST("api/register")
    @FormUrlEncoded
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") password_confirmation: String
    ): Call<RegisterResponse>

    @GET("api/posts")
    fun getPosts(): Call<List<Post>>

    @GET("api/posts/{id}")
    fun getPostById(@Path("id") id: Int): Call<Post>

    @POST("api/posts")
    @FormUrlEncoded
    fun createPost(
        @Header("Authorization") authToken: String,
        @Field("title") title: String,
        @Field("body") body: String
    ): Call<Void>

    @PUT("api/posts/{id}")
    @FormUrlEncoded
    fun updatePost(
        @Header("Authorization") authToken: String,
        @Path("id") id: Int,
        @Field("title") title: String,
        @Field("body") body: String
    ): Call<Void>

    @DELETE("api/posts/{id}")
    fun deletePost(@Header("Authorization") authToken: String, @Path("id") id: Int): Call<Void>
}

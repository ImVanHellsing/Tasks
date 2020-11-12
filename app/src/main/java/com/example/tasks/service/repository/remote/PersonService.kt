package com.example.tasks.service.repository.remote

import com.example.tasks.service.model.HeaderModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PersonService {
    /**
     * Setup the resource answer
     */
    @POST("Authentication/Login")
    @FormUrlEncoded
    fun Login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<HeaderModel>

    @POST("Authentication/Create")
    @FormUrlEncoded
    fun Create(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("receivedNews") news: Boolean
    ): Call<HeaderModel>
}
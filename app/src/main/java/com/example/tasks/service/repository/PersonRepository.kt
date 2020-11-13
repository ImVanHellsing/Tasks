package com.example.tasks.service.repository

import android.content.Context
import com.example.tasks.R
import com.example.tasks.service.model.HeaderModel
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.repository.remote.PersonService
import com.example.tasks.service.repository.remote.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonRepository(val context: Context) {
    // Retrofit Instance
    private val mApi = RetrofitClient.createService(PersonService::class.java)

    fun login(email: String, password: String, listener: APIListener<HeaderModel>) {
        val call: Call<HeaderModel> = mApi.Login(email, password)

        // Assincronos task
        call.enqueue(object : Callback<HeaderModel> {
            override fun onResponse(call: Call<HeaderModel>, response: Response<HeaderModel>) {
                // Check Responde Code
                if (response.code() != TaskConstants.HTTP.SUCCESS) {
                    // Sending the converted JSON to string as answer to 'onFailure' method
                    listener.onFailure(
                        Gson().fromJson(
                            response.errorBody()!!.string(),
                            String::class.java
                        )
                    )
                } else {
                    response.body()?.let { listener.onSucess(it) }
                }
            }

            override fun onFailure(call: Call<HeaderModel>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }

        })
    }

    fun create(name: String, email: String, password: String, listener: APIListener<HeaderModel>) {
        val call: Call<HeaderModel> = mApi.Create(name, email, password, true)

        call.enqueue(object : Callback<HeaderModel> {
            override fun onResponse(call: Call<HeaderModel>, response: Response<HeaderModel>) {
                if (response.code() != TaskConstants.HTTP.SUCCESS) {

                    listener.onFailure(
                        Gson().fromJson(
                            response.errorBody()!!.string(),
                            String::class.java
                        )
                    )
                } else {
                    response.body()?.let { listener.onSucess(it) }
                }
            }

            override fun onFailure(call: Call<HeaderModel>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }

        })

    }
}
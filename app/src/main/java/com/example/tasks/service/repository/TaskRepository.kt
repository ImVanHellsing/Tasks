package com.example.tasks.service.repository

import android.content.Context
import com.example.tasks.R
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.model.TaskModel
import com.example.tasks.service.repository.remote.RetrofitClient
import com.example.tasks.service.repository.remote.TaskService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class TaskRepository(val context: Context) {

    // Retrofit Instance
    private val mApi = RetrofitClient.createService(TaskService::class.java)

    fun create(newTask: TaskModel, listener: APIListener<Boolean>) {

        val call: Call<Boolean> = mApi.store(
            newTask.priorityId,
            newTask.description,
            newTask.dueDate,
            newTask.complete
        )

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
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

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }
        })
    }

    fun getAll(listener: APIListener<List<TaskModel>>) {
        val call: Call<List<TaskModel>> = mApi.getAll()
        listBehavior(call, listener)
    }

    fun getNextWeek(listener: APIListener<List<TaskModel>>) {
        val call: Call<List<TaskModel>> = mApi.nextWeek()
        listBehavior(call, listener)
    }

    fun getOverdue(listener: APIListener<List<TaskModel>>) {
        val call: Call<List<TaskModel>> = mApi.overdue()
        listBehavior(call, listener)
    }

    private fun listBehavior(call: Call<List<TaskModel>>, listener: APIListener<List<TaskModel>>) {
        call.enqueue(object : Callback<List<TaskModel>> {
            override fun onResponse(
                call: Call<List<TaskModel>>,
                response: Response<List<TaskModel>>
            ) {
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

            override fun onFailure(call: Call<List<TaskModel>>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }
        })
    }
}
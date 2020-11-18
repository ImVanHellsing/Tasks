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

class TaskRepository(val context: Context) : BaseRepository(context) {

    // Retrofit Instance
    private val mApi = RetrofitClient.createService(TaskService::class.java)

    fun getOne(taskId: Int, listener: APIListener<TaskModel>) {

        if (!isConnectionAvailable(context)) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        val call: Call<TaskModel> = mApi.getOne(taskId)

        call.enqueue(object : Callback<TaskModel> {
            override fun onResponse(call: Call<TaskModel>, response: Response<TaskModel>) {
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

            override fun onFailure(call: Call<TaskModel>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }
        })
    }

    fun getAll(listener: APIListener<List<TaskModel>>) {
        val call: Call<List<TaskModel>> = mApi.getAll()
        listBehavior(call, listener)
    }

    fun nextWeek(listener: APIListener<List<TaskModel>>) {
        val call: Call<List<TaskModel>> = mApi.nextWeek()
        listBehavior(call, listener)
    }

    fun overdue(listener: APIListener<List<TaskModel>>) {
        val call: Call<List<TaskModel>> = mApi.overdue()
        listBehavior(call, listener)
    }

    fun delete(id: Int, listener: APIListener<Boolean>) {

        if (!isConnectionAvailable(context)) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        val call: Call<Boolean> = mApi.delete(id)

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.code() != TaskConstants.HTTP.SUCCESS) {
                    listener.onFailure(
                        Gson().fromJson(
                            response.errorBody()!!.string(),
                            String::class.java
                        )
                    )
                } else response.body()?.let { listener.onSucess(it) }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }

        })
    }

    fun onUpdateStatus(id: Int, complete: Boolean, listener: APIListener<Boolean>) {

        if (!isConnectionAvailable(context)) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        // Manage task by Id
        val call = if (complete) mApi.complete(id) else mApi.undo(id)

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.code() != TaskConstants.HTTP.SUCCESS) {
                    listener.onFailure(
                        Gson().fromJson(
                            response.errorBody()!!.string(),
                            String::class.java
                        )
                    )
                } else response.body()?.let { listener.onSucess(it) }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }
        })
    }

    fun create(task: TaskModel, listener: APIListener<Boolean>) {

        val call: Call<Boolean> = mApi.create(
            task.priorityId,
            task.description,
            task.dueDate,
            task.complete
        )

        onCreateBehavior(call, listener)
    }

    fun update(task: TaskModel, listener: APIListener<Boolean>) {

        val call: Call<Boolean> = mApi.update(
            task.id,
            task.priorityId,
            task.description,
            task.dueDate,
            task.complete
        )

        onCreateBehavior(call, listener)
    }

    private fun listBehavior(call: Call<List<TaskModel>>, listener: APIListener<List<TaskModel>>) {

        if (!isConnectionAvailable(context)) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        call.enqueue(object : Callback<List<TaskModel>> {
            override fun onFailure(call: Call<List<TaskModel>>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }

            override fun onResponse(
                call: Call<List<TaskModel>>,
                response: Response<List<TaskModel>>
            ) {
                val code = response.code()
                if (code != TaskConstants.HTTP.SUCCESS) {
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
        })
    }

    private fun onCreateBehavior(call: Call<Boolean>, listener: APIListener<Boolean>) {

        if (!isConnectionAvailable(context)) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        call.enqueue(object : Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }

            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.code() != TaskConstants.HTTP.SUCCESS) {
                    listener.onFailure(
                        Gson().fromJson(
                            response.errorBody()!!.string(),
                            String::class.java
                        )
                    )
                } else response.body()?.let { listener.onSucess(it) }
            }
        })
    }
}
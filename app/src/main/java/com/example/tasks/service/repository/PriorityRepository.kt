package com.example.tasks.service.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.model.PriorityModel
import com.example.tasks.service.repository.local.TaskDatabase
import com.example.tasks.service.repository.remote.PriorityService
import com.example.tasks.service.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PriorityRepository(val context: Context): BaseRepository(context) {

    // Database Instance
    private val mPriorityDatabase = TaskDatabase.getDatabase(context).priorityDAO()

    // Retrofit Instance
    private val mApi = RetrofitClient.createService(PriorityService::class.java)

    fun getAll() {

        if (!isConnectionAvailable(context)) {
            return
        }

        val call: Call<List<PriorityModel>> = mApi.getAll()

        call.enqueue(object : Callback<List<PriorityModel>> {
            override fun onResponse(
                call: Call<List<PriorityModel>>,
                response: Response<List<PriorityModel>>
            ) {
                if (response.code() == TaskConstants.HTTP.SUCCESS) {
                    mPriorityDatabase.clear()
                    response.body()?.let { mPriorityDatabase.store(it) }
                }
            }

            override fun onFailure(call: Call<List<PriorityModel>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getDescriptionById(id: Int) = mPriorityDatabase.getDescription(id)

    fun listAll() = mPriorityDatabase.listPriorities()
}
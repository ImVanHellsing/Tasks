package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidationListener
import com.example.tasks.service.model.TaskModel
import com.example.tasks.service.repository.TaskRepository

class AllTasksViewModel(application: Application) : AndroidViewModel(application) {

    private val mTaskRepository = TaskRepository(application)

    private val mTaskList = MutableLiveData<List<TaskModel>>()
    var taskList: LiveData<List<TaskModel>> = mTaskList

    fun list() {
        mTaskRepository.getAll(object : APIListener<List<TaskModel>> {
            override fun onSucess(model: List<TaskModel>) {
                mTaskList.value = model
            }

            override fun onFailure(msg: String) {
                mTaskList.value = arrayListOf()
            }
        })
    }
}
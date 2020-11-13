package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidationListener
import com.example.tasks.service.model.PriorityModel
import com.example.tasks.service.model.TaskModel
import com.example.tasks.service.repository.PriorityRepository
import com.example.tasks.service.repository.TaskRepository
import com.example.tasks.service.repository.local.TaskDatabase

class TaskFormViewModel(application: Application) : AndroidViewModel(application) {

    private val mPriorityRepository = PriorityRepository(application)
    private val mTaskRepository = TaskRepository(application)

    private val mPriorityList = MutableLiveData<List<PriorityModel>>()
    var priorityList: LiveData<List<PriorityModel>> = mPriorityList

    private val mValidation = MutableLiveData<ValidationListener>()
    var validation: LiveData<ValidationListener> = mValidation

    fun listPriorities() {
        mPriorityList.value = mPriorityRepository.listAll()
    }

    fun store(newTask: TaskModel) {
        mTaskRepository.create(newTask, object : APIListener<Boolean> {
            override fun onSucess(model: Boolean) {
                mValidation.value = ValidationListener()
            }

            override fun onFailure(msg: String) {
                mValidation.value = ValidationListener(msg)
            }
        })
    }
}
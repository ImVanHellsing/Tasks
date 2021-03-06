package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidationListener
import com.example.tasks.service.model.TaskModel
import com.example.tasks.service.repository.TaskRepository

class AllTasksViewModel(application: Application) : AndroidViewModel(application) {

    private val mTaskRepository = TaskRepository(application)

    private var mTaskFilter = 0

    private val mTaskList = MutableLiveData<List<TaskModel>>()
    var taskList: LiveData<List<TaskModel>> = mTaskList

    private val mValidation = MutableLiveData<ValidationListener>()
    var validation: LiveData<ValidationListener> = mValidation

    fun list(filter: Int) {
        mTaskFilter = filter

        val listener = object : APIListener<List<TaskModel>> {
            override fun onSucess(model: List<TaskModel>) {
                mTaskList.value = model
            }

            override fun onFailure(msg: String) {
                mTaskList.value = arrayListOf()
                mValidation.value = ValidationListener(msg)
            }
        }

        when (mTaskFilter) {
            TaskConstants.FILTER.ALL -> {
                mTaskRepository.getAll(listener)
            }
            TaskConstants.FILTER.NEXT -> {
                mTaskRepository.nextWeek(listener)
            }
            TaskConstants.FILTER.EXPIRED -> {
                mTaskRepository.overdue(listener)
            }
        }
    }

    fun onDelete(id: Int) {
        mTaskRepository.delete(id, object : APIListener<Boolean> {
            override fun onSucess(model: Boolean) {
                list(mTaskFilter)
                mValidation.value = ValidationListener()
            }

            override fun onFailure(msg: String) {
                mValidation.value = ValidationListener(msg)
                println("Ocorreu uma falha ao tentar deletar a sua tarefa!")
            }

        })
    }

    private fun updateStatus(id: Int, complete: Boolean) {
        mTaskRepository.onUpdateStatus(id, complete, object : APIListener<Boolean> {
            override fun onSucess(model: Boolean) {
                list(mTaskFilter)
            }

            override fun onFailure(msg: String) {}

        })
    }

    fun onUndo(id: Int) {
        updateStatus(id, false)
    }

    fun onComplete(id: Int) {
        updateStatus(id, true)
    }
}
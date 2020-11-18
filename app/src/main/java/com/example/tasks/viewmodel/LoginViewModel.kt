package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.service.model.HeaderModel
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidationListener
import com.example.tasks.service.repository.PersonRepository
import com.example.tasks.service.repository.PriorityRepository
import com.example.tasks.service.repository.local.SecurityPreferences
import com.example.tasks.service.repository.remote.RetrofitClient

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val mSharedPreferences = SecurityPreferences(application)
    private val mPersonRepository = PersonRepository(application)
    private val mPriorityRepository = PriorityRepository(application)

    private val mLogin = MutableLiveData<ValidationListener>()
    var login: LiveData<ValidationListener> = mLogin

    private val mLoggedUser = MutableLiveData<Boolean>()
    var loggedUser: LiveData<Boolean> = mLoggedUser

    /**
     * Faz login usando API
     */
    fun doLogin(email: String, password: String) {
        mPersonRepository.login(email, password, object : APIListener<HeaderModel> {
            override fun onSucess(model: HeaderModel) {
                mSharedPreferences.store(TaskConstants.SHARED.TOKEN_KEY, model.token)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_NAME, model.name)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_KEY, model.personKey)

                RetrofitClient.addHeaders(model.personKey, model.token)

                mLogin.value = ValidationListener()
            }

            override fun onFailure(msg: String) {
                mLogin.value = ValidationListener(msg)
            }

        })
    }

    /**
     * Verifica se usuário está logado
     */
    fun verifyLoggedUser() {

        val personKey = mSharedPreferences.get(TaskConstants.SHARED.PERSON_KEY)
        val tokenKey = mSharedPreferences.get(TaskConstants.SHARED.TOKEN_KEY)


        val logged = (tokenKey != "" && personKey != "")

        RetrofitClient.addHeaders(personKey, tokenKey)

        mLoggedUser.value = logged

        if (!logged) {
            mPriorityRepository.getAll()
        }
    }
}
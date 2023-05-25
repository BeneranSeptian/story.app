package com.seftian.storyapp.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.seftian.storyapp.ui.activities.login.LoginViewModel
import com.seftian.storyapp.ui.activities.signup.SignupViewModel

class MainViewModelFactory(): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if(modelClass.isAssignableFrom(SignupViewModel::class.java)){
            return SignupViewModel() as T
        }

        if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel() as T
        }

        throw IllegalArgumentException ("UnknownViewModel")
    }

}
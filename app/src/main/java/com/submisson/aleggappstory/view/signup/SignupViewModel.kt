package com.submisson.aleggappstory.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.submisson.aleggappstory.data.UserRepository
import com.submisson.aleggappstory.data.response.RegisterResponse
import com.submisson.aleggappstory.data.Result

class SignupViewModel(private val repository: UserRepository): ViewModel() {

    private val _registerViewModel = MediatorLiveData<Result<RegisterResponse>>()
    val registerViewModel: LiveData<Result<RegisterResponse>> = _registerViewModel

    fun register(name: String, email: String, password: String){
        val liveData = repository.register(name, email, password)
        _registerViewModel.addSource(liveData){ result ->
            _registerViewModel.value = result
        }
    }
}
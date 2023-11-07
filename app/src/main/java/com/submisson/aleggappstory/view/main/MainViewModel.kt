package com.submisson.aleggappstory.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.submisson.aleggappstory.data.Result
import com.submisson.aleggappstory.data.UserRepository
import com.submisson.aleggappstory.data.pref.UserModel
import com.submisson.aleggappstory.data.response.ListStoryItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository): ViewModel() {
    fun getStories(token : String): LiveData<PagingData<ListStoryItem>> =
        repository.getStories(token).cachedIn(viewModelScope)

    fun getSession(): LiveData<UserModel>{
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
package com.submisson.aleggappstory.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.submisson.aleggappstory.data.UserRepository
import com.submisson.aleggappstory.data.response.ListStoryItem
import com.submisson.aleggappstory.data.Result
import com.submisson.aleggappstory.data.pref.UserModel

class MapsViewModel(private val repository: UserRepository): ViewModel() {
    private val _mapsLocationViewModel = MediatorLiveData<Result<List<ListStoryItem>>>()
    val mapsLocationViewModel : LiveData<Result<List<ListStoryItem>>> = _mapsLocationViewModel

    fun getStoriesWithLocation(token: String){
        val liveData = repository.getStoryWithLocation(token)
        _mapsLocationViewModel.addSource(liveData){ result ->
            _mapsLocationViewModel.value = result
        }
    }

    fun getSession(): LiveData<UserModel>{
        return repository.getSession().asLiveData()
    }
}
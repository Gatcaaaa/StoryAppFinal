package com.submisson.aleggappstory.view.upload

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.submisson.aleggappstory.data.UserRepository
import com.submisson.aleggappstory.data.pref.UserModel
import com.submisson.aleggappstory.data.response.UploadStoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.submisson.aleggappstory.data.Result

class AddStoryViewModel(private val repository: UserRepository): ViewModel() {
    private val _addStoryViewModel = MediatorLiveData<Result<UploadStoriesResponse>>()
    val addStoryViewModel: LiveData<Result<UploadStoriesResponse>> = _addStoryViewModel

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        currentLocation: Location?
    ) {
        val liveData = repository.addStory(token, file, description,currentLocation)
        _addStoryViewModel.addSource(liveData) { result ->
            _addStoryViewModel.value = result
        }
    }
}
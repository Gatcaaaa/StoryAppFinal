package com.submisson.aleggappstory.data

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.submisson.aleggappstory.data.pref.UserModel
import com.submisson.aleggappstory.data.pref.UserPreference
import com.submisson.aleggappstory.data.response.ListStoryItem
import com.submisson.aleggappstory.data.response.LoginResponse
import com.submisson.aleggappstory.data.response.RegisterResponse
import com.submisson.aleggappstory.data.response.UploadStoriesResponse
import com.submisson.aleggappstory.data.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserRepository private constructor(

    private val userPreference: UserPreference,
    private val apiService: ApiService
){
    suspend fun saveSession(user: UserModel){
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel>{
        return userPreference.getSession()
    }

    suspend fun logout(){
        userPreference.logout()
    }

    fun register(
        name: String,
        email: String,
        password: String
    ):LiveData<Result<RegisterResponse>> =
        liveData(Dispatchers.IO){
            emit(Result.Loading)
            try {
                val response = apiService.register(name, email, password)
                emit(Result.Success(response))
            } catch (e: Exception){
                emit(Result.Error(e.message.toString()))
            }
        }

    fun login(
        email: String,
        password: String
    ): LiveData<Result<LoginResponse>> =
        liveData(Dispatchers.IO){
            emit(Result.Loading)
            try {
                val response = apiService.login(email, password)
                val token = response.loginResult.token
                saveSession(UserModel(email, token))
                emit(Result.Success(response))
            } catch (e: Exception){
                emit(Result.Error(e.message.toString()))
            }
        }

   fun getStories(token: String): LiveData<PagingData<ListStoryItem>>{
       return Pager(
           config = PagingConfig(
               pageSize = 5
           ),
           pagingSourceFactory = {
               PagingSource("Bearer $token", apiService)
           }
       ).liveData
   }

    fun addStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        currentLocation: Location?
    ): LiveData<Result<UploadStoriesResponse>> =
        liveData(Dispatchers.IO){
            emit(Result.Loading)
            try {
                val response = if (currentLocation != null){
                    apiService.uploadStories(
                        "Bearer $token",
                        file, description,
                        currentLocation.latitude.toString()
                            .toRequestBody("text/plain".toMediaType()),
                        currentLocation.longitude.toString()
                            .toRequestBody("text/plain".toMediaType())
                    )
                } else {
                    apiService.uploadStories("Bearer $token", file, description)
                }
                emit(Result.Success(response))
            } catch (e: Exception){
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getStoryWithLocation(token: String): LiveData<Result<List<ListStoryItem>>> =
        liveData(Dispatchers.IO){
            emit(Result.Loading)
            try {
                val response = apiService.getStoriesWithLocation("Bearer $token")
                val storyItem = response.listStory
                emit(Result.Success(storyItem))
            } catch (e: Exception){
                emit(Result.Error(e.message.toString()))
            }
        }

    companion object {
        @Volatile
        private var instance : UserRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository = instance ?: synchronized(this){
            instance ?: UserRepository(userPreference,apiService)
        }.also { instance = it }
    }
}
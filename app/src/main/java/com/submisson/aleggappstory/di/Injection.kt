package com.submisson.aleggappstory.di

import android.content.Context
import com.submisson.aleggappstory.data.UserRepository
import com.submisson.aleggappstory.data.pref.UserPreference
import com.submisson.aleggappstory.data.pref.dataStore
import com.submisson.aleggappstory.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.apiService()
        return UserRepository.getInstance(pref, apiService)
    }
}
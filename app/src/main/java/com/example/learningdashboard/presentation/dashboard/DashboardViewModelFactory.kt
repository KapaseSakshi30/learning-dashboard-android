package com.example.learningdashboard.presentation.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.learningdashboard.data.local.database.AppDatabase
import com.example.learningdashboard.data.remote.api.MockApiService
import com.example.learningdashboard.data.remote.repository.CourseRepositoryImpl
import com.example.learningdashboard.domain.usecase.GetCoursesUseCase
import com.example.learningdashboard.domain.usecase.GetProfileUseCase
import com.example.learningdashboard.domain.usecase.SaveProfileUseCase

class DashboardViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            val db = AppDatabase.getInstance(context.applicationContext)
            val userDao = db.userDao()
            val courseDao = db.courseDao()
            val repository = CourseRepositoryImpl(MockApiService(), courseDao)

            return DashboardViewModel(
                getCoursesUseCase = GetCoursesUseCase(repository),
                getProfileUseCase = GetProfileUseCase(userDao),
                saveProfileUseCase = SaveProfileUseCase(userDao)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
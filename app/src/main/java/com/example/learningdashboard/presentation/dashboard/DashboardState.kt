package com.example.learningdashboard.presentation.dashboard


import com.example.learningdashboard.domain.model.Course

sealed class DashboardState {
    object Idle : DashboardState()
    object Loading : DashboardState()
    object Empty : DashboardState()
    data class Success(val courses: List<Course>) : DashboardState()
    data class Error(val message: String) : DashboardState()
}
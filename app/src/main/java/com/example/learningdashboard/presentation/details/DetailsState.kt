package com.example.learningdashboard.presentation.details


import com.example.learningdashboard.domain.model.Course

sealed class DetailsState {
    object Loading : DetailsState()
    data class Success(val course: Course) : DetailsState()
    data class Error(val message: String) : DetailsState()
}
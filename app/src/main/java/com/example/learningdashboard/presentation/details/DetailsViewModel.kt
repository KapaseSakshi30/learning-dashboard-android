package com.example.learningdashboard.presentation.details


import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.core.base.BaseViewModel
import com.example.learningdashboard.core.network.NetworkResult
import com.example.learningdashboard.domain.usecase.GetCourseDetailsUseCase
import com.example.learningdashboard.domain.usecase.ToggleLessonCompletionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val getCourseDetailsUseCase: GetCourseDetailsUseCase,
    private val toggleLessonCompletionUseCase: ToggleLessonCompletionUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<DetailsState>(DetailsState.Loading)
    val uiState: StateFlow<DetailsState> = _uiState.asStateFlow()

    fun loadCourseDetails(courseId: String) {
        viewModelScope.launch {
            getCourseDetailsUseCase(courseId).collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> DetailsState.Loading
                    is NetworkResult.Success -> DetailsState.Success(result.data)
                    is NetworkResult.Error -> DetailsState.Error(result.message)
                }
            }
        }
    }

    fun toggleLesson(courseId: String, lessonId: String) {
        viewModelScope.launch {
            when (val result = toggleLessonCompletionUseCase(courseId, lessonId)) {
                is NetworkResult.Success -> _uiState.value = DetailsState.Success(result.data)
                is NetworkResult.Error -> _uiState.value = DetailsState.Error(result.message)
                is NetworkResult.Loading -> Unit
            }
        }
    }
}
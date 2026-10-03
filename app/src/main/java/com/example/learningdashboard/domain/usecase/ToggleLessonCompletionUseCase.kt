package com.example.learningdashboard.domain.usecase


import com.example.learningdashboard.core.network.NetworkResult
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.repository.CourseRepository

class ToggleLessonCompletionUseCase(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(courseId: String, lessonId: String): NetworkResult<Course> {
        return repository.toggleLessonCompletion(courseId, lessonId)
    }
}
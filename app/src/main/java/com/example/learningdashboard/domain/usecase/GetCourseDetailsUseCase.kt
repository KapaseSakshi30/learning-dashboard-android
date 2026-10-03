package com.example.learningdashboard.domain.usecase


import com.example.learningdashboard.core.network.NetworkResult
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow

class GetCourseDetailsUseCase(
    private val repository: CourseRepository
) {
    operator fun invoke(courseId: String): Flow<NetworkResult<Course>> {
        return repository.getCourseDetails(courseId)
    }
}
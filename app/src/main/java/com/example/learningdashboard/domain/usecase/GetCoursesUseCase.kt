package com.example.learningdashboard.domain.usecase


import com.example.learningdashboard.core.network.NetworkResult
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow

class GetCoursesUseCase(
    private val repository: CourseRepository
) {
    operator fun invoke(): Flow<NetworkResult<List<Course>>> {
        return repository.getCourses()
    }
}
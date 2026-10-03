package com.example.learningdashboard.data.remote.repository
//
//
//import com.example.learningdashboard.core.network.NetworkResult
//import com.example.learningdashboard.data.local.dao.CourseDao
//import com.example.learningdashboard.data.mapper.toDomainModel
//import com.example.learningdashboard.data.mapper.toEntity
//import com.example.learningdashboard.data.remote.api.MockApiService
//import com.example.learningdashboard.domain.model.Course
//import com.example.learningdashboard.domain.repository.CourseRepository
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//
//class CourseRepositoryImpl(
//    private val apiService: MockApiService,
//    private val courseDao: CourseDao
//) : CourseRepository {
//
//    override fun getCourses(): Flow<NetworkResult<List<Course>>> = flow {
//        emit(NetworkResult.Loading)
//
//        val cachedCourses = courseDao.getAllCourses().map { it.toDomainModel() }
//        if (cachedCourses.isNotEmpty()) {
//            emit(NetworkResult.Success(cachedCourses))
//        }
//
//        try {
//            val remoteDtos = apiService.fetchCourses()
//            val entities = remoteDtos.map { it.toEntity() }
//            courseDao.insertCourses(entities)
//
//            remoteDtos.forEach { courseDto ->
//                courseDto.lessonItems?.let { lessons ->
//                    courseDao.insertLessons(lessons.map { it.toEntity(courseDto.id.toString()) })
//                }
//            }
//
//            val updatedCourses = courseDao.getAllCourses().map { it.toDomainModel() }
//            emit(NetworkResult.Success(updatedCourses))
//        } catch (e: Exception) {
//            if (cachedCourses.isEmpty()) {
//                emit(NetworkResult.Error("Network failure and no offline data available.", e))
//            }
//        }
//    }
//
//    override fun getCourseDetails(courseId: String): Flow<NetworkResult<Course>> = flow {
//        emit(NetworkResult.Loading)
//        val courseEntity = courseDao.getCourseById(courseId)
//        if (courseEntity == null) {
//            emit(NetworkResult.Error("Course not found"))
//            return@flow
//        }
//        val lessons = courseDao.getLessonsForCourse(courseId).map { it.toDomainModel() }
//        val course = courseEntity.toDomainModel().copy(lessons = lessons)
//        emit(NetworkResult.Success(course))
//    }
//
//    override suspend fun toggleLessonCompletion(
//        courseId: String,
//        lessonId: String
//    ): NetworkResult<Course> {
//        return try {
//            val lessons = courseDao.getLessonsForCourse(courseId)
//            val targetLesson = lessons.find { it.id == lessonId }
//                ?: return NetworkResult.Error("Lesson not found")
//
//            val newStatus = !targetLesson.isCompleted
//            courseDao.updateLessonAndRecalculateProgress(courseId, lessonId, newStatus)
//
//            val updatedCourseEntity = courseDao.getCourseById(courseId)!!
//            val updatedLessons = courseDao.getLessonsForCourse(courseId).map { it.toDomainModel() }
//            val updatedCourse = updatedCourseEntity.toDomainModel().copy(lessons = updatedLessons)
//
//            NetworkResult.Success(updatedCourse)
//        } catch (e: Exception) {
//            NetworkResult.Error("Failed to update lesson", e)
//        }
//    }
//}

import com.example.learningdashboard.core.network.NetworkResult
import com.example.learningdashboard.data.local.dao.CourseDao
import com.example.learningdashboard.data.mapper.toDomainModel
import com.example.learningdashboard.data.mapper.toEntity
import com.example.learningdashboard.data.remote.api.MockApiService
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CourseRepositoryImpl(
    private val apiService: MockApiService,
    private val courseDao: CourseDao
) : CourseRepository {

    override fun getCourses(): Flow<NetworkResult<List<Course>>> = flow {
        emit(NetworkResult.Loading)

        // Get cached courses
        val cachedCourses = courseDao
            .getAllCourses()
            .map { it.toDomainModel() }

        // Emit cached data if available
        if (cachedCourses.isNotEmpty()) {
            emit(NetworkResult.Success(cachedCourses))
        }

        try {
            // Fetch courses from API
            val remoteDtos = apiService.fetchCourses()

            // Save courses locally
            val courseEntities = remoteDtos.map { it.toEntity() }
            courseDao.insertCourses(courseEntities)

            // Save lessons locally
            remoteDtos.forEach { courseDto ->
                courseDto.lessonItems?.let { lessons ->
                    val lessonEntities = lessons.map { it.toEntity(courseDto.id.toString()) }
                    courseDao.insertLessons(lessonEntities)
                }
            }

            // Get updated data from Room
            val updatedCourses = courseDao.getAllCourses().map { it.toDomainModel() }

            emit(NetworkResult.Success(updatedCourses))

        } catch (e: Exception) {

            // Only show an error if there is no cached data
            if (cachedCourses.isEmpty()) {
                emit(
                    NetworkResult.Error(
                        "Network failure and no offline data available.",
                        e
                    )
                )
            }
        }
    }

    override fun getCourseDetails(
        courseId: String
    ): Flow<NetworkResult<Course>> = flow {

        emit(NetworkResult.Loading)

        val courseEntity = courseDao.getCourseById(courseId)

        if (courseEntity == null) {
            emit(NetworkResult.Error("Course not found"))
            return@flow
        }

        val lessons = courseDao
            .getLessonsForCourse(courseId)
            .map { it.toDomainModel() }

        val course = courseEntity
            .toDomainModel()
            .copy(lessons = lessons)

        emit(NetworkResult.Success(course))
    }
    override suspend fun toggleLessonCompletion(
        courseId: String,
        lessonId: String
    ): NetworkResult<Course> {
        return try {
            val cId = courseId.toInt()
            val lId = lessonId.toInt()

            // 1. Get current lesson status from Room
            val lessons = courseDao.getLessonsForCourse(courseId)
            val targetLesson = lessons.find { it.id == lessonId }
                ?: return NetworkResult.Error("Lesson not found")

            val newStatus = !targetLesson.isCompleted

            // 2. Update local Room DB first
            courseDao.updateLessonAndRecalculateProgress(
                courseId = courseId,
                lessonId = lessonId,
                newCompletionStatus = newStatus
            )

            // 3. Sync state with Mock API so fetchCourses() won't reset to 50%
            try {
                apiService.updateLessonStatus(
                    courseId = cId,
                    lessonId = lId,
                    isCompleted = newStatus
                )
            } catch (e: Exception) {
                // Ignore API sync failures when offline
            }

            // 4. Return updated course from Room
            val updatedCourseEntity = courseDao.getCourseById(courseId)
                ?: return NetworkResult.Error("Course not found")
            val updatedLessons = courseDao.getLessonsForCourse(courseId).map { it.toDomainModel() }

            NetworkResult.Success(
                updatedCourseEntity.toDomainModel().copy(lessons = updatedLessons)
            )
        } catch (e: Exception) {
            NetworkResult.Error("Failed to update lesson", e)
        }
    }
}

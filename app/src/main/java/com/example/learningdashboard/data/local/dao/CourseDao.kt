//package com.example.learningdashboard.data.local.dao
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import androidx.room.Transaction
//import com.example.learningdashboard.data.local.entity.CourseEntity
//import com.example.learningdashboard.data.local.entity.LessonEntity
//
//@Dao
//abstract class CourseDao {
//
//    @Query("SELECT * FROM courses")
//    abstract suspend fun getAllCourses(): List<CourseEntity>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract suspend fun insertCourses(courses: List<CourseEntity>)
//
//    @Query("SELECT * FROM courses WHERE id = :courseId")
//    abstract suspend fun getCourseById(courseId: String): CourseEntity?
//
//    @Query("SELECT * FROM lessons WHERE courseId = :courseId")
//    abstract suspend fun getLessonsForCourse(courseId: String): List<LessonEntity>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract suspend fun insertLessons(lessons: List<LessonEntity>)
//
//    @Query("UPDATE lessons SET isCompleted = :isCompleted WHERE id = :lessonId")
//    abstract suspend fun updateLessonCompletion(lessonId: String, isCompleted: Boolean)
//
//    @Query("UPDATE courses SET progressPercentage = :progress WHERE id = :courseId")
//    abstract suspend fun updateCourseProgress(courseId: String, progress: Int)
//
//    @Transaction
//    fun updateLessonAndRecalculateProgress(
//        courseId: String,
//        lessonId: String,
//        newCompletionStatus: Boolean
//    ) {
//        updateLessonCompletionSync(lessonId, newCompletionStatus)
//        val lessons = getLessonsForCourseSync(courseId)
//        if (lessons.isNotEmpty()) {
//            val completedCount = lessons.count { it.isCompleted }
//            val newProgress = ((completedCount.toDouble() / lessons.size) * 100).toInt()
//            updateCourseProgressSync(courseId, newProgress)
//        }
//    }
//
//    // Synchronous helpers used internally by the @Transaction method
//    @Query("UPDATE lessons SET isCompleted = :isCompleted WHERE id = :lessonId")
//    protected abstract fun updateLessonCompletionSync(lessonId: String, isCompleted: Boolean)
//
//    @Query("SELECT * FROM lessons WHERE courseId = :courseId")
//    protected abstract fun getLessonsForCourseSync(courseId: String): List<LessonEntity>
//
//    @Query("UPDATE courses SET progressPercentage = :progress WHERE id = :courseId")
//    protected abstract fun updateCourseProgressSync(courseId: String, progress: Int)
//}
package com.example.learningdashboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.learningdashboard.data.local.entity.CourseEntity
import com.example.learningdashboard.data.local.entity.LessonEntity

@Dao
interface CourseDao {

    @Query("SELECT * FROM courses")
    suspend fun getAllCourses(): List<CourseEntity>

    // CHANGE: Use IGNORE instead of REPLACE to protect local progress updates
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: String): CourseEntity?

    @Query("SELECT * FROM lessons WHERE courseId = :courseId")
    suspend fun getLessonsForCourse(courseId: String): List<LessonEntity>

    // CHANGE: Use IGNORE instead of REPLACE
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET isCompleted = :isCompleted WHERE id = :lessonId")
    suspend fun updateLessonCompletion(lessonId: String, isCompleted: Boolean)

    @Query("UPDATE courses SET progressPercentage = :progress WHERE id = :courseId")
    suspend fun updateCourseProgress(courseId: String, progress: Int)

    @Transaction
    suspend fun updateLessonAndRecalculateProgress(
        courseId: String,
        lessonId: String,
        newCompletionStatus: Boolean
    ) {
        updateLessonCompletion(
            lessonId = lessonId,
            isCompleted = newCompletionStatus
        )

        val lessons = getLessonsForCourse(courseId)

        if (lessons.isNotEmpty()) {
            val completedCount = lessons.count { it.isCompleted }

            val progress = ((completedCount.toDouble() / lessons.size) * 100).toInt()

            updateCourseProgress(
                courseId = courseId,
                progress = progress
            )
        }
    }
}

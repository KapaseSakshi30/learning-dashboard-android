package com.example.learningdashboard.data.mapper

import com.example.learningdashboard.data.local.entity.LessonEntity
import com.example.learningdashboard.data.remote.dto.LessonDto
import com.example.learningdashboard.domain.model.Lesson

fun LessonEntity.toDomainModel(): Lesson = Lesson(
    id = id,
    courseId = courseId,
    title = title,
    isCompleted = isCompleted
)

fun LessonDto.toEntity(courseId: String): LessonEntity = LessonEntity(
    id = id.toString(),
    courseId = courseId,
    title = title,
    isCompleted = isCompleted
)
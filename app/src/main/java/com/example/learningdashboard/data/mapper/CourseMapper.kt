package com.example.learningdashboard.data.mapper


import com.example.learningdashboard.data.local.entity.CourseEntity
import com.example.learningdashboard.data.remote.dto.CourseDto
import com.example.learningdashboard.domain.model.Course

fun CourseEntity.toDomainModel(): Course = Course(
    id = id,
    title = title,
    instructor = instructor,
    progressPercentage = progressPercentage,
    totalLessons = totalLessons
)

fun CourseDto.toEntity(): CourseEntity = CourseEntity(
    id = id.toString(),
    title = title,
    instructor = instructor,
    progressPercentage = progress,
    totalLessons = lessons
)
package com.example.learningdashboard.domain.usecase

import com.example.learningdashboard.data.local.dao.UserDao
import com.example.learningdashboard.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class GetProfileUseCase(private val userDao: UserDao) {
    operator fun invoke(): Flow<UserEntity?> = userDao.getUser()
}
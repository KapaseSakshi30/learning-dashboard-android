package com.example.learningdashboard.domain.usecase


import com.example.learningdashboard.data.local.dao.UserDao
import com.example.learningdashboard.data.local.entity.UserEntity

class SaveProfileUseCase(private val userDao: UserDao) {
    suspend operator fun invoke(name: String, email: String) {
        val updatedUser = UserEntity(
            id = "CURRENT_USER", // Fixed primary key ensures room executes an UPDATE rather than inserting duplicates
            name = name,
            email = email
        )
        userDao.insertOrUpdateUser(updatedUser)
    }
}
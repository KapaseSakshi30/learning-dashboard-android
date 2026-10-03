package com.example.learningdashboard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "CURRENT_USER",
    val name: String,
    val email: String
)
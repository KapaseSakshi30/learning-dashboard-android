package com.example.learningdashboard.domain.usecase


import android.util.Patterns
import com.example.learningdashboard.core.network.NetworkResult
import kotlinx.coroutines.delay

class LoginUseCase {
    suspend operator fun invoke(email: String, password: String): NetworkResult<Boolean> {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return NetworkResult.Error("Please enter a valid email address.")
        }
        if (password.length < 6) {
            return NetworkResult.Error("Password must be at least 6 characters.")
        }
        delay(1000) // Simulate network delay
        return if (email == "user@example.com" && password == "password123") {
            NetworkResult.Success(true)
        } else {
            NetworkResult.Error("Invalid credentials. Use user@example.com / password123")
        }
    }
}
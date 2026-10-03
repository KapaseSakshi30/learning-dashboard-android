package com.example.learningdashboard.domain.usecase

import com.example.learningdashboard.core.network.NetworkResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        loginUseCase = LoginUseCase()
    }

    @Test
    fun invalidEmailReturnsError() = runBlocking {
        val result = loginUseCase("invalid-email", "password123")
        assertTrue(result is NetworkResult.Error)
        assertEquals("Please enter a valid email address.", (result as NetworkResult.Error).message)
    }
}
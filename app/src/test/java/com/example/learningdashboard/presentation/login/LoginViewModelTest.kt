package com.example.learningdashboard.presentation.login

import com.example.learningdashboard.core.network.NetworkResult
import com.example.learningdashboard.domain.usecase.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val loginUseCase: LoginUseCase = mock()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loginFailureEmitsErrorState() = runTest {
        `when`(loginUseCase("bad@email.com", "123"))
            .thenReturn(NetworkResult.Error("Invalid credentials"))

        viewModel.login("bad@email.com", "123")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value is LoginState.Error)
    }
}
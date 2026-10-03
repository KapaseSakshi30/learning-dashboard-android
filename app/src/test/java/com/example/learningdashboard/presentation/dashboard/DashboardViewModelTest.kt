package com.example.learningdashboard.presentation.dashboard


import com.example.learningdashboard.core.network.NetworkResult
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.usecase.GetCoursesUseCase
import com.example.learningdashboard.domain.usecase.GetProfileUseCase
import com.example.learningdashboard.domain.usecase.SaveProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getCoursesUseCase: GetCoursesUseCase = mock()
    private val getProfileUseCase: GetProfileUseCase = mock()
    private val saveProfileUseCase: SaveProfileUseCase = mock()

    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DashboardViewModel(getCoursesUseCase,getProfileUseCase,saveProfileUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadCoursesEmitsSuccessState() = runTest {
        val dummyCourses = listOf(Course("1", "Kotlin Basics", "John", 50, 4))
        `when`(getCoursesUseCase()).thenReturn(
            flowOf(NetworkResult.Loading, NetworkResult.Success(dummyCourses))
        )

        viewModel.loadCourses()
        testDispatcher.scheduler.advanceUntilIdle()

        val currentState = viewModel.uiState.value
        assert(currentState is DashboardState.Success)
        assertEquals(dummyCourses, (currentState as DashboardState.Success).courses)
    }
}
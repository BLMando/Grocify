package com.example.grocify.viewmodels

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Test for [SignInViewModel] class.
 * Uses Robolectric for testing, simulating android environment.
 */
@RunWith(RobolectricTestRunner::class)
class SignInViewModelTest {

    /**
     * Using InstantTaskExecutor to perform LiveData.
     * This rule ensures that all operations on LiveData are executed synchronously.
     */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    /**
     * Mock for [FirebaseAuth] class.
     * Is necessary to simulate Firebase authentication.
     */
    @Mock
    private lateinit var firebaseAuth: FirebaseAuth

    /**
     * Mock for [Task] class returned by [FirebaseAuth.signInWithEmailAndPassword].
     * Is necessary to simulate Firebase authentication result.
     */
    @Mock
    private lateinit var authResultTask: Task<AuthResult>

    /**
     * Mock for [Application] class.
     * Is necessary to initialize Firebase and other dependencies.
     */
    @Mock
    private lateinit var application: Application

    /**
     * Mock for [SignInClient] class.
     * Is necessary to simulate Google Sign In.
     */
    @Mock
    private lateinit var signInClient: SignInClient

    /**
     * Instance of [SignInViewModel] to be tested.
     */
    private lateinit var signInViewModel: SignInViewModel

    /**
     * Test dispatcher for running tests asynchronously.
     * Necessary for testing coroutines.
     */
    private val testDispatcher = StandardTestDispatcher()

    /**
     * Set up the test environment before each test case.
     * Initializes mocks, sets up the test dispatcher, and initializes Firebase.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        Dispatchers.setMain(testDispatcher)

        val context: Context = RuntimeEnvironment.getApplication()
        FirebaseApp.initializeApp(context)

        signInViewModel = SignInViewModel(application, signInClient)
    }

    /**
     * Tear down the test environment after each test case.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test case for [SignInViewModel.signInWithCredentials] method.
     * Checks if the method updates the state correctly for an invalid password.
     * Uses [runTest] to run the test case asynchronously.
     */
    @Test
    fun `signInWithCredentials updates state for invalid password`() = runTest {
        signInViewModel.signInWithCredentials("test@example.com", "12345")

        val state = signInViewModel.signInState.value
        assertEquals("Inserisci una password valida (almeno sei caratteri)", state.passwordError)
        assertFalse(state.isPasswordValid)
    }


    /**
     * Test case for [SignInViewModel.signInWithCredentials] method.
     * Checks if the method updates the state correctly for an invalid email.
     * Uses [runTest] to run the test case asynchronously.
     */
    @Test
    fun `signInWithCredentials updates state for invalid email`() = runTest {
        signInViewModel.signInWithCredentials("invalidemail", "password123")

        val state = signInViewModel.signInState.value
        assertEquals("Inserisci un email valida", state.emailError)
        assertFalse(state.isEmailValid)
    }

    /**
     * Test case for [SignInViewModel.signInWithCredentials] method.
     * Checks if the method updates the state correctly for a successful sign-in attempt.
     * Uses [runTest] to run the test case asynchronously.
     * Uses [UnconfinedTestDispatcher] to avoid conflicts with the main thread dispatcher.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `signInWithCredentials updates state for successful sign-in`() = runTest(UnconfinedTestDispatcher()) {
        `when`(authResultTask.isSuccessful).thenReturn(true)
        `when`(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(authResultTask)
        signInViewModel.signInWithCredentials("test@example.com", "password123")

        authResultTask.addOnCompleteListener {
            val state = signInViewModel.signInState.value

            assertTrue(state.isPasswordValid)
            assertTrue(state.isEmailValid)
            assertTrue(state.isSuccessful)
        }
    }
}


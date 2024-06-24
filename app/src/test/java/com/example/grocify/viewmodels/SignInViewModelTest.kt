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

// Indica che la classe di test utilizza Robolectric per eseguire i test, simulando l'ambiente Android.
@RunWith(RobolectricTestRunner::class)
class SignInViewModelTest {

    // Utilizza InstantTaskExecutorRule per eseguire operazioni LiveData in modo sincrono nel thread dei test.
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Crea un mock per FirebaseAuth, necessario per simulare l'autenticazione Firebase.
    @Mock
    private lateinit var firebaseAuth: FirebaseAuth

    @Mock
    private lateinit var authResultTask: Task<AuthResult>

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var signInClient: SignInClient

    private lateinit var signInViewModel: SignInViewModel

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        // Setup: prepara l'ambiente di test
        MockitoAnnotations.openMocks(this) // Apre i mock per l'annotazione per inizializzare i mock dichiarati nella classe.
        Dispatchers.setMain(testDispatcher) // Imposta il dispatcher principale per i test di coroutine, necessario per eseguire operazioni asincrone in un ambiente controllato.

        val context: Context = RuntimeEnvironment.getApplication() // Ottiene il contesto dell'applicazione per inizializzare Firebase con Robolectric.
        FirebaseApp.initializeApp(context) // Inizializza Firebase con il contesto dell'applicazione ottenuto da Robolectric.

        signInViewModel = SignInViewModel(application, signInClient) // Inizializza signInViewModel utilizzando i mock di Application e SignInClient.
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        // Teardown: pulisce l'ambiente di test dopo il suo utilizzo
        Dispatchers.resetMain() // Ripristina il dispatcher principale dopo i test di coroutine per evitare effetti collaterali su altri test.
    }

    @Test
    fun `signInWithCredentials updates state for invalid password`() = runTest {
        // Test: verifica che il metodo signInWithCredentials gestisca correttamente una password non valida
        signInViewModel.signInWithCredentials("test@example.com", "12345") // Simula una chiamata a signInWithCredentials con una password non valida

        val state = signInViewModel.signInState.value // Ottiene lo stato risultante nel ViewModel
        assertEquals("Inserisci una password valida (almeno sei caratteri)", state.passwordError) // Verifica che l'errore sulla password sia corretto
        assertFalse(state.isPasswordValid) // Verifica che la password non sia valida
    }

    @Test
    fun `signInWithCredentials updates state for invalid email`() = runTest {
        // Test: verifica che il metodo signInWithCredentials gestisca correttamente un'email non valida
        signInViewModel.signInWithCredentials("invalidemail", "password123") // Simula una chiamata a signInWithCredentials con un'email non valida

        val state = signInViewModel.signInState.value // Ottiene lo stato risultante nel ViewModel
        assertEquals("Inserisci un email valida", state.emailError) // Verifica che l'errore sull'email sia corretto
        assertFalse(state.isEmailValid) // Verifica che l'email non sia valida
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `signInWithCredentials updates state for successful sign-in`() = runTest(UnconfinedTestDispatcher()) {
        // Test: verifica che il metodo signInWithCredentials gestisca correttamente un login con successo
        `when`(authResultTask.isSuccessful).thenReturn(true) // Configura il mock per restituire un risultato positivo
        `when`(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(authResultTask) // Configura il mock per il metodo di autenticazione di Firebase

        signInViewModel.signInWithCredentials("test@example.com", "password123") // Esegue signInWithCredentials con credenziali valide

        // Attendi il completamento dell'operazione asincrona
        authResultTask.addOnCompleteListener {
            val state = signInViewModel.signInState.value // Ottiene lo stato risultante nel ViewModel dopo il completamento dell'operazione

            assertTrue(state.isPasswordValid) // Verifica che la password sia valida
            assertTrue(state.isEmailValid) // Verifica che l'email sia valida
            assertTrue(state.isSuccessful) // Verifica che il login sia stato eseguito con successo
        }
    }
}


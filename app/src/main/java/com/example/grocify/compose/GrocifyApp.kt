package com.example.grocify.compose

import android.app.Activity
import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.grocify.HomeUserScreen
import com.example.grocify.compose.signIn.GoogleAuthUiClient
import com.example.grocify.compose.signIn.SignInScreen
import com.example.grocify.viewmodels.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun GrocifyApp(){
    val navController = rememberNavController()
    GrocifyNavHost(
        navController = navController
    )
}

@Composable
fun GrocifyNavHost(navController: NavHostController) {
    val activity = (LocalContext.current as Activity)
    val googleAuthClient by lazy {
        GoogleAuthUiClient(
            context = activity,
            oneTapClient = Identity.getSignInClient(activity)
        )
    }

    NavHost(navController = navController, startDestination = Screen.SignInScreen.route) {
        composable(route = Screen.SignInScreen.route) {

            val viewModel: SignInViewModel = viewModel()
            val state = viewModel.googleSignInState.collectAsState()

            //DA DECOMMENTARE NON APPENA FINITA LA FUNZIONALITA' DI LOGOUT
            //il codice viene eseguito ad ogni render del composable
            /*LaunchedEffect(key1 = Unit) {
                if(googleAuthClient.getSignedInUser() != null){
                    navController.navigate(Screen.SignUpScreen.route)
                }
            }*/

            //launcher per l'intent di login
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if(result.resultCode == RESULT_OK){
                        //se l'intent di login è andato a buon fine, allora si fa il login
                        CoroutineScope(Dispatchers.Main).launch {
                            val signInResult = googleAuthClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            viewModel.onSignInResult(signInResult)
                        }

                    }
                }
            )

            //quando il signIn è andato a buon fine si naviga nella home
            LaunchedEffect(key1 = state.value.isSignInSuccessful) {
                if(state.value.isSignInSuccessful){
                    navController.navigate(Screen.HomeUser.route)
                    viewModel.resetGoogleState()
                }
            }

            SignInScreen(
                viewModel = viewModel,
                onGoSignUp = { navController.navigate(Screen.SignUpScreen.route) },
                onSignInSuccessful = { navController.navigate(Screen.HomeUser.route) },
                onSignInClick = {
                    CoroutineScope(Dispatchers.Main).launch {
                        val signInIntentSender = googleAuthClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                }
            )
        }

        composable(route = Screen.SignUpScreen.route){
            SignUpScreen (
                onGoSignIn = { navController.navigate(Screen.SignInScreen.route) },
                onSignUpSuccess = { navController.navigate(Screen.HomeUser.route) }
            )
        }

        composable(route = Screen.HomeUser.route){
            HomeUserScreen()
        }

    }
}

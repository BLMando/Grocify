package com.example.grocify.compose

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.grocify.compose.homeUser.HomeUserScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
fun GrocifyApp() {
    val navController = rememberNavController()
    GrocifyNavHost(
        navController = navController
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GrocifyNavHost(navController: NavHostController) {

    val activity = (LocalContext.current as Activity)
    val scanner = GmsBarcodeScanning.getClient(activity)

    NavHost(navController = navController, startDestination = Screen.SignInScreen.route) {
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(
                context = activity,
                onGoSignUp = { navController.navigate(Screen.SignUpScreen.route) },
                onSignInSuccessful = { navController.navigate(Screen.ScanProductScreen.route) }
            )
        }

        composable(route = Screen.SignUpScreen.route){
            SignUpScreen (
                onGoSignIn = { navController.navigate(Screen.SignInScreen.route) },
                onSignUpSuccess = { navController.navigate(Screen.HomeUser.route) }
            )
        }

        composable(route = Screen.HomeUser.route){
            HomeUserScreen(
                onProfileClick = { navController.navigate(Screen.UserProfile.route) }
            )
        }

        composable(route = Screen.ScanProductScreen.route){
            ScanProductScreen(viewModel(),scanner)
        }

    }
}

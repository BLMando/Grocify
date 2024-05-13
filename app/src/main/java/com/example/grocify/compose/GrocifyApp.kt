package com.example.grocify.compose

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun GrocifyApp(){
    val navController = rememberNavController()
    GrocifyNavHost(navController = navController)
}

@Composable
fun GrocifyNavHost(navController: NavHostController) {

    val activity = (LocalContext.current as Activity)

    NavHost(navController = navController, startDestination = Screen.SignInScreen.route) {

        //DEFAULT SCREEN
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(
                context = activity,
                onGoSignUp = { navController.navigate(Screen.SignUpScreen.route) },
                onSignInSuccessful = { navController.navigate(Screen.HomeScreen.route) }
            )
        }

        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen(
                onGoSignIn = { navController.navigate(Screen.SignInScreen.route) },
                onSignUpSuccess = { navController.navigate(Screen.HomeScreen.route) }
            )
        }

        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        //END DEFAULT SCREEN


        //USER SCREEN
        composable(
            route = Screen.CategoryItems.route,
            arguments = Screen.CategoryItems.navArguments
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("categoryId")
            CategoryItemsScreen(
                categoryId = url,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = Screen.UserProfile.route) {
            UserProfileScreen(
                context = activity,
                onSignOut = { navController.navigate(Screen.SignInScreen.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = Screen.GiftScreen.route) {
            GiftProductScreen()
        }
        //END USER SCREEN

    }
}

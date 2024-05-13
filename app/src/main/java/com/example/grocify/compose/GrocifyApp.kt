package com.example.grocify.compose

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation


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
                navController = navController
            )
        }

        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen(
                onGoSignIn = { navController.navigate(Screen.SignInScreen.route) },
                onSignUpSuccess = { navController.navigate("user") }
            )
        }
        //END DEFAULT SCREEN


        //USER SCREEN
        navigation(
            route = "user",
            startDestination = Screen.HomeUserScreen.route
        ){
            composable(route = Screen.HomeUserScreen.route){
                HomeUserScreen(
                    onProfileClick = { navController.navigate(Screen.UserProfile.route) },
                    onCategoryClick = { navController.navigate(Screen.CategoryItems.route) },
                )
            }

            composable(
                route = Screen.CategoryItems.route,
                arguments = Screen.CategoryItems.navArguments
            ) { backStackEntry ->
                val url = backStackEntry.arguments?.getString("categoryId")
                CategoryItemsScreen(
                    categoryId = url,
                    onBackClick = { navController.popBackStack() },
                    onCatalogClick = { navController.navigate(Screen.HomeUserScreen.route) },
                    onGiftClick = { navController.navigate(Screen.GiftScreen.route) }
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
        }
        //END USER SCREEN

        navigation(
            route = "admin",
            startDestination = Screen.HomeAdminScreen.route
        ){
            composable(route = Screen.HomeAdminScreen.route){
                HomeAdminScreen()
            }
        }

        navigation(
            route = "driver",
            startDestination = Screen.HomeDriverScreen.route
        ){
            composable(route = Screen.HomeDriverScreen.route){
                HomeDriverScreen()
            }
        }



    }
}

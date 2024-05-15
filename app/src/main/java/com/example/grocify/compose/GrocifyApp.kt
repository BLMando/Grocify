package com.example.grocify.compose

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.grocify.compose.screens.CartScreen
import com.example.grocify.compose.screens.home.HomeAdminScreen
import com.example.grocify.compose.screens.home.HomeDriverScreen
import com.example.grocify.compose.screens.home.HomeUserScreen
import com.example.grocify.compose.screens.CategoryItemsScreen
import com.example.grocify.compose.screens.GiftProductScreen
import com.example.grocify.compose.screens.ScanProductScreen
import com.example.grocify.compose.screens.SignInScreen
import com.example.grocify.compose.screens.SignUpScreen
import com.example.grocify.compose.screens.UserProfileScreen

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
fun GrocifyApp(){
    val navController = rememberNavController()
    GrocifyNavHost(navController = navController)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GrocifyNavHost(navController: NavHostController) {

    val activity = (LocalContext.current as Activity)
    val scanner = GmsBarcodeScanning.getClient(activity)

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
            //Bottom navigation route
            val onCatalogClick = { navController.navigate(Screen.HomeUserScreen.route) }
            val onGiftClick = { navController.navigate(Screen.GiftScreen.route) }
            val onPhysicalCartClick = { navController.navigate(Screen.CartScreen.route) }
            val onVirtualCartClick = { navController.navigate(Screen.ScanScreen.route) }

            composable(route = Screen.HomeUserScreen.route){
                HomeUserScreen(
                    onProfileClick = { navController.navigate(Screen.UserProfile.route) },
                    onCategoryClick = {
                        navController.navigate(Screen.CategoryItems.createRoute(
                            categoryId = it
                        ))},
                    onGiftClick = onGiftClick,
                    onPhysicalCartClick = onPhysicalCartClick,
                    onVirtualCartClick = onVirtualCartClick
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
                    onCatalogClick = onCatalogClick,
                    onGiftClick = onGiftClick,
                    onPhysicalCartClick = onPhysicalCartClick,
                    onVirtualCartClick = onVirtualCartClick,
                )
            }

            composable(route = Screen.UserProfile.route) {
                UserProfileScreen(
                    context = activity,
                    onSignOut = { navController.navigate(Screen.SignInScreen.route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(route = Screen.ScanScreen.route){
                ScanProductScreen(
                    scanner = scanner,
                    onCatalogClick = onCatalogClick,
                    onGiftClick = onGiftClick,
                    onPhysicalCartClick = onPhysicalCartClick
                )
            }

            composable(route = Screen.CartScreen.route){
                CartScreen(
                    onCatalogClick = onCatalogClick,
                    onGiftClick = onGiftClick,
                    onVirtualCartClick = onVirtualCartClick
                )
            }

            composable(route = Screen.GiftScreen.route) {
                GiftProductScreen(
                    onCatalogClick = onCatalogClick,
                    onPhysicalCartClick = onPhysicalCartClick,
                    onVirtualCartClick = onVirtualCartClick,
                )
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

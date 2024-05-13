package com.example.grocify.compose

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route:String,
    val navArguments: List<NamedNavArgument> = emptyList()
){

    data object SignInScreen: Screen("login")
    data object SignUpScreen: Screen("register")
    data object HomeScreen: Screen("home")
    data object UserProfile: Screen("user_profile")
    data object CategoryItems: Screen(
        route = "category_items/{categoryId}",
        navArguments = listOf(
            navArgument("categoryId") {
                type = NavType.StringType
            }
        )
    ){
        fun createRoute(categoryId: String) = "category_items/${categoryId}"
    }

    data object GiftScreen : Screen("gift")



}
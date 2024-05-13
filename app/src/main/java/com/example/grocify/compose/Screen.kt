package com.example.grocify.compose

import androidx.navigation.NavArgument

sealed class Screen (
    val route:String,
    val navArguments: List<NavArgument> = emptyList()
){
    data object SignInScreen: Screen("login")
    data object SignUpScreen: Screen("register")
    data object HomeUser: Screen("home_user")
    data object UserProfile: Screen("user_profile")


}
package com.example.grocify.compose

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(
    val route:String,
    val navArguments: List<NamedNavArgument> = emptyList()
){
    //USER ROUTES
    data object SignInScreen: Screen("login")
    data object SignUpScreen: Screen("register")
    data object HomeUserScreen: Screen("home_user")
    data object CategoryItems: Screen(
        route = "category_items/{categoryId}",
        navArguments = listOf(
            navArgument("categoryId") {
                type = NavType.StringType
            }
        )
    ){ fun createRoute(categoryId: String) = "category_items/${categoryId}" }
    data object ScanScreen: Screen("scan")
    data object CartScreen: Screen("cart")
    data object GiftScreen : Screen("gift")
    data object UserAccount: Screen("user_account")
    data object UserProfile: Screen("user_profile")
    data object UserAddresses: Screen("user_addresses")
    data object UserOrders: Screen("user_orders")
    data object UserPayment: Screen("user_payment")

    data object CheckoutScreen: Screen(
        route = "checkout_screen/{flagCart}/{totalPrice}",
        navArguments = listOf(
            navArgument("flagCart") {
                type = NavType.StringType
            },
            navArgument("totalPrice") {
                type = NavType.StringType
            }
        )
    ){
        fun createRoute(flagCart: String, totalPrice: String) = "checkout_screen/${flagCart}/${totalPrice}"
    }

    data object OrderSuccessScreen: Screen(
        route = "order_success/{flagCart}",
        navArguments = listOf(
            navArgument("flagCart") {
                type = NavType.StringType
            }
        )
    ){
        fun createRoute(flagCart: String) = "order_success/${flagCart}"
    }

    data object TrackOrderScreen: Screen("track_order")
    //END USER ROUTES


    //DRIVER ROUTES
    data object HomeDriverScreen: Screen("home_driver")
    data object OrderDetailsScreen: Screen("order_details")
    data object MapScreen:  Screen("map")
    //END DRIVER ROUTES


    //ADMIN ROUTES
    data object HomeAdminScreen: Screen("home_admin")
    //END ADMIN ROUTES
}
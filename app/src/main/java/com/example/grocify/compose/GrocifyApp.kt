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
import com.example.grocify.compose.screens.CheckoutScreen
import com.example.grocify.compose.screens.GiftProductScreen
import com.example.grocify.compose.screens.MapScreen
import com.example.grocify.compose.screens.OrderDetailsScreen
import com.example.grocify.compose.screens.OrderSuccessScreen
import com.example.grocify.compose.screens.ScanProductScreen
import com.example.grocify.compose.screens.SignInScreen
import com.example.grocify.compose.screens.SignUpScreen
import com.example.grocify.compose.screens.TrackOrderScreen
import com.example.grocify.compose.screens.account.UserAccountScreen
import com.example.grocify.compose.screens.account.UserAddressScreen
import com.example.grocify.compose.screens.account.UserOrdersScreen
import com.example.grocify.compose.screens.account.UserPaymentsScreen
import com.example.grocify.compose.screens.account.UserProfileScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@Composable
fun GrocifyApp(){
    val navController = rememberNavController()
    GrocifyNavHost(navController = navController)
}

@OptIn(ExperimentalPermissionsApi::class)
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


        //USER SCREENS
        navigation(
            route = "user",
            startDestination = Screen.HomeUserScreen.route
        ){
            //Bottom navigation route
            val onCatalogClick = { navController.navigate(Screen.HomeUserScreen.route) }
            val onGiftClick = { navController.navigate(Screen.GiftScreen.route) }
            val onPhysicalCartClick = { navController.navigate(Screen.ScanScreen.route) }
            val onVirtualCartClick = { navController.navigate(Screen.CartScreen.route) }

            composable(route = Screen.HomeUserScreen.route){
                HomeUserScreen(
                    onAccountClick = { navController.navigate(Screen.UserAccount.route) },
                    onCategoryClick = {
                        navController.navigate(Screen.CategoryItems.createRoute(
                            categoryId = it
                        ))},
                    onGiftClick = onGiftClick,
                    onPhysicalCartClick = onPhysicalCartClick,
                    onVirtualCartClick = onVirtualCartClick,
                    onTrackOrderClick = {
                        navController.navigate(Screen.TrackOrderScreen.createRoute(
                        orderId = it
                    )) }
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

            composable(route = Screen.ScanScreen.route){
                ScanProductScreen(
                    activity = activity,
                    onCatalogClick = onCatalogClick,
                    onGiftClick = onGiftClick,
                    onVirtualCartClick = onVirtualCartClick,
                    onCheckoutClick = { navController.navigate(Screen.CheckoutScreen.createRoute(
                        flagCart = "store",
                        totalPrice = it,
                    ))},
                    onTrackOrderClick = {
                        navController.navigate(Screen.TrackOrderScreen.createRoute(
                            orderId = it
                        )) },
                )
            }

            composable(route = Screen.CartScreen.route){
                CartScreen(
                    onCatalogClick = onCatalogClick,
                    onGiftClick = onGiftClick,
                    onPhysicalCartClick = onPhysicalCartClick,
                    onCheckoutClick = { navController.navigate(Screen.CheckoutScreen.createRoute(
                        flagCart = "online",
                        totalPrice = it,
                    ))},
                    onTrackOrderClick = {
                        navController.navigate(Screen.TrackOrderScreen.createRoute(
                            orderId = it
                        )) },
                )
            }

            composable(route = Screen.GiftScreen.route) {
                GiftProductScreen(
                    onCatalogClick = onCatalogClick,
                    onPhysicalCartClick = onPhysicalCartClick,
                    onVirtualCartClick = onVirtualCartClick,
                )
            }

            composable(route = Screen.UserAccount.route) {
                UserAccountScreen(
                    context = activity,
                    onBackClick = { navController.popBackStack() },
                    onLogOutClick = { navController.navigate(Screen.SignInScreen.route) },
                    onUserProfileClick = { navController.navigate(Screen.UserProfile.route) },
                    onUserAddressesClick = { navController.navigate(Screen.UserAddresses.route) },
                    onUserOrdersClick = { navController.navigate(Screen.UserOrders.route) }
                ) { navController.navigate(Screen.UserPayment.route) }
            }

            composable(route = Screen.UserProfile.route){
                UserProfileScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(route = Screen.UserAddresses.route){
                UserAddressScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(route = Screen.UserPayment.route){
                UserPaymentsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(route = Screen.UserOrders.route){
                UserOrdersScreen(
                    onBackClick = { navController.popBackStack() },
                    onTrackOrderClick = { navController.navigate(Screen.TrackOrderScreen.createRoute(
                        orderId = it
                    )) }
                )
            }

            composable(
              route = Screen.CheckoutScreen.route,
              arguments = Screen.CheckoutScreen.navArguments
            ) { backStackEntry ->
                val flagCart = backStackEntry.arguments?.getString("flagCart")
                val totalPrice = backStackEntry.arguments?.getString("totalPrice")
                val onConfirmClick: (String, String) -> Unit = { _ , orderId ->
                    navController.navigate(
                        Screen.OrderSuccessScreen.createRoute(
                            flagCart = flagCart!!,
                            orderId = orderId
                        )
                    )
                }
                CheckoutScreen(
                    flagCart = flagCart!!,
                    totalPrice = totalPrice!!,
                    onBackClick = {navController.popBackStack() },
                    onAddressClick = {navController.navigate(Screen.UserAddresses.route)},
                    onPaymentMethodClick = {navController.navigate(Screen.UserPayment.route)},
                    onCatalogClick = onCatalogClick,
                    onGiftClick = onGiftClick,
                    onVirtualCartClick = onVirtualCartClick,
                    onConfirmClick = onConfirmClick
                )
            }

            composable(
                route = Screen.OrderSuccessScreen.route,
                arguments = Screen.OrderSuccessScreen.navArguments
            )
            { backStackEntry ->
                val flagCart = backStackEntry.arguments?.getString("flagCart")
                val orderId = backStackEntry.arguments?.getString("orderId")
                OrderSuccessScreen(
                    flagCart = flagCart!!,
                    orderId = orderId!!,
                    onHomeClick = onCatalogClick,
                    onTrackOrderClick = { navController.navigate(Screen.TrackOrderScreen.createRoute(
                        orderId = orderId
                    )) }
                )
            }

            composable(
                route = Screen.TrackOrderScreen.route,
                arguments = Screen.TrackOrderScreen.navArguments
            ){ backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId")
                TrackOrderScreen(
                    orderId = orderId!!,
                    onBackClick = onCatalogClick
                )
            }
        }
        //END USER SCREENS

        //DRIVER SCREENS
        navigation(
            route = "driver",
            startDestination = Screen.HomeDriverScreen.route
        ){

            composable(route = Screen.HomeDriverScreen.route){
                HomeDriverScreen(
                    context = activity,
                    onLogOutClick = { navController.navigate(Screen.SignInScreen.route) },
                    onGroceryClick = {navController.navigate(Screen.OrderDetailsScreen.createRoute(
                        orderId = it
                    ))}
                )
            }

            composable(
                route = Screen.OrderDetailsScreen.route,
                arguments = Screen.OrderDetailsScreen.navArguments
            ){ backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId")
                OrderDetailsScreen(
                    orderId = orderId!!,
                    onBackClick = {navController.popBackStack()}
                )
            }

            composable(route = Screen.MapScreen.route){
                MapScreen(
                    context = activity,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }


        //END DRIVER SCREENS


        //ADMIN SCREENS
        navigation(
            route = "admin",
            startDestination = Screen.HomeAdminScreen.route
        ){
            composable(route = Screen.HomeAdminScreen.route){
                HomeAdminScreen()
            }
        }
        //END ADMIN SCREENS
    }
}

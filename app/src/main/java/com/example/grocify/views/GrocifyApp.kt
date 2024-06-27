package com.example.grocify.views

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.grocify.views.screens.CartScreen
import com.example.grocify.views.screens.home.HomeAdminScreen
import com.example.grocify.views.screens.home.HomeDriverScreen
import com.example.grocify.views.screens.home.HomeUserScreen
import com.example.grocify.views.screens.CategoryItemsScreen
import com.example.grocify.views.screens.CheckoutScreen
import com.example.grocify.views.screens.GiftProductScreen
import com.example.grocify.views.screens.MapScreen
import com.example.grocify.views.screens.OrderDetailsScreen
import com.example.grocify.views.screens.OrderFinishedScreen
import com.example.grocify.views.screens.OrderSuccessScreen
import com.example.grocify.views.screens.SaleGiftScreen
import com.example.grocify.views.screens.ScanProductScreen
import com.example.grocify.views.screens.SignInScreen
import com.example.grocify.views.screens.SignUpScreen
import com.example.grocify.views.screens.TrackOrderScreen
import com.example.grocify.views.screens.UsersManagementScreen
import com.example.grocify.views.screens.account.UserAccountScreen
import com.example.grocify.views.screens.account.UserAddressScreen
import com.example.grocify.views.screens.account.UserOrdersScreen
import com.example.grocify.views.screens.account.UserPaymentsScreen
import com.example.grocify.views.screens.account.UserProfileScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi

/**
 * Main app composable that configures navigation graph
 */
@Composable
fun GrocifyApp() {
    val navController = rememberNavController()
    GrocifyNavHost(navController = navController)
}

/**
 * Is the NavHost composable that configures the
 * different screens and navigation within the app
 */
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
                    onTrackOrderClick = {
                        navController.navigate(Screen.TrackOrderScreen.createRoute(
                            orderId = it
                        )) },
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
                val onTrackOrderClick: (String, String?) -> Unit = { _, fromScreen ->
                    navController.navigate(
                        Screen.TrackOrderScreen.createRoute(
                            orderId = orderId!!,
                            fromScreen = fromScreen
                        )
                    )
                }
                OrderSuccessScreen(
                    flagCart = flagCart!!,
                    orderId = orderId!!,
                    onHomeClick = onCatalogClick,
                    onTrackOrderClick = onTrackOrderClick
                )
            }

            composable(
                route = Screen.TrackOrderScreen.route,
                arguments = Screen.TrackOrderScreen.navArguments
            ){ backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId")
                val fromScreen = backStackEntry.arguments?.getString("fromScreen") ?: ""
                TrackOrderScreen(
                    orderId = orderId!!,
                    onBackClick = {
                        if(fromScreen.isEmpty())
                            navController.popBackStack()
                        else
                            onCatalogClick()
                    },
                    onQRScanned = { navController.navigate(Screen.OrderFinishedScreen.route)}
                )
            }

            composable(
                route = Screen.OrderFinishedScreen.route
            ){
                OrderFinishedScreen(
                    onHomeClick = onCatalogClick,
                )
            }
        }
        //END USER SCREENS

        //DRIVER SCREENS
        navigation(
            route = "driver",
            startDestination = Screen.HomeDriverScreen.route
        ){
            composable(
                route = Screen.HomeDriverScreen.route
            ){
                val onGroceryClick: (String, String) -> Unit = { orderId , destination ->
                    navController.navigate(
                        Screen.OrderDetailsScreen.createRoute(
                            orderId = orderId,
                            destination = destination,
                        )
                    )
                }

                val mapRedirect: (String, String) -> Unit = { orderId , destination ->
                    navController.navigate(
                        Screen.MapScreen.createRoute(
                            orderId = orderId,
                            destination = destination,
                        )
                    )
                }
                HomeDriverScreen(
                    context = activity,
                    onLogOutClick = { navController.navigate(Screen.SignInScreen.route) },
                    onGroceryClick = onGroceryClick,
                    mapRedirect = mapRedirect,
                    onQRScanned = { navController.navigate(Screen.HomeDriverScreen.route) }
                )
            }

            composable(
                route = Screen.OrderDetailsScreen.route,
                arguments = Screen.OrderDetailsScreen.navArguments
            ){ backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId")
                val destination = backStackEntry.arguments?.getString("destination")
                val onProceedClick: (String, String) -> Unit = { _ , _ ->
                    navController.navigate(
                        Screen.MapScreen.createRoute(
                            destination = destination!!,
                            orderId = orderId!!,
                        )
                    )
                }
                OrderDetailsScreen(
                    activity = activity,
                    orderId = orderId!!,
                    destination = destination!!,
                    onBackClick = {navController.popBackStack()},
                    onProceedClick = onProceedClick
                )
            }

            composable(
                route = Screen.MapScreen.route,
                arguments = Screen.MapScreen.navArguments
            ){ backStackEntry ->
                val destination = backStackEntry.arguments?.getString("destination")
                val orderId = backStackEntry.arguments?.getString("orderId")
                MapScreen(
                    context = activity,
                    destination = destination,
                    orderId = orderId,
                    onBackClick = { navController.popBackStack() },
                    onQRScanned = { navController.navigate(Screen.HomeDriverScreen.route) },
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
                HomeAdminScreen(
                    context = activity,
                    onSaleClick = {navController.navigate(Screen.SaleGiftScreen.createRoute(
                        flagPage = it
                    ))},
                    onGiftClick =  {navController.navigate(Screen.SaleGiftScreen.createRoute(
                        flagPage = it
                    ))},
                    onUsersClick = { navController.navigate(Screen.UsersManagementScreen.route) },
                    onLogOutClick = { navController.navigate(Screen.SignInScreen.route) }
                )
            }

            composable(
                route = Screen.SaleGiftScreen.route,
                arguments = Screen.SaleGiftScreen.navArguments
            ){ backStackEntry ->
                val flagPage = backStackEntry.arguments?.getString("flagPage").toBoolean()
                SaleGiftScreen(
                    isSaleContent = flagPage,
                    onStatsClick = { navController.navigate(Screen.HomeAdminScreen.route) },
                    onSaleClick = {navController.navigate(Screen.SaleGiftScreen.createRoute(
                        flagPage = it
                    ))},
                    onGiftClick =  {navController.navigate(Screen.SaleGiftScreen.createRoute(
                        flagPage = it
                    ))},
                    onUsersClick = { navController.navigate(Screen.UsersManagementScreen.route) },
                )
            }

            composable(
                route = Screen.UsersManagementScreen.route,
                arguments = Screen.UsersManagementScreen.navArguments
            ){ backStackEntry ->
                val flagPage = backStackEntry.arguments?.getString("flagPage").toBoolean()
                UsersManagementScreen(
                    onStatsClick = { navController.navigate(Screen.HomeAdminScreen.route) },
                    onSaleClick = {navController.navigate(Screen.SaleGiftScreen.createRoute(
                        flagPage = it
                    ))},
                    onGiftClick =  {navController.navigate(Screen.SaleGiftScreen.createRoute(
                        flagPage = it
                    ))},
                )
            }

        }

        //END ADMIN SCREENS
    }
}

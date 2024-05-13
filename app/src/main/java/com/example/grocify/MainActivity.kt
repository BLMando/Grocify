package com.example.grocify


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.grocify.compose.GrocifyApp
import com.example.grocify.compose.homeUser.HomeUserScreen
import com.example.grocify.ui.theme.GrocifyTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        setContent {
            GrocifyTheme {
                GrocifyApp()
            }
        }
    }
}

/********* UI SIGN-IN / SIGN - UP *********/

/*@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    GrocifyTheme {
        SignUpScreen(viewModel = ,onGoSignIn = {}, onSignUpSuccess = {})
    }
}*/

//@Preview(showBackground = true)
/*@Composable
fun SignInScreenPreview() {
    GrocifyTheme {
        SignInScreen {
            navController.navigate(Screen.SignUpScreen.route)
        }
    }
}*/

/********* UI DEL DRIVER *********/

//@Preview(showBackground = true)
@Composable
fun HomeDriverScreenPreview() {
    GrocifyTheme {
        HomeDriverScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun OrderDetailsScreenPreview() {
    GrocifyTheme {
        OrderDetailsScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    GrocifyTheme {
        //MapScreen()
    }
}

/********* UI DELL'UTENTE *********/

//@Preview(showBackground = true)
@Composable
fun HomeUserScreenPreview() {
    GrocifyTheme {
        //HomeUserScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun CategoryItemsScreenPreview() {
    GrocifyTheme {
       CategoryItemsScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun ScanProductScreenPreview() {
    GrocifyTheme {
        //ScanProductScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    GrocifyTheme {
        CartScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
    GrocifyTheme {
        CheckoutScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun OrderSuccessScreenPreview() {
    GrocifyTheme {
        OrderSuccessScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun TrackOrderScreenPreview() {
    GrocifyTheme {
        TrackOrderScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    GrocifyTheme {
        UserProfileScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun UserOptionsScreenPreview() {
    GrocifyTheme {
        UserOptionsScreen(
            topBarText = "Indirizzi di spedizione",
            titleFirst = "Indirizzo in uso",
            titleSecond = "I tuoi indirizzi"
        )
    }
}

//@Preview(showBackground = true)
@Composable
fun GiftProductScreenPreview() {
    GrocifyTheme {
       GiftProductScreen()
    }
}

/********* UI ADMIN *********/
//@Preview(showBackground = true)
@Composable
fun HomeAdminScreenPreview() {
    GrocifyTheme {
        HomeAdminScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun SaleGiftScreenPreview() {
    GrocifyTheme {
        SaleGiftScreen(isSaleContent = false)
    }
}




















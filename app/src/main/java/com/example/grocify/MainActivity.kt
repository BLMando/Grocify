package com.example.grocify


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.grocify.compose.GrocifyApp
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
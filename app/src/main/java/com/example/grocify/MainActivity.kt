package com.example.grocify


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.grocify.compose.GrocifyApp
import com.example.grocify.ui.theme.GrocifyTheme

class MainActivity : AppCompatActivity() {

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
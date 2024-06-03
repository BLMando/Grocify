package com.example.grocify


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.grocify.compose.GrocifyApp
import com.example.grocify.ui.theme.GrocifyTheme


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        //createCommunicationChannel(this)

        setContent {
            GrocifyTheme {
                GrocifyApp()
            }
        }
    }

    /*private fun createCommunicationChannel(context: Context) {
        val channel = NotificationChannel(
            context.getString(R.string.default_notification_channel_id),
            "OrderStatus",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }*/
}
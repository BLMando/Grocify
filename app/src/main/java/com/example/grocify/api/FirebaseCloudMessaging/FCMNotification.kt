package com.example.grocify.api.FirebaseCloudMessaging

import com.squareup.moshi.JsonClass

data class FCMNotification(
    val message: Message
)

data class Message(
    val token: String,
    val notification: Notification
)

data class Notification(
    val title: String,
    val body: String
)

@JsonClass(generateAdapter = true)
data class FCMResponse(
    val name: String
)
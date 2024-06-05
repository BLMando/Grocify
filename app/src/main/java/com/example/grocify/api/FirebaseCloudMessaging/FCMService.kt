package com.example.grocify.api.FirebaseCloudMessaging

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMService {
    @Headers("Authorization: Bearer ya29.a0AXooCgu8yBjkdFkJkPMlbQQ7_bwMrXSOT7mTTfNLEFlMC4-TexWBZAQyIExa__bdCnomhtlge77Qi7M8q_T7EQbZnh7uDueoAf5eN14VkvZaaU8GySbRywYqzvQ4gmEI5vj8kVnQQqgLfnmnIRhA5dPB_JDgngOpgwrJaCgYKAXgSARASFQHGX2MirLw2NHpqRx3s6v2D9O2SqA0171") // Replace with a method to get OAuth2 token dynamically
    @POST("v1/projects/grocify-b90b4/messages:send")
    fun sendNotification(@Body notification: FCMNotification): Call<FCMResponse>
}
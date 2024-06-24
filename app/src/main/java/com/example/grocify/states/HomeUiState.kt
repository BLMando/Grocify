package com.example.grocify.states

import com.example.grocify.data.remote.SentimentAnalysis.SentimentData
import com.example.grocify.model.Category
import com.example.grocify.model.Order
import com.example.grocify.model.Review


data class HomeUserUiState(
    val currentUserName: String? = "",
    val categories: List<Category> = emptyList(),
    val orderId: String = ""
)


data class HomeDriverUiState(
    val currentUserName: String? = "",
    val orders: List<Order> = emptyList()
)

data class HomeAdminUiState(
    val currentUserName: String? = "",
    val reviews: List<Review> = emptyList(),
    val sentimentAnalysisResult: HashMap<Int, List<SentimentData>> =  hashMapOf(),
    val analysisIsLoaded: Boolean = false,
    val top10Products: List<Pair<String, Int>> = emptyList(),
    val top10Categories: List<Pair<String, Int>> = emptyList(),
    val averageMonthlyOrders: List<Pair<String, Int>> = emptyList(),
    val averageMonthlyUsersExpense: List<Pair<String, Int>> = emptyList()
)



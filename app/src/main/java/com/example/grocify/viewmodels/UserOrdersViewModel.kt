package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.UserOrdersUiState
import com.example.grocify.model.Order
import com.example.grocify.model.Review
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserOrdersViewModel (application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserOrdersUiState())
    val uiState: StateFlow<UserOrdersUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    fun setReviewIconClicked(value: Boolean) = run {
        _uiState.update { currentState ->
            currentState.copy(
                isReviewClicked = value
            )
        }
    }

    fun setOrderReviewId(order: Order){
        _uiState.update { currentState ->
            currentState.copy(
                orderReview = order
            )
        }
    }

    fun getAllOrders(){
        viewModelScope.launch {
            db.collection("orders")
                .whereEqualTo("userId",auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { documents ->
                    val ordersList:MutableList<Order> = mutableListOf()
                    for (document in documents) {
                        document.toObject(Order::class.java).let { order ->
                            ordersList.add(order)
                        }
                    }
                    _uiState.update { currentState ->
                        currentState.copy(orders = ordersList.toList())
                    }
                }
        }
    }

    fun addOrderReview(orderId: String, userId: String, text: String, rating: Float){
        viewModelScope.launch {
            db.collection("orders_reviews")
               .add(
                   Review(
                       orderId = orderId,
                       userId = userId,
                       review = text,
                       rating = rating,
                       date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                   )
               ).addOnSuccessListener {
                   _uiState.update { currentState ->
                       currentState.copy(
                           ordersReviewed = _uiState.value.ordersReviewed + orderId
                       ) }

               }
        }
    }

    fun getOrdersReviewed() {
        viewModelScope.launch {
            db.collection("orders_reviews")
                .get()
                .addOnSuccessListener { ordersReviewed ->
                    val listOrderId = mutableListOf<String>()
                    for (orderReviewed in ordersReviewed)
                        listOrderId.add(orderReviewed.toObject(Review::class.java).orderId)

                    _uiState.update { currentState ->
                        currentState.copy(ordersReviewed = listOrderId.toList())
                    }
                }
        }
    }

}

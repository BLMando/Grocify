package com.example.grocify.viewmodels

import android.app.Application
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
                       rating = rating
                   )
               )
        }
    }

}

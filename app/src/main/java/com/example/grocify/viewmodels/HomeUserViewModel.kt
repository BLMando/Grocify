package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.states.HomeUserUiState
import com.example.grocify.model.Category
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel class for HomeUserScreen
 * @param application - Application context
 */
class HomeUserViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUserUiState())
    val uiState: StateFlow<HomeUserUiState> = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    /**
     * Function to get the name of the currently signed-in user
     */
    fun getSignedInUserName() {
        val currentUser = auth.currentUser?.uid
        viewModelScope.launch {
            db.collection("users")
                .whereEqualTo("uid", currentUser)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        _uiState.update { currentState ->
                            currentState.copy(currentUserName = document.data["name"].toString().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
        }
    }

    /**
     * Function to get the categories from the database
     * and update the UI state with the categories list
     * @see Category
     */
     fun getCategories() {

         val categories: MutableList<Category> = mutableListOf()

         viewModelScope.launch {
            db.collection("categories")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val id = document.id
                        val name = document.get("nome").toString().replaceFirstChar { it.uppercase() }
                        val image = document.get("immagine").toString()
                        categories.add(Category(id,name,image))
                    }
                    _uiState.update {
                        it.copy(categories = categories.toList())
                    }
                }
        }
    }

    /**
     * Function to check if the user has any active orders
     * and update the UI state with the order ID if present
     * @see HomeUserUiState
     */
    fun checkOrdersStatus(){
        db.collection("orders")
            .whereEqualTo("userId", auth.currentUser!!.uid)
            .whereNotEqualTo("status", "concluso")
            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                if (documentSnapshot!!.documents.isEmpty()) {
                    _uiState.update {
                        it.copy(orderId = "")
                    }
                }
                else {
                    val orderId = documentSnapshot.documents[0].get("orderId").toString()
                    _uiState.update {
                        it.copy(orderId = orderId)
                    }
                }
            }
    }
}
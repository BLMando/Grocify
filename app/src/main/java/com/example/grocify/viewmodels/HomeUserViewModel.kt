package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.Category
import com.example.grocify.data.HomeUserUiState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeUserViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUserUiState())
    val uiState: StateFlow<HomeUserUiState> = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun getSignedInUser() {
        val currentUser = auth.currentUser?.email
        viewModelScope.launch {
            db.collection("users")
                .whereEqualTo("email", currentUser)
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

     fun getCategories() {

         val categories: MutableList<Category> = mutableListOf()

         viewModelScope.launch {
            db.collection("categories")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val name = document.get("nome").toString().replaceFirstChar { it.uppercase() }
                        val image = document.get("immagine").toString()
                        categories.add(Category(name,image))
                    }
                    _uiState.update {
                        it.copy(categories = categories.toList())
                    }
                }
        }
    }
}
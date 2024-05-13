package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.HomeUserUiState
import com.example.grocify.data.signIn.UserData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeUserViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUserUiState())
    val uiState: StateFlow<HomeUserUiState> = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun getSignedInUser() =  auth.currentUser?.run {
        val username = displayName?.split(" ")
        _uiState.update {
            it.copy(currentUserName = username?.get(0))
        }
    }

     fun getCategories() {
        val categories = mutableListOf<String>()

        viewModelScope.launch {
            db.collection("prodotti")
                .orderBy("categoria")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        categories.add(document.data["categoria"].toString())
                        _uiState.update {
                            it.copy(categories = categories.distinct())
                        }
                    }
                }
        }
    }
}
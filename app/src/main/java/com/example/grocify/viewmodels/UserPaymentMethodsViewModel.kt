package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.grocify.data.UserPaymentMethodsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserPaymentMethodsViewModel (application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserPaymentMethodsUiState())
    val uiState: StateFlow<UserPaymentMethodsUiState> = _uiState.asStateFlow()

    fun setFABClicked(value: Boolean) = run {
        _uiState.update { currentState ->
            currentState.copy(
                isFABClicked = value
            )
        }
    }

}
package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.grocify.data.UserAddressesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserAddressesViewModel(application: Application):AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserAddressesUiState())
    val uiState: StateFlow<UserAddressesUiState> = _uiState.asStateFlow()

    fun setFABClicked(value: Boolean) = run {
        _uiState.update { currentState ->
            currentState.copy(
                isFABClicked = value
            )
        }
    }

}
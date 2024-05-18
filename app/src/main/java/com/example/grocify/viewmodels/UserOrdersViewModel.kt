package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.grocify.data.UserOrdersUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserOrdersViewModel (application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserOrdersUiState())
    val uiState: StateFlow<UserOrdersUiState> = _uiState.asStateFlow()


    fun setReviewIconClicked(value: Boolean) = run {
        _uiState.update { currentState ->
            currentState.copy(
                isReviewClicked = value
            )
        }
    }

}

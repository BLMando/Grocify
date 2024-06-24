package com.example.grocify.states

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.grocify.databinding.MapLayoutBinding
import com.tomtom.sdk.routing.route.Route

data class MapUiState(
    val requestLocationPermissions: Boolean? = null,
    val locationAcquired: Boolean = false,
    val route: Route? = null,
    val binding: MapLayoutBinding? = null,
    val openDialog: Boolean = false,
    val mapWidth: Dp = 400.dp,
    val mapHeight: Dp = 600.dp
)

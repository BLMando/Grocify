package com.example.grocify.data

import android.net.Uri
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.grocify.databinding.MapLayoutBinding
import com.tomtom.sdk.routing.route.Route

data class MapUiState(
    val requestLocationPermissions: Boolean? = null,
    val locationAcquired: Boolean = true,
    val route: Route? = null,
    val binding: MapLayoutBinding? = null,
    val openDialog: Boolean = false,
    val mapWidth: Dp = 400.dp,
    val mapHeight: Dp = 600.dp
)

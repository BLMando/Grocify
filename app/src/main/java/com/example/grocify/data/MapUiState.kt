package com.example.grocify.data

import androidx.viewbinding.ViewBinding
import com.tomtom.sdk.routing.route.Route

data class MapUiState(
    val requestLocationPermissions: Boolean? = null,
    val route: Route? = null,
    val binding: ViewBinding? = null
)

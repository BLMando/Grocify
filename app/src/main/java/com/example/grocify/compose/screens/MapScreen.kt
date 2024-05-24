package com.example.grocify.compose.screens

import android.Manifest
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.grocify.databinding.MapLayoutBinding
import com.example.grocify.viewmodels.MapViewModel
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.ui.MapFragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.viewbinding.ViewBinding
import com.example.grocify.BuildConfig
import com.example.grocify.R
import com.tomtom.sdk.common.measures.UnitSystem
import com.tomtom.sdk.navigation.UnitSystemType
import com.tomtom.sdk.navigation.ui.NavigationFragment
import com.tomtom.sdk.navigation.ui.NavigationUiOptions
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    context: Activity,
    viewModel: MapViewModel = viewModel()
){
    val contextCompose = LocalContext.current
    val uiState = viewModel.uiState.collectAsState()
    val fragmentManager = (LocalContext.current as? FragmentActivity)?.supportFragmentManager

    //METODO PER LA GESTIONE DEI PERMESSI PER LA GEOLOCALIZZAZIONE
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            viewModel.showUserLocation()
        } else {
            Toast.makeText(
                contextCompose,
                ContextCompat.getString(
                    context.applicationContext,
                    R.string.location_permission_denied
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(key1 = uiState.value.requestLocationPermissions) {
        if(uiState.value.requestLocationPermissions == true) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

    }

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        text = "Navigazione",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight(500),
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {  }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "arrow back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .height(600.dp)
                    .width(350.dp)
                    .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                if(uiState.value.binding == null)
                    TomTomMapContainer(viewModel)
                else
                    AndroidViewBinding(factory = { _, _, _ ->
                        uiState.value.binding!!
                    })
            }

            Button(
                onClick = {
                    if (uiState.value.route != null) {
                        viewModel.initNavigationFragment(
                            MapLayoutBinding.inflate(context.layoutInflater),
                            fragmentManager
                        )
                        if (uiState.value.binding != null) viewModel.startNavigation(uiState.value.route!!)
                    }
                },
                Modifier
                    .width(350.dp)
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Navigation,
                    contentDescription = "navigation icon",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White,
                )
                Text(
                    "Avvia navigazione",
                    color = Color.White,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }

        /*Dialog(
            "Sei arrivato a destinazione",
            true,
            Icons.Filled.LocationOn,
            "Scansiona",
            Icons.Filled.QrCodeScanner
        ) {
            Column (
                Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "15:30",
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Text(
                    text = "Scansiona il QR code del cliente per confermare la ricezione dell'ordine",
                    style = TextStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }*/
    }
}


@Composable
fun TomTomMapContainer(viewModel: MapViewModel) {

    val fragmentManager = (LocalContext.current as? FragmentActivity)?.supportFragmentManager

    AndroidViewBinding(factory = { inflater, _, _->

        val binding = MapLayoutBinding.inflate(inflater)

        val mapOptions = MapOptions(mapKey = BuildConfig.TOMTOM_API_KEY)
        val mapFragment = MapFragment.newInstance(mapOptions)

        // Aggiungi il MapFragment al FragmentManager
        fragmentManager?.beginTransaction()
            ?.replace(binding.mapFragment.id, mapFragment)
            ?.commit()

        viewModel.initMap(mapFragment)
        viewModel.initNavigationTileStore()
        viewModel.initLocationProvider()
        viewModel.initRouting()
        viewModel.initNavigation()

        binding

    })
}


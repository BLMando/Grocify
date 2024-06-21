package com.example.grocify.views.screens

import android.Manifest
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocify.BuildConfig
import com.example.grocify.R
import com.example.grocify.databinding.MapLayoutBinding
import com.example.grocify.ui.theme.BlueMedium
import com.example.grocify.viewmodels.MapViewModel
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.ui.MapFragment
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    context: Activity,
    onBackClick: () -> Unit,
    onQRScanned: () -> Unit,
    destination: String?,
    orderId: String?
){
    val contextCompose = LocalContext.current
    val scanner = GmsBarcodeScanning.getClient(context)
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

    LaunchedEffect(key1 = uiState.value.locationAcquired) {
        if(uiState.value.locationAcquired) {
            viewModel.calculateRouteTo(destination!!)
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
                        onClick = onBackClick
                    ) {
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
                    .height(uiState.value.mapHeight)
                    .width(uiState.value.mapWidth)
            ) {
                TomTomMapContainer(viewModel,uiState.value.binding)
            }

            Button(
                onClick = {
                    if (uiState.value.route != null) {
                        viewModel.initNavigationFragment(
                            MapLayoutBinding.inflate(context.layoutInflater),
                            fragmentManager,
                            orderId
                        )
                        viewModel.startNavigation(uiState.value.route!!,orderId)
                    }
                },
                Modifier
                    .width(400.dp)
                    .height(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Navigation,
                    contentDescription = "navigation icon",
                    modifier = Modifier.size(25.dp),
                    tint = Color.White,
                )
                Text(
                    "Avvia navigazione",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }

    Dialog(
        uiState.value.openDialog,
        orderId!!,
        viewModel,
        scanner,
        onQRScanned
    )

    LocationDialog(uiState.value.locationAcquired)
}


@Composable
fun TomTomMapContainer(viewModel: MapViewModel, bind: MapLayoutBinding?) {

    val fragmentManager = (LocalContext.current as? FragmentActivity)?.supportFragmentManager

    AndroidViewBinding(factory = { inflater, _, _ ->

        val binding = bind ?: MapLayoutBinding.inflate(inflater)

        if (bind == null){
            val mapOptions = MapOptions(
                mapKey = BuildConfig.TOMTOM_API_KEY,
                cameraOptions = CameraOptions(
                    position = GeoPoint(41.29246, 12.5736108),
                    zoom = 4.5,
                    tilt = 45.0,
                    rotation = 5.0
                )
            )

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
        }

        binding

    })
}

@Composable
fun Dialog(
    state: Boolean,
    orderId: String,
    viewModel: MapViewModel,
    scanner: GmsBarcodeScanner,
    onQRScanned: () -> Unit,
) {
    if (state) {
        AlertDialog(
            onDismissRequest = { viewModel.setDialogState(false) },
            title = {
                Text(
                    text = "Sei arrivato a destinazione",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),

                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription ="dialog icon",
                    tint = BlueMedium,
                    modifier = Modifier
                        .padding(top = 35.dp)
                        .height(70.dp)
                        .fillMaxWidth(),
                )
            },
            text = {
                Column (
                    Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).toString(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Scansiona il QR code del cliente per confermare la ricezione dell'ordine",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { scanner.startScan().addOnSuccessListener {QRcode ->
                        if(orderId == QRcode.rawValue.toString()){
                            viewModel.setOrderConclude(orderId)
                            viewModel.setDialogState(false)
                            onQRScanned()
                        }
                    }},
                    Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Scansiona",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            },
            properties = DialogProperties(
                dismissOnClickOutside = false
            )
        )
    }
}

@Composable
fun LocationDialog(
    state:Boolean
) {
    if (!state) {
        AlertDialog(
            containerColor = Color.White,
            shape = AlertDialogDefaults.shape,
            onDismissRequest = { },
            title = {
                Text(
                    text = "Geolocalizzazione in corso...",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),

                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription ="dialog icon",
                    tint = BlueMedium,
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .height(55.dp)
                        .fillMaxWidth(),
                )
            },
            text = {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    CircularProgressIndicator()
                }

            },
            confirmButton = {}
        )
    }
}







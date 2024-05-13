package com.example.grocify

import CameraScreen
import android.Manifest
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.components.ItemsQuantitySelector
import com.example.grocify.components.ListItems
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun ScanProductScreen() {

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var analyzerType by remember { mutableStateOf(AnalyzerType.UNDEFINED) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = { Text(
                    text = "Scansiona i prodotti",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight(500),
                        color = Color.Black,
                    ),
                ) },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            Box(modifier = Modifier.padding(16.dp)) { // Aggiungi un padding per allineare con il BottomAppBar
                if (cameraPermissionState.status.isGranted) {
                    if (analyzerType == AnalyzerType.UNDEFINED) {
                        FloatingActionButton(
                            onClick = { analyzerType = AnalyzerType.BARCODE },
                            containerColor = BlueDark,
                        ) {
                            Image(
                                painterResource(id = R.drawable.bar_code),
                                contentDescription = "bar code scanner",
                                colorFilter = ColorFilter.tint(Color.White),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    } else {

                        CameraScreen(analyzerType)
                    }
                } else if (cameraPermissionState.status.shouldShowRationale) {
                    Text("Camera Permission permanently denied")
                } else {
                    SideEffect {
                        cameraPermissionState.run { launchPermissionRequest() }
                    }
                    Text("No Camera Permission")
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier
                    .shadow(10.dp, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                tonalElevation = 30.dp,
                containerColor = Color.White,
                actions = {
                    Row (
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Icon(
                                Icons.Filled.ShoppingBag,
                                contentDescription = "Localized description"
                            )
                            Text(
                                text = "Catalogo",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = Color.Black

                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = "Localized description",
                                tint = BlueLight
                            )
                            Text(
                                text = "Scansiona",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = BlueLight
                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Icon(Icons.Filled.CardGiftcard, contentDescription = "Localized description")
                            Text(
                                text = "Per te",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = Color.Black
                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Localized description")
                            Text(
                                text = "Carrello",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = Color.Black
                                )
                            )
                        }
                    }
                },
            )
        },
        content = { innerPadding ->
            Column (
                Modifier.padding(innerPadding)
            ) {
                Text(
                    text = "La tua spesa",
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Divider(
                    color = Color.LightGray,
                    thickness = 0.6.dp,
                    modifier = Modifier.padding(start= 20.dp,top = 10.dp,end = 20.dp,bottom = 15.dp),
                )
                LazyColumn{
                    items(5){
                        ListItems("food","Apples","300g"){
                            ItemsQuantitySelector()
                        }
                    }
                }
            }
        }
    )
    }


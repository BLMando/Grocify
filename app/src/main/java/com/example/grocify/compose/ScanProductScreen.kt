package com.example.grocify.compose

import android.annotation.SuppressLint
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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocify.R
import com.example.grocify.viewmodels.ScanProductScreenViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun ScanProductScreen(
    viewModel: ScanProductScreenViewModel = viewModel(),
    scanner: GmsBarcodeScanner
) {

    val scanUiState by viewModel.scanUiState.collectAsState()

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
        floatingActionButton = //{ ScanBarcode({ barcodeScanner.startScan() }, barcodeScanner.barCodeResults.collectAsStateWithLifecycle().value) },//{
        {
            Box(modifier = Modifier.padding(16.dp)) { // Aggiungi un padding per allineare con il BottomAppBar
                FloatingActionButton(

                    //onClick = { analyzerType = AnalyzerType.BARCODE; viewModel.setStatoBarcode() },
                    onClick = { scanner.startScan()
                        .addOnSuccessListener { barcode ->
                            val rawValue: String? = barcode.rawValue
                            viewModel.aggiungiRiga(rawValue.toString())
                            Log.v("funziona", rawValue.toString())
                        }
                        .addOnCanceledListener {
                            Log.v("funziona", "chiuso è")
                        }
                        .addOnFailureListener { e ->
                            Log.v("funziona", "no")
                        }},
                    containerColor = BlueDark,
                ) {
                    Image(
                        painterResource(id = R.drawable.bar_code),
                        contentDescription = "bar code scanner",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier.size(30.dp)
                    )
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
                    var currentIndex = 0

                    scanUiState.lista?.let {
                        items(it.size){
                            ListItems(image = scanUiState.lista!![currentIndex]?.image, name = scanUiState.lista!![currentIndex]?.name, quantity = scanUiState.lista!![currentIndex]?.quantity){
                                scanUiState.lista!![currentIndex]?.units?.let { it1 ->
                                    ItemsQuantitySelector(
                                        it1
                                    )
                                }
                            }
                            currentIndex = (currentIndex + 1)
                        }
                    }
                }
            }
        }
    )
}
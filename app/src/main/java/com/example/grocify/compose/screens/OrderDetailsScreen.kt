package com.example.grocify.compose.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneOutline
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.ui.theme.BlueDark
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocify.compose.components.CartItems
import com.example.grocify.ui.theme.BlueMedium
import com.example.grocify.viewmodels.OrderDetailsViewModel
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    viewModel: OrderDetailsViewModel = viewModel(),
    orderId: String,
    onBackClick: () -> Unit,
    onProceedClick: (destination: String, orderId: String) -> Unit,
    activity: Activity,
    destination: String
) {

    val uiState = viewModel.uiState.collectAsState()
    val scanner = GmsBarcodeScanning.getClient(activity)

    LaunchedEffect(key1 = Unit) {
        viewModel.getOrderProducts(orderId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = { Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight(500),
                                color = Color.Black,
                            ),
                        ) {
                            append("Ordine ")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight(500),
                                color = Color.Black,
                                fontStyle = FontStyle.Italic
                            )
                        ) {
                            append(orderId)
                        }
                    }
                ) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back icon"
                        )
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                    scanner.startScan().addOnSuccessListener { barcode ->
                        viewModel.markProduct(barcode.rawValue.toString(),orderId)
                    }},
                containerColor = BlueDark,
            )
            {
                Icon(imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription =
                    "scan",
                    tint = Color.White,
                    modifier = Modifier
                    .size(30.dp)
                )
        } },
        content = { innerPadding ->
            Column(
                Modifier.padding(innerPadding)
            ) {
                Text(
                    text = "Prodotti",
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Divider(
                    color = Color.LightGray,
                    thickness = 0.6.dp,
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp),
                )
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(uiState.value.products.size) { index ->
                        //Log.v("OrderDetail", uiState.value.products[index].toString())
                        CartItems(
                            product = uiState.value.products[index],
                            viewModel = viewModel,
                            flagCart = "",
                            productMarked = if (uiState.value.isProductsMarked.isEmpty()) false else uiState.value.isProductsMarked[index]
                        )
                    }
                }
            }

            if ((uiState.value.isProductsMarked.all{ it }) && (uiState.value.products.size != 0))
                AlertDialog(
                    onDismissRequest = { },
                    title = {
                        Text(
                            text = "Ordine completato",
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
                            imageVector = Icons.Filled.DoneOutline,
                            contentDescription = "icona",
                            tint = BlueMedium,
                            modifier = Modifier
                                .padding(top = 35.dp)
                                .height(70.dp)
                                .fillMaxWidth(),
                        )
                    },
                    text = {
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Raggiungi Mattia Mandorlini",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            Text(
                                text = destination,
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { onProceedClick(destination, orderId) },
                            Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 10.dp),
                            shape = RoundedCornerShape(25)
                        ) {
                            Text(
                                text = "Procedi",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            )
                        }
                    },
                    containerColor = Color.White
                )

        }
    )
}



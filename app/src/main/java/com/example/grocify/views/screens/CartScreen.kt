package com.example.grocify.views.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.grocify.views.components.CartItems

import com.example.grocify.views.components.CheckoutBox
import com.example.grocify.views.components.MovingTextAndIconRow
import com.example.grocify.views.components.UserBottomNavigation

import com.example.grocify.model.Product
import com.example.grocify.utils.anyToDouble
import com.example.grocify.viewmodels.CartViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel(),
    onCatalogClick: () -> Unit,
    onGiftClick: () -> Unit,
    onPhysicalCartClick: () -> Unit,
    onCheckoutClick: (totalPrice: String) -> Unit,
    onTrackOrderClick: (orderId: String) -> Unit,
) {
    val onlineUiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.initializeProductsList("online")
        viewModel.checkOrdersStatus()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = { Text(
                    text = "Il tuo carrello",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight(500),
                        color = Color.Black,
                    ),
                ) },
            )
        },
        bottomBar = {
            UserBottomNavigation(
                ref = "virtualCart",
                onCatalogClick = onCatalogClick,
                onGiftClick = onGiftClick,
                onVirtualCartClick = { },
                onPhysicalCartClick = onPhysicalCartClick,
            )
        },
        content = { innerPadding ->
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                if(onlineUiState.orderId != ""){
                    MovingTextAndIconRow(onlineUiState.orderId, onTrackOrderClick)
                }

                if (onlineUiState.productsList.isEmpty()) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nessun prodotto nel carrello",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }else {
                    Column {
                        Text(
                            text = "Prodotti aggiunti al carrello",
                            modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )

                        Divider(
                            color = Color.LightGray,
                            thickness = 0.6.dp,
                            modifier = Modifier.padding(
                                start = 20.dp,
                                top = 10.dp,
                                end = 20.dp,
                                bottom = 15.dp
                            ),
                        )


                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            if (onlineUiState.productsList != emptyList<Product>()) {
                                items(onlineUiState.productsList.size) { index ->
                                    val product = onlineUiState.productsList[index]
                                    product.let {
                                        CartItems(
                                            product = it,
                                            viewModel = viewModel,
                                            flagCart = "online"
                                        )
                                    }
                                }
                            }
                        }
                        if (onlineUiState.productsList != emptyList<Product>()) {
                            val shipping = "1.50"
                            if (anyToDouble(onlineUiState.totalPrice)!! > 1.5) {
                                CheckoutBox(
                                    "Riepilogo ordine",
                                    (String.format(
                                        "%.2f",
                                        (onlineUiState.totalPrice - shipping.toDouble())
                                    )).replace(',', '.') + "€",
                                    shipping + "€",
                                    (String.format("%.2f", onlineUiState.totalPrice)).replace(
                                        ',',
                                        '.'
                                    ) + "€",
                                    "Checkout",
                                    onCheckoutClick = { onCheckoutClick(onlineUiState.totalPrice.toString()) },
                                )

                            }
                        }
                    }
                }
            }
        }
    )
}

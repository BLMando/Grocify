package com.example.grocify.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.grocify.R

import com.example.grocify.components.CartItems

import com.example.grocify.components.CheckoutBox
import com.example.grocify.components.UserBottomNavigation

import com.example.grocify.components.anyToDouble
import com.example.grocify.viewmodels.CartViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel(),
    onCatalogClick: () -> Unit,
    onGiftClick: () -> Unit,
    onVirtualCartClick: () -> Unit,
    onCheckoutClick: () -> Unit,
) {


    val scanUiState by viewModel.scanUiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.initializeProductsList("online")
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
                onVirtualCartClick = onVirtualCartClick,
                onPhysicalCartClick = {}
            )
        },
        content = { innerPadding ->
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
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
                        modifier = Modifier.padding(start= 20.dp,top = 10.dp,end = 20.dp,bottom = 15.dp),
                    )


                    LazyColumn (
                        modifier = Modifier.weight(1f)
                    ){
                        val productsList = viewModel.getProductsList()
                        //NON rimuovere il controllo che la lista non sia vuota altrimenti l'app non mostra la lista aggiornata
                        if(productsList != null){
                            items(productsList.size) { index ->
                                val product = productsList[index]
                                product?.let {
                                    CartItems(
                                        id = it.id,
                                        name = it.name,
                                        price = it.price,
                                        quantity = it.quantity,
                                        image = it.image,
                                        units = it.units,
                                        viewModel = viewModel,
                                        flagCart = "online"
                                    )
                                }
                            }
                        }
                    }

                    //NON rimuovere il controllo che la lista non sia vuota altrimenti l'app non mostra la lista aggiornata
                    if (scanUiState.productsList!= null){
                        val totalPrice = viewModel.getTotalPrice()
                        val shipping = "1.50"
                        if(anyToDouble(totalPrice)!! > 1.5){
                            CheckoutBox(
                                "Riepilogo ordine",
                                (String.format("%.2f",(anyToDouble(totalPrice)!! - anyToDouble(shipping)!!)) + "€").replace(',', '.'),
                                shipping + "€",
                                totalPrice,
                                "Checkout",
                                onCheckoutClick = onCheckoutClick,
                            )

                        }
                    }

                }
            }
        }
    )
}

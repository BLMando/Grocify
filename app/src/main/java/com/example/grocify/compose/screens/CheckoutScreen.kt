package com.example.grocify.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.components.CheckoutBox
import com.example.grocify.ui.theme.BlueLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = { Text(
                    text = "Opzioni di pagamento",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight(500),
                        color = Color.Black,
                    ),
                ) },
                navigationIcon = {
                    IconButton(
                        onClick = { /*TODO*/ }
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "arrow back")
                    }
                }
            )
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
                            )
                            Text(
                                text = "Scansiona",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = Color.Black
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
                            Icon(
                                Icons.Filled.ShoppingCart,
                                contentDescription = "Localized description",
                                tint = BlueLight
                            )
                            Text(
                                text = "Carrello",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = BlueLight
                                )
                            )
                        }
                    }
                },
            )
        },
        content = { innerPadding ->
            Column (
                modifier = Modifier.fillMaxHeight().padding(innerPadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Informazioni",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier.padding(start=15.dp,top=15.dp, bottom = 10.dp)
                    )
                    PaymentOptionsCard()
                    DeliveryOptionCard()
                }
                CheckoutBox(
                    "Riepilogo ordine",
                    "$5.00",
                    "$1.50",
                    "$6.50",
                    "Conferma"
                )
            }

        }
    )
}

@Composable
fun DeliveryOptionCard() {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(top = 10.dp, start =  10.dp,end=10.dp,bottom = 20.dp)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Informazioni di spedizione",
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "arrow forward",
                        Modifier.size(15.dp)
                    )
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Indirizzo di spezione"
                )
                Text(
                    text = "123 Main St."
                )
            }
        }
    }
}

@Composable
fun PaymentOptionsCard() {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(10.dp)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column(
            Modifier.fillMaxWidth(),

        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pagamento",
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "arrow forward",
                        Modifier.size(15.dp)
                    )
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(end = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically){
                    IconButton(
                        onClick = { /*TODO*/ }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CreditCard,
                            contentDescription = "credit card",
                            tint = BlueLight,
                        )
                    }
                    Text(
                        text = "**** **** **** 1234"
                    )
                }

                Text(
                    text = "01/24"
                )
            }
        }
    }
}

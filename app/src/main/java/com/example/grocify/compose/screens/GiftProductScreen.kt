package com.example.grocify.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CardGiftcard
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.R
import com.example.grocify.compose.components.UserBottomNavigation
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftProductScreen(
    onCatalogClick: () -> Unit,
    onPhysicalCartClick: () -> Unit,
    onVirtualCartClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = { Text(
                        text = "Per te",
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
                ref = "gift",
                onCatalogClick = onCatalogClick,
                onGiftClick = {  },
                onPhysicalCartClick = onPhysicalCartClick,
                onVirtualCartClick = onVirtualCartClick
            )
        },
        content = { innerPadding ->
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ){
                GiftInfoCard()
                Text(
                    text = "Premi omaggio",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp
                    ),
                    modifier = Modifier.padding(start = 15.dp,top = 10.dp,bottom = 20.dp)
                )
                repeat(3){
                    GiftProduct(true)
                }
            }
        }
    )
}

@Composable
fun GiftInfoCard() {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = BlueDark
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 20.dp)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column(
            Modifier.padding(15.dp)
        ){
            Text(
                text = "Valida dal 01/04/2024 al 30/04/2024",
                Modifier.padding(bottom = 30.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight(600)
                )
            )
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                        )
                    ) {
                        append("Hai speso: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight(500),
                            color = Color.White,
                        )
                    ) {
                        append("$105,00")
                    }
                }
            )
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Slider(
                    value = 100.0F,
                    onValueChange = {  },
                    colors = SliderDefaults.colors(
                        thumbColor = BlueLight,
                        activeTrackColor = BlueLight,
                        inactiveTrackColor = Color.White,
                    ),
                    valueRange = 0f..200f
                )
                Row(
                    Modifier
                        .fillMaxWidth(0.81f)
                        .padding(bottom = 25.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ){
                    Text(
                        text = "$50",
                        style = TextStyle(
                            fontSize = 17.sp,
                            color = Color.White,
                            fontWeight = FontWeight(600)
                        )
                    )
                    Text(
                        text = "$100",
                        style = TextStyle(
                            fontSize = 17.sp,
                            color = Color.White,
                            fontWeight = FontWeight(600)
                        )
                    )
                    Text(
                        text = "$200",
                        Modifier.padding(start = 70.dp),
                        style = TextStyle(
                            fontSize = 17.sp,
                            color = Color.White,
                            fontWeight = FontWeight(600)
                        )
                    )
                }
                Text(
                    text = "Ti rimangono 22 giorni",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight(500),
                    )
                )
            }

        }
    }
}

@Composable
fun GiftProduct(available:Boolean) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 10.dp)
                .height(70.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Image(
                    painter = painterResource(id = R.drawable.food),
                    contentDescription = "food",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(65.dp)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(15.dp))
                )
                Column (
                    Modifier.padding(start = 10.dp)
                ) {

                    Text(
                        text = "Apples 100g",
                        Modifier.padding(bottom = 5.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Light,
                            fontSize = 16.sp
                        )
                    )

                    Text(
                        text = "Ricompensa sbloccata",
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    )
                }

            }
            if(available) {
                IconButton(
                    onClick = { /*TODO*/ },
                    Modifier.border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(15.dp)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddShoppingCart,
                        contentDescription = "cart icon"
                    )
                }
            }
        }
    }
}



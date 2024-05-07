package com.example.grocify

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.ui.theme.BlueLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItemsScreen(){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(text= "Frutta e verdura",
                        style = TextStyle(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                    ) },
                navigationIcon = {
                    IconButton(
                        onClick = { /*TODO*/ }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Arrow back"
                        )
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
                                contentDescription = "Localized description",
                                tint = BlueLight
                            )
                            Text(
                                text = "Catalogo",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = BlueLight,
                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = "Localized description"
                            )
                            Text(
                                text = "Scansiona",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = Color.Black,
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
                                    color = Color.Black,
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
                                    color = Color.Black,
                                )
                            )
                        }
                    }
                },
            )
        },
        content = { innerPadding ->
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding),
                    columns = GridCells.Fixed(2),
                ){
                    items(6){
                        CategoryItemCard()
                    }
                }
        }
    )
}

@Composable
fun CategoryItemCard() {
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){
            Image(
                painter = painterResource(id = R.drawable.food),
                contentDescription = "food image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                    .width(170.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(30.dp))
            )
            Column (
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ){
                Text(
                    text = "Apples",
                    style = TextStyle(
                        fontSize = 15.sp,
                        color = Color(0xFF3f4145)
                    ),
                    modifier = Modifier.padding(start = 18.dp, top = 10.dp)
                )
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                            ),
                        ) {
                            append("$0.99 ")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = 13.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("/100g")
                        }
                    },
                    modifier = Modifier.padding(start = 18.dp)
                )
            }


            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = "Aggiungi")
            }

        }
    }
}

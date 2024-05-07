package com.example.grocify

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight
import com.example.grocify.ui.theme.ExtraLightGray


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleGiftScreen(isSaleContent:Boolean){

    val title = if (isSaleContent) "Applicazione sconti" else "Gestione omaggi"
    val description = if (isSaleContent) "Scegli i prodotti e applica lo sconto" else "Scegli i prodotti omaggi del mese"
    val buttonText = if (isSaleContent) "Applica sconto" else "Conferma prodotti omaggio"

    var name by rememberSaveable { mutableStateOf("") }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                       text = title,
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                    ) },
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
                                Icons.Filled.BarChart,
                                contentDescription = "Localized description",
                            )
                            Text(
                                text = "Statistiche",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = Color.Black,
                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Icon(
                                Icons.Filled.AttachMoney,
                                contentDescription = "Localized description",
                                tint = if(isSaleContent) BlueLight else Color.Black
                            )
                            Text(
                                text = "Sconti",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = if(isSaleContent) BlueLight else Color.Black
                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Icon(
                                Icons.Filled.CardGiftcard,
                                contentDescription = "Localized description",
                                tint = if(!isSaleContent) BlueLight else Color.Black

                            )
                            Text(
                                text = "Omaggi",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = if(!isSaleContent) BlueLight else Color.Black
                                )
                            )
                        }
                    }
                },
            )
        },
        content = { innerPadding ->
            Column(
                Modifier.fillMaxHeight().padding(innerPadding),
            ) {
                Text(
                    text = description,
                    modifier = Modifier.padding(20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                if(isSaleContent) SaleContent() else GiftContent()
                Button(
                    onClick = { /*TODO*/ },
                    Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    shape = RoundedCornerShape(10)
                ) {
                    Text(text = buttonText)
                }
            }

        }
    )
}

@Composable
fun SaleContent() {
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val heightPx = displayMetrics.heightPixels.dp
    var sale by rememberSaveable { mutableStateOf("") }

    Column(
        Modifier.fillMaxHeight(0.9f),
        verticalArrangement = Arrangement.SpaceBetween
    ){
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.height(430.dp)
        ){
            items(20){
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
                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Image(
                            painterResource(R.drawable.food),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp))
                        )

                        Text(
                            "Some Text In the middle",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 5.dp,bottom =  5.dp)
                        )
                    }
                }
            }
        }

        Card(
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
        ){
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "Sconto",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(end = 20.dp)
                )
                TextField(
                    value = sale,
                    onValueChange = {sale = it},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.height(20.dp),
                    colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = ExtraLightGray,
                            unfocusedIndicatorColor = BlueDark
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Percent,
                            contentDescription = "icon"
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun GiftContent() {
    Column(
        horizontalAlignment = Alignment.End
    ){
        Text(
            text = "Selezionati 2/3",
            Modifier.padding(end = 18.dp),
            style = TextStyle(
                fontSize = 15.sp
            )
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.height(500.dp)
        ) {
            items(20){
                Card (
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .padding(10.dp)
                        .shadow(5.dp, shape = RoundedCornerShape(20.dp),
                            ambientColor = Color.Black,
                        )
                        .border(2.dp, BlueDark,RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Image(
                            painterResource(R.drawable.food),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp))
                        )

                        Text(
                            "Some Text In the middle",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 5.dp,bottom =  5.dp)
                        )
                    }
                }
            }
        }
    }
}

package com.example.grocify.compose.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.R
import com.example.grocify.ui.theme.BlueLight
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.common.ChartDataCollection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAdminScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight(300),
                                    color = Color.Black,
                                ),
                            ) {
                                append("Ciao ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                )
                            ) {
                                append("Admin!")
                            }
                        }
                    ) },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.icon),
                        contentDescription = "app logo",
                        modifier = Modifier.padding(start = 20.dp,end=10.dp)
                    )
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
                                Icons.Filled.BarChart,
                                contentDescription = "Localized description",
                                tint = BlueLight
                            )
                            Text(
                                text = "Statistiche",
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
                                Icons.Filled.AttachMoney,
                                contentDescription = "Localized description"
                            )
                            Text(
                                text = "Sconti",
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
                                text = "Omaggi",
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
            Column(
                Modifier.padding(innerPadding)
            ){
                Text(
                    text ="Dashboard",
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )

                LazyColumn {
                    items(4){
                        CardChart("I prodotti più venduti")
                    }
                }
            }

        }
    )
}

@Composable
fun CardChart(title:String) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 20.dp)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column (
            Modifier.padding(15.dp)
        ){
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 15.sp,

                    )
            )

            BarChart(
                dataCollection =  ChartDataCollection(
                    listOf(
                        BarData(10f, "Category A", color = Color(0xffed625d)),
                        BarData(20f, "Category B", color = Color(0xffed125d)),
                        BarData(50f, "Category C", color = Color(0xffed225d)),
                        BarData(40f, "Category D", color = Color(0xffed325d)),
                        BarData(23f, "Category E", color = Color(0xffed425d)),
                        BarData(35F, "Category F", color = Color(0xffed525d)),
                        BarData(20f, "Category K", color = Color(0xffed615d)),
                        BarData(50f, "Category L", color = Color(0xffed625d))
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

        }
    }
}
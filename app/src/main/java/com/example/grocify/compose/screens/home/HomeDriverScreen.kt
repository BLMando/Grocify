package com.example.grocify.compose.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.ui.theme.BlueDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDriverScreen() {
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        text = "Ordini del 4 Marzo",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight(500),
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                    )
                }
            )
        },
        content = { innerPadding ->
            LazyColumn (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = innerPadding
            ) {
                items(5){
                    Spacer(modifier = Modifier.size(20.dp))
                    OrderItem()
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    )
}

@Composable
fun OrderItem(){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .width(IntrinsicSize.Min)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .width(IntrinsicSize.Min)
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#12345",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Ora: 17:30",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Destinazione:",
                    fontSize = 15.sp,
                )
                Text(
                    text = "Porto San Giorio, Via Cavour",
                    fontSize = 15.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Numero di articoli:",
                    fontSize = 15.sp
                )
                Text(
                    text = "12",
                    fontSize = 15.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Totale:",
                    fontSize = 15.sp
                )
                Text(
                    text = "50.00$",
                    fontSize = 15.sp
                )
            }

            Button(
                onClick = { /*TODO*/ },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = BlueDark
                ),
                modifier = Modifier
                    .width(325.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Visibility,
                    contentDescription = "eye icon",
                    modifier = Modifier.size(20.dp),
                    tint =  Color.White,
                )
                Text(
                    "Vedi spesa",
                    color = Color.White,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}
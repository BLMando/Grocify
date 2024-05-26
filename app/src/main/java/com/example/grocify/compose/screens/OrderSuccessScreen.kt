package com.example.grocify.compose.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.R

@Composable
fun OrderSuccessScreen(
    flagCart: String,
    onHomeClick: () -> Unit,
    onTrackOrder: () -> Unit,
){
    Column (
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ){

        Text(
            text = "Perfetto!",
            style = TextStyle(
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        )
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "app icon",
                modifier = Modifier.size(160.dp).padding(bottom = 20.dp)
            )
            Text(
                text = "Il tuo ordine è stato effettuato con successo!",
                style = TextStyle(
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Row (
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Button(
                onClick = onHomeClick,
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 15.dp,
                    bottom = 15.dp
                )
            ) {
                Text(
                    text = "Ritorna al catalogo",

                    )
            }
            if(flagCart == "online")
                OutlinedButton(
                    onClick = onTrackOrder,
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 15.dp,
                        bottom = 15.dp
                    ),
                    border = BorderStroke(
                        1.dp,
                        Color.Black
                    )
                ) {
                    Text(
                        text = "Monitora l'ordine",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    )
                }
        }
    }
}
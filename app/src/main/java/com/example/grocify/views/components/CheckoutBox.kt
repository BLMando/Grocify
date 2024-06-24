package com.example.grocify.views.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.views.theme.LightGray

@Composable
fun CheckoutBox(
    title: String?,
    subtotal: String?,
    shipping: String?,
    total: String,
    buttonText: String,
    buttonEnabled: Boolean = true,
    onCheckoutClick: () -> Unit,
) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = LightGray
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(10.dp)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column (
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(title != null){
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp, top = 10.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title
                    )
                }
            }

            if(subtotal != null){
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp, top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Totale parziale"
                    )
                    Text(
                        text = subtotal
                    )
                }
            }

            if (shipping != null) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text ="Spedizione"
                    )
                    Text(
                        text = shipping
                    )
                }
            }
            Row (
                Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Totale",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = total,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = { onCheckoutClick() },
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(bottom = 10.dp),
                enabled = buttonEnabled,
            ) {
                Text(text = buttonText)
            }
        }
    }
}
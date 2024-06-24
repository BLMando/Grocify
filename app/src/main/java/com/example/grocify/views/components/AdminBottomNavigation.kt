package com.example.grocify.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.grocify.views.theme.BlueLight
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBottomNavigation(
    ref: String = "",
    onStatsClick: () -> Unit,
    onSaleClick: (flagPage: Boolean) -> Unit,
    onGiftClick: (flagPage: Boolean) -> Unit,
    onUsersClick: () -> Unit,
){
    var statsColor  = Color.Black
    var saleColor   = Color.Black
    var giftColor   = Color.Black
    var personColor = Color.Black

    when(ref){
        "stats"  -> statsColor = BlueLight
        "sale"  -> saleColor = BlueLight
        "gift"  -> giftColor = BlueLight
        "users" -> personColor = BlueLight
    }

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
                    modifier = Modifier.clickable {
                        onStatsClick()
                    }
                ){
                    Icon(
                        Icons.Filled.BarChart,
                        contentDescription = "StatsIcon",
                        tint = statsColor
                    )
                    Text(
                        text = "Statistiche",
                        Modifier.padding(top = 7.dp),
                        style = TextStyle(
                            color = statsColor,
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onSaleClick(true)
                    }
                ){
                    Icon(
                        Icons.Filled.AttachMoney,
                        contentDescription = "SaleIcon",
                        tint = saleColor
                    )
                    Text(
                        text = "Sconti",
                        Modifier.padding(top = 7.dp),
                        style = TextStyle(
                            color = saleColor,
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onGiftClick(false)
                    }
                ){
                    Icon(
                        Icons.Filled.CardGiftcard,
                        contentDescription = "GiftIcon",
                        tint = giftColor
                    )
                    Text(
                        text = "Omaggi",
                        Modifier.padding(top = 7.dp),
                        style = TextStyle(
                            color = giftColor,
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onUsersClick()
                    }
                ){
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "PersonIcon",
                        tint = personColor
                    )
                    Text(
                        text = "Utenti",
                        Modifier.padding(top = 7.dp),
                        style = TextStyle(
                            color = personColor,
                        )
                    )
                }
            }
        }
    )
}

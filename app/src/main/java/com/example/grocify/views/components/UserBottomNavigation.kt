package com.example.grocify.views.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
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
import androidx.compose.ui.unit.sp
import com.example.grocify.ui.theme.BlueLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserBottomNavigation(
    ref: String = "",
    onCatalogClick: () -> Unit,
    onGiftClick: () -> Unit,
    onPhysicalCartClick: () -> Unit,
    onVirtualCartClick: () -> Unit
){

    var virtualCartColor= Color.Black
    var physicalCartColor = Color.Black
    var giftColor = Color.Black
    var catalogColor = Color.Black

    when(ref){
        "virtualCart" -> virtualCartColor = BlueLight
        "physicalCart" -> physicalCartColor = BlueLight
        "gift" -> giftColor = BlueLight
        "catalog" -> catalogColor = BlueLight
    }

    BottomAppBar(
        windowInsets = TopAppBarDefaults.windowInsets,
        modifier = Modifier
            .shadow(10.dp, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
        tonalElevation = 30.dp,
        containerColor = Color.White,
        actions = {
            Row (
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onCatalogClick()
                    }
                ){
                    Icon(
                        Icons.Filled.ShoppingBag,
                        contentDescription = "Localized description",
                        tint = catalogColor
                    )
                    Text(
                        text = "Catalogo",
                        Modifier.padding(top = 3.5.dp),
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = catalogColor,
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onVirtualCartClick()
                    }
                ){
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = "Localized description",
                        tint = virtualCartColor
                    )
                    Text(
                        text = "Online",
                        Modifier.padding(top = 3.5.dp),
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = virtualCartColor,
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onPhysicalCartClick()
                    }
                ){
                    Icon(
                        Icons.Filled.Store,
                        contentDescription = "Localized description",
                        tint = physicalCartColor
                    )
                    Text(
                        text = "Negozio",
                        Modifier.padding(top = 3.5.dp),
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = physicalCartColor,
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onGiftClick()
                    }
                ){
                    Icon(
                        Icons.Filled.CardGiftcard,
                        contentDescription = "Localized description",
                        tint = giftColor
                    )
                    Text(
                        text = "Per te",
                        Modifier.padding(top = 3.5.dp),
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = giftColor,
                        )
                    )
                }
            }
        },
    )
}
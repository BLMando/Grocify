package com.example.grocify.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberImagePainter
import com.example.grocify.R
import com.example.grocify.ui.theme.BlueLight
import com.example.grocify.ui.theme.BlueMedium
import com.example.grocify.ui.theme.LightGray
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.grocify.viewmodels.CartViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ListItems(image: String?, name: String?, quantity: Any?, content: @Composable() () -> Unit ){

    val painter = rememberImagePainter(
        data = image,
        builder = {
            // You can customize image loading parameters here if needed
        }
    )
    Row (
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 10.dp)
            .height(100.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start =10.dp)
        ){
            Image(
                painter = painter,
                contentDescription = "food",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(85.dp)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(15.dp))
            )
            Column (Modifier.padding(start = 10.dp)) {
                Text(
                    text = name.toString(),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(bottom = 5.dp)
                )


                Text(
                    text = "Quantità: $quantity",
                    style = TextStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = 18.sp
                    )
                )
            }
        }

        content()
    }
}

fun getResourceId(name: String): Int {
    return try {
        R.drawable::class.java.getField(name).getInt(null)
    } catch (e: Exception) {
        Log.e("DrawableCatch","Errore nell'ottenere l'ID del drawable")
    }
}

fun anyToInt(value: Any?): Int? {
    return when (value) {
        is Int -> value
        is String -> value.toIntOrNull()
        is Number -> value.toInt()
        else -> null
    }
}

fun anyToDouble(value: Any?): Double? {
    return when (value) {
        is Double -> value
        is String -> value.toDoubleOrNull()
        is Number -> value.toDouble()
        else -> null
    }
}

fun checkName(name: String?): String{
    if(name.toString().count() > 14){
        return name!!.substring(0, 14) + "..."
    }
    else{
        return name.toString()
    }
}

@Composable
fun ItemsQuantitySelector(units: Any?, id: String?, price: Any?, viewModel: CartViewModel, flagCart: String){
    var state = units.toString()
    var isUpdating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
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
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            IconButton(
                onClick = {
                            if (!isUpdating && anyToInt(state)!! > 1) {
                                isUpdating = true
                                scope.launch {
                                    viewModel.addValueToProductUnits(id, price, -1, flagCart)
                                    state = viewModel.getUnitsById(id)
                                    delay(250)  // Debounce delay
                                    isUpdating = false
                                }
                            }
                        },
                Modifier
                    .padding(5.dp)
                    .size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "decrease",
                )
            }
            Text(
                text = state.toString(),
                style = TextStyle(
                    fontSize = 15.sp
                ),
                modifier = Modifier.padding(5.dp)
            )
            IconButton(
                onClick = {
                            if (!isUpdating) {
                                isUpdating = true
                                scope.launch {
                                    viewModel.addValueToProductUnits(id, price, 1, flagCart)
                                    state = viewModel.getUnitsById(id)
                                    delay(250)  // Debounce delay
                                    isUpdating = false
                                }
                            }

                          },
                Modifier
                    .padding(5.dp)
                    .size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "increase",
                )
            }
        }
    }
}

@Composable
fun CartItems(
    id: String?,
    name: String?,
    price: Any?,
    quantity: String?,
    image: String?,
    units: Any?,
    viewModel: CartViewModel,
    flagCart: String
) {
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
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .size(65.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                SubcomposeAsyncImage(
                    model = image,
                    contentDescription = name.toString(),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        //.padding()
                        .width(170.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = BlueMedium,
                            strokeCap = StrokeCap.Round
                        )
                    } else {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.padding(start = 10.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = checkName(name.toString()),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Text(
                            //text = quantity.toString() + "  " + (String.format("%.2f", anyToDouble(price)) + "€").replace(',', '.'),
                            text = (String.format("%.2f", anyToDouble(price)) + "€").replace(',', '.'),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Light,

                            )
                    }
                    Row(
                        modifier = Modifier.padding(end = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Absolute.Right
                    ) {

                        ItemsQuantitySelector(units, id, price, viewModel, flagCart)
                        IconButton(
                            onClick = { viewModel.removeFromCart(id, price, flagCart) },
                            Modifier
                                .padding(5.dp)
                                .size(18.dp)
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Localized description",
                                tint = Color.Red,
                            )
                        }
                    }
                }
            }
        }
    }


@Composable
fun CheckoutBox(
    title: String?,
    subtotal:String?,
    shipping:String?,
    total:String,
    buttonText: String,
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
                    .padding(bottom = 10.dp)
            ) {
                Text(text = buttonText)
            }
        }
    }
}

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
                        onPhysicalCartClick()
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
                        onVirtualCartClick()
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

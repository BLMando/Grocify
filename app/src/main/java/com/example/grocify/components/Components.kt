package com.example.grocify.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberImagePainter
import com.example.grocify.R
import com.example.grocify.ui.theme.LightGray


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

@Composable
fun Dialog(
    title:String,
    state:Boolean,
    icon:ImageVector,
    buttonText:String,
    buttonIcon:ImageVector,
    content: @Composable() () -> Unit
) {

    val dialogState = rememberSaveable { mutableStateOf(state) }

    if (dialogState.value) {
        AlertDialog(
            onDismissRequest = { dialogState.value = false },
            title = {
                Text(
                    text = title,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            },
            icon = {
                Icon(imageVector = icon, contentDescription ="dialog icon" )
            },
            text = {
               content()
            },
            confirmButton = {
                Button(
                    onClick = { dialogState.value = false },
                    Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = buttonText,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                    Icon(
                        imageVector = buttonIcon,
                        contentDescription = "button icon",
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            },
            properties = DialogProperties(
                dismissOnClickOutside = false
            )
        )
    }
}

@Composable
fun ItemsQuantitySelector(units: Int){
    val state: MutableState<Int> = remember { mutableStateOf(units) }
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
                onClick = { state.value -= 1 },
                Modifier.padding(5.dp).size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "decrease",
                )
            }
            Text(
                text = state.value.toString(),
                style = TextStyle(
                    fontSize = 15.sp
                ),
                modifier = Modifier.padding(5.dp)
            )
            IconButton(
                onClick = { state.value += 1 },
                Modifier.padding(5.dp).size(18.dp)
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
fun CheckoutBox(title: String,subtotal:String,shipping:String,total:String,buttonText: String) {
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
            Row (
                Modifier.fillMaxWidth().padding(start = 15.dp,end = 15.dp,top = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = title
                )
            }
            Row (
                Modifier.fillMaxWidth().padding(start = 15.dp,end = 15.dp,top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Totale parziale"
                )
                Text(
                    text = subtotal
                )
            }
            Row (
                Modifier.fillMaxWidth().padding(start = 15.dp,end = 15.dp,bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Spedizione"
                )
                Text(
                    text = shipping
                )
            }
            Row (
                Modifier.fillMaxWidth().padding(start = 15.dp,end = 15.dp,top = 10.dp,bottom = 10.dp),
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
                onClick = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth(0.95f).padding(bottom = 10.dp)
            ) {
                Text(text = buttonText)
            }
        }
    }
}


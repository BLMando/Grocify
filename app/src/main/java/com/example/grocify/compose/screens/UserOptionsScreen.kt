package com.example.grocify.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DepartureBoard
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserOptionsScreen(
    topBarText:String,
    titleFirst:String,
    titleSecond:String
){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = { Text(
                    text = topBarText,
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight(500),
                        color = Color.Black,
                    ),
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

        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = BlueDark,
            )
            {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "fab icon",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                )
            } },
        content = { innerPadding ->
            Column (
                Modifier.padding(innerPadding)
            ) {
                Text(
                    text = titleFirst,
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp
                    )
                )
                OrderSection(titleSecond = titleSecond)
                InputDialog("Dicci cosa pensi","Invia")
            }
        }
    )
}

@Composable
fun AddressSection(titleSecond: String){
    Column {
        AddressCard(true)

        Text(
            text = titleSecond,
            modifier = Modifier.padding(top = 20.dp, start = 20.dp),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 23.sp
            )
        )
        LazyColumn {
            items(4) {
                AddressCard(false)
            }
        }
    }
}
@Composable
fun AddressCard(selected:Boolean){

    val spotColor = if (selected) BlueDark else Color.Black
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 15.dp)
            .shadow(
                5.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black,
                spotColor = spotColor
            )
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Indirizzo di casa",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(start = 15.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopEnd)
                ) {
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreHoriz,
                            contentDescription = "More"
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        properties = PopupProperties(
                            dismissOnClickOutside = true,
                        ),
                        modifier = Modifier.background(Color.White)
                    ) {
                        if(!selected){
                            DropdownMenuItem(
                                text = { Text("Seleziona") },
                                onClick = { /*TODO*/ }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Modifica") },
                            onClick = { /*TODO*/ }
                        )
                        DropdownMenuItem(
                            text = { Text("Elimina") },
                            onClick = { /*TODO*/ }
                        )
                    }
                }
            }
            Text(
                text = "Via di casa, 123",
                modifier = Modifier.padding(start = 15.dp,bottom = 10.dp)
            )
        }
    }
}

@Composable
fun PaymentMethodSection(titleSecond: String){
    Column {
       PaymentMethodCard(selected = true)

        Text(
            text = titleSecond,
            modifier = Modifier.padding(top = 20.dp, start = 20.dp),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 23.sp
            )
        )
        LazyColumn {
            items(4) {
                PaymentMethodCard(selected = false)
            }
        }
    }
}
@Composable
fun PaymentMethodCard(selected:Boolean){

    val spotColor = if (selected) BlueDark else Color.Black
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 15.dp)
            .shadow(
                5.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black,
                spotColor = spotColor
            )
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Mattia Mandorlini",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(start = 15.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopEnd)
                ) {
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreHoriz,
                            contentDescription = "More"
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        properties = PopupProperties(
                            dismissOnClickOutside = true,
                        ),
                        modifier = Modifier.background(Color.White)
                    ) {
                        if(!selected){
                            DropdownMenuItem(
                                text = { Text("Seleziona") },
                                onClick = { /*TODO*/ }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Modifica") },
                            onClick = { /*TODO*/ }
                        )
                        DropdownMenuItem(
                            text = { Text("Elimina") },
                            onClick = { /*TODO*/ }
                        )
                    }
                }
            }
            Row (
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "**** **** **** 1234",
                    modifier = Modifier.padding(start = 15.dp,bottom = 10.dp)
                )
                Text(
                    text = "01/24",
                    modifier = Modifier.padding(end = 20.dp,bottom = 10.dp)
                )
            }

        }
    }
}

@Composable
fun OrderSection(titleSecond: String){
    Column {

        OrderCard("Spedito",Icons.Filled.DepartureBoard,true)

        Text(
            text = titleSecond,
            modifier = Modifier.padding(top = 20.dp, start = 20.dp),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 23.sp
            )
        )
        LazyColumn {
            items(4) {
                OrderCard(state = "Consegnato", iconState = Icons.Filled.Done, actualOrder = false)
            }
        }
    }
}
@Composable
fun OrderCard(
    state:String,
    iconState:ImageVector,
    actualOrder:Boolean
){
    val optionButtonIcon = if(actualOrder) Icons.Filled.LocalShipping else Icons.Filled.RateReview
    val optionButtonText = if(actualOrder) "Traccia l'ordine" else "Recensisci"
    val spotColor = if (actualOrder) BlueDark else Color.Black

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 15.dp)
            .shadow(
                5.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black,
                spotColor = spotColor
            )
            .clip(RoundedCornerShape(20.dp))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Ordine #1234",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )

                Text(
                    text = "Data: 10/05/2024",
                    Modifier.padding(vertical = 5.dp)
                )

                Text(
                    text = "Totale: $50.00",
                )
            }

            Column(
                Modifier.padding(end = 10.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(
                            width = 0.3.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ){
                    Icon(
                        imageVector = iconState,
                        contentDescription ="state",
                        Modifier.padding(end = 5.dp),
                    )
                    Text(
                        state,
                        style = TextStyle(
                            fontSize = 14.sp,
                        )
                    )
                }

                Spacer(modifier = Modifier.size(15.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = BlueDark,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ){
                    Icon(
                        imageVector = optionButtonIcon,
                        contentDescription ="state",
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(
                        text = optionButtonText,
                        style = TextStyle(
                            fontSize = 14.sp,

                        )
                    )
                }
            }
        }
    }
}

@Composable
fun InputDialog(title:String,buttonText:String){

    val dialogState = rememberSaveable { mutableStateOf(true) }

    if (dialogState.value) {
        AlertDialog(
            onDismissRequest = { dialogState.value = false },
            title = {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = title,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )

                    IconButton(
                        onClick = { dialogState.value = false },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "close icon"
                        )
                    }
                }

            },
            text = {
                PaymentInputDialogContent()
            },
            confirmButton = {
                Button(
                    onClick = { dialogState.value = false },
                    Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical =15.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = buttonText,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun AddressInputDialogContent(){

    var addressName by rememberSaveable { mutableStateOf("") }
    var address by  rememberSaveable { mutableStateOf("") }

    Column{
        OutlinedTextField(
            value = addressName,
            onValueChange = {
                addressName = it
            },
            label = { Text(text = "Nome indirizzo",color = Color.Black) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color(0, 0, 0, 50)
            ),
            modifier = Modifier
                .width(325.dp)
                .padding(0.dp, 10.dp, 0.dp, 10.dp),
            textStyle = TextStyle(
                color = Color.Black
            )
        )

        OutlinedTextField(
            value = address,
            label = { Text(text = "Indirizzo",color = Color.Black) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = {
                address = it
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color(0, 0, 0, 50)
            ),
            modifier = Modifier
                .width(325.dp)
                .padding(0.dp, 10.dp, 0.dp, 10.dp),
            textStyle = TextStyle(
                color = Color.Black
            )
        )
    }
}

@Composable
fun PaymentInputDialogContent(){

    var owner by rememberSaveable { mutableStateOf("") }
    var cardNumber by  rememberSaveable { mutableStateOf("") }
    var expireDate by  rememberSaveable { mutableStateOf("") }
    var cvc by  rememberSaveable { mutableStateOf("") }

    Column{
        OutlinedTextField(
            value = owner,
            onValueChange = {
                owner = it
            },
            label = { Text(text = "Intestatario",color = Color.Black) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueLight,
                unfocusedBorderColor = Color(0, 0, 0, 50)
            ),
            modifier = Modifier

                .padding(0.dp, 10.dp, 0.dp, 10.dp),
            textStyle = TextStyle(
                color = Color.Black
            )
        )

        OutlinedTextField(
            value = cardNumber,
            label = { Text(text = "Numero carta",color = Color.Black) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = {
                cardNumber = it
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueLight,
                unfocusedBorderColor = Color(0, 0, 0, 50)
            ),
            modifier = Modifier

                .padding(0.dp, 10.dp, 0.dp, 10.dp),
            textStyle = TextStyle(
                color = Color.Black
            )
        )

        OutlinedTextField(
            value = expireDate,
            label = { Text(text = "Data di scadenza", color = Color.Black) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = {
                expireDate = it
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueLight,
                unfocusedBorderColor = Color(0, 0, 0, 50)
            ),
            modifier = Modifier
                .padding(0.dp, 10.dp, 0.dp, 10.dp),
            textStyle = TextStyle(
                color = Color.Black
            )
        )

        OutlinedTextField(
            value = cvc,
            label = { Text(text = "CVC", color = Color.Black) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = {
                cvc = it
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueLight,
                unfocusedBorderColor = Color(0, 0, 0, 50)
            ),
            modifier = Modifier
                .padding(0.dp, 10.dp, 0.dp, 10.dp),
            textStyle = TextStyle(
                color = Color.Black
            )
        )
    }
}

@Composable
fun OrderInputDialogContent(){

    var rating by rememberSaveable { mutableFloatStateOf(1f) } //default rating will be 1
    var text by rememberSaveable { mutableStateOf("") }

    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ){
        StarRatingBar(
            maxStars = 5,
            rating = rating,
            onRatingChanged = {
                rating = it
            }
        )

        TextField(
            value = text,
            onValueChange = {text = it},
            Modifier.padding(top = 20.dp),
            singleLine = false,
            placeholder = {Text(text = "Inserisci un commento...")}
        )
    }

}

@Composable
fun StarRatingBar(
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit
) {
    val density = LocalDensity.current.density
    val starSize = (16f * density).dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 1..maxStars) {

            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFFC700),
                modifier = Modifier
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            onRatingChanged(i.toFloat())
                        }
                    )
                    .width(starSize)
                    .height(starSize)
            )
        }
    }
}
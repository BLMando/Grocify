package com.example.grocify.compose.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.viewmodels.UserAddressesViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAddressScreen(
    viewModel: UserAddressesViewModel = viewModel(),
    onBackClick: () -> Unit
){

    val uiState = viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        text = "Indirizzi di spedizione",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight(500),
                            color = Color.Black,
                        ),
                    ) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
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
                onClick = { viewModel.setFABClicked(true) },
                containerColor = BlueDark,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "fab icon",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                )
            }
            AddressDialog(uiState.value.isFABClicked, viewModel)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                Text(
                    text = "Indirizzo corrente",
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )

                AddressCard(true)

                Text(
                    text = "Indizzi disponibili",
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                LazyColumn {
                    items(4) {
                        AddressCard(false)
                    }
                }
            }
        }
    )
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
fun AddressDialog(fabState: Boolean,viewModel: UserAddressesViewModel){

    if (fabState) {
        AlertDialog(
            onDismissRequest = { viewModel.setFABClicked(false) },
            title = {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Nuovo indirizzo",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )

                    IconButton(
                        onClick = { viewModel.setFABClicked(false) },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "close icon"
                        )
                    }
                }

            },
            text = { AddressInputDialogContent() },
            confirmButton = {
                Button(
                    onClick = { viewModel.setFABClicked(false) },
                    Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical =15.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Aggiungi",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
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
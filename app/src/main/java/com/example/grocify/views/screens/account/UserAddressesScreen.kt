package com.example.grocify.views.screens.account


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.grocify.views.theme.BlueDark
import com.example.grocify.viewmodels.UserAddressesViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocify.states.UserAddressesUiState
import com.example.grocify.model.Address
import com.example.grocify.views.theme.BlueLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAddressScreen(
    viewModel: UserAddressesViewModel = viewModel(),
    onBackClick: () -> Unit
){

    val uiState = viewModel.uiState.collectAsState()

    /**
     * Effect that reloads addresses after each operations
     */
    LaunchedEffect(key1 = Unit, key2 = uiState.value.isUDSuccessful, key3 = uiState.value.isInsertSuccessful) {
        viewModel.getAllAddresses()
    }

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
            AddressDialog(uiState.value, viewModel)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                if(uiState.value.result.isEmpty() && uiState.value.addresses.isNotEmpty()) {
                    Text(
                        text = "Indirizzo corrente",
                        modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )

                    val addressListWithSelected = uiState.value.addresses.filter { it.selected }

                    if (addressListWithSelected.isEmpty())
                        Row(
                            Modifier.fillMaxWidth().padding(top = 20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nessun indirizzo attualmente in uso",
                                style = TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    else
                        AddressCard(addressListWithSelected.first(), viewModel)


                    Text(
                        text = "Indizzi disponibili",
                        modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )

                    val addressListWithoutSelected: List<Address> = if(addressListWithSelected.isEmpty())
                        uiState.value.addresses.filter { !it.selected }
                    else
                        uiState.value.addresses.minus(
                            addressListWithSelected.first()
                        )

                    if(addressListWithoutSelected.isEmpty())
                        Row(
                            Modifier.fillMaxWidth().padding(top = 20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nessun altro indirizzo disponibile",
                                style = TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    else
                        LazyColumn {
                            items(addressListWithoutSelected.size) {
                                AddressCard(addressListWithoutSelected[it],viewModel)
                            }
                        }
                }else{
                    Row(
                        Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.value.result,
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    )
}
@Composable
fun AddressCard(address: Address,viewModel: UserAddressesViewModel){

    val spotColor = if (address.selected) BlueDark else Color.Black
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
                    "${address.name}, ${address.city}",
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
                        if(!address.selected){
                            DropdownMenuItem(
                                text = { Text("Seleziona") },
                                onClick = {
                                    viewModel.setAddressSelected(address)
                                    expanded = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Modifica") },
                            onClick = {
                                viewModel.setFABClicked(true)
                                viewModel.updateAddress(address,false)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Elimina") },
                            onClick = {
                                viewModel.deleteAddress(address)
                                expanded = false
                            }
                        )
                    }
                }
            }
            Text(
                text = "${address.address}, ${address.civic}",
                modifier = Modifier.padding(start = 15.dp,bottom = 10.dp)
            )
        }
    }
}


@Composable
fun AddressDialog(uiState: UserAddressesUiState, viewModel: UserAddressesViewModel){

    // variabiles handling insert and update operations
    var addressName by rememberSaveable { mutableStateOf("")}
    var city by rememberSaveable { mutableStateOf("")}
    var address by  rememberSaveable { mutableStateOf("") }
    var civic by  rememberSaveable { mutableStateOf("") }

    var addressNameChange by rememberSaveable { mutableStateOf(false) }
    var cityChange by rememberSaveable { mutableStateOf(false)}
    var addressChange by rememberSaveable { mutableStateOf(false) }
    var civicChange by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = uiState.isInsertSuccessful) {
        if(uiState.isInsertSuccessful){
            viewModel.setFABClicked(false)
            addressName = ""
            addressNameChange = false
            city = ""
            cityChange = false
            address = ""
            addressChange = false
            civic = ""
            civicChange = false
        }
    }

    if (uiState.isFABClicked) {
        AlertDialog(
            onDismissRequest = { viewModel.setFABClicked(false) },
            title = {
                Text(
                    text = "Nuovo indirizzo",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                )
            },
            text = {
                Column{
                    OutlinedTextField(
                    value = if(uiState.addressToUpdate != null && !addressNameChange) uiState.addressToUpdate.name else addressName,
                        singleLine = true,
                        onValueChange = {
                            addressName = it
                            addressNameChange = true
                        },
                        label = { Text(text = "Nome indirizzo",color = Color.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueLight,
                            unfocusedBorderColor = Color(0, 0, 0, 50)
                        ),
                        textStyle = TextStyle(
                            color = Color.Black
                        ),
                        isError = !uiState.isAddressNameValid,
                        supportingText = {
                            if (!uiState.isAddressNameValid)
                                Text(
                                    text = uiState.addressNameError,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Red,
                                    textAlign = TextAlign.Start
                                )
                        },
                        trailingIcon = {
                            if (!uiState.isAddressNameValid)
                                Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                        }
                    )

                    OutlinedTextField(
                        value = if(uiState.addressToUpdate != null && !cityChange) uiState.addressToUpdate.city else city,
                        singleLine = true,
                        label = { Text(text = "Città",color = Color.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        onValueChange = {
                            city = it
                            cityChange = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueLight,
                            unfocusedBorderColor = Color(0, 0, 0, 50)
                        ),
                        textStyle = TextStyle(
                            color = Color.Black
                        ),
                        isError = !uiState.isCityValid,
                        supportingText = {
                            if (!uiState.isCityValid)
                                Text(
                                    text = uiState.cityError,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Red,
                                    textAlign = TextAlign.Start
                                )
                        },
                        trailingIcon = {
                            if (!uiState.isCityValid)
                                Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                        }
                    )

                    OutlinedTextField(
                        value = if(uiState.addressToUpdate != null && !addressChange) uiState.addressToUpdate.address else address,
                        singleLine = true,
                        label = { Text(text = "Indirizzo",color = Color.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        onValueChange = {
                            address = it
                            addressChange = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueLight,
                            unfocusedBorderColor = Color(0, 0, 0, 50)
                        ),
                        textStyle = TextStyle(
                            color = Color.Black
                        ),
                        isError = !uiState.isAddressValid,
                        supportingText = {
                            if (!uiState.isAddressValid)
                                Text(
                                    text = uiState.addressError,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Red,
                                    textAlign = TextAlign.Start
                                )
                        },
                        trailingIcon = {
                            if (!uiState.isAddressValid)
                                Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                        }
                    )

                    OutlinedTextField(
                        value = if(uiState.addressToUpdate != null && !civicChange) uiState.addressToUpdate.civic.toString() else civic,
                        singleLine = true,
                        label = { Text(text = "Civico",color = Color.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = {
                            civic = it
                            civicChange = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueLight,
                            unfocusedBorderColor = Color(0, 0, 0, 50)
                        ),
                        textStyle = TextStyle(
                            color = Color.Black
                        ),
                        isError = !uiState.isCivicValid,
                        supportingText = {
                            if (!uiState.isCivicValid)
                                Text(
                                    text = uiState.civicError,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Red,
                                    textAlign = TextAlign.Start
                                )
                        },
                        trailingIcon = {
                            if (!uiState.isCivicValid)
                                Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                        }
                    )
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = { viewModel.setFABClicked(false); viewModel.resetFABField() },
                    Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 10.dp),
                    shape = RoundedCornerShape(25)
                ) {
                    Text(
                        text = "Indietro",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    )
                }
            },
            dismissButton = {
                if(uiState.addressToUpdate == null)
                    Button(
                        onClick = { viewModel.addNewAddress(addressName,address,city,civic)  },
                        Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 10.dp),
                        shape = RoundedCornerShape(25)
                    ) {
                        Text(
                            text = "Aggiungi",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        )
                    }
                else {
                    // update address
                    val _civic = if(!civicChange) uiState.addressToUpdate.civic else civic
                    val _address = if(!addressChange) uiState.addressToUpdate.address else address
                    val _city = if(!cityChange) uiState.addressToUpdate.city else city
                    val _addressName = if(!addressNameChange) uiState.addressToUpdate.name else addressName

                    val newAddress = Address(
                        _addressName,
                        _address,
                        _city,
                        _civic,
                        uiState.addressToUpdate.selected
                    )

                    Button(
                        onClick = { viewModel.updateAddress(newAddress, true) },
                        Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 10.dp),
                        shape = RoundedCornerShape(25)
                    ) {
                        Text(
                            text = "Modifica",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        )
                    }
                }
            },
            containerColor = Color.White
        )
    }
}
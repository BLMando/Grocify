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
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocify.states.UserPaymentMethodsUiState
import com.example.grocify.model.PaymentMethod
import com.example.grocify.views.theme.BlueDark
import com.example.grocify.views.theme.BlueLight
import com.example.grocify.utils.calculateCardNumberSelection
import com.example.grocify.utils.calculateExpiryDateSelection
import com.example.grocify.utils.formatCreditCardNumber
import com.example.grocify.utils.formatExpiryDate
import com.example.grocify.utils.formatNumber
import com.example.grocify.viewmodels.UserPaymentMethodsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPaymentsScreen(
    viewModel: UserPaymentMethodsViewModel = viewModel(),
    onBackClick: () -> Unit
){
    val uiState = viewModel.uiState.collectAsState()

    /**
     * Effect that reloads payment methods after each operations
     */
    LaunchedEffect(key1 = Unit, key2 = uiState.value.isUDSuccessful, key3 = uiState.value.isInsertSuccessful) {
        viewModel.getAllPaymentMethods()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        text = "Metodi di pagamento",
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
                PaymentDialog(uiState.value, viewModel)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                if (uiState.value.result.isEmpty() && uiState.value.paymentMethods.isNotEmpty()) {
                    Text(
                        text = "Metodo di pagamento corrente",
                        modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )

                    val paymentMethodsListWithSelected =
                        uiState.value.paymentMethods.filter { it.selected }

                    if (paymentMethodsListWithSelected.isEmpty())
                        Row(
                            Modifier.fillMaxWidth().padding(top = 20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nessun metodo di pagamento attualmente in uso",
                                style = TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    else
                        PaymentMethodCard(paymentMethodsListWithSelected.first(), viewModel)

                    Text(
                        text = "Metodi di pagamento disponibili",
                        modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )

                    val paymentMethodsListWithoutSelected: List<PaymentMethod> = if(paymentMethodsListWithSelected.isEmpty())
                        uiState.value.paymentMethods.filter { !it.selected }
                    else
                        uiState.value.paymentMethods.minus(
                            paymentMethodsListWithSelected.first()
                        )


                    if(paymentMethodsListWithoutSelected.isEmpty())
                        Row(
                            Modifier.fillMaxWidth().padding(top = 20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nessun altro metodo di pagamento disponibile",
                                style = TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    else
                        LazyColumn {
                            items(paymentMethodsListWithoutSelected.size) {
                                PaymentMethodCard(paymentMethodsListWithoutSelected[it],viewModel = viewModel)
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
fun PaymentMethodCard(paymentMethod: PaymentMethod, viewModel: UserPaymentMethodsViewModel){

    val spotColor = if (paymentMethod.selected) BlueDark else Color.Black
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
                    paymentMethod.owner,
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
                        if(!paymentMethod.selected){
                            DropdownMenuItem(
                                text = { Text("Seleziona") },
                                onClick = {
                                    viewModel.setPaymentMethodSelected(paymentMethod)
                                    expanded = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Modifica") },
                            onClick = {
                                viewModel.setFABClicked(true)
                                viewModel.updatePaymentMethod(paymentMethod,false)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Elimina") },
                            onClick = {
                                viewModel.deletePaymentMethod(paymentMethod)
                                expanded = false
                            }
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
                    text = paymentMethod.number,
                    modifier = Modifier.padding(start = 15.dp,bottom = 10.dp)
                )
                Text(
                    text = paymentMethod.expireDate,
                    modifier = Modifier.padding(end = 20.dp,bottom = 10.dp)
                )
            }

        }
    }
}


@Composable
fun PaymentDialog(uiState: UserPaymentMethodsUiState, viewModel: UserPaymentMethodsViewModel){

    // variables handling insert and update operations
    var owner by rememberSaveable { mutableStateOf("") }
    var cardNumber by  remember { mutableStateOf(TextFieldValue("")) }
    var expireDate by  remember { mutableStateOf(TextFieldValue("")) }
    var cvc by  remember { mutableStateOf(TextFieldValue("")) }

    var ownerChange by rememberSaveable { mutableStateOf(false) }
    var cardNumberChange by rememberSaveable { mutableStateOf(false) }
    var expireDateChange by rememberSaveable { mutableStateOf(false) }
    var cvcChange by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = uiState.isInsertSuccessful) {
        if(uiState.isInsertSuccessful){
            viewModel.setFABClicked(false)
            owner = ""
            ownerChange = false
            cardNumber = TextFieldValue("")
            cardNumberChange = false
            expireDate = TextFieldValue("")
            expireDateChange = false
            cvc = TextFieldValue("")
            cvcChange = false
        }
    }

    if (uiState.isFABClicked) {
        AlertDialog(
            onDismissRequest = { viewModel.setFABClicked(false) },
            title = {
                Text(
                    text = "Nuovo metodo di pagamento",
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
                        value = if(uiState.paymentMethodToUpdate != null && !ownerChange) uiState.paymentMethodToUpdate.owner else owner,
                        singleLine = true,
                        onValueChange = {
                            owner = it
                            ownerChange = true
                        },
                        placeholder = { Text(text = "Nome Cognome",color = Color.Gray) },
                        label = { Text(text = "Intestatario",color = Color.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueLight,
                            unfocusedBorderColor = Color(0, 0, 0, 50)
                        ),
                        textStyle = TextStyle(
                            color = Color.Black
                        ),
                        isError = !uiState.isOwnerValid,
                        supportingText = {
                            if (!uiState.isOwnerValid)
                                Text(
                                    text = uiState.ownerError,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Red,
                                    textAlign = TextAlign.Start
                                )
                        },
                        trailingIcon = {
                            if (!uiState.isOwnerValid)
                                Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                        }
                    )

                    OutlinedTextField(
                        value = if(uiState.paymentMethodToUpdate != null && !cardNumberChange) TextFieldValue(uiState.paymentMethodToUpdate.number) else cardNumber,
                        singleLine = true,
                        placeholder = { Text(text = "XXXX XXXX XXXX XXXX",color = Color.Gray) },
                        label = { Text(text = "Numero carta",color = Color.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = { newValue ->
                            val formattedValue = formatCreditCardNumber(newValue.text)
                            val newSelection = calculateCardNumberSelection(newValue, formattedValue)
                            cardNumber = newValue.copy(text = formattedValue, selection = newSelection)
                            cardNumberChange = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueLight,
                            unfocusedBorderColor = Color(0, 0, 0, 50)
                        ),
                        textStyle = TextStyle(
                            color = Color.Black
                        ),
                        isError = !uiState.isNumberValid,
                        supportingText = {
                            if (!uiState.isNumberValid)
                                Text(
                                    text = uiState.numberError,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Red,
                                    textAlign = TextAlign.Start
                                )
                        },
                        trailingIcon = {
                            if (!uiState.isNumberValid)
                                Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                        }
                    )

                    OutlinedTextField(
                        value = if(uiState.paymentMethodToUpdate != null && !expireDateChange) TextFieldValue(uiState.paymentMethodToUpdate.expireDate) else expireDate,
                        singleLine = true,
                        placeholder = { Text(text = "gg/aa",color = Color.Gray) },
                        label = { Text(text = "Data di scadenza", color = Color.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = { newValue ->
                            val formattedValue = formatExpiryDate(newValue.text)
                            val newSelection = calculateExpiryDateSelection(newValue, formattedValue)
                            expireDate = newValue.copy(text = formattedValue, selection = newSelection)
                            expireDateChange = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueLight,
                            unfocusedBorderColor = Color(0, 0, 0, 50)
                        ),
                        textStyle = TextStyle(
                            color = Color.Black
                        ),
                        isError = !uiState.isExpireDateValid,
                        supportingText = {
                            if (!uiState.isExpireDateValid)
                                Text(
                                    text = uiState.expireDateError,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Red,
                                    textAlign = TextAlign.Start
                                )
                        },
                        trailingIcon = {
                            if (!uiState.isExpireDateValid)
                                Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                        }
                    )

                    OutlinedTextField(
                        value = if(uiState.paymentMethodToUpdate != null && !cvcChange) TextFieldValue(uiState.paymentMethodToUpdate.cvc) else cvc,
                        singleLine = true,
                        label = { Text(text = "CVC", color = Color.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = { newValue ->
                            val formattedValue = formatNumber(newValue.text,3)
                            cvc = TextFieldValue(formattedValue, newValue.selection)
                            cvcChange = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueLight,
                            unfocusedBorderColor = Color(0, 0, 0, 50)
                        ),
                        textStyle = TextStyle(
                            color = Color.Black
                        ),
                        isError = !uiState.isCvcValid,
                        supportingText = {
                            if (!uiState.isCvcValid)
                                Text(
                                    text = uiState.cvcError,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Red,
                                    textAlign = TextAlign.Start
                                )
                        },
                        trailingIcon = {
                            if (!uiState.isCvcValid)
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
                if(uiState.paymentMethodToUpdate == null)
                    Button(
                        onClick = { viewModel.addNewPaymentMethod(owner,cardNumber.text,cvc.text,expireDate.text) },
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
                else{
                    //update payment method
                    val _owner = if(!ownerChange) uiState.paymentMethodToUpdate.owner else owner
                    val _cvc = if(!cvcChange) uiState.paymentMethodToUpdate.cvc else cvc.text
                    val _cardNumber = if(!cardNumberChange) uiState.paymentMethodToUpdate.number else cardNumber.text
                    val _expireDate = if(!expireDateChange) uiState.paymentMethodToUpdate.expireDate else expireDate.text

                    val newPaymentMethod = PaymentMethod(
                        owner = _owner,
                        number = _cardNumber,
                        cvc = _cvc,
                        expireDate = _expireDate,
                        selected = uiState.paymentMethodToUpdate.selected
                    )

                    Button(
                        onClick = { viewModel.updatePaymentMethod(newPaymentMethod, true) },
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






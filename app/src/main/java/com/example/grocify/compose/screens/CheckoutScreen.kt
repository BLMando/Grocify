package com.example.grocify.compose.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocify.components.CheckoutBox
import com.example.grocify.components.UserBottomNavigation
import com.example.grocify.data.CheckoutUiState
import com.example.grocify.ui.theme.BlueLight
import com.example.grocify.ui.theme.BlueMedium
import com.example.grocify.util.anyToDouble
import com.example.grocify.viewmodels.CheckoutViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = viewModel(),
    flagCart: String,
    totalPrice: String,
    onBackClick: () -> Unit,
    onAddressClick: () -> Unit,
    onPaymentMethodClick: () -> Unit,
    onCatalogClick: () -> Unit,
    onGiftClick: () -> Unit,
    onVirtualCartClick: () -> Unit,
    onConfirmClick: (flagCart:String, orderId:String) -> Unit,
) {

    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getCurrentInfo()
        viewModel.userHasRunningOrder()
    }


    LaunchedEffect(key1 = uiState.value.orderId) {
        if(uiState.value.orderId.isNotEmpty()){
            onConfirmClick(flagCart,uiState.value.orderId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = { Text(
                    text = "Opzioni di pagamento",
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
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "arrow back")
                    }
                }
            )
        },
        bottomBar = {
            UserBottomNavigation(
                ref = if(flagCart == "online") "virtualCart" else "physicalCart",
                onCatalogClick = onCatalogClick,
                onGiftClick = onGiftClick,
                onPhysicalCartClick = {},
                onVirtualCartClick = onVirtualCartClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Informazioni",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(start = 15.dp, top = 15.dp, bottom = 10.dp)
                )
                PaymentOptionsCard(onPaymentMethodClick, uiState.value)
                DeliveryOptionCard(onAddressClick, uiState.value)
            }

            val shipping = "1.50"
            CheckoutBox(
                "Riepilogo ordine",
                if (flagCart == "online") (String.format("%.2f", (anyToDouble(totalPrice)!! - anyToDouble(shipping)!!))).replace(',', '.') + "€" else null,
                if (flagCart == "online") shipping + "€" else null,
                (String.format("%.2f", anyToDouble(totalPrice))).replace(',', '.') + "€",
                "Conferma",
                buttonEnabled = uiState.value.resultAddress.isEmpty() && uiState.value.resultPaymentMethod.isEmpty() && uiState.value.userHasRunningOrder == false
            ) {
                viewModel.createNewOrder(flagCart,anyToDouble(totalPrice)!!)
            }

            ExistingRunningOrderDialog(uiState.value.userHasRunningOrder)
        }
    }
}

@Composable
fun ExistingRunningOrderDialog(userHasRunningOrder: Boolean?) {

    var dialogState by remember {
        mutableStateOf(true)
    }

    if (userHasRunningOrder == true && dialogState) {
        AlertDialog(
            onDismissRequest = { dialogState = false },
            title = {
                Text(
                    text = "Ordine in corso",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),

                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            },
            icon = {
                   Icon(
                       imageVector = Icons.Outlined.Info,
                       contentDescription = "icona di informazione",
                       tint = BlueMedium,
                       modifier = Modifier
                           .padding(top = 35.dp)
                           .height(70.dp)
                           .fillMaxWidth(),
                   )
            },
            text = {
                Text(
                    text = "Non è possibile procedere con un altro ordine finché quello attualmente in corso non è concluso",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { dialogState = false },
                    Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 10.dp),
                    shape = RoundedCornerShape(25)
                ) {
                    Text(
                        text = "Ho capito",
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
fun DeliveryOptionCard(onAddressClick: () -> Unit, uiState: CheckoutUiState) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 20.dp)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onAddressClick() }
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Indirizzo di spedizione",
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "arrow forward",
                    Modifier.size(15.dp)
                )
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                if (uiState.resultAddress.isEmpty()) {
                    Text(
                        text = "${uiState.currentAddress?.name}, ${uiState.currentAddress?.city}"
                    )
                    Text(
                        text = "${uiState.currentAddress?.address}, ${uiState.currentAddress?.civic}"
                    )
                }else{
                    Text(
                        text = uiState.resultAddress
                    )
                }
            }

        }
    }
}

@Composable
fun PaymentOptionsCard(onPaymentMethodClick: () -> Unit, uiState: CheckoutUiState) {
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
            .clickable { onPaymentMethodClick() }
    ) {
        Column(
            Modifier.fillMaxWidth(),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pagamento",
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "arrow forward",
                    Modifier.size(15.dp)
                )

            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(uiState.resultPaymentMethod.isEmpty()){
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(
                            imageVector = Icons.Filled.CreditCard,
                            contentDescription = "credit card",
                            tint = BlueLight,
                            modifier = Modifier.padding(end = 10.dp)
                        )

                        uiState.currentPaymentMethod?.let {
                            Text(
                                text = it.number
                            )
                        }
                    }

                    uiState.currentPaymentMethod?.let {
                        Text(
                            text = it.expireDate
                        )
                    }
                }else{
                    Text(
                        text = uiState.resultPaymentMethod
                    )
                }
            }
        }
    }
}

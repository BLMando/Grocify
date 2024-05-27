package com.example.grocify.compose.screens


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocify.components.CheckoutBox
import com.example.grocify.components.UserBottomNavigation
import com.example.grocify.data.CheckoutUiState
import com.example.grocify.ui.theme.BlueLight
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
                ref = "virtualCart",
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
                buttonEnabled = uiState.value.result.isEmpty() && uiState.value.resultAddress.isEmpty() && uiState.value.resultPaymentMethod.isEmpty()
            ) {
                viewModel.createNewOrder(flagCart,anyToDouble(totalPrice)!!)
            }
        }

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
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(uiState.resultAddress.isEmpty()){
                    uiState.currentAddress?.let {
                        Text(
                            text = it.name
                        )
                    }
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

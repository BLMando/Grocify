package com.example.grocify.views.screens.home

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.grocify.R
import com.example.grocify.model.Order
import com.example.grocify.views.theme.BlueDark
import com.example.grocify.viewmodels.HomeDriverViewModel
import com.example.grocify.views.components.MapDialog
import com.example.grocify.views.theme.BlueLight
import com.example.grocify.views.theme.BlueMedium
import com.google.android.gms.auth.api.identity.Identity
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDriverScreen(
    context: Activity,
    onLogOutClick: () -> Unit,
    onGroceryClick: (String, String) -> Unit,
    mapRedirect: (String, String) -> Unit,
    onQRScanned: () -> Unit,
) {
    /**
     * Instantiate the HomeDriverViewModel passing parameters through the factory method
     * @param context Activity
     * @param OneTapClient The Google Sign-In client
     */
    val viewModel: HomeDriverViewModel = viewModel(factory = viewModelFactory {
        addInitializer(HomeDriverViewModel::class) {
            HomeDriverViewModel(context.application, Identity.getSignInClient(context))
        }
    })

    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getSignedInDriverName()
        viewModel.getOrders()
    }


    Scaffold (
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight(300),
                                    color = Color.Black,
                                ),
                            ) {
                                append("Ciao ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                )
                            ) {
                                append(uiState.value.currentUserName)
                            }
                        }
                    ) },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.icon),
                        contentDescription = "app logo",
                        modifier = Modifier.padding(start = 20.dp,end=10.dp)
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.signOut(); onLogOutClick() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Profile icon"
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                Modifier.padding(innerPadding)
            ) {
                Text(
                    text = "Gli ordini in corso",
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                // if there are no orders in status "in consegna" or "consegnato" show
                // the lazycolumn with the orders in status "in preparazione" or "in attesa"
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Log.v("HomeDriver", uiState.value.orders.toString())
                    items(uiState.value.orders.size) {
                        if(uiState.value.orders[it].orderId.isNotEmpty()) {
                            Spacer(modifier = Modifier.size(20.dp))
                            OrderItem(
                                {
                                    onGroceryClick(
                                        uiState.value.orders[it].orderId,
                                        uiState.value.orders[it].destination
                                    )
                                },
                                uiState.value.orders[it],
                                viewModel,
                                {
                                    mapRedirect(
                                        uiState.value.orders[it].orderId,
                                        uiState.value.orders[it].destination
                                    )
                                },
                                { onQRScanned() },
                                context
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun OrderItem(
    onGroceryClick: () -> Unit,
    order: Order,
    viewModel: HomeDriverViewModel,
    mapRedirect: () -> Unit,
    onQRScanned: () -> Unit,
    context: Activity
) {

    val currentDriverId = viewModel.getCurrentDriverId()

    val buttonColor = when(order.status){
        "in attesa" -> BlueDark
        "in preparazione" -> BlueMedium
        "in consegna" -> BlueLight
        "consegnato" -> Blue
        else -> {BlueDark}
    }

    val buttonText = when(order.status){
        "in attesa" -> "Vedi spesa"
        "in preparazione" -> "Continua spesa"
        "in consegna" -> "Riprendi la consegna"
        "consegnato" -> "Scannerizza il QR"
        else -> ""
    }

    var dialogState by rememberSaveable { mutableStateOf(false) }

    MapDialog(
        state = dialogState,
        orderId = order.orderId,
        viewModel = viewModel,
        scanner = GmsBarcodeScanning.getClient(context),
        onQRScanned = onQRScanned,
        fromScreen = "home_driver",
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth(0.9f)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderId,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "${order.date} - ${order.time}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Destinazione:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.destination,
                    fontSize = 14.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Numero di articoli:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.cart.size.toString(),
                    fontSize = 14.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Totale:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${order.totalPrice}€",
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    /**
                     * Set up actions to do with different orders status
                     */
                    if ((currentDriverId == order.driverId && order.status == "in preparazione") || order.status == "in attesa") {
                        onGroceryClick()
                    }
                    if (currentDriverId == order.driverId && order.status == "in consegna") {
                        mapRedirect()
                    }

                    if (currentDriverId == order.driverId && order.status == "consegnato") {
                        dialogState = true
                    }

                },
                shape = RoundedCornerShape(25),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = buttonColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Visibility,
                    contentDescription = "eye icon",
                    modifier = Modifier.size(20.dp),
                    tint =  Color.White,
                )
                Text(
                    text = buttonText,
                    color = Color.White,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}
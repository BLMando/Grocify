package com.example.grocify.views.screens.account

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DepartureBoard
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocify.states.UserOrdersUiState
import com.example.grocify.model.Order
import com.example.grocify.views.theme.BlueDark
import com.example.grocify.viewmodels.UserOrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserOrdersScreen(
    viewModel: UserOrdersViewModel = viewModel(),
    onBackClick: () -> Unit,
    onTrackOrderClick: (orderId: String) -> Unit
){

    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllOrders()
    }

    LaunchedEffect(key1 = uiState.value.ordersReviewed) {
        viewModel.getOrdersReviewed()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        text = "Storico degli ordini",
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text(
                text = "Ordine corrente",
                modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )

            val currentOrder = uiState.value.orders.filter { it.status != "concluso" }
            val pastOrders = uiState.value.orders.filter { it.status == "concluso" }

            if (currentOrder.isEmpty())
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nessun ordine in corso",
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp
                        )
                    )
                }
            else
                OrderCard(
                    viewModel = null,
                    currentOrder[0],
                    false,
                    Icons.Filled.DepartureBoard,
                    true,
                    onTrackOrderClick
                )

            Text(
                text = "Ordini passati",
                modifier = Modifier.padding(top = 20.dp, start = 20.dp),

                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )

            if (pastOrders.isEmpty())
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nessun ordine effettuato",
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp
                        )
                    )
                }
            else {
                LazyColumn {
                    items(pastOrders.size) {
                        OrderCard(
                            viewModel = viewModel,
                            pastOrders[it],
                            hasReview = uiState.value.ordersReviewed.contains(pastOrders[it].orderId),
                            iconState = Icons.Filled.Done,
                            actualOrder = false
                        ) { }
                    }
                }
                OrderDialog(uiState.value.orderReview, uiState.value.isReviewClicked, viewModel!!, uiState.value)
            }
        }
    }
}
@Composable
fun OrderCard(
    viewModel: UserOrdersViewModel?,
    order: Order,
    hasReview: Boolean,
    iconState: ImageVector,
    actualOrder: Boolean,
    onTrackOrderClick: (orderId: String) -> Unit
){
    val optionButtonIcon = if(actualOrder) Icons.Filled.LocalShipping
    else if (hasReview) Icons.Filled.ThumbUpAlt else Icons.Filled.RateReview

    val optionButtonText = if(actualOrder) "Traccia l'ordine" else if(hasReview) "Grazie!" else "Recensisci"
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
                    "Ordine ${order.orderId}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )

                Text(
                    text = "Data: ${order.date}",
                    Modifier.padding(vertical = 5.dp)
                )

                Text(
                    text = "Totale: ${order.totalPrice}€",
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
                        order.status.replaceFirstChar {it.uppercase() },
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
                        .clickable {
                            if (!actualOrder && !hasReview) {
                                viewModel?.setReviewIconClicked(true)
                                viewModel?.setOrderReviewId(order)
                            }else if (actualOrder)
                                onTrackOrderClick(order.orderId)
                        }
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
fun OrderDialog(
    order: Order,
    fabState: Boolean,
    viewModel: UserOrdersViewModel,
    uiState: UserOrdersUiState
){

    var rating by rememberSaveable { mutableFloatStateOf(1f) } //default rating will be 1
    var text by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(key1 = fabState) {
        if (!fabState) {
            rating = 1F
            text = ""
        }
    }

    if (fabState) {
        AlertDialog(
            onDismissRequest = { viewModel.setReviewIconClicked(false) },
            title = {
                Text(
                    text = "Dicci cosa pensi",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                )
            },
            text = {
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
                        Modifier
                            .height(150.dp)
                            .padding(top = 20.dp),
                        singleLine = false,
                        label = {
                            Text(text = "Inserisci un commento")
                        },
                        isError = !uiState.isTextValid,
                        supportingText = {
                            if(!uiState.isTextValid)
                                Text(
                                    text = uiState.textError,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Red,
                                    textAlign = TextAlign.Start
                                )
                        },
                        trailingIcon = {
                            if (!uiState.isTextValid)
                                Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
                        }
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                                viewModel.addOrderReview(order.orderId,order.userId,text,rating)
                                if (!uiState.isTextValid)
                                    viewModel.setReviewIconClicked(false)
                            },
                    Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 10.dp),
                    shape = RoundedCornerShape(25)
                ) {
                    Text(
                        text = "Invia",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    )
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = { viewModel.setReviewIconClicked(false) },
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
            containerColor = Color.White
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
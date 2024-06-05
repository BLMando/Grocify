package com.example.grocify.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.grocify.compose.components.MovingTextAndIconRow
import com.example.grocify.compose.components.UserBottomNavigation
import com.example.grocify.model.ProductType
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight
import com.example.grocify.ui.theme.BlueMedium
import com.example.grocify.viewmodels.GiftProductViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftProductScreen(
    viewModel: GiftProductViewModel = viewModel(),
    onCatalogClick: () -> Unit,
    onPhysicalCartClick: () -> Unit,
    onVirtualCartClick: () -> Unit,
    onTrackOrderClick: (orderId: String) -> Unit,
) {

    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.resetFields()
        viewModel.getThresholdProducts()
        viewModel.calculateMoneySpentDuringCurrentMonth()
        viewModel.checkIfAlreadyRedeemed()
        viewModel.checkIfInCart()
        viewModel.checkOrdersStatus()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = { Text(
                        text = "Per te",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight(500),
                        color = Color.Black,
                    ),
                ) },
            )
        },
        bottomBar = {
            UserBottomNavigation(
                ref = "gift",
                onCatalogClick = onCatalogClick,
                onGiftClick = {  },
                onPhysicalCartClick = onPhysicalCartClick,
                onVirtualCartClick = onVirtualCartClick
            )
        },
        content = { innerPadding ->
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ){

                if(uiState.value.orderId != ""){
                    MovingTextAndIconRow(uiState.value.orderId, onTrackOrderClick)
                }

                GiftInfoCard(
                    moneySpent = uiState.value.moneySpent,
                    startOfMonth = uiState.value.startOfMonth.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    endOfMonth = uiState.value.endOfMonth.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    daysLeft = uiState.value.daysLeft
                )
                Text(
                    text = "Premi omaggio",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp
                    ),
                    modifier = Modifier.padding(start = 15.dp,top = 10.dp,bottom = 20.dp)
                )
                repeat(uiState.value.thresholdProducts.size) { index ->
                        val product = uiState.value.thresholdProducts[index]
                        product.let {
                            GiftProduct(
                                it,
                                uiState.value.moneySpent,
                                viewModel
                            )
                        }
                    }
            }
        }
    )
}

@Composable
fun GiftInfoCard(moneySpent: Float, startOfMonth: String, endOfMonth: String, daysLeft: Long) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = BlueDark
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 20.dp)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column(
            Modifier.padding(15.dp)
        ){
            Text(
                text = "Valida dal " + startOfMonth +" al "+ endOfMonth,
                Modifier.padding(bottom = 30.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight(600)
                )
            )
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                        )
                    ) {
                        append("Hai speso: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight(500),
                            color = Color.White,
                        )
                    ) {
                        append(String.format("%.2f", moneySpent).replace(',', '.') + "€")
                    }
                }
            )
            val valueRange = 0f..200f

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Slider(
                    value = moneySpent,
                    onValueChange = {},
                    colors = SliderDefaults.colors(
                        thumbColor = BlueLight,
                        activeTrackColor = BlueLight,
                        inactiveTrackColor = Color.White,
                    ),
                    valueRange = valueRange
                )

                BoxWithConstraints(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 25.dp, end = 8.dp)
                ) {
                    val sliderWidth = maxWidth * 0.5f
                    val labelSpacing = sliderWidth / (valueRange.endInclusive - valueRange.start)

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "0€",
                            style = TextStyle(
                                fontSize = 17.sp,
                                color = Color.White,
                                fontWeight = FontWeight.W600
                            )
                        )
                        Spacer(modifier = Modifier.width(labelSpacing * 50))
                        Text(
                            text = "50€",
                            style = TextStyle(
                                fontSize = 17.sp,
                                color = Color.White,
                                fontWeight = FontWeight.W600
                            )
                        )
                        Spacer(modifier = Modifier.width(labelSpacing * 50))
                        Text(
                            text = "100€",
                            style = TextStyle(
                                fontSize = 17.sp,
                                color = Color.White,
                                fontWeight = FontWeight.W600
                            )
                        )
                        Spacer(modifier = Modifier.width(labelSpacing * 100))
                        Text(
                            text = "200€",
                            style = TextStyle(
                                fontSize = 17.sp,
                                color = Color.White,
                                fontWeight = FontWeight.W600
                            )
                        )
                    }
                }

                Text(
                    text = "Ti rimangono " + daysLeft.toString() + " giorni",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.W500,
                    )
                )
            }

        }
    }
}

@Composable
fun GiftProduct(product: ProductType, moneySpent: Float, viewModel: GiftProductViewModel) {

    var expanded by rememberSaveable { mutableStateOf(false) }

    val flagRedeemed = viewModel.getFlagThreshold(product.threshold)

    val message: String

    if(!flagRedeemed)
        if(product.threshold < moneySpent)
            message = "Ricompensa sbloccata"
        else
            message = "Ti mancano " + String.format("%.2f", (product.threshold - moneySpent)).replace(',', '.') + "€"
    else
        message = "Ricompensa già riscattata"
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 10.dp)
                .height(70.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                SubcomposeAsyncImage(
                    model = product.image,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(65.dp)
                        .height(65.dp)
                        .clip(RoundedCornerShape(15.dp))
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
                Column (
                    Modifier.padding(start = 10.dp)
                ) {

                    Text(
                        text = product.name + " " + product.quantity,
                        Modifier.padding(bottom = 5.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Light,
                            fontSize = 16.sp
                        )
                    )

                    Text(
                        text = message,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    )
                }

            }
            if(!flagRedeemed)
                if(product.threshold < moneySpent){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.TopEnd)
                    ) {

                        IconButton(
                            onClick = { expanded = !expanded },

                            Modifier.border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(15.dp)
                            )

                        )
                        {
                            Icon(
                                imageVector = Icons.Filled.AddShoppingCart,
                                contentDescription = "cart icon"
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
                            DropdownMenuItem(
                                text = { Text("Online") },
                                onClick = {
                                    viewModel.addToCart(product, "online")
                                    expanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Negozio") },
                                onClick = {
                                    viewModel.addToCart(product, "store")
                                    expanded = false
                                }
                            )

                        }
                    }

                }
        }
    }
}



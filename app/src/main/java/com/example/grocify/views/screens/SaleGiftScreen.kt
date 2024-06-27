package com.example.grocify.views.screens

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.grocify.components.AdminBottomNavigation
import com.example.grocify.model.Product
import com.example.grocify.model.ProductType
import com.example.grocify.views.theme.BlueDark
import com.example.grocify.views.theme.BlueLight
import com.example.grocify.views.theme.BlueMedium
import com.example.grocify.views.theme.ExtraLightGray
import com.example.grocify.utils.formatNumber
import com.example.grocify.viewmodels.SaleGiftViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleGiftScreen(
    viewModel: SaleGiftViewModel = viewModel(),
    isSaleContent:Boolean,
    onStatsClick: () -> Unit,
    onSaleClick: (flagPage: Boolean) -> Unit,
    onGiftClick: (flagPage: Boolean) -> Unit,
    onUsersClick: () -> Unit,
){
    val uiState = viewModel.uiState.collectAsState()

    var discount by rememberSaveable { mutableStateOf("") }

    val title = if (isSaleContent) "Applicazione sconti" else "Gestione omaggi"
    val description = if (isSaleContent) "Scegli i prodotti e applica lo sconto" else "Scegli i prodotti omaggi del mese"
    val buttonText = if (isSaleContent) "Applica sconto" else "Conferma prodotti omaggio"

    LaunchedEffect(key1 = Unit) {
        viewModel.resetFields()
        viewModel.getProducts()
        if(!isSaleContent)
            viewModel.initializeSelectedProducts()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                       text = title,
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                    ) },
            )
        },
        bottomBar = {
            AdminBottomNavigation(
                ref = if (isSaleContent) "sale" else "gift",
                onStatsClick = onStatsClick,
                onSaleClick = if (isSaleContent) ({}) else onSaleClick,
                onGiftClick = if (isSaleContent) onGiftClick else ({}),
                onUsersClick = onUsersClick
            )
        },
        content = { innerPadding ->
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(innerPadding),
            ) {

                Text(
                    text = description,
                    modifier = Modifier.padding(20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Column(
                    Modifier.fillMaxHeight(if(isSaleContent) 0.7f else 0.9f),
                    verticalArrangement = if(isSaleContent) Arrangement.SpaceBetween else Arrangement.Top,
                    horizontalAlignment = if(isSaleContent) Alignment.Start else Alignment.End
                ){
                    if(!isSaleContent) {
                        var size = "0"
                        if (uiState.value.selectedProducts != emptyList<Product>())
                            size = uiState.value.selectedProducts.size.toString()
                        Text(
                            text = "Selezionati $size/3",
                            Modifier.padding(end = 18.dp),
                            style = TextStyle(
                                fontSize = 15.sp
                            )
                        )
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.height(if (isSaleContent) 430.dp else 500.dp)
                    ){
                        if (uiState.value.products != emptyList<ProductType>()) {
                            items(uiState.value.products.size) { index ->
                                val product = uiState.value.products[index]
                                ShowProducts(
                                    product = product,
                                    isSaleContent,
                                    viewModel
                                )
                            }
                        }
                    }

                }
                if(isSaleContent)
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .padding(10.dp)
                            .shadow(
                                5.dp,
                                shape = RoundedCornerShape(20.dp),
                                ambientColor = Color.White
                            )
                            .clip(RoundedCornerShape(20.dp))
                    ){
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = "Sconto",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(end = 20.dp)
                            )

                            TextField(
                                value = discount,
                                onValueChange = { it ->
                                    if(it.all { it.isDigit() })
                                        if (it.length <= 2)
                                            discount = it
                                    },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.height(50.dp),
                                textStyle = TextStyle(
                                    color = Color.Black
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = ExtraLightGray,
                                    unfocusedIndicatorColor = BlueDark,
                                    focusedTextColor = Color.Black
                                ),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Percent,
                                        contentDescription = "IconPercentage"
                                    )
                                }
                            )
                        }
                    }
                Button(
                    onClick = {
                        if(isSaleContent){
                            viewModel.updateDiscountProducts(formatNumber(discount,2))
                            discount = ""
                        }
                        else{
                            viewModel.updateThresholdProducts()
                        }
                      },
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    enabled = if(!isSaleContent) if(uiState.value.selectedProducts.size == 3) true else false else if(uiState.value.selectedProducts.size >= 1) true else false,
                    shape = RoundedCornerShape(10)
                ) {
                    Text(text = buttonText)
                }
            }

        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowProducts(
    product: ProductType,
    isSaleContent: Boolean,
    viewModel: SaleGiftViewModel,
){

    var color = BlueMedium
    var isSelected by remember { mutableStateOf(false) }
    val isClickable = if(!isSaleContent) if(product in viewModel.getSelectedProducts() || viewModel.getSelectedProducts().size<3) true else false else true
    if(!isSaleContent) {//se l'admin si trova sulla pagina omaggi
        if (product in viewModel.getSelectedProducts()) {//prendo i prodotti con soglia già presenti sul db
            isSelected = true
            //in base alla posizione del prodotto omaggio nella lista, coloro il bordo della card in maniera diversa
            when (viewModel.getSelectedProducts().indexOf(product)) {
                0 -> color = BlueLight//colore per il primo prodotto omaggio
                1 -> color = BlueMedium//colore per il secondo prodotto omaggio
                2 -> color = BlueDark//colore per il terzo prodotto omaggio
            }
        }
    }
    else{//se l'admin si trova sulla pagina sconti
        if(product.discount != 0.00){//se il prodotto mostrato nella card è scontato
            isSelected = true
            color = Color.Red
        }
    }


    var isTrembling: Boolean by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val shakeAnimation = remember {
        Animatable(0f)
    }

    LaunchedEffect(isTrembling) {
        if (isTrembling) {
            scope.launch {
                while (isTrembling) {
                    shakeAnimation.animateTo(
                        targetValue = 2f,
                        animationSpec = tween(
                            durationMillis = 25,
                            easing = LinearEasing
                        )
                    )
                    delay(50)
                    shakeAnimation.animateTo(
                        targetValue = -2f,
                        animationSpec = tween(
                            durationMillis = 25,
                            easing = LinearEasing
                        )
                    )
                    delay(50)
                }
            }
        } else {
            shakeAnimation.animateTo(0f)
        }
    }

    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .graphicsLayer {
                translationX = shakeAnimation.value
            }
            .padding(10.dp)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 2.dp,
                color = if (isSelected) color else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .combinedClickable(
                enabled = isClickable,
                onClick = {
                            //se l'admin si trova sulla pagina sconti e preme la card contenente il prodotto
                            if(isSaleContent){
                                isSelected  = !isSelected
                                isTrembling = false//disattivo l'animazione se attiva
                                if (product.discount == 0.00)//se il prodotto non ha sconto
                                    if (isSelected)//se è stato selezionato
                                        viewModel.addSelectedProduct(product)//lo aggiungo alla lista dei prodotti selezionati
                                    else//altrimenti
                                        viewModel.removeSelectedProduct(product)//lo rimuovo dalla lista dei prodotti selezionati
                            }
                            else//altrimenti se l'admin si trova sulla pagina omaggi e preme la card contenente il prodotto
                                //se il prodotto in considerazione è un prodotto omaggio o la lista dei prodotti omaggi non è completa
                                if(product in viewModel.getSelectedProducts() || viewModel.getSelectedProducts().size<3){
                                    isSelected  = !isSelected
                                    if (isSelected)//se il prodotto è stato selezionato
                                        viewModel.addSelectedProduct(product)//lo aggiungo alla lista dei prodotti selezionati
                                    else//altrimenti se è stato deselezionato
                                        viewModel.removeSelectedProduct(product)//lo rimuovo dalla lista dei prodotti selezionati
                                }

                          },
                onLongClick = {
                            if(isSaleContent)//se l'admin si trova sulla pagina sconti e tiene premuta la card contenente il prodotto
                                //se il prodotto è un prodotto scontato
                                if (product.discount != 0.00)
                                    //triggero l'animazione
                                    isTrembling = !isTrembling
                            },
            )

    ) {
        Box(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = product.image,
                contentDescription = product.name,
                contentScale = ContentScale.FillBounds,
                alignment = Alignment.Center
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

                if (isTrembling) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Red, shape = RoundedCornerShape(50))
                            .clickable {
                                isSelected = false
                                isTrembling = false
                                viewModel.removeDiscountedProduct(product)
                            }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Remove",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}



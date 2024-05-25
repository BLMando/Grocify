package com.example.grocify.compose.screens



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.grocify.components.UserBottomNavigation
import com.example.grocify.data.Product
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight
import com.example.grocify.ui.theme.BlueMedium
import com.example.grocify.ui.theme.Purple80
import com.example.grocify.viewmodels.CategoryItemsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItemsScreen(
    viewModel: CategoryItemsViewModel = viewModel(),
    categoryId: String?,
    onBackClick: () -> Unit,
    onCatalogClick: () -> Unit,
    onGiftClick: () -> Unit,
    onPhysicalCartClick: () -> Unit,
    onVirtualCartClick: () -> Unit
) {

    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getProducts(categoryId)
        viewModel.getTotalPrice()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        text= uiState.value.categoryName,
                        style = TextStyle(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
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
        bottomBar = {
            UserBottomNavigation(
                onCatalogClick = onCatalogClick,
                onGiftClick = onGiftClick,
                onPhysicalCartClick = onPhysicalCartClick,
                onVirtualCartClick = onVirtualCartClick
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ){
                //se sono presenti prodotti nella categoria
                if(uiState.value.isSuccessful)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                    ){
                        //ciclo i prodotti della categoria e li mostro nell'app
                        items(uiState.value.products.size){
                            CategoryItemCard(uiState.value.products[it], viewModel, "online")
                        }
                    }
                else
                    Text(
                        text = "Nessun prodotto presente in questa categoria",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    )
            }
        }
    )
}

@Composable
fun CategoryItemCard(product: Product, viewModel: CategoryItemsViewModel, flagCart: String) {
    val scope = rememberCoroutineScope()
    var isAddingToCart by remember { mutableStateOf(false) }
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
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){
            SubcomposeAsyncImage(
                model = product.image,
                contentDescription = "food image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                    .width(170.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(30.dp))
            ){
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
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ){
                Text(
                    text = product.name,
                    style = TextStyle(
                        fontSize = 15.sp,
                        color = Color(0xFF3f4145)
                    ),
                    modifier = Modifier.padding(start = 18.dp, top = 10.dp)
                )
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                            ),
                        ) {
                            append("${product.price}€")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = 13.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("/${product.quantity}")
                        }
                    },
                    modifier = Modifier.padding(start = 18.dp)
                )
            }


            Button(
                onClick = {

                    if (!isAddingToCart ) {
                        isAddingToCart   = true
                        scope.launch {
                            viewModel.addToCart(product, flagCart)
                            delay(300)  // Debounce delay
                            isAddingToCart  = false
                        }
                    }
                         },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                enabled = !isAddingToCart,


                colors = ButtonDefaults.buttonColors(

                    containerColor  = if (isAddingToCart) Color.Black else BlueDark,
                    contentColor = Color.White,
                    disabledContainerColor = BlueMedium,
                )
            ) {
                if (isAddingToCart) {
                    CircularProgressIndicator(
                        color = Color.White,

                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(text = "Aggiungi")
                }
            }
        }
    }
}

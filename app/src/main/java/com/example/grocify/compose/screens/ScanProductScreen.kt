package com.example.grocify.compose.screens


import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocify.R
import com.example.grocify.components.CartItems
import com.example.grocify.components.CheckoutBox
import com.example.grocify.components.UserBottomNavigation
import com.example.grocify.model.Product
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.util.anyToDouble
import com.example.grocify.viewmodels.CartViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun ScanProductScreen(
    viewModel: CartViewModel = viewModel(),
    activity: Activity,
    onCatalogClick: () -> Unit,
    onGiftClick: () -> Unit,
    onPhysicalCartClick: () -> Unit,
    onCheckoutClick: (totalPrice: String) -> Unit,
) {
    val scanner = GmsBarcodeScanning.getClient(activity)
    val storeUiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.initializeProductsList("store")
    }

   Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = { Text(
                    text = "Il tuo carrello",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight(500),
                        color = Color.Black,
                    ),
                ) },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            Box(modifier = Modifier.padding(16.dp)) { // Aggiungi un padding per allineare con il BottomAppBar
                FloatingActionButton(
                    onClick = { scanner.startScan()
                        .addOnSuccessListener { barcode ->
                            viewModel.addRow(barcode.rawValue.toString())
                        }},
                    containerColor = BlueDark,
                ) {
                    Image(
                        painterResource(id = R.drawable.bar_code),
                        contentDescription = "bar code scanner",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        },
        bottomBar = {
            UserBottomNavigation(
                ref = "physicalCart",
                onCatalogClick = onCatalogClick,
                onGiftClick = onGiftClick,
                onPhysicalCartClick = onPhysicalCartClick,
                onVirtualCartClick = {  }
            )
        },
        content = { innerPadding ->
            Column (
                Modifier.padding(innerPadding)
            ) {
                //NON rimuovere il controllo che la lista non sia vuota altrimenti l'app non mostra la lista aggiornata
                if (storeUiState.productsList!= emptyList<Product>()){
                    if(anyToDouble(storeUiState.totalPrice)!! > 0){
                        CheckoutBox(
                            null,
                            null,
                            null,
                            (String.format("%.2f", storeUiState.totalPrice)).replace(',', '.') + "€",
                            "Checkout",
                            onCheckoutClick = {onCheckoutClick(storeUiState.totalPrice.toString())},
                        )

                    }
                }
                Column {
                    Text(
                        text = "La tua spesa",
                        modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                    Divider(
                        color = Color.LightGray,
                        thickness = 0.6.dp,
                        modifier = Modifier.padding(start= 20.dp,top = 10.dp,end = 20.dp,bottom = 15.dp),
                    )
                    LazyColumn{

                        //NON rimuovere il controllo che la lista non sia vuota altrimenti l'app non mostra la lista aggiornata
                        if(storeUiState.productsList != emptyList<Product>()){
                            items(storeUiState.productsList.size) { index ->
                                val product = storeUiState.productsList[index]
                                product.let {
                                    CartItems(
                                        id = it.id,
                                        name = it.name,
                                        price = it.price,
                                        quantity = it.quantity,
                                        image = it.image,
                                        units = it.units,
                                        viewModel = viewModel,
                                        flagCart = "store"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
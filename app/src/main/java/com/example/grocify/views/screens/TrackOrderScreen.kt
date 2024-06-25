package com.example.grocify.views.screens

import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.twotone.SwipeUp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.views.theme.BlueDark
import com.example.grocify.views.theme.BlueLight
import com.example.grocify.views.theme.BlueMedium
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocify.states.TrackOrderUiState
import com.example.grocify.viewmodels.TrackOrderViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackOrderScreen(
    viewModel: TrackOrderViewModel = viewModel(),
    onBackClick: () -> Unit,
    orderId: String,
    onQRScanned: () -> Unit
){

    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getCurrentOrder(orderId)
    }

    /**
     * Effect to handle route after qr code is scanned by the driver
     */
    LaunchedEffect(key1 = uiState.value.order.status) {
        if(uiState.value.order.status == "concluso"){
            onQRScanned()
        }

        if(uiState.value.order.status == "consegnato"){
            viewModel.getDriverName(orderId)
        }
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
                        text= "Stato dell'ordine",
                        style = TextStyle(
                            fontSize = 30.sp,
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
        content = {innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.BottomCenter
            ){
                Column (
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, bottom = 30.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "N° $orderId",
                            style = TextStyle(
                                fontSize = 20.sp,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }

               
                    TrackingState(
                        Icons.Filled.LocalShipping,
                        "Ordine in corso di elaborazione",
                        "Ordine effettuato in data ${uiState.value.order.date} alle ore ${uiState.value.order.time}.",
                        true
                    )
                    TrackingState(
                        Icons.Filled.AccessTimeFilled,
                        "In preparazione",
                        if(uiState.value.order.status == "in attesa") "" else "Stiamo preparando la tua spesa. ${LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm"))}",
                        uiState.value.order.status == "in preparazione" || uiState.value.order.status == "in consegna" || uiState.value.order.status == "consegnato"
                    )
                    TrackingState(
                        Icons.Filled.Map,
                        "In consegna",
                        if(uiState.value.order.status != "in consegna" && uiState.value.order.status != "consegnato") "" else "La tua spesa è in arrivo con un nostro driver. ${
                            LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm"))}",
                        uiState.value.order.status == "in consegna" || uiState.value.order.status == "consegnato"
                    )
                    TrackingState(
                        Icons.Filled.CheckCircle,
                        "Consegnato",
                        if(uiState.value.order.status != "consegnato") "" else "La tua spesa è stata consegnata, apri il QRCode in basso. ${LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm"))}",
                        uiState.value.order.status == "consegnato"
                    )
                }
                if(uiState.value.order.status == "consegnato"){
                    QRCodeInfo(uiState.value,orderId)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeInfo(state: TrackOrderUiState, orderId: String) {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Animation for the swipe up icon
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Column(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { _, _ -> /* Handle drag if needed */ },
                    onDragEnd = { showBottomSheet = true },
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.TwoTone.SwipeUp,
            contentDescription = "Swipe up",
            modifier = Modifier
                .size(40.dp)
                .offset { IntOffset(0, offsetY.dp.roundToPx()) },
            tint = BlueLight
        )
    }

    if(showBottomSheet)
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.ExpandedShape },
            containerColor = Color.White,

        ) {
            Column (
                Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Divider(
                    color = BlueDark,
                    thickness = 5.dp,
                    modifier = Modifier
                        .width(100.dp)
                        .padding(top = 30.dp, bottom = 30.dp)
                        .clip(RoundedCornerShape(50))
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ){
                    Text(
                        text = "Il tuo driver:",
                        style = TextStyle(fontSize = 18.sp)
                    )
                    Text(
                        text = state.driverName,
                        style = TextStyle(fontSize = 18.sp)
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ){
                    Text(
                        text = "Numero di prodotti:",
                        style = TextStyle(fontSize = 18.sp)
                    )
                    Text(
                        text = "${state.order.cart.size}",
                        style = TextStyle(fontSize = 18.sp)
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Prezzo totale:",
                        style = TextStyle(fontSize = 18.sp)
                    )
                    Text(
                        text = "${state.order.totalPrice}€",
                        style = TextStyle(fontSize = 18.sp)
                    )
                }
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 10.dp,end = 10.dp,top = 10.dp)
                )
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Fai scansionare il QR CODE al driver dopo aver ricevuto la spesa",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Image(
                        bitmap = qrCodeGenerator(orderId).asImageBitmap(),
                        contentDescription = "QR Code"
                    )
                }
            }
        }
}

/**
 * Function to generate a QR code bitmap based on the order ID
 * @param orderId: String representing the order ID
 * @return Bitmap representing the QR code
 */
fun qrCodeGenerator(orderId: String): Bitmap {
    val mfw = MultiFormatWriter()

    try {
        val bitMatrix: BitMatrix = mfw.encode(orderId, BarcodeFormat.QR_CODE, 600, 600)
        val barcodeEncoder = BarcodeEncoder()

        return barcodeEncoder.createBitmap(bitMatrix)

    } catch (e: WriterException) {
        throw RuntimeException(e)
    }
}

@Composable
fun TrackingState(icon:ImageVector,text:String,subText:String,active:Boolean) {
    val tintColor = if(active) Color.White else Color.LightGray

    val iconModifier = if(active){
        Modifier
            .background(BlueMedium, RoundedCornerShape(50))
            .border(
                width = 1.5.dp, color = BlueDark, shape = RoundedCornerShape(50)
            )
            .padding(5.dp)
    }else{
        Modifier
            .background(Color.Transparent, RoundedCornerShape(50))
            .border(
                width = 1.5.dp, color = Color.LightGray, shape = RoundedCornerShape(50)
            )
            .padding(5.dp)
    }

    Row (
        Modifier
            .fillMaxWidth()
            .padding(start = 30.dp),
        verticalAlignment = Alignment.Top
    ){
        Column(
            Modifier.padding(end = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Localized description",
                tint = tintColor,
                modifier = iconModifier
            )
            if(text != "Consegnato")
                Divider(
                    modifier = Modifier
                        .height(120.dp)
                        .width(1.5.dp),
                    color = Color.Black
                )
        }
        Column{
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = subText,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            )
        }

    }
}

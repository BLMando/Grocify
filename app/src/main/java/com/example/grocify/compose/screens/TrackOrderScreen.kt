package com.example.grocify.compose.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueMedium
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackOrderScreen(){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(text= "Stato dell'ordine #1234",
                        style = TextStyle(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                    ) },
                navigationIcon = {
                    IconButton(
                        onClick = { /*TODO*/ }
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
                        .fillMaxSize()
                        .padding(top=50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TrackingState(Icons.Filled.LocalShipping,"Ordine in corso di elaborazione","Stiamo preparando il tuo ordine. 22 Aprile 2024, 15:30",true)
                    TrackingState(Icons.Filled.AccessTimeFilled,"In preparazione","Stiamo impacchettando la tua spesa. 22 Aprile 2024, 15:50",true)
                    TrackingState(Icons.Filled.Map,"In consegna","La tua spesa è in arrivo con un nostro driver",false)
                    TrackingState(Icons.Filled.CheckCircle,"Consegnato","La tua spesa è stata consegnata. 22 Aprile 2024, 16:30",false)
                }

                QRCodeInfo()
            }
        }
    )
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun QRCodeInfo() {

    val swipeableState = rememberSwipeableState(0)
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val heightPx = displayMetrics.heightPixels.toFloat()
    val anchors = mapOf(heightPx/2 to 0, 0f to 1) // Maps anchor points (in px) to statesto 0, sizePx to 1) // Maps anchor points (in px) to states

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 30.dp,
            draggedElevation = 10.dp,

        ),
        modifier = Modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Vertical
            )
            .offset { IntOffset(0, swipeableState.offset.value.roundToInt()) }
            .shadow(
                10.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                ambientColor = Color.Black
            )
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
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
                    text = "Nome:",
                    style = TextStyle(fontSize = 18.sp)
                )
                Text(
                    text = "Mattia Mandorlini",
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
                    text = "Quantità:",
                    style = TextStyle(fontSize = 18.sp)
                )
                Text(
                    text = "6 prodotti",
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
                    text = "$145.00",
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
                    bitmap = qrCodeGenerator().asImageBitmap(),
                    contentDescription = "QR Code",

                )
            }
        }
    }
}
fun qrCodeGenerator(): Bitmap {
    val mfw = MultiFormatWriter()

    try {
        val bitMatrix: BitMatrix = mfw.encode("Prova", BarcodeFormat.QR_CODE, 600, 600)
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

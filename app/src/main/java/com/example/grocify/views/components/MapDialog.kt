package com.example.grocify.views.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.AndroidViewModel
import com.example.grocify.viewmodels.HomeDriverViewModel
import com.example.grocify.viewmodels.MapDialog
import com.example.grocify.viewmodels.MapViewModel
import com.example.grocify.views.theme.BlueMedium
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MapDialog(
    state: Boolean,
    orderId: String,
    viewModel: AndroidViewModel,
    scanner: GmsBarcodeScanner,
    onQRScanned: () -> Unit,
    fromScreen: String,
) {
    /**
     *  Dialog to show when the user arrives at the destination.
     *  Checks the fromScreen parameter to determine which view model to use.
     */
    if (fromScreen == "map") {
        val mapViewModel = viewModel as? MapViewModel
        mapViewModel?.let {
            if (state)
                AlertDialogComponent(it, orderId, scanner, onQRScanned)
        }
    } else {
        val homeDriverViewModel = viewModel as? HomeDriverViewModel
        homeDriverViewModel?.let {
            if (state)
                AlertDialogComponent(it, orderId, scanner, onQRScanned)
        }
    }

}

@Composable
fun <T> AlertDialogComponent(viewModel: T, orderId: String, scanner: GmsBarcodeScanner, onQRScanned: () -> Unit)
        where T : AndroidViewModel, T : MapDialog {
    AlertDialog(
        onDismissRequest = { viewModel.setDialogState(false) },
        title = {
            Text(
                text = "Sei arrivato a destinazione",
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
                imageVector = Icons.Filled.QrCodeScanner,
                contentDescription ="dialog icon",
                tint = BlueMedium,
                modifier = Modifier
                    .padding(top = 35.dp)
                    .height(70.dp)
                    .fillMaxWidth(),
            )
        },
        text = {
            Column (
                Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Scansiona il QR code del cliente per confermare la ricezione dell'ordine",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { scanner.startScan().addOnSuccessListener {QRcode ->
                    if(orderId == QRcode.rawValue.toString()){
                        viewModel.setOrderConclude(orderId)
                        viewModel.setDialogState(false)
                        onQRScanned()
                    }
                }},
                Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Scansiona",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = false
        )
    )
}

package com.example.grocify

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BarcodeAnalyzer(private val context: Context) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @OptIn(ExperimentalPermissionsApi::class)
    @SuppressLint("UnsafeOptInUsageError")

    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image
            ?.let { image ->
                scanner.process(
                    InputImage.fromMediaImage(
                        image, imageProxy.imageInfo.rotationDegrees
                    )
                ).addOnSuccessListener { barcode ->
                    barcode?.takeIf { it.isNotEmpty() }
                        ?.mapNotNull { it.rawValue }
                        ?.joinToString(",")
                        ?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show()

                        trovaProdotto(barcode?.mapNotNull{ it.rawValue }.toString())}


                }.addOnCompleteListener {
                    imageProxy.close()
                }
            }
    }
}

private fun trovaProdotto(stringa: String){

    //Log.v("myTag", stringa.substring( 1, stringa.length - 1 ))
    if (stringa.substring( 1, stringa.length - 1 ) == "pizza"){
        readDataFromFirestore()
    }
    //dovrà essere letto il db se trovato il prodotto aggiunto, e fatto il reindirizzamento a ScanProductScreen
    //ScanProductScreen()
    readDataFromFirestore()
}
private fun readDataFromFirestore() {
    val db = Firebase.firestore
    db.collection("prodotti")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                Log.v("firestore", "${document.id} => ${document.data}")
            }
        }
        .addOnFailureListener { exception ->
            Log.v("firestore", "Error getting documents.", exception)
        }
}
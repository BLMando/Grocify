package com.example.grocify



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.example.grocify.databinding.MapLayoutBinding
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.internal.ApiKey
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.ui.MapFragment

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(apiKey: String,tomTomMap: TomTomMap,mapFragment: MapFragment){
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        text = "Navigazione",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight(500),
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {  }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "arrow back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .height(600.dp)
                    .width(350.dp)
                    .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                TomTomMapContainer(apiKey,tomTomMap,mapFragment)
            }

            Button(
                onClick = {/*TODO*/ },
                Modifier
                    .width(350.dp)
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Navigation,
                    contentDescription = "navigation icon",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White,
                )
                Text(
                    "Avvia navigazione",
                    color = Color.White,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }

        /*Dialog(
            "Sei arrivato a destinazione",
            true,
            Icons.Filled.LocationOn,
            "Scansiona",
            Icons.Filled.QrCodeScanner
        ) {
            Column (
                Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "15:30",
                    style = TextStyle(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Text(
                    text = "Scansiona il QR code del cliente per confermare la ricezione dell'ordine",
                    style = TextStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }*/
    }
}



@Composable
fun TomTomMapContainer(apiKey: String,tomTomMap: TomTomMap,mapFragment: MapFragment) {

    val fragmentManager = (LocalContext.current as? FragmentActivity)?.supportFragmentManager

    AndroidViewBinding(factory = { inflater, _, _->
        val binding = MapLayoutBinding.inflate(inflater)
        val mapOptions = MapOptions(mapKey = apiKey)

        mapFragment = MapFragment.newInstance(mapOptions)


        // Aggiungi il MapFragment al FragmentManager
        fragmentManager?.beginTransaction()
            ?.replace(binding.mapFragment.id,mapFragment)
            ?.commit()


        mapFragment.getMapAsync{ map ->
            tomTomMap = map
        }

        binding

    })
}*/
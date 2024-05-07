package com.example.grocify

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight
import com.example.grocify.ui.theme.ExtraLightGray
import com.example.grocify.ui.theme.LightGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(text= "Grocify account",
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
        content = { innerPadding ->
            val label = arrayOf("Il tuo profilo","Indirizzi di spedizione", "Metodo di pagamento", "Storico degli ordini")
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                UserInfo()

                Column{
                    label.forEach { label -> UserOptions(label,false) }
                }

                UserOptions("Esci",true)

            }
        }
    )
}

@Composable
fun UserInfo() {
    Column(
        Modifier
            .fillMaxWidth().padding(top=30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(id = R.drawable.user_default) ,
            contentDescription = "user default image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(12.dp)
                .border(
                    3.dp, brush = Brush.radialGradient(
                        0.2f to BlueLight,
                        1f to BlueDark
                    ), RoundedCornerShape(50)
                )
                .size(95.dp)
                .clip(RoundedCornerShape(20.dp))
        )

        Text(
            text = "Mattia Mandorlini",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            text = "mando3228@gmail.com",
            style = TextStyle(
                fontSize = 15.sp,
                color = Color.Gray
            )
        )
    }
}

@Composable
fun UserOptions(text:String,logOut:Boolean) {

    val cardColor = if(logOut) BlueLight else Color.White
    val textColor = if(logOut) Color.White else Color.Black
    val icon = if(logOut) Icons.AutoMirrored.Filled.ExitToApp else Icons.AutoMirrored.Filled.ArrowForwardIos
    val iconColor = if(logOut) Color.White else Color.Black
    val iconSize = if(logOut) 25.dp else 15.dp


    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 15.dp)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Row (
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(500),
                    color = textColor
                ),
                modifier = Modifier.padding(20.dp)
            )
            Icon(
                imageVector = icon,
                contentDescription = "arrow icon",
                tint = iconColor,
                modifier = Modifier
                    .padding(end = 20.dp, top = 15.dp, bottom = 15.dp)
                    .size(iconSize)
            )
        }
    }

}



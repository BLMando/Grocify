package com.example.grocify.compose.screens

import android.app.Activity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.SubcomposeAsyncImage
import com.example.grocify.model.User
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight
import com.example.grocify.viewmodels.UserProfileViewModel
import com.google.android.gms.auth.api.identity.Identity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    context: Activity,
    onSignOut: () -> Unit,
    onBackClick: () -> Unit
){

    val viewModel: UserProfileViewModel = viewModel(factory = viewModelFactory {
        addInitializer(UserProfileViewModel::class) {
            UserProfileViewModel(context.application, Identity.getSignInClient(context))
        }
    })

    LaunchedEffect(key1 = Unit) {
        viewModel.getSignedInUser()
    }

    val uiState = viewModel.uiState.collectAsState()

    val label = arrayOf(
        "Il tuo profilo",
        "Indirizzi di spedizione",
        "Metodo di pagamento",
        "Storico degli ordini"
    )


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        text= "Grocify account",
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
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxHeight()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            UserInfo(uiState.value.user)

            Column {
                label.forEach { label -> UserOptions(label, false) {} }
            }

            UserOptions("Esci", true) {
                viewModel.signOut()
                onSignOut()
            }

        }
    }
}

@Composable
fun UserInfo(userData: User) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        SubcomposeAsyncImage(
            model = userData.profilePic,
            contentDescription = "user default image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .border(
                    3.dp, brush = Brush.radialGradient(
                        0.2f to BlueLight,
                        1f to BlueDark
                    ), RoundedCornerShape(50)
                )
                .size(95.dp)
                .clip(CircleShape),
            loading = {
                CircularProgressIndicator()
            }
        )

        Text(
            text = "${userData.name} ${userData.surname}",
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )

        userData.email?.let {
            Text(
                text = it,
                style = TextStyle(
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            )
        }
    }
}

@Composable
fun UserOptions(text:String, logOut:Boolean, action: () -> Unit) {

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
                .fillMaxWidth()
                .clickable {
                    if (logOut)
                        action()
                },
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




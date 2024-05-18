package com.example.grocify.compose.screens.account

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DepartureBoard
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.viewmodels.UserOrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserOrdersScreen(
    viewModel: UserOrdersViewModel = viewModel(),
    onBackClick: () -> Unit
){

    val uiState = viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        text = "Storico degli ordini",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight(500),
                            color = Color.Black,
                        ),
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
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                Text(
                    text = "Ordine corrente",
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )

                OrderCard(
                    viewModel = null,
                    "Spedito",
                    Icons.Filled.DepartureBoard,
                    true
                )

                Text(
                    text = "Ordini passati",
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                LazyColumn {
                    items(4) {
                        OrderCard(
                            viewModel = viewModel,
                            state = "Consegnato",
                            iconState = Icons.Filled.Done,
                            actualOrder = false
                        )
                        OrderDialog(uiState.value.isReviewClicked,viewModel)
                    }
                }


            }
        }
    )
}
@Composable
fun OrderCard(
    viewModel: UserOrdersViewModel?,
    state:String,
    iconState:ImageVector,
    actualOrder:Boolean
){
    val optionButtonIcon = if(actualOrder) Icons.Filled.LocalShipping else Icons.Filled.RateReview
    val optionButtonText = if(actualOrder) "Traccia l'ordine" else "Recensisci"
    val spotColor = if (actualOrder) BlueDark else Color.Black

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 15.dp)
            .shadow(
                5.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black,
                spotColor = spotColor
            )
            .clip(RoundedCornerShape(20.dp))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Ordine #1234",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )

                Text(
                    text = "Data: 10/05/2024",
                    Modifier.padding(vertical = 5.dp)
                )

                Text(
                    text = "Totale: $50.00",
                )
            }

            Column(
                Modifier.padding(end = 10.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(
                            width = 0.3.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ){
                    Icon(
                        imageVector = iconState,
                        contentDescription ="state",
                        Modifier.padding(end = 5.dp),
                    )
                    Text(
                        state,
                        style = TextStyle(
                            fontSize = 14.sp,
                        )
                    )
                }

                Spacer(modifier = Modifier.size(15.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = BlueDark,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .clickable {
                            if(!actualOrder)
                                viewModel?.setReviewIconClicked(true)
                        }
                ){
                    Icon(
                        imageVector = optionButtonIcon,
                        contentDescription ="state",
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Text(
                        text = optionButtonText,
                        style = TextStyle(
                            fontSize = 14.sp,
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun OrderDialog(fabState: Boolean,viewModel: UserOrdersViewModel){
    Log.d("orderdialog", fabState.toString())
    if (fabState) {
        AlertDialog(
            onDismissRequest = { viewModel.setReviewIconClicked(false) },
            title = {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Dicci cosa pensi",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )

                    IconButton(
                        onClick = { viewModel.setReviewIconClicked(false) },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "close icon"
                        )
                    }
                }

            },
            text = { OrderInputDialogContent() },
            confirmButton = {
                Button(
                    onClick = { viewModel.setReviewIconClicked(false) },
                    Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical =15.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Invia",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    )
                }
            },
            containerColor = Color.White
        )
    }
}


@Composable
fun OrderInputDialogContent(){

    var rating by rememberSaveable { mutableFloatStateOf(1f) } //default rating will be 1
    var text by rememberSaveable { mutableStateOf("") }

    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ){
        StarRatingBar(
            maxStars = 5,
            rating = rating,
            onRatingChanged = {
                rating = it
            }
        )

        TextField(
            value = text,
            onValueChange = {text = it},
            Modifier.padding(top = 20.dp),
            singleLine = false,
            placeholder = {Text(text = "Inserisci un commento...")}
        )
    }

}

@Composable
fun StarRatingBar(
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit
) {
    val density = LocalDensity.current.density
    val starSize = (16f * density).dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 1..maxStars) {

            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFFC700),
                modifier = Modifier
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            onRatingChanged(i.toFloat())
                        }
                    )
                    .width(starSize)
                    .height(starSize)
            )
        }
    }
}
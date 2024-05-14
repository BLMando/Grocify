package com.example.grocify


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DoneOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.components.Dialog
import com.example.grocify.components.ListItems
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = { Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight(500),
                                color = Color.Black,
                            ),
                        ) {
                            append("Ordine ")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight(500),
                                color = Color.Black,
                                fontStyle = FontStyle.Italic
                            )
                        ) {
                            append("#12345")
                        }
                    }
                ) },
                navigationIcon = {
                    IconButton(
                        onClick = {  }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "hide_password"
                        )
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = BlueDark,
            )
            {
            Image(
                painterResource(id = R.drawable.bar_code),
                contentDescription = "bar code scanner",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .size(30.dp)
            )
        } },
        content = { innerPadding ->
            Dialog(
                "Ordine completato",
                true,
                Icons.Filled.DoneOutline,
                "Procedi alla spedizione",
                Icons.AutoMirrored.Filled.ArrowForward
            ){
                Column (
                    Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "Raggiungi Mattia Mandorlini",
                        style = TextStyle(
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Text(
                        text = "Porto San Giorgio, Via Cavour 12",
                        style = TextStyle(
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            Column (
                Modifier.padding(innerPadding)
            ) {
                Text(
                    text = "Prodotti",
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Divider(
                    color = Color.LightGray,
                    thickness = 0.6.dp,
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp),
                )
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(5) {
                        ListItems("food","Apples","300g"){
                            val checkedState = rememberSaveable { mutableStateOf(true) }
                            Checkbox(
                                checked = checkedState.value,
                                onCheckedChange = { checkedState.value = it },
                                modifier = Modifier
                                    .padding(end = 10.dp),
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BlueLight,
                                    uncheckedColor = BlueLight,
                                    checkmarkColor = Color.White,
                                )
                            )
                        }
                    }
                }
            }
        }
    )
}

package com.example.grocify.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.grocify.R
import com.example.grocify.data.Category
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight
import com.example.grocify.ui.theme.BlueMedium
import com.example.grocify.viewmodels.HomeUserViewModel
import com.example.grocify.viewmodels.HomeViewModel
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.common.ChartDataCollection

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
){
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getUserRole()
    }

    when(uiState.value.userRole){
        "user" -> HomeUserScreen(navController)
        "admin" -> HomeAdminScreen(navController)
        "driver" -> HomeDriverScreen(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeUserScreen(
    navController: NavController,
    viewModel: HomeUserViewModel = viewModel()
){

    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getSignedInUser()
        viewModel.getCategories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = { Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight(300),
                                color = Color.Black,
                            ),
                        ) {
                            append("Ciao ")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                            )
                        ) {
                            append("${uiState.value.currentUserName}!")
                        }
                    }
                ) },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.icon),
                        contentDescription = "app logo",
                        modifier = Modifier.padding(start = 20.dp,end=10.dp)
                    )
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(Screen.UserProfile.route) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile icon"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier
                    .shadow(10.dp, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                tonalElevation = 30.dp,
                containerColor = Color.White,
                actions = {
                    Row (
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Icon(
                                Icons.Filled.ShoppingBag,
                                contentDescription = "Localized description",
                                tint = BlueLight
                            )
                            Text(
                                text = "Catalogo",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = BlueLight,
                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.UserProfile.route)
                            }
                        ){
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = "Localized description"
                            )
                            Text(
                                text = "Scansiona",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = Color.Black,
                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.GiftScreen.route)
                            }
                        ){
                            Icon(
                                Icons.Filled.CardGiftcard,
                                contentDescription = "Localized description"
                            )
                            Text(
                                text = "Per te",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = Color.Black,
                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Localized description")
                            Text(
                                text = "Carrello",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = Color.Black,
                                )
                            )
                        }
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding),
        ) {
            Text(
                text = "Cerca per categoria",
                Modifier.padding(start = 10.dp, top = 20.dp, bottom = 10.dp),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )

            Divider(
                color = Color.LightGray,
                thickness = 0.6.dp,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            )

            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth(),
                columns = GridCells.Fixed(2),
            ) {
                items(uiState.value.categories.size) {
                    CategoryCard(
                        uiState.value.categories[it]
                    ) {
                        navController.navigate(
                            Screen.CategoryItems.createRoute(
                                categoryId = uiState.value.categories[it].id
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: Category, onCategoryClick: () -> Unit) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(10.dp)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onCategoryClick() }
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){
            SubcomposeAsyncImage(
                model = category.image,
                contentDescription = "Category image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 10.dp, start = 5.dp, end = 5.dp)
                    .width(170.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(30.dp))
            ){
                val state = painter.state
                if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = BlueMedium,
                        strokeCap = StrokeCap.Round
                    )
                } else {
                    SubcomposeAsyncImageContent()
                }
            }

            Text(
                text = category.name,
                style = TextStyle(
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 5.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAdminScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight(300),
                                    color = Color.Black,
                                ),
                            ) {
                                append("Ciao ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                )
                            ) {
                                append("Admin!")
                            }
                        }
                    ) },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.icon),
                        contentDescription = "app logo",
                        modifier = Modifier.padding(start = 20.dp,end=10.dp)
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier
                    .shadow(10.dp, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                tonalElevation = 30.dp,
                containerColor = Color.White,
                actions = {
                    Row (
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Icon(
                                Icons.Filled.BarChart,
                                contentDescription = "Localized description",
                                tint = BlueLight
                            )
                            Text(
                                text = "Statistiche",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = BlueLight,
                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Icon(
                                Icons.Filled.AttachMoney,
                                contentDescription = "Localized description"
                            )
                            Text(
                                text = "Sconti",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = Color.Black,
                                )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Icon(Icons.Filled.CardGiftcard, contentDescription = "Localized description")
                            Text(
                                text = "Omaggi",
                                Modifier.padding(top = 7.dp),
                                style = TextStyle(
                                    color = Color.Black,
                                )
                            )
                        }
                    }
                },
            )
        },
        content = { innerPadding ->
            Column(
                Modifier.padding(innerPadding)
            ){
                Text(
                    text ="Dashboard",
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )

                LazyColumn {
                    items(4){
                        CardChart("I prodotti più venduti")
                    }
                }
            }

        }
    )
}

@Composable
fun CardChart(title:String) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 20.dp)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column (
            Modifier.padding(15.dp)
        ){
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 15.sp,

                    )
            )

            BarChart(
                dataCollection =  ChartDataCollection(
                    listOf(
                        BarData(10f, "Category A", color = Color(0xffed625d)),
                        BarData(20f, "Category B", color = Color(0xffed125d)),
                        BarData(50f, "Category C", color = Color(0xffed225d)),
                        BarData(40f, "Category D", color = Color(0xffed325d)),
                        BarData(23f, "Category E", color = Color(0xffed425d)),
                        BarData(35F, "Category F", color = Color(0xffed525d)),
                        BarData(20f, "Category K", color = Color(0xffed615d)),
                        BarData(50f, "Category L", color = Color(0xffed625d))
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDriverScreen(navController: NavController) {
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        text = "Ordini del 4 Marzo",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight(500),
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                    )
                }
            )
        },
        content = { innerPadding ->
            LazyColumn (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = innerPadding
            ) {
                items(5){
                    Spacer(modifier = Modifier.size(20.dp))
                    OrderItem()
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    )
}

@Composable
fun OrderItem(){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .width(IntrinsicSize.Min)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .width(IntrinsicSize.Min)
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#12345",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Ora: 17:30",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Destinazione:",
                    fontSize = 15.sp,
                )
                Text(
                    text = "Porto San Giorio, Via Cavour",
                    fontSize = 15.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Numero di articoli:",
                    fontSize = 15.sp
                )
                Text(
                    text = "12",
                    fontSize = 15.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Totale:",
                    fontSize = 15.sp
                )
                Text(
                    text = "50.00$",
                    fontSize = 15.sp
                )
            }

            Button(
                onClick = { /*TODO*/ },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = BlueDark
                ),
                modifier = Modifier
                    .width(325.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Visibility,
                    contentDescription = "eye icon",
                    modifier = Modifier.size(20.dp),
                    tint =  Color.White,
                )
                Text(
                    "Vedi spesa",
                    color = Color.White,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}
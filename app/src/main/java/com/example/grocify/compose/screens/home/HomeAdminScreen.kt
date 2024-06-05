package com.example.grocify.compose.screens.home

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Reviews
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.grocify.R
import com.example.grocify.components.AdminBottomNavigation
import com.example.grocify.compose.screens.account.StarRatingBar
import com.example.grocify.ui.theme.BlueLight
import com.example.grocify.viewmodels.HomeAdminViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.common.ChartDataCollection

data class TabRowItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val screen: @Composable () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeAdminScreen(
    context: Activity,
    onSaleClick: (flagPage: Boolean) -> Unit,
    onGiftClick: (flagPage: Boolean) -> Unit,
    onUsersClick: () -> Unit,
    onLogOutClick: () -> Unit,
) {

    val viewModel: HomeAdminViewModel = viewModel(factory = viewModelFactory {
        addInitializer(HomeAdminViewModel::class) {
            HomeAdminViewModel(context.application, Identity.getSignInClient(context))
        }
    })

    val uiState = viewModel.uiState.collectAsState()


    LaunchedEffect(key1 = Unit) {
        viewModel.getSignedInUserName()
    }

    val tabItems = listOf(
        TabRowItem(
            title = "Statistiche",
            selectedIcon = Icons.Filled.BarChart,
            unSelectedIcon = Icons.Outlined.BarChart,
            screen = { StatisticsContent() }
        ),
        TabRowItem(
            title = "Recensioni",
            selectedIcon = Icons.Filled.Reviews,
            unSelectedIcon = Icons.Outlined.Reviews,
            screen = { ReviewsContent() }
        )
    )

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState {
        tabItems.size
    }


    LaunchedEffect(key1 = selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(key1 = pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }


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
                                append(uiState.value.currentUserName)
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
                        onClick = { viewModel.signOut(); onLogOutClick() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Profile icon"
                        )
                    }
                }
            )
        },
        bottomBar = {
            AdminBottomNavigation(
                ref = "stats",
                onStatsClick = {},
                onSaleClick = onSaleClick,
                onGiftClick = onGiftClick,
                onUsersClick = onUsersClick
            )
        },
        content = { innerPadding ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.Start
            ){

                Text(
                    text ="Dashboard",
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 10.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                )

                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabItems.forEachIndexed { index, item ->
                       Tab(
                           selected = index == selectedTabIndex,
                           onClick = { selectedTabIndex = index },
                           text = {
                               Text(text = item.title)
                           },
                           icon = {
                               Icon(
                                   imageVector = if (index == selectedTabIndex) item.selectedIcon else item.unSelectedIcon,
                                   contentDescription = item.title
                               )
                           }
                       )
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {index ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        tabItems[index].screen()
                    }
                }
            }
        }
    )
}
@Composable
fun ReviewsContent() {
    LazyColumn {
        items(4){
            ReviewCard()
        }
    }
}

@Composable
fun StatisticsContent(){
    LazyColumn {
        items(4){
            CardChart("I prodotti più venduti")
        }
    }
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

@Composable
fun ReviewCard() {
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
        Column(
            Modifier.padding(20.dp)
        ) {
            Text(
                text = "Ordine #123456",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 20.dp)
            )
            StarRatingBar(
                rating = 2F,
                onRatingChanged = {},
                maxStars = 5
            )
            Text(
                text = "Ho preso un paio di stivali dal negozio X e sono molto soddisfatto. Sono di alta qualità e valgono il prezzo. Il negozio offriva anche la spedizione gratuita a quel prezzo, quindi è un vantaggio!.",
                style = TextStyle(
                    fontSize = 15.sp,
                ),
                modifier = Modifier.padding(vertical = 20.dp)
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
/*
@Preview
@Composable
fun HomeAdminScreenPreview() {
    HomeAdminScreen(context = Activity(),{})
}*/
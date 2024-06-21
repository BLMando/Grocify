package com.example.grocify.views.screens.home

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.LegendToggle
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.outlined.InsertChart
import androidx.compose.material.icons.outlined.Reviews
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.grocify.R
import com.example.grocify.api.SentimentAnalysis.SentimentData
import com.example.grocify.components.AdminBottomNavigation
import com.example.grocify.views.screens.account.StarRatingBar
import com.example.grocify.data.HomeAdminUiState
import com.example.grocify.model.Review
import com.example.grocify.ui.theme.BlueMedium
import com.example.grocify.ui.theme.MIXED
import com.example.grocify.ui.theme.NEGATIVE
import com.example.grocify.ui.theme.NEUTRAL
import com.example.grocify.ui.theme.POSITIVE
import com.example.grocify.viewmodels.HomeAdminViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.common.ChartDataCollection
import com.himanshoe.charty.line.CurveLineChart
import com.himanshoe.charty.line.config.LineConfig

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
        viewModel.getAllReviews()
        viewModel.getTop10MostBoughtProducts()
        viewModel.getTop10MostBoughtCategories()
        viewModel.getAverageMonthlyOrders()
        viewModel.getAverageMonthlyUsersExpense()
    }


    //Dichiarazione oggetti della data class che rappresentano le tab
    val tabItems = listOf(
        TabRowItem(
            title = "Statistiche",
            selectedIcon = Icons.Filled.InsertChart,
            unSelectedIcon = Icons.Outlined.InsertChart,
            screen = { StatisticsContent(uiState.value) }
        ),
        TabRowItem(
            title = "Recensioni",
            selectedIcon = Icons.Filled.Reviews,
            unSelectedIcon = Icons.Outlined.Reviews,
            screen = { ReviewsContent(uiState.value,viewModel) }
        )
    )

    //Gestione del pager
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
fun ReviewsContent(uiState: HomeAdminUiState,viewModel: HomeAdminViewModel) {
    LazyColumn {
        items(uiState.reviews.size){
            ReviewCard(
                uiState.reviews[it],
                viewModel = viewModel,
                if(uiState.sentimentAnalysisResult[it] == null) emptyList() else uiState.sentimentAnalysisResult[it]!!,
                it,
                analysisIsLoaded = uiState.analysisIsLoaded
            )
        }
    }
}

@Composable
fun StatisticsContent(uiState: HomeAdminUiState){

    val charts = hashMapOf(
        "I 10 prodotti più venduti" to uiState.top10Products,
        "Le 10 categorie più vendute" to uiState.top10Categories,
        "Media ordini mensili" to uiState.averageMonthlyOrders,
        "Spesa media mensile degli utenti" to uiState.averageMonthlyUsersExpense
    )

    LazyColumn {
        items(charts.size){
            BarChartCard(data = charts.values.elementAt(it), title = charts.keys.elementAt(it))
        }
    }
}

@Composable
fun BarChartCard(data: List<Pair<String, Int>>,title:String) {

        var dialogState by remember { mutableStateOf(false) }
        val barData: MutableList<BarData> = mutableListOf()
        val colorList: List<Color> = listOf(
            Color(0xffed625d),
            Color(0xffed125d),
            Color(0xffed225d),
            Color(0xffed325d),
            Color(0xffed425d),
            Color(0xffed525d),
            Color(0xffed615d),
            Color(0xffed635d),
            Color(0xffed735d),
            Color(0xffed835d),
            Color(0xffed935d),
            Color(0xffeda35d)
        )

        Card(
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
            if(data.isEmpty())
                CircularProgressIndicator()
            else {
                data.forEach { product ->
                    barData.add(
                        BarData(
                            product.second.toFloat(),
                            data.indexOf(product) + 1,
                            colorList[data.indexOf(product)]
                        )
                    )
                }
                Column(
                    Modifier.padding(15.dp)
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    when (title) {
                        "Media ordini mensili" -> CurveLineChart(
                            dataCollection = ChartDataCollection(barData),
                            lineConfig = LineConfig(
                                hasDotMarker = true,
                                hasSmoothCurve = true,
                                strokeSize = 1f
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )

                        "Spesa media mensile degli utenti" -> CurveLineChart(
                            dataCollection = ChartDataCollection(barData),
                            lineConfig = LineConfig(
                                hasDotMarker = true,
                                hasSmoothCurve = true,
                                strokeSize = 1f
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )

                        else -> BarChart(
                            dataCollection = ChartDataCollection(barData),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }

                    TextButton(
                        onClick = { dialogState = true }
                    ) {
                        Text(
                            text = "Legenda",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                }
            }
        }

        if (dialogState)
            AlertDialog(
                onDismissRequest = { dialogState = false },
                title = {
                    Text(
                        text = "Legenda",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier
                            .padding(top = 5.dp, bottom = 10.dp)
                            .fillMaxWidth(),

                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.LegendToggle,
                        contentDescription = "icona",
                        tint = BlueMedium,
                        modifier = Modifier
                            .padding(top = 35.dp)
                            .height(70.dp)
                            .fillMaxWidth(),
                    )
                },
                text = {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(data.size) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        bottom = 10.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${it + 1}:",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center,
                                    ),
                                    modifier = Modifier.padding(
                                        end = 10.dp
                                    )
                                )

                                Text(
                                    text = data[it].first.replaceFirstChar { it.uppercase() },
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }

                    }

                },
                properties = DialogProperties(
                    dismissOnClickOutside = true
                ),
                confirmButton = {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = { dialogState = false }
                        ) {
                            Text("Chiudi")
                        }
                    }

                },
                containerColor = Color.White
            )
}

@Composable
fun ReviewCard(
    review: Review,
    viewModel: HomeAdminViewModel,
    sentimentAnalysisData: List<SentimentData>,
    index: Int,
    analysisIsLoaded: Boolean
) {
    //variabile di stato per il LinearLoader
    var loaderState by rememberSaveable { mutableStateOf(false) }

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
                text = "Ordine ${review.orderId}",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            StarRatingBar(
                rating = review.rating,
                onRatingChanged = {},
                maxStars = 5
            )

            Text(
                text = review.review,
                style = TextStyle(
                    fontSize = 15.sp,
                ),
                modifier = Modifier.padding(vertical = 20.dp)
            )

            if(sentimentAnalysisData.isEmpty()) {
                TextButton(
                    onClick = {
                        loaderState = true
                        viewModel.sentimentAnalysis(review.review, index)
                    },
                    enabled = !loaderState
                ) {
                    Text(text = "Mostra analisi")
                }

                if (loaderState && !analysisIsLoaded)
                    LinearProgressIndicator(
                       modifier = Modifier.fillMaxWidth()
                    )
            }else{
                //resetto lo stato
                loaderState = false
                viewModel.resetAnalysisIsLoaded()

                //stato della legenda
                var dialogState by remember { mutableStateOf(false) }

                val barData = mutableListOf<BarData>()
                val sentimentColorList: MutableList<Color> = mutableListOf()

                sentimentAnalysisData.forEach { data ->
                    //per ogni entity associo un coloro al relativo sentiment
                    val score = hashMapOf(
                        POSITIVE to data.positive.toFloat(),
                        NEGATIVE to data.negative.toFloat(),
                        MIXED to data.mixed.toFloat(),
                        NEUTRAL to data.neutral.toFloat()
                    )

                    //ricavo il sentiment predominante
                    val sentimentScore = score.values.max() - (score.values.sum() - score.values.max())

                    //ricavo il colore del sentimentScore
                    val sentimentColor =
                        score.filterValues { it == score.values.max() }.keys.first()
                    sentimentColorList.add(sentimentColor)

                    //creo la barra del grafico relativa a quell'entity
                    barData.add(
                        BarData(
                            sentimentScore,
                            sentimentAnalysisData.indexOf(data) + 1,
                            color = sentimentColor
                        )
                    )
                }

                BarChart(
                    dataCollection = ChartDataCollection(barData),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { dialogState = true }
                    ) {
                        Text(
                            text = "Legenda",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }

                    TextButton(
                        onClick = { viewModel.resetSentimentAnalysisResult(index) }
                    ) {
                        Text(
                            text = "Chiudi analisi",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                }


                if (dialogState)
                    AlertDialog(
                        onDismissRequest = { dialogState = false },
                        title = {
                            Column {
                                Text(
                                    text = "Legenda",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center,
                                    ),
                                    modifier = Modifier
                                        .padding(top = 5.dp, bottom = 10.dp)
                                        .fillMaxWidth(),

                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    sentimentColorList.toSet().forEach { color ->
                                        //rimuovo i duplicati dall'array e associo ad ogni colore una legenda
                                        val text = when (color) {
                                            POSITIVE -> "Positivo"
                                            NEGATIVE -> "Negativo"
                                            NEUTRAL -> "Neutro"
                                            else -> "Misto"
                                        }
                                        Row(
                                            modifier = Modifier.padding(end = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Divider(
                                                color = color,
                                                modifier = Modifier
                                                    .padding(end = 5.dp)
                                                    .height(10.dp)
                                                    .width(10.dp)
                                            )
                                            Text(
                                                text = text,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.LegendToggle,
                                contentDescription = "icona",
                                tint = BlueMedium,
                                modifier = Modifier
                                    .padding(top = 35.dp)
                                    .height(70.dp)
                                    .fillMaxWidth(),
                            )
                        },
                        text = {
                            LazyColumn(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(sentimentAnalysisData.size) {
                                    Row(
                                        Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "${it + 1}:",
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                                textAlign = TextAlign.Center,
                                            ),
                                            modifier = Modifier.padding(
                                                bottom = 10.dp,
                                                end = 10.dp
                                            )
                                        )

                                        Text(
                                            text = sentimentAnalysisData[it].entity.replaceFirstChar { it.uppercase() },
                                            style = TextStyle(
                                                fontSize = 15.sp,
                                                textAlign = TextAlign.Center,
                                                color = sentimentColorList[it]
                                            )
                                        )
                                    }
                                }

                            }

                        },
                        properties = DialogProperties(
                            dismissOnClickOutside = true
                        ),
                        confirmButton = {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TextButton(
                                    onClick = { dialogState = false }
                                ) {
                                    Text("Chiudi")
                                }
                            }

                        },
                        containerColor = Color.White
                    )
            }
        }
    }
}
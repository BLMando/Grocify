package com.example.grocify.views.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.grocify.R
import com.example.grocify.views.components.MovingTextAndIconRow
import com.example.grocify.views.components.UserBottomNavigation
import com.example.grocify.model.Category
import com.example.grocify.views.theme.BlueMedium
import com.example.grocify.viewmodels.HomeUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeUserScreen(
    viewModel: HomeUserViewModel = viewModel(),
    onCategoryClick: (categoryId: String) -> Unit,
    onGiftClick: () -> Unit,
    onPhysicalCartClick: () -> Unit,
    onVirtualCartClick: () -> Unit,
    onAccountClick: () -> Unit,
    onTrackOrderClick: (orderId: String) -> Unit
){

    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getSignedInUserName()
        viewModel.getCategories()
        viewModel.checkOrdersStatus()
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
                            append("${uiState.value.currentUserName}")
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
                        onClick = onAccountClick
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
            UserBottomNavigation(
                ref = "catalog",
                onCatalogClick = {  },
                onGiftClick = onGiftClick,
                onPhysicalCartClick = onPhysicalCartClick,
                onVirtualCartClick = onVirtualCartClick
            )
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding),
        ) {

            if(uiState.value.orderId != ""){
                MovingTextAndIconRow(uiState.value.orderId, onTrackOrderClick)
            }

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
                        onCategoryClick(uiState.value.categories[it].id)
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
                // handle loading and error states for image
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
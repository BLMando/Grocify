package com.example.grocify.views.screens


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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.grocify.components.AdminBottomNavigation
import com.example.grocify.model.User
import com.example.grocify.views.theme.BlueDark
import com.example.grocify.views.theme.BlueLight
import com.example.grocify.views.theme.BlueMedium
import com.example.grocify.viewmodels.UsersManagementViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersManagementScreen(
    viewModel: UsersManagementViewModel = viewModel(),
    onStatsClick: () -> Unit,
    onSaleClick: (flagPage: Boolean) -> Unit,
    onGiftClick: (flagPage: Boolean) -> Unit,
){
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.resetFields()
        viewModel.getUsers()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        text = "Gestione utenti",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                    ) },
            )
        },
        bottomBar = {
            AdminBottomNavigation(
                ref = "users",
                onStatsClick = onStatsClick,
                onSaleClick = onSaleClick,
                onGiftClick = onGiftClick,
                onUsersClick = {}
            )
        },
        content = { innerPadding ->
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(innerPadding),
            ) {

                Text(
                    text = "Elenco utenti",
                    modifier = Modifier.padding(20.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Column(
                    Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.End
                ){

                    LazyColumn{
                        if (uiState.value.users != emptyList<User>()) {
                            items(uiState.value.users.size) { index ->
                                val user = uiState.value.users[index]
                                user.let {
                                    UserCard(
                                        user = it,
                                        viewModel
                                    )
                                }
                            }
                        }
                    }

                }
            }

        }
    )
}
@Composable
fun UserCard(user: User, viewModel: UsersManagementViewModel){

    val spotColor = Color.Black
    var expanded by rememberSaveable { mutableStateOf(false) }

    var borderColor by remember { mutableStateOf(if (user.role == "user") BlueLight else BlueDark)}


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
                .border(3.dp, borderColor, RoundedCornerShape(20.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(50.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = user.profilePic,
                    contentDescription = user.email,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
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
            }
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {


                    Text(
                         "${user.email}",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.TopEnd)
                    ) {
                        IconButton(
                            onClick = { expanded = !expanded },
                            modifier = Modifier.padding(end = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreHoriz,
                                contentDescription = "More"
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            properties = PopupProperties(
                                dismissOnClickOutside = true,
                            ),
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (user.role == "driver") "Retrocedi" else "Promuovi") },
                                onClick = {
                                    borderColor = if (user.role == "driver") BlueLight else BlueDark
                                    viewModel.updateUserRole(user)

                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "${user.name} ${user.surname}",
                    modifier = Modifier.padding(start = 15.dp, bottom = 10.dp)
                )
            }
        }

    }
}

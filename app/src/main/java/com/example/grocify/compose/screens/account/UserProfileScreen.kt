package com.example.grocify.compose.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.grocify.model.User
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight
import com.example.grocify.ui.theme.ExtraLightGray
import com.example.grocify.viewmodels.UserProfileViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel = viewModel(),
    onBackClick: () -> Unit
){

    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getUserProfile()
    }

    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var nameChange by rememberSaveable { mutableStateOf(false) }
    var surnameChange by rememberSaveable { mutableStateOf(false) }
    var passwordChange by rememberSaveable { mutableStateOf(false) }

    var showPassword by rememberSaveable { mutableStateOf(false) }
    var modifyButtonState by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = uiState.value.error, key2 = uiState.value.isSuccessful){
        if(uiState.value.error != null){
            scope.launch {
                snackbarHostState
                    .showSnackbar(
                        message = uiState.value.error!!,
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
            }
        }

        if(uiState.value.isSuccessful){
            scope.launch {
                snackbarHostState
                    .showSnackbar(
                        message = "Modifica dei dati avvenuta con successo",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
            }
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                title = {
                    Text(
                        text = "Il tuo profilo",
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState){
                if(uiState.value.error.isNullOrEmpty())
                    Snackbar(
                        snackbarData = it,
                        containerColor = BlueLight,
                        contentColor = Color.White,
                        actionColor = Color.Black
                    )
                else
                    Snackbar(
                        snackbarData = it,
                        containerColor = ExtraLightGray,
                        contentColor = Color.Black,
                        actionColor = Color.Black
                    )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            SubcomposeAsyncImage(
                model = uiState.value.user.profilePic,
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .background(Color.White)
                    .padding(vertical = 40.dp)
            ) {
                OutlinedTextField(
                    value = if (nameChange) name else uiState.value.user.name.toString(),
                    singleLine = true,
                    label = { Text(text = "Nome", color = Color.Black) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = {
                        name = it
                        nameChange = true
                        modifyButtonState = true
                    },

                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueLight,
                        unfocusedBorderColor = Color(0, 0, 0, 50)
                    ),
                    modifier = Modifier
                        .width(325.dp)
                        .padding(0.dp, 0.dp, 0.dp, 5.dp),
                    textStyle = TextStyle(
                        color = Color.Black
                    ),
                    isError = !uiState.value.isNameValid,
                    supportingText = {
                        if (!uiState.value.isNameValid)
                            Text(
                                text = uiState.value.nameError,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Red,
                                textAlign = TextAlign.Start
                            )
                    },
                    trailingIcon = {
                        if (!uiState.value.isNameValid)
                            Icon(
                                Icons.Filled.Error,
                                "error",
                                tint = MaterialTheme.colorScheme.error
                            )
                    }
                )

                OutlinedTextField(
                    value = if (surnameChange) surname else uiState.value.user.surname.toString(),
                    singleLine = true,
                    label = { Text(text = "Cognome", color = Color.Black) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = {
                        surname = it
                        surnameChange = true
                        modifyButtonState = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueLight,
                        unfocusedBorderColor = Color(0, 0, 0, 50)
                    ),
                    modifier = Modifier
                        .width(325.dp)
                        .padding(0.dp, 10.dp, 0.dp, 5.dp),
                    textStyle = TextStyle(
                        color = Color.Black
                    ),
                    isError = !uiState.value.isSurnameValid,
                    supportingText = {
                        if (!uiState.value.isSurnameValid)
                            Text(
                                text = uiState.value.surnameError,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Red,
                                textAlign = TextAlign.Start
                            )
                    },
                    trailingIcon = {
                        if (!uiState.value.isSurnameValid)
                            Icon(
                                Icons.Filled.Error,
                                "error",
                                tint = MaterialTheme.colorScheme.error
                            )
                    }
                )

                OutlinedTextField(
                    value = uiState.value.user.email.toString(),
                    singleLine = true,
                    label = { Text(text = "Email", color = Color.Black) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0, 0, 0, 50),
                        focusedContainerColor = ExtraLightGray,
                        unfocusedBorderColor = Color(0, 0, 0, 50),
                        unfocusedContainerColor = ExtraLightGray
                    ),
                    modifier = Modifier
                        .width(325.dp)
                        .padding(0.dp, 10.dp, 0.dp, 5.dp),
                    textStyle = TextStyle(
                        color = Color.Black
                    )
                )

                OutlinedTextField(
                    value = if (passwordChange) password else uiState.value.user.password.toString(),
                    singleLine = true,
                    label = { Text(text = "Password", color = Color.Black) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = {
                        password = it
                        passwordChange = true
                        modifyButtonState = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueLight,
                        unfocusedBorderColor = Color(0, 0, 0, 50)
                    ),
                    modifier = Modifier
                        .width(325.dp)
                        .padding(0.dp, 10.dp, 0.dp, 5.dp),
                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    textStyle = TextStyle(
                        color = Color.Black
                    ),
                    isError = !uiState.value.isPasswordValid,
                    supportingText = {
                        if (!uiState.value.isPasswordValid)
                            Text(
                                text = uiState.value.passwordError,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Red,
                                textAlign = TextAlign.Start
                            )
                    },
                    trailingIcon = {
                        if (showPassword) {
                            IconButton(onClick = { showPassword = false }) {
                                Icon(
                                    imageVector = Icons.Filled.Visibility,
                                    contentDescription = "hide_password"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { showPassword = true }) {
                                Icon(
                                    imageVector = Icons.Filled.VisibilityOff,
                                    contentDescription = "hide_password"
                                )
                            }
                        }
                    }
                )

                Button(
                    onClick = {
                        viewModel.updateUserProfile(
                            User(
                                uid = uiState.value.user.uid,
                                name = if(nameChange) name else uiState.value.user.name,
                                surname = if(surnameChange) surname else uiState.value.user.surname,
                                email = uiState.value.user.email,
                                password = if(passwordChange) password else uiState.value.user.password ,
                                profilePic = uiState.value.user.profilePic,
                                role = uiState.value.user.role
                            )
                        )
                    },
                    shape = RoundedCornerShape(25),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = BlueDark
                    ),
                    enabled = modifyButtonState,
                    modifier = Modifier.width(325.dp)
                ) {
                    Text(
                        "Modifica",
                        color = Color.White,
                        modifier = Modifier.padding(10.dp),
                        fontSize = 17.sp,
                    )
                }
            }
        }
    }
}
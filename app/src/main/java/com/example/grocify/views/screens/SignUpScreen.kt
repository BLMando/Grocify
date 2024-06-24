package com.example.grocify.views.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight
import com.example.grocify.ui.theme.ExtraLightGray
import com.example.grocify.viewmodels.SignUpViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = viewModel(),
    onGoSignIn: () -> Unit,
    onSignUpSuccess: () -> Unit
) {

    val signUpUiState by viewModel.signUpState.collectAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showPasswordConfirm by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = signUpUiState.signUpError){
        signUpUiState.signUpError?.let { error ->
            scope.launch {
                snackbarHostState
                    .showSnackbar(
                        message = error,
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
            }
        }
    }

    LaunchedEffect(key1 = signUpUiState.isSuccessful) {
        if(signUpUiState.isSuccessful)
            onSignUpSuccess()
    }


    val colorStops = arrayOf(
        0.2f to BlueLight,
        1f to BlueDark
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState){
                Snackbar(
                    snackbarData = it,
                    containerColor = ExtraLightGray,
                    contentColor = Color.Black,
                    actionColor = Color.Black
                )
            }
        }
    ) { contentPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .shadow(1.dp)
                    .background(Brush.horizontalGradient(colorStops = colorStops)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Unisciti a Grocify",
                    style = TextStyle(
                        fontSize = 35.sp,
                        fontWeight = FontWeight(700),
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.size(30.dp))

            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.width(325.dp)
            ){
                OutlinedTextField(
                    value = name,
                    singleLine = true,
                    label = { Text(text = "Nome", color = Color.Black) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = {
                        name = it
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueLight,
                        unfocusedBorderColor = Color(0, 0, 0, 50)
                    ),
                    modifier = Modifier
                        .width(155.dp)
                        .padding(0.dp, 10.dp, 0.dp, 5.dp),
                    textStyle = TextStyle(
                        color = Color.Black
                    ),
                    isError = !signUpUiState.isNameValid,
                    supportingText = {
                        if(!signUpUiState.isNameValid)
                            Text(
                                text = signUpUiState.nameError,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Red,
                                textAlign = TextAlign.Start
                            )
                    },
                    trailingIcon = {
                        if (!signUpUiState.isNameValid)
                            Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
                    }
                )

                OutlinedTextField(
                    value = surname,
                    singleLine = true,
                    label = { Text(text = "Cognome", color = Color.Black) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = {
                        surname = it
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueLight,
                        unfocusedBorderColor = Color(0, 0, 0, 50)
                    ),
                    modifier = Modifier
                        .width(155.dp)
                        .padding(0.dp, 10.dp, 0.dp, 5.dp),
                    textStyle = TextStyle(
                        color = Color.Black
                    ),
                    isError = !signUpUiState.isSurnameValid,
                    supportingText = {
                        if(!signUpUiState.isSurnameValid)
                            Text(
                                text = signUpUiState.surnameError,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Red,
                                textAlign = TextAlign.Start
                            )
                    },
                    trailingIcon = {
                        if (!signUpUiState.isSurnameValid)
                            Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
                    }
                )
            }

            OutlinedTextField(
                value = email,
                singleLine = true,
                label = { Text(text = "Email", color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = {
                    email = it
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
                isError = !signUpUiState.isEmailValid,
                supportingText = {
                    if(!signUpUiState.isEmailValid)
                        Text(
                            text = signUpUiState.emailError,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Red,
                            textAlign = TextAlign.Start
                        )
                },
                trailingIcon = {
                    if (!signUpUiState.isEmailValid)
                        Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colorScheme.error)
                }

            )

            OutlinedTextField(
                value = password,
                singleLine = true,
                label = { Text(text = "Password", color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = {
                    password = it
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
                isError = !signUpUiState.isPasswordValid,
                supportingText = {
                    if(!signUpUiState.isPasswordValid)
                        Text(
                            text = signUpUiState.passwordError,
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
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    }
                }
            )

            OutlinedTextField(
                value = confirmPassword,
                singleLine = true,
                label = { Text(text = "Conferma password", color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = {
                    confirmPassword = it
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueLight,
                    unfocusedBorderColor = Color(0, 0, 0, 50)
                ),
                modifier = Modifier
                    .width(325.dp)
                    .padding(0.dp, 10.dp, 0.dp,0.dp),
                visualTransformation = if (showPasswordConfirm) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                textStyle = TextStyle(
                    color = Color.Black
                ),
                isError = !signUpUiState.isConfirmPasswordValid,
                supportingText = {
                    if(!signUpUiState.isConfirmPasswordValid)
                        Text(
                            text = signUpUiState.confirmPasswordError,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Red,
                            textAlign = TextAlign.Start
                        )
                },
                trailingIcon = {
                    if (showPasswordConfirm) {
                        IconButton(onClick = { showPasswordConfirm = false }) {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { showPasswordConfirm = true }) {
                            Icon(
                                imageVector = Icons.Filled.VisibilityOff,
                                contentDescription = "hide_password"
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.size(20.dp))

            Button(
                onClick = {
                    viewModel.signUp(name,surname,email,password,confirmPassword)
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = BlueDark
                ),
                modifier = Modifier
                    .width(325.dp)
            ) {
                Text(
                    "Registrati",
                    color = Color.White,
                    modifier = Modifier.padding(10.dp),
                    fontSize = 17.sp,
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            TextButton(
                onClick = { onGoSignIn() }
            ){
                Text(
                    text = "Hai già un account? Accedi",
                    fontSize = 14.sp
                )
            }
        }
    }
}

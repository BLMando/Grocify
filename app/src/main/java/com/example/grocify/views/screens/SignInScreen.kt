package com.example.grocify.views.screens


import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.example.grocify.R
import com.example.grocify.views.theme.BlueDark
import com.example.grocify.views.theme.BlueLight
import com.example.grocify.views.theme.ExtraLightGray
import com.example.grocify.viewmodels.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun SignInScreen(
    context: Activity,
    onGoSignUp: () -> Unit,
    navController: NavController,
) {

   /**
     * Instantiate the SignInViewModel passing parameters through the factory method
     * @param context Activity
     * @param OneTapClient The Google Sign-In client
    */
    val viewModel: SignInViewModel = viewModel(factory = viewModelFactory {
        addInitializer(SignInViewModel::class) {
            SignInViewModel(context.application, Identity.getSignInClient(context))
        }
    })

    val googleUiState = viewModel.googleSignInState.collectAsState()
    val signInUiState = viewModel.signInState.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    /**
     * Creates and remembers a launcher to handle the result of an activity
     * started for result, specifically for activities that return
     * results via an intent sender (e.g., authentication flows).
     */
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if(result.resultCode == Activity.RESULT_OK){
                CoroutineScope(Dispatchers.Main).launch {
                    val signInResult = viewModel.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    viewModel.onSignInResult(signInResult)
                }
            }
        }
    )

    /**
     * Effect that handles the sign-in result and navigates to the appropriate route
     */
    LaunchedEffect(key1 = Triple(Unit, googleUiState.value.isSignInSuccessful, signInUiState.value.isSuccessful)) {
        if (viewModel.isUserSignedIn() || googleUiState.value.isSignInSuccessful || signInUiState.value.isSuccessful) {
            val role = viewModel.getUserRole()
            handlePostSignInRoute(role, navController)
        }
        if (googleUiState.value.isSignInSuccessful) {
            viewModel.resetGoogleState()
        }
    }


    /**
     * Effect that handles the sign-in error and displays a snackbar with the error message
     */
    LaunchedEffect(key1 = Pair(googleUiState.value.signInError, signInUiState.value.signInError)) {
        val error = googleUiState.value.signInError ?: signInUiState.value.signInError
        error?.let {
            scope.launch {
                snackbarHostState
                    .showSnackbar(
                        message = it,
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ){
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
            Image(
                painterResource(id = R.drawable.icon),
                contentDescription = "Cart button icon",
                modifier = Modifier.size(150.dp)
            )

            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight(700),
                            color = Color.Black
                        )
                    ) {
                        append("Bentornato su ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight(700),
                            color = BlueLight,
                            shadow = Shadow(
                                color = Color.Black, offset = Offset(5.5f, 6.0f), blurRadius = 3f
                            ),
                            letterSpacing = 2.sp
                        )
                    ) {
                        append("Grocify")
                    }
                }
            )

            Spacer(modifier = Modifier.size(15.dp))

            if(viewModel.isUserSignedIn()) {
                Text(
                    text = "Accesso in corso...",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color(0xFF030303)
                    )
                )
                Spacer(modifier = Modifier.size(20.dp))
                CircularProgressIndicator(
                    color = BlueLight,
                    strokeWidth = 3.dp,
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                )
            }else{
                Text(
                    text = "Accedi al tuo account",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF030303)
                    )
                )

                Spacer(modifier = Modifier.size(20.dp))

                OutlinedTextField(
                    value = email,
                    label = { Text(text = "Email", color = Color.Black) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    onValueChange = {
                        email = it
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueLight,
                        unfocusedBorderColor = Color(0, 0, 0, 50)
                    ),
                    modifier = Modifier
                        .width(325.dp)
                        .padding(0.dp, 10.dp, 0.dp, 10.dp)
                        .testTag("EmailInputField"),
                    textStyle = TextStyle(
                        color = Color.Black
                    ),
                    isError = !signInUiState.value.isEmailValid,
                    supportingText = {
                        if (!signInUiState.value.isEmailValid)
                            Text(
                                text = signInUiState.value.emailError,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Red,
                                textAlign = TextAlign.Start
                            )
                    },
                    trailingIcon = {
                        if (!signInUiState.value.isEmailValid)
                            Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                    }
                )

                OutlinedTextField(
                    value = password,
                    label = { Text(text = "Password", color = Color.Black) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = {
                        password = it
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueLight,
                        unfocusedBorderColor = Color(0, 0, 0, 50)
                    ),
                    modifier = Modifier
                        .width(325.dp)
                        .padding(0.dp, 10.dp, 0.dp, 10.dp)
                        .testTag("PasswordInputField"),
                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    isError = !signInUiState.value.isPasswordValid,
                    supportingText = {
                        if (!signInUiState.value.isPasswordValid)
                            Text(
                                text = signInUiState.value.passwordError,
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
                                    contentDescription = "Toggle password visibility"
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
                    },
                    textStyle = TextStyle(
                        color = Color.Black
                    )
                )

                Button(
                    onClick = { viewModel.signInWithCredentials(email, password) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = BlueDark
                    ),
                    modifier = Modifier
                        .width(325.dp)
                        .padding(0.dp, 20.dp, 0.dp, 0.dp)
                        .testTag("SignInButton"),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        "Accedi",
                        color = Color.White,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 17.sp,
                    )
                }

                Spacer(modifier = Modifier.size(20.dp))

                TextButton(
                    onClick = { onGoSignUp() },
                    modifier = Modifier.testTag("GoSignUpButton")
                ) {
                    Text(
                        text = "Non hai un account? Registrati ora!",
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "Oppure",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.size(15.dp))


                OutlinedButton(
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            val signInIntentSender = viewModel.signInWithGoogle()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch
                                ).build()
                            )
                        }},
                    modifier = Modifier
                        .width(325.dp)
                        .testTag("GoogleSignInButton"),
                ) {
                    Image(
                        painterResource(id = R.drawable.google_logo),
                        contentDescription = "Cart button icon",
                        modifier = Modifier.size(30.dp)
                    )

                    Text(
                        text = "Continua con Google",
                        modifier = Modifier.padding(start = 10.dp),
                        color = Color.Black,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}

fun handlePostSignInRoute(userRole: String?,navController: NavController) {
    when(userRole){
        "user" -> navController.navigate("user")
        "admin" -> navController.navigate("admin")
        "driver" -> navController.navigate("driver")
    }
}





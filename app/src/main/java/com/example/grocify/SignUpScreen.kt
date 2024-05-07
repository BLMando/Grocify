package com.example.grocify


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight

@Composable
fun SignUpScreen(){

    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showPasswordConfirm by rememberSaveable { mutableStateOf(false) }

    val colorStops = arrayOf(
        0.2f to BlueLight,
        1f to BlueDark
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
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
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color(0, 0, 0, 50)
                ),
                modifier = Modifier
                    .width(150.dp)
                    .padding(0.dp, 10.dp, 0.dp, 10.dp),
                textStyle = TextStyle(
                    color = Color.Black
                )
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
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color(0, 0, 0, 50)
                ),
                modifier = Modifier
                    .width(150.dp)
                    .padding(0.dp, 10.dp, 0.dp, 10.dp),
                textStyle = TextStyle(
                    color = Color.Black
                )
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
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color(0, 0, 0, 50)
            ),
            modifier = Modifier
                .width(325.dp)
                .padding(0.dp, 10.dp, 0.dp, 10.dp),
            textStyle = TextStyle(
                color = Color.Black
            )

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
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color(0, 0, 0, 50)
            ),
            modifier = Modifier
                .width(325.dp)
                .padding(0.dp, 10.dp, 0.dp, 10.dp),
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()

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
            },
            textStyle = TextStyle(
                color = Color.Black
            )
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
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color(0, 0, 0, 50)
            ),
            modifier = Modifier
                .width(325.dp)
                .padding(0.dp, 10.dp, 0.dp, 10.dp),
            visualTransformation = if (showPasswordConfirm) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()

            },
            trailingIcon = {
                if (showPasswordConfirm) {
                    IconButton(onClick = { showPasswordConfirm = false }) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = "hide_password"
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
            },
            textStyle = TextStyle(
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.size(50.dp))

        Button(
            onClick = { /*TODO*/ },
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

        Spacer(modifier = Modifier.size(30.dp))

        Text(
            text = "Hai già un account? Accedi",
            fontSize = 14.sp,
            modifier = Modifier.clickable {
                    //Go to SignInScreen
            }
        )
    }
}

package com.example.grocify

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight


@Composable
fun SignInScreen(){

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painterResource(id = R.drawable.icon),
            contentDescription ="Cart button icon",
            modifier = Modifier.size(150.dp)
        )

        val offset = Offset(5.5f, 6.0f)
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
                            color = Color.Black, offset = offset, blurRadius = 3f
                        ),
                        letterSpacing = 2.sp
                    )
                ) {
                    append("Grocify")
                }
            }
        )

        Spacer(modifier = Modifier.size(15.dp))

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
            label = { Text(text = "Email",color = Color.Black) },
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
                .padding(0.dp, 10.dp, 0.dp, 10.dp),
            textStyle = TextStyle(
                color = Color.Black
            )
        )

        OutlinedTextField(
            value = password,
            label = { Text(text = "Password",color = Color.Black) },
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

        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = BlueDark
            ),
            modifier = Modifier
                .width(325.dp)
                .padding(0.dp, 25.dp, 0.dp, 0.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                "Accedi",
                color = Color.White,
                modifier = Modifier.padding(8.dp),
                fontSize = 17.sp,
            )
        }

        Spacer(modifier = Modifier.size(30.dp))

        Text(text = "Oppure")

        OutlinedButton(
            onClick = {},
            modifier = Modifier
                .width(325.dp)
                .padding(0.dp, 30.dp, 0.dp, 0.dp)
        ) {
            Image(
                painterResource(id = R.drawable.google_logo),
                contentDescription ="Cart button icon",
                modifier = Modifier.size(30.dp))

            Text(
                text = "Continua con Google",
                modifier =  Modifier.padding(start = 10.dp),
                color = Color.Black,
                fontSize = 17.sp
            )
        }

        Spacer(modifier = Modifier.size(30.dp))

        Text(
            text = "Non hai un account? Registrati ora!",
            fontSize = 14.sp,
            modifier = Modifier.clickable {
            }
        )
    }
}



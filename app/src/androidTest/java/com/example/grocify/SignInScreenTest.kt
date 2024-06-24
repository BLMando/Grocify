package com.example.grocify

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.grocify.views.screens.SignInScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInTestScreen {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testSignInScreen() {
        composeTestRule.setContent {
            SignInScreen(
                context = LocalContext.current as Activity,
                onGoSignUp = {/*TODO*/},
                navController = rememberNavController()
            )
        }

        // Attende che la UI sia stabile
        composeTestRule.waitForIdle()

        // Simula l'inserimento di un'email e una password e controllo se la password viene mostrata oscurata
        composeTestRule.onNodeWithTag("EmailInputField").performTextInput("test@example.com")

        composeTestRule.onNodeWithTag("PasswordInputField").performTextInput("password")
        composeTestRule.onNodeWithTag("PasswordInputField", useUnmergedTree = true).assertTextEquals("••••••••")

        // Simula in click sull'icona di "Mostra/Nascondi password" e controlla se la password viene mostrata in chiaro
        composeTestRule.onNodeWithContentDescription("Toggle password visibility").performClick()
        composeTestRule.onNodeWithTag("PasswordInputField", useUnmergedTree = true).assertTextEquals("password")


        // Simula in click sul pulsante "Accedi"
        composeTestRule.onNodeWithTag("SignInButton").performClick()

        // Simula in click sul pulsante "Non hai un account? Regitrati!"
        composeTestRule.onNodeWithTag("GoSignUpButton").performClick()

        // Simula in click sul pulsante "Continua con Google"
        composeTestRule.onNodeWithTag("GoogleSignInButton").performClick()
    }
}
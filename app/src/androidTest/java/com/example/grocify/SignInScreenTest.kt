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


/**
 * Class to test the [SignInScreen].
 */
@RunWith(AndroidJUnit4::class)
class SignInTestScreen {

    /**
     * Rule to use Compose test functions.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * Test function to test the [SignInScreen].
     * It checks if the UI elements are displayed correctly and if the password is masked correctly.
     */
    @Test
    fun testSignInScreen() {
        /**
         * Set the content view to the [SignInScreen] with the provided parameters.
         */
        composeTestRule.setContent {
            SignInScreen(
                context = LocalContext.current as Activity,
                onGoSignUp = {/*TODO*/},
                navController = rememberNavController()
            )
        }

        // Wait for the layout to be inflated
        composeTestRule.waitForIdle()

        // Simulates email and password typing in the respective input fields
        composeTestRule.onNodeWithTag("EmailInputField").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("PasswordInputField").performTextInput("password")
        // Simulates password masking
        composeTestRule.onNodeWithTag("PasswordInputField", useUnmergedTree = true).assertTextEquals("••••••••")

        // Simulates toggling the password visibility and if password is masked or not
        composeTestRule.onNodeWithContentDescription("Toggle password visibility").performClick()
        composeTestRule.onNodeWithTag("PasswordInputField", useUnmergedTree = true).assertTextEquals("password")


        // Simulates a click on the "Sign In" button
        composeTestRule.onNodeWithTag("SignInButton").performClick()

        // Simulates a click on the "Non hai un account? Registrati!" button
        composeTestRule.onNodeWithTag("GoSignUpButton").performClick()

       // Simulates a click on the "Continua con Google" button
        composeTestRule.onNodeWithTag("GoogleSignInButton").performClick()
    }
}
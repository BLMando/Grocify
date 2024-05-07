package com.example.grocify


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.grocify.ui.theme.GrocifyTheme
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private var oneTapClient: SignInClient? = null
    private lateinit var signInRequest: BeginSignInRequest
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        setContent {
            GrocifyTheme {
                SignInScreen()
            }
        }

        auth = Firebase.auth
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

    }

    fun signInWithGoogle() {
        CoroutineScope(Dispatchers.Main).launch {
            signingGoogle()
        }
    }

    fun signOut() {
        auth.signOut()
        Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show()
    }

    private suspend fun signingGoogle() {
        val result = oneTapClient?.beginSignIn(signInRequest)?.await()
        val intentSenderRequest = IntentSenderRequest.Builder(result!!.pendingIntent).build()
        activityResultLauncher.launch(intentSenderRequest)
    }

    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val googleCredential = oneTapClient!!.getSignInCredentialFromIntent(result.data)
                    val idToken = googleCredential.googleIdToken

                    if (idToken != null) {
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT)
                                        .show()
                                    updateUI()
                                }
                            }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }

        }

    /*override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null){
            updateUI()
        }
    }*/

    private fun updateUI() {
        val user = Firebase.auth.currentUser
        user?.let {
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl
            val emailVerified = it.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            val uid = it.uid

            val newUser = hashMapOf(
                "name" to name,
                "email" to email
            )

            db.collection("users")
                .add(newUser)
                .addOnSuccessListener { documentReference ->
                    Log.d("firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("firestore", "Error adding document", e)
                }

            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("firestore", "${document.id} => ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("firestore", "Error getting documents.", exception)
                }
        }
    }
}

/********* UI SIGN-IN / SIGN - UP *********/

//@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    GrocifyTheme {
        SignUpScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    GrocifyTheme {
        SignInScreen()
    }
}

/********* UI DEL DRIVER *********/

//@Preview(showBackground = true)
@Composable
fun HomeDriverScreenPreview() {
    GrocifyTheme {
        HomeDriverScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun OrderDetailsScreenPreview() {
    GrocifyTheme {
        OrderDetailsScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    GrocifyTheme {
        //MapScreen()
    }
}

/********* UI DELL'UTENTE *********/

//@Preview(showBackground = true)
@Composable
fun HomeUserScreenPreview() {
    GrocifyTheme {
        HomeUserScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun CategoryItemsScreenPreview() {
    GrocifyTheme {
       CategoryItemsScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun ScanProductScreenPreview() {
    GrocifyTheme {
        ScanProductScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    GrocifyTheme {
        CartScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
    GrocifyTheme {
        CheckoutScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun OrderSuccessScreenPreview() {
    GrocifyTheme {
        OrderSuccessScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun TrackOrderScreenPreview() {
    GrocifyTheme {
        TrackOrderScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    GrocifyTheme {
        UserProfileScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun UserOptionsScreenPreview() {
    GrocifyTheme {
        UserOptionsScreen(
            topBarText = "Indirizzi di spedizione",
            titleFirst = "Indirizzo in uso",
            titleSecond = "I tuoi indirizzi"
        )
    }
}

//@Preview(showBackground = true)
@Composable
fun GiftProductScreenPreview() {
    GrocifyTheme {
       GiftProductScreen()
    }
}

/********* UI ADMIN *********/
//@Preview(showBackground = true)
@Composable
fun HomeAdminScreenPreview() {
    GrocifyTheme {
        HomeAdminScreen()
    }
}

//@Preview(showBackground = true)
@Composable
fun SaleGiftScreenPreview() {
    GrocifyTheme {
        SaleGiftScreen(isSaleContent = false)
    }
}




















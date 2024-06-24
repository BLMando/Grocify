package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.remote.RetrofitObject
import com.example.grocify.data.remote.SentimentAnalysis.SentimentData
import com.example.grocify.states.HomeAdminUiState
import com.example.grocify.model.Review
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.CancellationException

/**
 * ViewModel class for HomeAdminScreen
 * @param application - Application context
 */
@Suppress("UNCHECKED_CAST")
class HomeAdminViewModel(application: Application, private val mOneTapClient: SignInClient): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeAdminUiState())
    val uiState: StateFlow<HomeAdminUiState> = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    /**
     * Function to get the 10 most bought products
     * It iterates over all the orders and counts the quantity of each product
     * Then it sorts the map by value in descending order and takes the first 10 entries
     * @return List of 10 most bought products
     */
    fun getTop10MostBoughtProducts(){
        val productQuantityMap = mutableMapOf<String, Int>()

        viewModelScope.launch {
            db.collection("orders")
                .get()
                .addOnSuccessListener { orders ->
                    for (order in orders.documents) {
                        val cart: List<HashMap<String, Any>> = order.get("cart") as List<HashMap<String, Any>>

                        for (product in cart) {
                            val name = product["name"] as String
                            val unit = (product["units"] as Long).toInt()
                            productQuantityMap[name] = productQuantityMap.getOrDefault(name, 0) + unit
                        }
                    }
                    val sortedItems = productQuantityMap.toList().sortedByDescending { (_, value) -> value }.take(10)

                    _uiState.update { currentState ->
                        currentState.copy(
                            top10Products = sortedItems
                        )
                    }
                }
        }
    }

    /**
     * Function to get the 10 most bought categories
     * It iterates over all the orders and counts the quantity of each category
     * Then it sorts the map by value in descending order and takes the first 10 entries
     * @return List of 10 most bought categories
     */
    fun getTop10MostBoughtCategories(){

        val categoriesQuantityMap = mutableMapOf<String, Int>()

        viewModelScope.launch {
            db.collection("orders")
                .get()
                .addOnSuccessListener { orders ->
                    for (order in orders.documents) {
                        val cart: List<HashMap<String, Any>> = order.get("cart") as List<HashMap<String, Any>>

                        for (product in cart) {
                            val productId = product["id"] as String
                            db.collection("prodotti")
                                .document(productId)
                                .get()
                                .addOnSuccessListener { prod ->
                                    val category = prod.get("categoria") as String
                                    db.collection("categories")
                                        .document(category)
                                        .get()
                                        .addOnSuccessListener { cat ->
                                            val categoryName = cat.get("nome") as String
                                            categoriesQuantityMap[categoryName] = categoriesQuantityMap.getOrDefault(categoryName, 0) + 1

                                            val sortedCategory = categoriesQuantityMap.toList().sortedByDescending { (_, value) -> value }.take(10)

                                            _uiState.update { currentState ->
                                                currentState.copy(
                                                    top10Categories = sortedCategory
                                                )
                                            }
                                        }
                                }
                        }
                    }
                }
        }
    }


    /**
     * Function to get the average monthly orders
     * It iterates over all the orders and counts the number of orders for each month
     * Then it sorts the map by key (month name) in alphabetical order
     * @return List of average monthly orders
     */
    fun getAverageMonthlyOrders(){

        val ordersQuantityMap = mutableMapOf<String, Int>()

        viewModelScope.launch {
            db.collection("orders")
                .get()
                .addOnSuccessListener { orders ->
                    for (order in orders.documents) {
                        val month = LocalDate.parse(order.get("date") as String, DateTimeFormatter.ofPattern("dd/MM/yyyy")).monthValue
                        var monthName = ""
                        when(month){
                            1 -> monthName = "Gennaio"
                            2 -> monthName = "Febbraio"
                            3 -> monthName = "Marzo"
                            4 -> monthName = "Aprile"
                            5 -> monthName = "Maggio"
                            6 -> monthName = "Giugno"
                            7 -> monthName = "Luglio"
                            8 -> monthName = "Agosto"
                            9 -> monthName = "Settembre"
                            10 -> monthName = "Ottobre"
                            11 -> monthName = "Novembre"
                            12 -> monthName = "Dicembre"
                        }
                        ordersQuantityMap[monthName] = ordersQuantityMap.getOrDefault(monthName, 0) + 1
                    }
                    _uiState.update {currentState ->
                        currentState.copy(
                            averageMonthlyOrders = ordersQuantityMap.toList()
                        )
                    }
                }
        }
    }

    /**
     * Function to get the average monthly users expense
     * It iterates over all the orders and sums the expense for each month
     * Then it sorts the map by key (month name) in alphabetical order
     * @return List of average monthly users expense
     */
    fun getAverageMonthlyUsersExpense(){

        val ordersQuantityMap = mutableMapOf<String, Int>()

        viewModelScope.launch {
            db.collection("orders")
                .get()
                .addOnSuccessListener { orders ->
                    for (order in orders.documents) {
                        //per ogni ordine prendo la il mese in numero e ottengo il nome del mese
                        val month = LocalDate.parse(order.get("date") as String, DateTimeFormatter.ofPattern("dd/MM/yyyy")).monthValue
                        var monthName = ""
                        when(month){
                            1 -> monthName = "Gennaio"
                            2 -> monthName = "Febbraio"
                            3 -> monthName = "Marzo"
                            4 -> monthName = "Aprile"
                            5 -> monthName = "Maggio"
                            6 -> monthName = "Giugno"
                            7 -> monthName = "Luglio"
                            8 -> monthName = "Agosto"
                            9 -> monthName = "Settembre"
                            10 -> monthName = "Ottobre"
                            11 -> monthName = "Novembre"
                            12 -> monthName = "Dicembre"
                        }
                        //ricavo la spesa di quell'ordine
                        val expense = (order.get("totalPrice") as Double).toInt()

                        //aggiungo il nome del mese al map e se esiste aggiorno il valore totale della spesa
                        ordersQuantityMap[monthName] = ordersQuantityMap.getOrDefault(monthName, 0) + expense
                    }
                    _uiState.update {currentState ->
                        currentState.copy(
                            averageMonthlyUsersExpense = ordersQuantityMap.toList()
                        )
                    }
                }

        }
    }

    /**
     * Function to get all the reviews
     * It retrieves all the reviews from the database and orders them by date in descending order
     * @return List of all the reviews
     */
    fun getAllReviews(){
        viewModelScope.launch {
            db.collection("orders_reviews")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { reviews ->
                    val reviewsList:MutableList<Review> = mutableListOf()
                    for (review in reviews) {
                        review.toObject(Review::class.java).let { reviewItem ->
                            reviewsList.add(reviewItem)
                        }
                    }
                    _uiState.update { currentState ->
                        currentState.copy(
                            reviews = reviewsList
                        )
                    }
                }
        }
    }

    /**
     * Function that calls the sentiment analysis function and store results
     * @param text - The review text to be analyzed
     * @param index - The index of the review in the list of reviews
     * @return List of sentiment data for the given review text
     */
    fun sentimentAnalysis(text:String,index:Int){
        viewModelScope.launch {
            val response = getSentimentAnalysis(text)

            val responseHashMap = hashMapOf(
                index to response
            )
            _uiState.update { currentState ->
                currentState.copy(
                    analysisIsLoaded = true,
                    sentimentAnalysisResult = addToSentimentAnalysisResult(_uiState.value.sentimentAnalysisResult,responseHashMap)
                )
            }
        }
    }

    /**
     * Function to add the sentiment analysis result to the existing result
     */
    private fun addToSentimentAnalysisResult(original: HashMap<Int,List<SentimentData>>, newEntries: HashMap<Int, List<SentimentData>>): HashMap<Int,List<SentimentData>> {

        val mergedResult = HashMap(original)

        for ((key, value) in newEntries) {
            mergedResult[key] = value
        }

        return mergedResult
    }

    /**
     * Function to perform the sentiment analysis on a given review text
     * It makes a network call to the sentiment analysis API and returns the result
     * @param text - The review text to be analyzed
     * @return List of sentiment data for the given review text
     */
     private suspend fun getSentimentAnalysis(text:String): List<SentimentData> = withContext(Dispatchers.Main) {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            try {
                val response = RetrofitObject.sentimentAnalysisService.getSentimentAnalysis(text)
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!.chart_data
                } else {
                    Log.d("AdminViewModel", "Not successful")
                    null
                }
            } catch (e: IOException) {
                Log.d("AdminViewModel", "No internet connection")
                null
            } catch (e: HttpException) {
                Log.d("AdminViewModel", "Unexpected response")
                null
            }
        }
         deferred.await()!!
    }

    /**
     * Function to get the name of the currently signed-in user
     */
    fun getSignedInUserName() {
        val currentUser = auth.currentUser?.uid
        viewModelScope.launch {
            db.collection("users")
                .whereEqualTo("uid", currentUser)
                .get()
                .addOnSuccessListener { documents ->
                    //prendo il nome dell'utente attualmente loggato (admin)
                    for (document in documents) {
                        _uiState.update { currentState ->
                            currentState.copy(currentUserName = document.data["name"].toString().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
        }
    }

    /**
     * Function to sign out the current user
     * It signs out the user from both the authentication and OneTap client
     */
    fun signOut(){
        viewModelScope.launch {
            try {
                mOneTapClient.signOut()
                auth.signOut()
            }catch(e: Exception){
                e.printStackTrace()
                if(e is CancellationException) throw  e
            }
        }
    }

    fun resetSentimentAnalysisResult(index: Int){
        _uiState.update { currentState ->
            currentState.copy(
                sentimentAnalysisResult = _uiState.value.sentimentAnalysisResult.filterKeys { it != index } as HashMap<Int,List<SentimentData>>
            )
        }
    }

    fun resetAnalysisIsLoaded(){
        _uiState.update { currentState ->
            currentState.copy(
                analysisIsLoaded = false
            )
        }
    }
}
package com.example.grocify.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.api.RetrofitObject
import com.example.grocify.api.SentimentAnalysis.SentimentData
import com.example.grocify.data.HomeAdminUiState
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

@Suppress("UNCHECKED_CAST")
class HomeAdminViewModel(application: Application, private val mOneTapClient: SignInClient): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeAdminUiState())
    val uiState: StateFlow<HomeAdminUiState> = _uiState.asStateFlow()

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    //GESTIONE STATISTICHE
    fun getTop10MostBoughtProducts(){
        val productQuantityMap = mutableMapOf<String, Int>()

        viewModelScope.launch {
            db.collection("orders")
                .get()
                .addOnSuccessListener { orders ->
                    for (order in orders.documents) {
                        //per ogni ordine prendo il carrello
                        val cart: List<HashMap<String, Any>> = order.get("cart") as List<HashMap<String, Any>>

                        for (product in cart) {
                            //per ogni prodotto nel carrello prendo il nome e la quantità acquistata
                            val name = product["name"] as String
                            val unit = (product["units"] as Long).toInt()

                            //aggiungo il nome del prodotto al map e se esiste aggiorno il valore
                            productQuantityMap[name] = productQuantityMap.getOrDefault(name, 0) + unit
                        }
                    }
                    //ordino il map in base al valore e prendo i primi 10
                    val sortedItems = productQuantityMap.toList().sortedByDescending { (_, value) -> value }.take(10)

                    _uiState.update { currentState ->
                        currentState.copy(
                            top10Products = sortedItems
                        )
                    }
                }
        }
    }

    fun getTop10MostBoughtCategories(){

        val categoriesQuantityMap = mutableMapOf<String, Int>()

        viewModelScope.launch {
            db.collection("orders")
                .get()
                .addOnSuccessListener { orders ->
                    for (order in orders.documents) {
                        //per ogni ordine prendo il carrello
                        val cart: List<HashMap<String, Any>> = order.get("cart") as List<HashMap<String, Any>>

                        for (product in cart) {
                            //per ogni prodotto nel carrello prendo l'id
                            val productId = product["id"] as String
                            db.collection("prodotti")
                                .document(productId)
                                .get()
                                .addOnSuccessListener { prod ->
                                    //nella collection prodotti accedo al prodotto con l'id productId e accedo alla categoria
                                    val category = prod.get("categoria") as String
                                    db.collection("categories")
                                        .document(category)
                                        .get()
                                        .addOnSuccessListener { cat ->
                                            //nella collection categories accedo alla categoria con l'id category e accedo al nome
                                            val categoryName = cat.get("nome") as String
                                            //aggiungo il nome della categoria al map e se esiste aggiorno il valore
                                            categoriesQuantityMap[categoryName] = categoriesQuantityMap.getOrDefault(categoryName, 0) + 1

                                            //ordino il map in base al valore e prendo i primi 10
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

    fun getAverageMonthlyOrders(){

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
                        //aggiungo il nome del mese al map e se esiste aggiorno il numero di ordini effettuati
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

    //GESTIONE RECENSIONI
    fun getAllReviews(){
        viewModelScope.launch {
            db.collection("orders_reviews")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { reviews ->
                    //prendo tutte le recensioni ordinate per data
                    val reviewsList:MutableList<Review> = mutableListOf()
                    for (review in reviews) {
                        //ogni recensione viene convertita in un oggetto Review
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

    fun sentimentAnalysis(text:String,index:Int){
        viewModelScope.launch {
            //faccio la chiamata all'API per ottenere la sentiment analysis
            val response = getSentimentAnalysis(text)

            //aggiungo la response al map per tenere traccia a quale recensione è associata
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

    private fun addToSentimentAnalysisResult(original: HashMap<Int,List<SentimentData>>, newEntries: HashMap<Int, List<SentimentData>>): HashMap<Int,List<SentimentData>> {
        //Nuova HashMap per mantenere i risultati del merge
        val mergedResult = HashMap(original)

        // Aggiungio tutte le entries al nuovo HashMap
        for ((key, value) in newEntries) {
            mergedResult[key] = value
        }

        return mergedResult
    }

     private suspend fun getSentimentAnalysis(text:String): List<SentimentData> = withContext(Dispatchers.Main) {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            try {
                //chiamo l'API
                val response = RetrofitObject.sentimentAnalysisService.getSentimentAnalysis(text)
                if (response.isSuccessful && response.body() != null) {
                    //se la risposta è andata a buon fine, prendo i dati
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
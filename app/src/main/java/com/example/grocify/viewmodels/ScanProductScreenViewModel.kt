package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.data.ScanProductScreenUiState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.grocify.data.Product
import kotlinx.coroutines.flow.update

class ScanProductScreenViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ScanProductScreenUiState())
    val scanUiState: StateFlow<ScanProductScreenUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore

    fun aggiungiRiga(prodotto: String) {
        viewModelScope.launch {
            db.collection("prodotti")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        for (document in documents) {
                            if(document.id == prodotto){
                                _uiState.update { currentState ->
                                    val nome     = document.get("nome")?.toString() ?: ""
                                    val prezzoKg = document.get("prezzo_al_kg")
                                    val prezzo  = document.get("prezzo_unitario")
                                    val quantita = document.get("quantita")?.toString() ?: ""
                                    val immagine = document.get("immagine")?.toString() ?: ""

                                    // Ensure currentState.lista is not null before calling plus
                                    val updatedLista = currentState.lista.orEmpty() + Product(nome, prezzoKg, prezzo, quantita, immagine,1)

                                    val setWithoutDuplicates = updatedLista.toSet()


                                    val listWithoutDuplicates = setWithoutDuplicates.toList()

                                    // Copy the current state and update the lista field with the new list
                                    currentState.copy(lista = listWithoutDuplicates, barcodeNonLetto = false)
                                }

                            }
                        }
                    }
                }
            }
    }
}
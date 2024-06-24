package com.example.grocify.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocify.states.UsersManagementUiState
import com.example.grocify.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UsersManagementViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UsersManagementUiState())
    val uiState: StateFlow<UsersManagementUiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore

    //estraggo tutti gli utenti che hanno come ruolo driver o user
    fun getUsers() {
        viewModelScope.launch {
            //ottengo tutti gli utenti
            db.collection("users")
                .get()
                .addOnSuccessListener { users ->
                    //se è presente almeno un utente
                    if(!users.isEmpty){
                        //ciclo gli utenti
                        for (user in users) {
                            val role = user.get("role")?.toString() ?: ""
                            //se l'utente che sto considerando non è un admin, quindi o è un driver o un user
                            if(role != "admin") {
                                //allora lo aggiungo alla lista users, che verrà mostrata poi all'admin
                                val uid = user.get("uid")?.toString() ?: ""
                                val name = user.get("name")?.toString() ?: ""
                                val surname = user.get("surname")?.toString() ?: ""
                                val email = user.get("email")?.toString() ?: ""
                                val profilePic = user.get("profilePic")?.toString() ?: ""


                                val person = User(
                                    uid,
                                    name,
                                    surname,
                                    email,
                                    "",
                                    profilePic,
                                    role
                                )

                                _uiState.update { currentState ->
                                    val updatedList = currentState.users + person
                                    currentState.copy(users = updatedList.toMutableList())
                                }
                            }
                        }
                    }
                }
        }
    }

    fun updateUserRole(user: User){
        viewModelScope.launch {
            //aggiorno i privilegi dell'utente selezionato sul firestore db
            val updateRoleTask = db.collection("users")
                .whereEqualTo("uid", user.uid)
                .get()
                .addOnSuccessListener { people ->
                    for (person in people) {
                        db.collection("users").document(person.id)
                            .update("role", if (user.role == "user") "driver" else "user")
                    }
                }

            updateRoleTask.await()//aspetto che l'aggiornamento sul database venga effettuato
            _uiState.value.users[_uiState.value.users.indexOf(user)].role = if (user.role == "user") "driver" else "user"
            /*_uiState.update { currentState ->
                //aggiorno i privilegi dell'utente anche nella lista in locale dell'app
                val updatedList = currentState.users
                updatedList[updatedList.indexOf(user)].role = if (user.role == "user") "driver" else "user"
                currentState.copy(users = updatedList)
            }*/
        }
    }

    fun resetFields(){
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(users = mutableListOf<User>() )
            }
        }
    }
}
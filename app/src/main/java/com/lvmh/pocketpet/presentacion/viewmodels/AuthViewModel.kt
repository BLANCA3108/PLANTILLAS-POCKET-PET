package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<AuthState>(AuthState.Loading)
    val uiState: StateFlow<AuthState> = _uiState

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        _isAuthenticated.value = currentUser != null

        _uiState.value = if (currentUser != null) {
            AuthState.Authenticated(currentUser.email ?: "")
        } else {
            AuthState.NotAuthenticated
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _uiState.value = AuthState.Success("Inicio de sesión exitoso")
                _isAuthenticated.value = true
            } catch (e: Exception) {
                _uiState.value = AuthState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _uiState.value = AuthState.Success("Registro exitoso")
                _isAuthenticated.value = true
            } catch (e: Exception) {
                _uiState.value = AuthState.Error(e.message ?: "Error en el registro")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _isAuthenticated.value = false
        _uiState.value = AuthState.NotAuthenticated
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object NotAuthenticated : AuthState()
    data class Authenticated(val email: String) : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

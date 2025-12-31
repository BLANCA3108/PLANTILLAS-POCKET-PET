// AuthViewModel.kt
package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(auth.currentUser != null)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        _isAuthenticated.value = auth.currentUser != null
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AuthState.Loading
                auth.signInWithEmailAndPassword(email, password).await()
                _isAuthenticated.value = true
                _uiState.value = AuthState.Success
            } catch (e: Exception) {
                _uiState.value = AuthState.Error(
                    e.message ?: "Error al iniciar sesión"
                )
                _isAuthenticated.value = false
            }
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AuthState.Loading
                auth.createUserWithEmailAndPassword(email, password).await()
                _isAuthenticated.value = true
                _uiState.value = AuthState.Success
            } catch (e: Exception) {
                _uiState.value = AuthState.Error(
                    e.message ?: "Error al crear cuenta"
                )
                _isAuthenticated.value = false
            }
        }
    }

    // 🔥 FUNCIÓN DE CERRAR SESIÓN
    fun signOut(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoggingOut.value = true
                _uiState.value = AuthState.Loading

                // Cerrar sesión de Firebase
                auth.signOut()

                // Actualizar estados
                _isAuthenticated.value = false
                _uiState.value = AuthState.Success
                _isLoggingOut.value = false

                // Ejecutar callback de éxito
                onSuccess()
            } catch (e: Exception) {
                _isLoggingOut.value = false
                _uiState.value = AuthState.Error(
                    e.message ?: "Error al cerrar sesión"
                )
                onError(e.message ?: "Error al cerrar sesión")
            }
        }
    }

    fun clearError() {
        _uiState.value = AuthState.Idle
    }
}
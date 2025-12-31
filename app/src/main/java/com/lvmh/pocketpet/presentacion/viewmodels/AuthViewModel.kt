package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lvmh.pocketpet.presentacion.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    // Estado de autenticación
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // 🆕 USER ID - AGREGADO
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    // Estado de la UI
    private val _uiState = MutableStateFlow<AuthState>(AuthState.NotAuthenticated)
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    // Usuario actual
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        checkAuthStatus()
    }

    // Verificar el estado de autenticación al iniciar
    private fun checkAuthStatus() {
        val user = auth.currentUser
        _isAuthenticated.value = user != null
        _currentUser.value = user
        _userId.value = user?.uid // 🆕 ACTUALIZAR userId

        if (user != null) {
            _uiState.value = AuthState.Authenticated(user.email ?: "")
        } else {
            _uiState.value = AuthState.NotAuthenticated
        }
    }

    // Registrar usuario con email y contraseña
    fun signUpWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthState.Error("Por favor completa todos los campos")
            return
        }

        if (password.length < 6) {
            _uiState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = AuthState.Loading

                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user

                if (user != null) {
                    _isAuthenticated.value = true
                    _currentUser.value = user
                    _userId.value = user.uid // 🆕 ACTUALIZAR userId
                    _uiState.value = AuthState.Success("Cuenta creada exitosamente")
                } else {
                    _uiState.value = AuthState.Error("Error al crear la cuenta")
                }
            } catch (e: Exception) {
                _isAuthenticated.value = false
                _userId.value = null // 🆕 LIMPIAR userId
                _uiState.value = AuthState.Error(
                    when {
                        e.message?.contains("email address is already in use") == true ->
                            "Este correo ya está registrado"
                        e.message?.contains("network") == true ->
                            "Error de conexión. Verifica tu internet"
                        else -> "Error: ${e.localizedMessage}"
                    }
                )
            }
        }
    }

    // Iniciar sesión con email y contraseña
    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthState.Error("Por favor completa todos los campos")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = AuthState.Loading

                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user

                if (user != null) {
                    _isAuthenticated.value = true
                    _currentUser.value = user
                    _userId.value = user.uid // 🆕 ACTUALIZAR userId
                    _uiState.value = AuthState.Success("Bienvenido de nuevo")
                } else {
                    _uiState.value = AuthState.Error("Error al iniciar sesión")
                }
            } catch (e: Exception) {
                _isAuthenticated.value = false
                _userId.value = null // 🆕 LIMPIAR userId
                _uiState.value = AuthState.Error(
                    when {
                        e.message?.contains("password is invalid") == true ||
                                e.message?.contains("no user record") == true ->
                            "Correo o contraseña incorrectos"
                        e.message?.contains("network") == true ->
                            "Error de conexión. Verifica tu internet"
                        else -> "Error: ${e.localizedMessage}"
                    }
                )
            }
        }
    }

    // Cerrar sesión
    fun signOut() {
        auth.signOut()
        _isAuthenticated.value = false
        _currentUser.value = null
        _userId.value = null // 🆕 LIMPIAR userId
        _uiState.value = AuthState.NotAuthenticated
    }

    // Limpiar errores
    fun clearError() {
        if (_uiState.value is AuthState.Error) {
            _uiState.value = AuthState.NotAuthenticated
        }
    }
}
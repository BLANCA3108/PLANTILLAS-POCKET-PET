// AuthState.kt
package com.lvmh.pocketpet.presentacion.auth

sealed class AuthState {
    object Loading : AuthState()
    object NotAuthenticated : AuthState()
    data class Authenticated(val email: String) : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
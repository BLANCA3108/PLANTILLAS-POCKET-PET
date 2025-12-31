package com.lvmh.pocketpet.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUser() = auth.currentUser

    fun isUserLoggedIn() = auth.currentUser != null

    suspend fun signOut() {
        auth.signOut()
    }
}
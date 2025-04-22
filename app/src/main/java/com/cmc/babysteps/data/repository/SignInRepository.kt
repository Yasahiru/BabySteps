package com.cmc.babysteps.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class SignInRepository(private val auth: FirebaseAuth) {
    suspend fun signInWithEmailAndPassword(email: String, password: String): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user != null
        } catch (e: Exception) {
            false
        }
    }
}
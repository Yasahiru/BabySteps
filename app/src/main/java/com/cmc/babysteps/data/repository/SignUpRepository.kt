package com.cmc.babysteps.data.repository

import android.util.Log
import com.cmc.babysteps.utils.FirebaseConfig.getCurrentUserId
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class SignUpRepository(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore) {

    suspend fun signUpWithEmailAndPassword(email: String, password: String): Boolean {
        var result: AuthResult? = null
        return try {
            result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user != null
        } catch (e: Exception) {
            false
        }
    }

    suspend fun saveUserData(userData: Map<String, Any>): Boolean {
        return try {
            val uid = getCurrentUserId()
            if (true) {
                firestore.collection("users").document(uid).set(userData).await()
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }
}
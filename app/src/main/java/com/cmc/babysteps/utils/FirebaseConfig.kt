package com.cmc.babysteps.utils

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseApp

object  FirebaseConfig {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun initialize(context: Context) {
        FirebaseApp.initializeApp(context)
    }

    fun getCurrentUserId(): String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid ?: throw IllegalStateException("User is not authenticated")
    }

}
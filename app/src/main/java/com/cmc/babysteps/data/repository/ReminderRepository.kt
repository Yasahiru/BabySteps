package com.cmc.babysteps.data.repository

import android.util.Log
import com.cmc.babysteps.data.model.ReminderItem
import com.cmc.babysteps.utils.FirebaseConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReminderRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Obtenir l'ID de l'utilisateur actuel ou une chaîne vide si non connecté
    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    // 🔄 Récupérer tous les rappels de l'utilisateur actuel sous forme de Flow
    fun getRemindersFlow(): Flow<List<ReminderItem>> = callbackFlow {
        val userRemindersCollection = FirebaseConfig.firestore
            .collection("users")
            .document(currentUserId)
            .collection("reminders")

        Log.d("ReminderRepository", "Initialisation de l'écoute des rappels pour l'utilisateur: $currentUserId")

        val query = userRemindersCollection
            .orderBy("date", Query.Direction.ASCENDING)
            .orderBy("time", Query.Direction.ASCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("ReminderRepository", "Error listening to reminders: ${error.message}", error)
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                Log.d("ReminderRepository", "Received snapshot with ${snapshot.size()} reminders")

                val reminders = snapshot.documents.mapNotNull { document ->
                    val reminder = document.toObject(ReminderItem::class.java)
                    reminder?.copy(id = document.id)
                }

                Log.d("ReminderRepository", "Transformed reminders: ${reminders.size}")
                trySend(reminders)  // Ensure this sends data on first load
            } else {
                Log.d("ReminderRepository", "Received null snapshot")
            }
        }


        awaitClose {
            Log.d("ReminderRepository", "Arrêt de l'écoute des rappels")
            listener.remove()
        }
    }


    // ✅ Ajouter un nouveau rappel
    suspend fun addReminder(reminderItem: ReminderItem): Result<Unit> {
        return try {
            val reminderWithUser = reminderItem.copy(userId = currentUserId)

            val userRemindersCollection = FirebaseConfig.firestore
                .collection("users")
                .document(currentUserId)
                .collection("reminders")

            userRemindersCollection.add(reminderWithUser).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ Supprimer un rappel depuis la sous-collection utilisateur
    suspend fun deleteReminder(reminderId: String): Result<Unit> {
        return try {
            FirebaseConfig.firestore
                .collection("users")
                .document(currentUserId)
                .collection("reminders")
                .document(reminderId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ Mettre à jour un rappel dans la sous-collection utilisateur
    suspend fun updateReminder(reminderItem: ReminderItem): Result<Unit> {
        return try {
            val id = reminderItem.id
            if (id.isNotEmpty()) {
                FirebaseConfig.firestore
                    .collection("users")
                    .document(currentUserId)
                    .collection("reminders")
                    .document(id)
                    .set(reminderItem)
                    .await()
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("ID du rappel est vide"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

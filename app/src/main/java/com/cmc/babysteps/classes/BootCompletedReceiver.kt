package com.cmc.babysteps.classes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.cmc.babysteps.data.repository.ReminderRepository
import com.cmc.babysteps.utils.ReminderNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootCompletedReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Créer le canal de notification (si nécessaire)
            ReminderNotificationManager.createNotificationChannel(context)

            // Reprogrammer toutes les notifications des rappels
            CoroutineScope(Dispatchers.IO).launch {
                val repository = ReminderRepository()
                try {
                    // Récupérer tous les rappels
                    val reminders = repository.getRemindersFlow().first()
                    if (reminders.isNotEmpty()) {
                        // Programmer la notification pour chaque rappel
                        reminders.forEach { reminder ->
                            ReminderNotificationManager.scheduleNotification(context, reminder)
                        }
                    } else {
                        Log.d(TAG, "No reminders found to schedule.")
                    }
                } catch (e: Exception) {
                    // Log de l'erreur pour le suivi
                    Log.e(TAG, "Error while scheduling notifications: ${e.message}", e)
                }
            }
        }
    }
}

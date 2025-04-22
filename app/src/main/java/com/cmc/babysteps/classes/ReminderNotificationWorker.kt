package com.cmc.babysteps.classes

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cmc.babysteps.utils.ReminderNotificationManager

class ReminderNotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val reminderId = inputData.getString("reminder_id")
        val reminderTitle = inputData.getString("reminder_title")
        val reminderDate = inputData.getString("reminder_date")
        val reminderTime = inputData.getString("reminder_time")

        // Vérification des données d'entrée
        if (reminderId.isNullOrEmpty() || reminderTitle.isNullOrEmpty() || reminderDate.isNullOrEmpty() || reminderTime.isNullOrEmpty()) {
            Log.e("ReminderWorker", "Missing required input data for reminder. id: $reminderId, title: $reminderTitle, date: $reminderDate, time: $reminderTime")
            return Result.failure()
        }

        Log.d("ReminderWorker", "Running worker for reminder '$reminderTitle' at $reminderDate $reminderTime")

        try {
            // Afficher la notification pour le rappel
            ReminderNotificationManager.showNotification(
                context,
                reminderId,
                reminderTitle,
                reminderDate,
                reminderTime
            )
        } catch (e: Exception) {
            Log.e("ReminderWorker", "Error showing notification for reminder '$reminderTitle': ${e.message}", e)
            return Result.failure()
        }

        return Result.success()
    }
}

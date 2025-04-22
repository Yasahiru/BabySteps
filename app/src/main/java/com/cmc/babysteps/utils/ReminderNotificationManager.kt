package com.cmc.babysteps.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.cmc.babysteps.R
import com.cmc.babysteps.classes.ReminderNotificationWorker
import com.cmc.babysteps.data.model.ReminderItem
import com.cmc.babysteps.ui.MainActivity
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object ReminderNotificationManager {
    private const val CHANNEL_ID = "reminder_notifications"
    private const val GROUP_KEY = "com.cmc.babysteps.REMINDERS"

    // Créer le canal de notification (obligatoire pour Android 8.0+)
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Rappels"
            val descriptionText = "Notifications pour les rappels"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            Log.d("ReminderNotificationManager", "Notification channel created")
        }
    }


    // Programmer une notification pour un rappel
    fun scheduleNotification(context: Context, reminder: ReminderItem) {
        try {
            // Supprimer toute notification existante pour ce rappel
            cancelScheduledNotification(context, reminder.id)

            // Convertir date et heure du rappel en LocalDateTime
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

            val reminderDate = LocalDate.parse(reminder.date, dateFormatter)
            val reminderTime = LocalTime.parse(reminder.time, timeFormatter)
            val reminderDateTime = LocalDateTime.of(reminderDate, reminderTime)

            // Calculer le délai jusqu'au moment du rappel
            val now = LocalDateTime.now()

            // Si la date est déjà passée, ne pas programmer de notification
            if (reminderDateTime.isBefore(now)) {
                Log.d("ReminderNotification", "Reminder for ${reminder.label} is in the past, skipping notification.")
                return
            }

            val delay = Duration.between(now, reminderDateTime)
            val delayInMillis = delay.toMillis()

            // Préparer les données pour le worker
            val inputData = Data.Builder()
                .putString("reminder_id", reminder.id)
                .putString("reminder_title", reminder.label)
                .putString("reminder_date", reminder.date)
                .putString("reminder_time", reminder.time)
                .build()

            // Créer et planifier le travail
            val notificationWork = OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
                .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(reminder.id)
                .build()

            // Enqueue the work for later execution
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "reminder_${reminder.id}",
                    ExistingWorkPolicy.REPLACE,
                    notificationWork
                )

            Log.d("ReminderNotification", "Notification scheduled for reminder ID: ${reminder.id}")

        } catch (e: Exception) {
            Log.e("ReminderNotification", "Error scheduling notification for reminder '${reminder.label}': ${e.message}", e)
        }
    }

    // Annuler une notification programmée
    fun cancelScheduledNotification(context: Context, reminderId: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(reminderId)
        Log.d("ReminderNotification", "Scheduled notification for reminder $reminderId cancelled.")
    }

    // Afficher une notification
    fun showNotification(context: Context, reminderId: String, title: String, date: String, time: String) {
        val notificationId = reminderId.hashCode()

        // Log de notification
        Log.d("ReminderNotification", "Notification for $title scheduled at $date $time")

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("reminder_id", reminderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText("Rappel prévu pour le $date à $time")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(GROUP_KEY)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
}

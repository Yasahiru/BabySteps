package com.cmc.babysteps.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.babysteps.data.model.ReminderItem
import com.cmc.babysteps.data.repository.ReminderRepository
import com.cmc.babysteps.utils.ReminderNotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val _noReminderMessage = MutableStateFlow<String?>(null)
    val noReminderMessage: StateFlow<String?> = _noReminderMessage.asStateFlow()

    private val repository = ReminderRepository()
    private val appContext = application.applicationContext

    private val _reminders = MutableStateFlow<List<ReminderItem>>(emptyList())
    val reminders: StateFlow<List<ReminderItem>> = _reminders.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Créer le canal de notification
        ReminderNotificationManager.createNotificationChannel(appContext)
        loadReminders()
    }

    // Charger les rappels
    private fun loadReminders() {
        _isLoading.value = false
        viewModelScope.launch {
            try {
                repository.getRemindersFlow().collect { remindersList ->
                    _reminders.value = remindersList
                    _noReminderMessage.value = if (remindersList.isEmpty()) "No reminder" else null

                    // Programmer les notifications pour tous les rappels chargés
                    remindersList.forEach { reminder ->
                        ReminderNotificationManager.scheduleNotification(appContext, reminder)
                    }
                }
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error loading reminders: ${e.message}")
                _errorMessage.value = "Error loading reminders: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Ajouter un rappel
    fun addReminder(
        reminderItem: ReminderItem,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        _isLoading.value = false
        viewModelScope.launch {
            try {
                repository.addReminder(reminderItem)
                    .onSuccess {
                        _errorMessage.value = null
                        onSuccess()
                        // La notification sera programmée lors de la mise à jour du flow
                    }
                    .onFailure { e ->
                        _errorMessage.value = "Erreur lors de l'ajout du rappel : ${e.message}"
                        onError(e as Exception)
                    }
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error adding reminder: ${e.message}")
                _errorMessage.value = "Error adding reminder: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Supprimer un rappel
    fun deleteReminder(reminderId: String) {
        if (reminderId.isEmpty()) {
            _errorMessage.value = "ID du rappel vide. Impossible de supprimer."
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.deleteReminder(reminderId)
                    .onSuccess {
                        // Annuler la notification programmée
                        ReminderNotificationManager.cancelScheduledNotification(appContext, reminderId)
                    }
                    .onFailure { e ->
                        _errorMessage.value = "Erreur lors de la suppression du rappel : ${e.message}"
                    }
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error deleting reminder: ${e.message}")
                _errorMessage.value = "Erreur lors de la suppression du rappel : ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Mettre à jour un rappel
    fun updateReminder(reminderItem: ReminderItem) {
        if (reminderItem.id.isEmpty()) {
            _errorMessage.value = "ID du rappel vide. Impossible de mettre à jour."
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.updateReminder(reminderItem)
                    .onSuccess {
                        // Reprogrammer la notification
                        ReminderNotificationManager.scheduleNotification(appContext, reminderItem)
                    }
                    .onFailure { e ->
                        _errorMessage.value = "Erreur lors de la mise à jour du rappel : ${e.message}"
                    }
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error updating reminder: ${e.message}")
                _errorMessage.value = "Erreur lors de la mise à jour du rappel : ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Nettoyer le message d'erreur
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

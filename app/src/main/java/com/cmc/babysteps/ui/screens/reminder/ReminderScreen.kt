package com.cmc.babysteps.ui.screens.reminder

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cmc.babysteps.data.model.ReminderItem
import com.cmc.babysteps.data.viewmodel.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReminderScreen() {
    val navController = rememberNavController()
    val viewModel: ReminderViewModel = viewModel()

    // Collecter les états
    val reminders by viewModel.reminders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()




    NavHost(navController, startDestination = "list") {
        composable("list") {
            ReminderListScreen(
                reminders = reminders,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onAddClicked = { navController.navigate("create") },
                onDeleteReminder = { reminderId -> viewModel.deleteReminder(reminderId) },
                onErrorDismissed = { viewModel.clearErrorMessage() }
            )
        }
        composable("create") {
            ReminderCreationScreen(
                onSaveClicked = { label, date, time ->
                    val newReminder = ReminderItem(label, date, time, "")
                    viewModel.addReminder(
                        newReminder,
                        onSuccess = { navController.popBackStack() },
                        onError = { e -> println("Error: ${e.message}") }
                    )
                },
                onBackClicked = { navController.popBackStack() },
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListScreen(
    reminders: List<ReminderItem>,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onAddClicked: () -> Unit,
    onDeleteReminder: (String) -> Unit,
    onErrorDismissed: () -> Unit
) {
    // État pour afficher la boîte de dialogue de confirmation de suppression
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var reminderToDelete by remember { mutableStateOf<ReminderItem?>(null) }

    // Afficher la boîte de dialogue d'erreur si nécessaire
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { onErrorDismissed() },
            title = { Text("Erreur") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { onErrorDismissed() }) {
                    Text("OK")
                }
            }
        )
    }

    // Boîte de dialogue de confirmation de suppression
    if (showDeleteConfirmDialog && reminderToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmDialog = false
                reminderToDelete = null
            },
            title = { Text("Confirmer la suppression") },
            text = { Text("Êtes-vous sûr de vouloir supprimer ce rappel ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        reminderToDelete?.id?.let { onDeleteReminder(it) }
                        showDeleteConfirmDialog = false
                        reminderToDelete = null
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        reminderToDelete = null
                    }
                ) {
                    Text("Annuler")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reminder",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddClicked() }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter un rappel"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (isLoading) {
                    // Afficher l'indicateur de chargement
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (reminders.isEmpty()) {
                    // Afficher le message d'état vide
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Vos rappels apparaîtront ici",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Afficher la liste des rappels
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(reminders) { reminder ->
                            SwipeableReminderCard(
                                reminder = reminder,
                                onDeleteClicked = {
                                    reminderToDelete = reminder
                                    showDeleteConfirmDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableReminderCard(
    reminder: ReminderItem,
    onDeleteClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reminder.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Date: ${reminder.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Heure: ${reminder.time}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Bouton de suppression
            IconButton(onClick = onDeleteClicked) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderCreationScreen(
    onBackClicked: () -> Unit,
    onSaveClicked: (label: String, date: String, time: String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var label by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("Sélectionner une date") }
    var timeText by remember { mutableStateOf("Sélectionner une heure") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dateText = format.format(date)
                    }
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annuler")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Sélectionner l'heure") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(onClick = {
                    showTimePicker = false
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    timeText = String.format("%02d:%02d", hour, minute)
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Ajouter un rappel",
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            // Only save if label is not empty and date/time are selected
                            if (label.isNotBlank() &&
                                dateText != "Sélectionner une date" &&
                                timeText != "Sélectionner une heure") {
                                onSaveClicked(label, dateText, timeText)
                            }
                        },
                        enabled = !isLoading
                    ) {
                        Text("Save", color = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Afficher un message d'erreur si présent
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Label input
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Libellé du rappel") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            // Date input
            OutlinedTextField(
                value = dateText,
                onValueChange = { },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = !isLoading,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }, enabled = !isLoading) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Sélectionner une date"
                        )
                    }
                }
            )

            // Time input
            OutlinedTextField(
                value = timeText,
                onValueChange = { },
                label = { Text("Heure") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = !isLoading,
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }, enabled = !isLoading) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Sélectionner une heure"
                        )
                    }
                }
            )

            // Indicateur de chargement
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
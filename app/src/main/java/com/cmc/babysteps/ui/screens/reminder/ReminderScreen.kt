package com.cmc.babysteps.ui.screens.reminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cmc.babysteps.data.model.ReminderItem
import com.cmc.babysteps.data.viewmodel.ReminderViewModel
import okhttp3.internal.notify
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReminderScreen() {
    val navController = rememberNavController()
    val viewModel: ReminderViewModel = viewModel()

    // Define pregnancy theme colors
    val babyPink = Color(0xFFF8BBD0)
    val lightPurple = Color(0xFFD1C4E9)
    val softBlue = Color(0xFFBBDEFB)
    val babyGreen = Color(0xFFDCEDC8)
    val accentPink = Color(0xFFAD1457)
    val backgroundPink = Color(0xFFFCE4EC)

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
                onErrorDismissed = { viewModel.clearErrorMessage() },
                backgroundColor = backgroundPink,
                accentColor = accentPink,
                cardColor = softBlue
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
                errorMessage = errorMessage,
                backgroundColor = backgroundPink,
                accentColor = accentPink,
                buttonColor = lightPurple
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
    onErrorDismissed: () -> Unit,
    backgroundColor: Color,
    accentColor: Color,
    cardColor: Color
) {
    // État pour afficher la boîte de dialogue de confirmation de suppression
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var reminderToDelete by remember { mutableStateOf<ReminderItem?>(null) }

    // Afficher la boîte de dialogue d'erreur si nécessaire
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { onErrorDismissed() },
            title = { Text("Erreur", color = accentColor) },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { onErrorDismissed() }) {
                    Text("OK", color = accentColor)
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
            title = { Text("Confirmer la suppression", color = accentColor) },
            text = { Text("Êtes-vous sûr de vouloir supprimer ce rappel ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        reminderToDelete?.id?.let { onDeleteReminder(it) }
                        showDeleteConfirmDialog = false
                        reminderToDelete = null
                    }
                ) {
                    Text("Supprimer", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        reminderToDelete = null
                    }
                ) {
                    Text("Annuler", color = accentColor)
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
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddClicked() },
                containerColor = accentColor,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter un rappel"
                )
            }
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = backgroundColor
            ) {
                if (isLoading) {
                    // Afficher l'indicateur de chargement
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = accentColor)
                    }
                } else if (reminders.isEmpty()) {
                    // Afficher le message d'état vide
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF8BBD0)
                            ),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = accentColor,
                                    modifier = Modifier.size(48.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Vos rappels apparaîtront ici",
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray
                                )

                                Text(
                                    text = "Appuyez sur + pour ajouter",
                                    fontWeight = FontWeight.Light,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                } else {
                    // Afficher la liste des rappels
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(reminders) { reminder ->
                            ReminderCard(
                                reminder = reminder,
                                onDeleteClicked = {
                                    reminderToDelete = reminder
                                    showDeleteConfirmDialog = true
                                },
                                cardColor = cardColor,
                                accentColor = accentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderCard(
    reminder: ReminderItem,
    onDeleteClicked: () -> Unit,
    cardColor: Color,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(4.dp)
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
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reminder.date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reminder.time,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }

            // Bouton de suppression
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.size(40.dp)
            ) {
                IconButton(onClick = onDeleteClicked) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
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
    errorMessage: String? = null,
    backgroundColor: Color,
    accentColor: Color,
    buttonColor: Color
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
                    Text("OK", color = accentColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annuler", color = accentColor)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Sélectionner l'heure", color = accentColor) },
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
                    Text("OK", color = accentColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Annuler", color = accentColor)
                }
            }
        )
    }

    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = {
//                    Text(
//                        text = "Ajouter un rappel",
//                        textAlign = TextAlign.Center,
//                        color = accentColor,
//                        fontWeight = FontWeight.Bold
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = { onBackClicked() }) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Retour",
//                            tint = accentColor
//                        )
//                    }
//                },
//                actions = {
//                    TextButton(
//                        onClick = {
//                            // Only save if label is not empty and date/time are selected
//                            if (label.isNotBlank() &&
//                                dateText != "Sélectionner une date" &&
//                                timeText != "Sélectionner une heure") {
//                                onSaveClicked(label, dateText, timeText)
//                            }
//                        },
//                        enabled = !isLoading
//                    ) {
//                        Text("Save", color = accentColor, fontWeight = FontWeight.Bold)
//                    }
//                },
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                    containerColor = backgroundColor
//                )
//            )
//        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = "Calendar",
                            tint = Color(0xFFAD1457),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFFAD1457)
                ),
                modifier = Modifier.shadow(4.dp)
            )

            // Afficher un message d'erreur si présent
            errorMessage?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Label input
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        label = { Text("Libellé du rappel") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Date input
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { },
                        label = { Text("Date") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Card(
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = buttonColor),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                IconButton(onClick = { showDatePicker = true }, enabled = !isLoading) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Sélectionner une date",
                                        tint = accentColor
                                    )
                                }
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
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Card(
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = buttonColor),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                IconButton(onClick = { showTimePicker = true }, enabled = !isLoading) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Sélectionner une heure",
                                        tint = accentColor
                                    )
                                }
                            }
                        }
                    )
                }
            }

            // Indicateur de chargement
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 16.dp),
                    color = accentColor
                )
            }
        }
    }
}
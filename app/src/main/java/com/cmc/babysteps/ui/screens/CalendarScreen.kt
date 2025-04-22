package com.cmc.babysteps.ui.screens

import android.content.Context
import android.net.Uri
import coil.compose.rememberAsyncImagePainter
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.core.net.toUri
import com.cmc.babysteps.classes.DialogState
import com.cmc.babysteps.data.model.CalendarEntry
import com.cmc.babysteps.data.viewmodel.CalendarViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

// Generate dates between two bounds
fun generateDateRange(start: LocalDate, end: LocalDate): List<LocalDate> =
    generateSequence(start) { it.plusDays(1) }.takeWhile { !it.isAfter(end) }.toList()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    context: Context,
    viewModel: CalendarViewModel,
    startDate: LocalDate,
    endDate: LocalDate
) {
    val entriesMap by viewModel.entriesByDate.collectAsState()
    var dialogState by remember { mutableStateOf<DialogState>(DialogState.None) }
    var noteText by remember { mutableStateOf("") }
    val uiImageUris = remember { mutableStateListOf<Uri>() }
    val uiVideoUris = remember { mutableStateListOf<Uri>() }
    val scope = rememberCoroutineScope()

    val pickImageLauncher = rememberLauncherForActivityResult(GetContent()) { it?.let(uiImageUris::add) }
    val pickVideoLauncher = rememberLauncherForActivityResult(GetContent()) { it?.let(uiVideoUris::add) }

    fun resetFields() {
        noteText = ""
        uiImageUris.clear()
        uiVideoUris.clear()
    }

    fun loadEntry(entry: CalendarEntry) {
        noteText = entry.note.orEmpty()
        uiImageUris.clear()
        uiImageUris.addAll(entry.imageUris.map { it.toUri() })
        uiVideoUris.clear()
        uiVideoUris.addAll(entry.videoUris.map { it.toUri() })
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Calendar") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(generateDateRange(startDate, endDate)) { date ->
                val entry = entriesMap[date]
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .width(550.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .clickable {
                            if (entry == null) {
                                resetFields()
                                dialogState = DialogState.Add(date)
                            } else {
                                loadEntry(entry)
                                dialogState = DialogState.View(date)
                            }
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(40.dp).background(Color.Cyan, CircleShape)
                    ) {
                        Text(
                            text = "${date.dayOfMonth}/${date.monthValue}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black
                        )
                    }

                    /* {
                        add logic to display the note if exist and the img
                    }*/

                    if (entry != null) {
                        Box(
                            Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape).align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }

    when (val state = dialogState) {
        is DialogState.View -> {
            val date = state.date
            Dialog(onDismissRequest = { dialogState = DialogState.None }) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Note for ${date.dayOfMonth}/${date.monthValue}/${date.year}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(noteText)
                        Spacer(Modifier.height(8.dp))
                        if (uiImageUris.isNotEmpty()) {
                            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                uiImageUris.forEach { uri ->
                                    Image(
                                        painter = rememberAsyncImagePainter(uri),
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        if (uiVideoUris.isNotEmpty()) {
                            uiVideoUris.forEach { uri ->
                                Text(uri.lastPathSegment.orEmpty())
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = {
                                dialogState = DialogState.Add(date)
                            }) {
                                Text("Edit")
                            }
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = {
                                scope.launch {
                                    viewModel.saveEntry(note = null, imageUris = emptyList(), videoUris = emptyList())
                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                                    dialogState = DialogState.None
                                }
                            }) {
                                Text("Delete", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }
        is DialogState.Add -> {
            val date = state.date
            Dialog(onDismissRequest = { dialogState = DialogState.None }) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Add/Edit Note for ${date.dayOfMonth}/${date.monthValue}/${date.year}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            label = { Text("Note") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { pickImageLauncher.launch("image/*") }) {
                                Icon(Icons.Filled.Add, contentDescription = "Add Image")
                            }
                            Text("${uiImageUris.size} image(s) added")
                        }
                        if (uiImageUris.isNotEmpty()) {
                            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                uiImageUris.forEach { uri ->
                                    Image(
                                        painter = rememberAsyncImagePainter(uri),
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { pickVideoLauncher.launch("video/*") }) {
                                Icon(Icons.Filled.Add, contentDescription = "Add Video")
                            }
                            Text("${uiVideoUris.size} video(s) added")
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { dialogState = DialogState.None }) {
                                Text("Cancel")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = {
                                scope.launch {
                                    // Check if the note is empty and there are no images or videos
                                    if (noteText.isEmpty() && uiImageUris.isEmpty() && uiVideoUris.isEmpty()) {
                                        dialogState = DialogState.None
                                        Toast.makeText(context, "Please add a note, image, or video", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val images = uiImageUris.map { it.toString() }
                                        val videos = uiVideoUris.map { it.toString() }
                                        viewModel.selectDate(date)
                                        viewModel.saveEntry(noteText, images, videos)
                                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                                        dialogState = DialogState.View(date)
                                    }
                                }
                            }) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
        DialogState.None -> {}
        is DialogState.Choice -> TODO()
    }
}

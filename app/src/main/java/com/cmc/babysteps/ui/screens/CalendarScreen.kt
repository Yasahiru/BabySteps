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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.core.net.toUri
import com.cmc.babysteps.classes.DialogState
import com.cmc.babysteps.data.model.CalendarEntry
import com.cmc.babysteps.data.viewmodel.CalendarViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.TextFieldDefaults


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

    // Pregnancy-themed colors
    val babyPink = Color(0xFFF8BBD0)
    val lightPurple = Color(0xFFD1C4E9)
    val softBlue = Color(0xFFBBDEFB)
    val babyGreen = Color(0xFFDCEDC8)
    val backgroundColor = Color(0xFFFCE4EC)

    // Format dates in a more readable way
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d")

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.DateRange,
                            contentDescription = "Calendar",
                            tint = Color(0xFFAD1457),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Pregnancy Journal",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFAD1457)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFFAD1457)
                ),
                modifier = Modifier.shadow(4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Track your journey, one day at a time",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                textAlign = TextAlign.Center,
                color = Color(0xFF880E4F),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(generateDateRange(startDate, endDate)) { date ->
                    val entry = entriesMap[date]
                    val hasEntry = entry != null

                    // Color based on whether an entry exists
                    val cardColor = when {
                        hasEntry -> babyPink.copy(alpha = 0.8f)
                        date.dayOfMonth % 3 == 0 -> lightPurple.copy(alpha = 0.4f)
                        date.dayOfMonth % 3 == 1 -> babyGreen.copy(alpha = 0.4f)
                        else -> softBlue.copy(alpha = 0.4f)
                    }

                    Card(
                        modifier = Modifier
                            .height(180.dp)
                            .fillMaxWidth()
                            .clickable {
                                if (entry == null) {
                                    resetFields()
                                    dialogState = DialogState.Add(date)
                                } else {
                                    loadEntry(entry)
                                    dialogState = DialogState.View(date)
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (hasEntry) 4.dp else 2.dp
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Date circle at the top
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(2.dp, if (hasEntry) Color(0xFFAD1457) else Color.LightGray, CircleShape)
                            ) {
                                Text(
                                    text = date.format(dateFormatter),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasEntry) Color(0xFFAD1457) else Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Entry content preview
                            if (hasEntry) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxSize().padding(top = 56.dp),
                                ) {
                                    // Show part of the note if it exists
                                    if (!entry.note.isNullOrEmpty()) {
                                        Text(
                                            text = entry.note.take(30) + if (entry.note.length > 30) "..." else "",
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center,
                                            color = Color(0xFF880E4F),
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Show icons for images/videos
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (entry.imageUris.isNotEmpty()) {
                                            Icon(
                                                Icons.Filled.Image,
                                                contentDescription = "Has Images",
                                                tint = Color(0xFF7B1FA2),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                " ${entry.imageUris.size}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF7B1FA2)
                                            )
                                        }

                                        if (entry.imageUris.isNotEmpty() && entry.videoUris.isNotEmpty()) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }

                                        if (entry.videoUris.isNotEmpty()) {
                                            Icon(
                                                Icons.Filled.Videocam,
                                                contentDescription = "Has Videos",
                                                tint = Color(0xFF7B1FA2),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                " ${entry.videoUris.size}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF7B1FA2)
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Add icon for empty entries
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.5f))
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = "Add Entry",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Text(
                                    "Add memory",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for viewing entries
    when (val state = dialogState) {
        is DialogState.View -> {
            val date = state.date
            Dialog(onDismissRequest = { dialogState = DialogState.None }) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 8.dp,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Text(
                            "Memory from ${date.month.name} ${date.dayOfMonth}, ${date.year}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFAD1457),
                            fontWeight = FontWeight.Bold
                        )

                        Divider(
                            color = babyPink,
                            thickness = 2.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        Text(
                            noteText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        if (uiImageUris.isNotEmpty()) {
                            Text(
                                "Photos",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFF7B1FA2),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )

                            Row(
                                Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                uiImageUris.forEach { uri ->
                                    Image(
                                        painter = rememberAsyncImagePainter(uri),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(2.dp, babyPink, RoundedCornerShape(8.dp))
                                    )
                                }
                            }
                        }

                        if (uiVideoUris.isNotEmpty()) {
                            Text(
                                "Videos",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFF7B1FA2),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )

                            Column(
                                Modifier.padding(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                uiVideoUris.forEach { uri ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(softBlue.copy(alpha = 0.3f))
                                            .padding(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Videocam,
                                            contentDescription = "Video",
                                            tint = Color(0xFF1565C0),
                                            modifier = Modifier.size(24.dp)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            uri.lastPathSegment.orEmpty(),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = {
                                    dialogState = DialogState.Add(date)
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF1565C0)
                                )
                            ) {
                                Text("Edit")
                            }

                            Spacer(Modifier.width(12.dp))

                            Button(
                                onClick = {
                                    scope.launch {
                                        viewModel.saveEntry(note = null, imageUris = emptyList(), videoUris = emptyList())
                                        Toast.makeText(context, "Memory deleted", Toast.LENGTH_SHORT).show()
                                        dialogState = DialogState.None
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEF5350)
                                )
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }

        // Dialog for adding/editing entries
        is DialogState.Add -> {
            val date = state.date
            Dialog(onDismissRequest = { dialogState = DialogState.None }) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 8.dp,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Text(
                            "Create Memory for ${date.month.name} ${date.dayOfMonth}, ${date.year}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFAD1457),
                            fontWeight = FontWeight.Bold
                        )

                        Divider(
                            color = babyPink,
                            thickness = 2.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            label = { Text("Your thoughts and feelings") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFFAD1457),
                                unfocusedIndicatorColor = babyPink
                            )
                        )


                        Spacer(Modifier.height(16.dp))

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = lightPurple.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Filled.Image,
                                        contentDescription = "Photos",
                                        tint = Color(0xFF7B1FA2)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        "Photos",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color(0xFF7B1FA2),
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        "${uiImageUris.size} added",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )

                                    Spacer(modifier = Modifier.weight(1f))

                                    Button(
                                        onClick = { pickImageLauncher.launch("image/*") },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF7B1FA2)
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text("Add", fontSize = 12.sp)
                                    }
                                }

                                if (uiImageUris.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        Modifier
                                            .horizontalScroll(rememberScrollState())
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        uiImageUris.forEach { uri ->
                                            Image(
                                                painter = rememberAsyncImagePainter(uri),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .border(1.dp, lightPurple, RoundedCornerShape(8.dp))
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = softBlue.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Filled.Videocam,
                                        contentDescription = "Videos",
                                        tint = Color(0xFF1565C0)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        "Videos",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color(0xFF1565C0),
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        "${uiVideoUris.size} added",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )

                                    Spacer(modifier = Modifier.weight(1f))

                                    Button(
                                        onClick = { pickVideoLauncher.launch("video/*") },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF1565C0)
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text("Add", fontSize = 12.sp)
                                    }
                                }

                                if (uiVideoUris.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Column(
                                        Modifier.padding(vertical = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        uiVideoUris.forEach { uri ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(softBlue.copy(alpha = 0.3f))
                                                    .padding(8.dp)
                                            ) {
                                                Icon(
                                                    Icons.Filled.Videocam,
                                                    contentDescription = "Video",
                                                    tint = Color(0xFF1565C0),
                                                    modifier = Modifier.size(20.dp)
                                                )

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Text(
                                                    uri.lastPathSegment.orEmpty(),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { dialogState = DialogState.None },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Gray
                                )
                            ) {
                                Text("Cancel")
                            }

                            Spacer(Modifier.width(12.dp))

                            Button(
                                onClick = {
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
                                            Toast.makeText(context, "Memory saved", Toast.LENGTH_SHORT).show()
                                            dialogState = DialogState.View(date)
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFAD1457)
                                )
                            ) {
                                Text("Save Memory")
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
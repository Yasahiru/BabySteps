package com.cmc.babysteps.ui.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmc.babysteps.R
import com.cmc.babysteps.data.viewmodel.HomeViewModel
import com.cmc.babysteps.ui.components.ShowWeekBottomSheet
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    val weekData = viewModel.weekData.value
    var selectedWeekbutton by remember { mutableStateOf<Int?>(null) }
    var currentPregnancyWeek = viewModel.currentPregnancyWeek.value
    val autoFetchWeek = currentPregnancyWeek?.toIntOrNull()

    val lang = getDeviceLanguage()

    LaunchedEffect(autoFetchWeek) {
        if (autoFetchWeek != null) {
            viewModel.fetchWeekData(lang, autoFetchWeek)
        }
    }

    var selectedWeek by remember { mutableStateOf(false) }

    val babyPink = Color(0xFFF8BBD0)
    val lightPurple = Color(0xFFD1C4E9)
    val softBlue = Color(0xFFBBDEFB)


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFCE4EC) // Light pink background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with baby icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(babyPink)
                        .padding(12.dp)
                ) {
                    // Replace with your actual baby icon resource
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_mylocation),
                        contentDescription = "Baby Icon",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.padding(horizontal = 12.dp))

                Text(
                    text = "BabySteps",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFAD1457) // Deep pink color
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    val pregnancyWeekText = when {
                        selectedWeekbutton != null -> "Week $selectedWeekbutton"
                        currentPregnancyWeek != null -> "Week $autoFetchWeek"
                        else -> "Select Your Week"
                    }

                    Text(
                        pregnancyWeekText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFFAD1457)
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Track your pregnancy journey week by week",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { showSheet = true },
                        modifier = Modifier.fillMaxWidth(0.7f),
                        colors = ButtonDefaults.buttonColors(containerColor = softBlue),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            "Choose Week",
                            modifier = Modifier.padding(vertical = 4.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (selectedWeekbutton != null || autoFetchWeek != null) {
                // Follow-up card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = lightPurple),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_my_calendar),
                                    contentDescription = "Calendar",
                                    tint = Color(0xFF7B1FA2)
                                )
                            }

                            Spacer(modifier = Modifier.padding(horizontal = 8.dp))

                            Text(
                                "Your Next Appointment",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF4A148C)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            weekData?.follow_up ?: "Loading your appointment details...",
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Advice card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = babyPink),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_info_details),
                                    contentDescription = "Advice",
                                    tint = Color(0xFFAD1457)
                                )
                            }

                            Spacer(modifier = Modifier.padding(horizontal = 8.dp))

                            Text(
                                "Mommy Advice",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF880E4F)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            weekData?.advice ?: "Loading advice for this week...",
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            } else {
                // Placeholder for when no week is selected
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = softBlue.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_help),
                            contentDescription = "Select Week",
                            tint = Color(0xFF1565C0),
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "Please select a pregnancy week to view your personalized information",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            color = Color(0xFF0D47A1)
                        )
                    }
                }
            }
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showSheet = false
                },
                sheetState = sheetState,
                containerColor = Color(0xFFFCE4EC)
            ) {
                ShowWeekBottomSheet { week ->
                    selectedWeekbutton = week
                    val lang = getDeviceLanguage()
                    viewModel.fetchWeekData(lang, week)

                    selectedWeek = true
                    showSheet = false
                }
            }
        }
    }
}

fun getDeviceLanguage(): String {
    val language = Locale.getDefault().language
    Log.v("data", "Language: $language")
    return when (language) {
        "fr" -> "french"
        "ar" -> "arabe"
        else -> "english"
    }
}
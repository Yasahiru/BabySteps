package com.cmc.babysteps.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Welcome to BabySteps!")
            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                showSheet = true
            }) {
                Text("Select Week")
            }

            val pregnancyWeekText = when {
                selectedWeekbutton != null -> "Selected Pregnancy Week: $selectedWeekbutton"
                currentPregnancyWeek != null -> "Current Pregnancy Week: $autoFetchWeek"
                else -> "Loading week data..."
            }

            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        pregnancyWeekText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("📆 Follow-up", fontWeight = FontWeight.Bold)
                    Text(weekData?.follow_up ?: "Loading...")
                }
            }

            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("💡 Advice", fontWeight = FontWeight.Bold)
                    Text(weekData?.advice ?: "Loading...")
                }
            }
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showSheet = false
                }, sheetState = sheetState
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

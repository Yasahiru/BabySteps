package com.cmc.babysteps.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cmc.babysteps.data.viewmodel.CalendarViewModel
import com.cmc.babysteps.ui.theme.BabyStepsTheme
import com.cmc.babysteps.utils.FirebaseConfig
import java.time.LocalDate
import androidx.lifecycle.ViewModelProvider
import com.cmc.babysteps.data.local.AppDatabase
import com.cmc.babysteps.data.viewmodel.CalendarViewModelFactory
import com.cmc.babysteps.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseConfig.initialize(this)

        val db = AppDatabase.getDatabase(applicationContext)
        val calendarDao = db.calendarDao()
        val calendarVm = ViewModelProvider(
            this,
            CalendarViewModelFactory(calendarDao)
        )[CalendarViewModel::class.java]

        fun loadDates(): Pair<LocalDate, LocalDate> {
            val start = calendarVm.startDate.value ?: LocalDate.of(2024, 4, 1)
            val end = calendarVm.endDate.value ?: LocalDate.of(2024, 5, 31)
            return start to end
        }

        setContent {
            BabyStepsTheme {
                MainScreen(
                    calendarViewModel = calendarVm,
                    onCalendarDatesRequested = { loadDates() }
                )
            }
        }
    }
}
package com.cmc.babysteps.data.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.cmc.babysteps.data.model.CalendarEntry
import com.cmc.babysteps.data.repository.CalendarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import androidx.compose.runtime.State

class CalendarViewModel(private val repository: CalendarRepository) : ViewModel() {

    init {
        Log.d("com.cmc.babysteps.data.viewmodel.CalendarViewModel", "CalendarViewModel CREATED")
    }

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> get() = _selectedDate

    fun selectDate(date: LocalDate) {
        Log.d("com.cmc.babysteps.data.viewmodel.CalendarViewModel", "Selected date: $date")
        _selectedDate.value = date
    }

    val entriesByDate: StateFlow<Map<LocalDate, CalendarEntry>> =
        repository.getAllEntries()
            .map { list -> list.associateBy { it.date } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    fun saveEntry(note: String?, imageUris: List<String>, videoUris: List<String>) {
        val entry = CalendarEntry(
            date = _selectedDate.value,
            note = note,
            imageUris = imageUris,
            videoUris = videoUris
        )
        Log.d("com.cmc.babysteps.data.viewmodel.CalendarViewModel", "Saving entry: $entry")
        viewModelScope.launch {
            repository.saveEntry(entry)
        }
    }

    // New function: Check if an entry exists for the selected date
    suspend fun isEntryExist(date: LocalDate): Boolean {
        val entry = repository.getEntryByDate(date.toString()).first()
        return entry != null
    }

    // New function: Get entry for a specific date
    suspend fun getEntryForDate(date: LocalDate): CalendarEntry? {
        return repository.getEntryByDate(date.toString()).first()
    }

    /** Debug helper: logs what's in the DB for this date. */
    fun logEntryForDate(date: LocalDate) {
        viewModelScope.launch {
            val entry = getEntryForDate(date)
            if (entry != null) {
                Log.d("CalendarDebug", "Entry for $date → note='${entry.note}', images=${entry.imageUris}, videos=${entry.videoUris}")
            } else {
                Log.d("CalendarDebug", "No entry found for $date")
            }
        }
    }

    private val _startDate = mutableStateOf<LocalDate?>(null)
    val startDate: State<LocalDate?> = _startDate

    private val _endDate = mutableStateOf<LocalDate?>(null)
    val endDate: State<LocalDate?> = _endDate

    fun setDateRange(start: LocalDate, end: LocalDate) {
        _startDate.value = start
        _endDate.value = end
    }


}

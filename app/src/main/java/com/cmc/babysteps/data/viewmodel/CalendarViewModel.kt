package com.cmc.babysteps.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.babysteps.data.model.CalendarEntry
import com.cmc.babysteps.data.repository.CalendarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarViewModel(private val repository: CalendarRepository) : ViewModel() {

    private val _allEntries = MutableStateFlow<List<CalendarEntry>>(emptyList())
    val allEntries: StateFlow<List<CalendarEntry>> = _allEntries

    // Map of date to entry for easy access in UI
    private val _entriesByDate = MutableStateFlow<Map<LocalDate, CalendarEntry>>(emptyMap())
    val entriesByDate: StateFlow<Map<LocalDate, CalendarEntry>> = _entriesByDate

    private val _currentEntry = MutableStateFlow<CalendarEntry?>(null)
    val currentEntry: StateFlow<CalendarEntry?> = _currentEntry

    // Selected date for adding/editing entries
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    init {
        loadAllEntries()
    }

    private fun loadAllEntries() {
        viewModelScope.launch {
            repository.getAllEntries().collect { entries ->
                _allEntries.value = entries

                // Create a map of date to entry for easy lookup in the UI
                val dateToEntryMap = entries.associate { entry ->
                    // Convert string date to LocalDate
                    val localDate = LocalDate.parse(entry.date, dateFormatter)
                    localDate to entry
                }
                _entriesByDate.value = dateToEntryMap
            }
        }
    }

    fun loadEntryForDate(date: String) {
        viewModelScope.launch {
            repository.getEntryByDate(date).collect { entry ->
                _currentEntry.value = entry
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        // Try to load entry for this date if it exists
        loadEntryForDate(date.format(dateFormatter))
    }

    fun saveEntry(note: String?, imageUris: List<String>, videoUris: List<String>) {
        val selectedDate = _selectedDate.value ?: return
        val dateString = selectedDate.format(dateFormatter)

        viewModelScope.launch {
            val entry = CalendarEntry(
                date = dateString,
                note = note,
                imageUris = imageUris,
                videoUris = videoUris
            )
            repository.saveEntry(entry)

            // Refresh data
            loadAllEntries()
        }
    }

    fun deleteEntry(entry: CalendarEntry) {
        viewModelScope.launch {
            repository.deleteNote(entry)
            // Refresh data
            loadAllEntries()
        }
    }
}
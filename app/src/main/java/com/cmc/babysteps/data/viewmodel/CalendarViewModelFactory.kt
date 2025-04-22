package com.cmc.babysteps.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cmc.babysteps.data.local.CalendarDao
import com.cmc.babysteps.data.repository.CalendarRepository

class CalendarViewModelFactory(
    private val calendarDao: CalendarDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            val repository = CalendarRepository(calendarDao)
            return CalendarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

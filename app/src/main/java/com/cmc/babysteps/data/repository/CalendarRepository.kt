package com.cmc.babysteps.data.repository

import com.cmc.babysteps.data.local.CalendarDao
import com.cmc.babysteps.data.model.CalendarEntry
import kotlinx.coroutines.flow.Flow

class CalendarRepository(private val dao: CalendarDao) {

    fun getAllEntries(): Flow<List<CalendarEntry>> = dao.getAllEntries()

    suspend fun saveEntry(entry: CalendarEntry) = dao.insertEntry(entry)

    fun getEntryByDate(date: String): Flow<CalendarEntry?> = dao.getEntryByDate(date)

    suspend fun saveNote(note: CalendarEntry) = dao.insertEntry(note)

    suspend fun deleteNote(note: CalendarEntry) = dao.deleteNote(note)
}

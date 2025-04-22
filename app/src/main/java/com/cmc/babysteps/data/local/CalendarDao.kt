package com.cmc.babysteps.data.local

import androidx.room.*
import com.cmc.babysteps.data.model.CalendarEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendar")
    fun getAllEntries(): Flow<List<CalendarEntry>>

    @Query("SELECT * FROM calendar WHERE date = :date")
    fun getEntryByDate(date: String): Flow<CalendarEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: CalendarEntry)

    @Delete
    suspend fun deleteNote(note: CalendarEntry)
}
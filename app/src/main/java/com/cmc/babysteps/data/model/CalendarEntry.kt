package com.cmc.babysteps.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "calendar")
data class CalendarEntry(
    @PrimaryKey(autoGenerate = true) val id: Int =0,
    val date: LocalDate,
    val note: String?,
    val imageUris: List<String> = emptyList(),
    val videoUris: List<String> = emptyList()
)
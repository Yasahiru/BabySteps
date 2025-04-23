package com.cmc.babysteps.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar")
data class CalendarEntry(
    @PrimaryKey
    val date: String,
    val note: String? = null,
    val imageUris: List<String> = emptyList(),
    val videoUris: List<String> = emptyList()
)
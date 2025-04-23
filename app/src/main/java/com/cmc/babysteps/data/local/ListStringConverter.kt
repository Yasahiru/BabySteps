package com.cmc.babysteps.data.local

import androidx.room.TypeConverter

class ListStringConverter {

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString("|||") // tu peux changer le séparateur si tu veux
    }

    @TypeConverter
    fun toList(data: String): List<String> {
        return if (data.isEmpty()) emptyList() else data.split("|||")
    }
}

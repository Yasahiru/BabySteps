package com.cmc.babysteps.data.local

import android.net.Uri
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import kotlin.collections.joinToString


@ProvidedTypeConverter
class UriListConverter {
    @TypeConverter
    fun fromUriList(list: List<Uri>?): String? {
        return list?.joinToString(",") { it.toString() }
    }

    @TypeConverter
    fun toUriList(data: String?): List<Uri>? {
        return data?.split(",")?.map { Uri.parse(it) }
    }

}

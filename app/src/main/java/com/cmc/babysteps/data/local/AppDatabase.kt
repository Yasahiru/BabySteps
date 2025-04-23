package com.cmc.babysteps.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.ProvidedTypeConverter
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cmc.babysteps.data.model.CalendarEntry

@TypeConverters(ListStringConverter::class)
@ProvidedTypeConverter
@Database(entities = [CalendarEntry::class], version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun calendar(): CalendarDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calendar_db"
                )
                    //.addTypeConverter(UriListConverter()) <-- ❌ À éviter si tu utilises @TypeConverters
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

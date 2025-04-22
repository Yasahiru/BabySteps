package com.cmc.babysteps.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cmc.babysteps.data.model.CalendarEntry

@Database(entities = [CalendarEntry::class], version = 2, exportSchema = false)
@TypeConverters(UriListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calendarDao(): CalendarDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "baby_steps_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

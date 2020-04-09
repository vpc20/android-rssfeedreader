package com.example.rssfeedreader.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RssSites::class], version = 2, exportSchema = false)
abstract class RssDatabase : RoomDatabase() {

    abstract val rssDatabaseDao: RssSitesDao

    companion object {
        @Volatile
        private var INSTANCE: RssDatabase? = null

        fun getInstance(context: Context): RssDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RssDatabase::class.java,
                        "rss_database"
                    )
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

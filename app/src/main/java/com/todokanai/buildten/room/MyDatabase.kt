package com.todokanai.buildten.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(MyTypeConverter::class)
@Database(entities = [Track::class,CurrentTrack::class,ScanPath::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase(){

    abstract fun trackDao() : TrackDao

    abstract fun scanPathDao() : ScanPathDao

    abstract fun currentTrackDao() : CurrentTrackDao

    companion object {
        private var instance: MyDatabase? = null

        @Synchronized
        fun getInstance(context: Context): MyDatabase {
            if (instance == null) {
                synchronized(MyDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MyDatabase::class.java,
                        "room_db",
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance!!
        }
    }
}
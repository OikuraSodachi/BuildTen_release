package com.todokanai.buildten.di

import android.content.Context
import com.todokanai.buildten.room.CurrentTrackDao
import com.todokanai.buildten.room.MyDatabase
import com.todokanai.buildten.room.ScanPathDao
import com.todokanai.buildten.room.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideMyDatabase(@ApplicationContext context: Context) : MyDatabase {
        return MyDatabase.getInstance(context)
    }

    @Provides
    fun provideTrackDao(myDatabase: MyDatabase) : TrackDao {
        return myDatabase.trackDao()
    }

    @Provides
    fun provideCurrentTrackDao(myDatabase: MyDatabase) : CurrentTrackDao {
        return myDatabase.currentTrackDao()
    }

    @Provides
    fun provideScanPathDao(myDatabase: MyDatabase) : ScanPathDao {
        return myDatabase.scanPathDao()
    }



}
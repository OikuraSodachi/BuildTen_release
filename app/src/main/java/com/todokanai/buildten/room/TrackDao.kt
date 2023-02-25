package com.todokanai.buildten.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("select * from room_track")
    fun getAll() : Flow<List<Track>>

    @Query("select * from room_track")
    suspend fun getAllNonFlow() : List<Track>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: Track)

    @Delete
    suspend fun delete(track: Track)

    @Query("Delete from room_track")
    suspend fun deleteAll()

    @Query("select id from room_track")
    fun getUri() : Flow<List<String>>

    @Query("select albumId from room_track")
    fun getAlbum() : Flow<List<String>>

}
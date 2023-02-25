package com.todokanai.buildten.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentTrackDao {

    @Query("select * from room_current_track")
    fun getAll() : Flow<List<CurrentTrack>>


    @Query("select * from room_current_track where `no`=:no")
    fun getByIndex(no:Long) : Flow<CurrentTrack>

    @Query("select * from room_current_track where `no`=:no")
    fun getByIndexNonFlow(no:Long) : CurrentTrack?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currentTrack: CurrentTrack)

    @Delete
    suspend fun delete(currentTrack: CurrentTrack)

    @Query("Delete from room_current_track")
    suspend fun deleteAll()

}
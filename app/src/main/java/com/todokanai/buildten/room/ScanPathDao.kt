package com.todokanai.buildten.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanPathDao {

    @Query("select scanPath from room_scanpath")
    fun getAllPath() : Flow<List<String>>

    @Query("select scanPath from room_scanpath")
    fun getAllPathNonFlow() : List<String>

    @Insert(onConflict = REPLACE)
    suspend fun insert(scanPath:ScanPath)

    @Delete
    suspend fun deleteItem(scanPath: ScanPath)

    @Query("delete from room_scanpath where scanPath =:string")
    suspend fun deleteByPath(string:String)

    @Query("delete from room_scanpath")
    suspend fun deleteAll()


}
package com.todokanai.buildten.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_scanpath")
data class ScanPath(
    @ColumnInfo val scanPath: String?
){
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo
    var no:Long? = null
}
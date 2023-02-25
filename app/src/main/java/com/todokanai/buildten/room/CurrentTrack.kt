package com.todokanai.buildten.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_current_track")
data class CurrentTrack(
    @ColumnInfo val id: String?,
    @ColumnInfo val title: String?,
    @ColumnInfo val artist: String?,
    @ColumnInfo val albumId: String?,
    @ColumnInfo val duration: Long?,
    @ColumnInfo val fileDir: String?
) {      // 행렬의 행 역할
    @PrimaryKey(autoGenerate = false) // no에 값이 없으면 자동증가된 숫자값을 db에 입력
    @ColumnInfo     // 열의 항목들
    var no: Long? = null

    fun toTrack():Track{
        return Track(id, title, artist, albumId, duration, fileDir)
    }


}

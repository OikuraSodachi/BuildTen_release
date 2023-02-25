package com.todokanai.buildten.room

import android.net.Uri
import android.provider.MediaStore
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_track")
data class Track(
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

    fun getTrackUri(): Uri {
        return Uri.withAppendedPath(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id     // 음원의 주소
        )
    }          //-----------음원의 Uri 주소 호출하는 함수

    fun getAlbumUri(): Uri? {
        return Uri.parse(
            "content://media/external/audio/albumart/$albumId"    //앨범 이미지 주소
        )
    }

    fun toCurrentTrack():CurrentTrack{
        return CurrentTrack(id, title, artist, albumId, duration, fileDir)
    }

}
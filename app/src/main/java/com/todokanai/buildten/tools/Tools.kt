package com.todokanai.buildten.tools

import android.net.Uri
import android.provider.MediaStore
import com.todokanai.buildten.application.MyApplication
import com.todokanai.buildten.room.Track

class Tools {
    fun uriToTrack(uri: Uri): Track {

        // 1. 음원 정보 주소
     //   val listUrl = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI // URI 값을 주면 나머지 데이터 모아옴

        // 2. 음원 정보 자료형 정의
        val proj = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.MediaColumns.DATA
        )
        // 3. 컨텐트리졸버의 쿼리에 주소와 컬럼을 입력하면 커서형태로 반환받는다
        val cursor = MyApplication.appContext.contentResolver?.query(uri,proj, null,
            null,null)
        val trackList = mutableListOf<Track>()
        while (cursor?.moveToNext() == true) {
            val id = cursor.getString(0)
            val title = cursor.getString(1)
            val artist = cursor.getString(2)
            val albumId = cursor.getString(3)
            val duration = cursor.getLong(4)
            val fileDir = cursor.getString(5)

            val track = Track(id, title, artist, albumId, duration, fileDir)
            trackList.add(track)
        }

        cursor?.close()
        return trackList.first()    // track 전체 반환
    }
}
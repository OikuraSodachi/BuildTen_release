package com.todokanai.buildten.viewmodel

import android.provider.MediaStore
import android.text.Editable
import androidx.lifecycle.ViewModel
import com.todokanai.buildten.application.MyApplication
import com.todokanai.buildten.myobjects.MyObjects.isShuffled
import com.todokanai.buildten.myobjects.MyObjects.playList
import com.todokanai.buildten.repository.DataStoreRepository
import com.todokanai.buildten.repository.ScanPathRepository
import com.todokanai.buildten.repository.TrackRepository
import com.todokanai.buildten.room.ScanPath
import com.todokanai.buildten.room.Track
import com.todokanai.buildten.tools.MediaPlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val spRepo : ScanPathRepository, private val trackRepo : TrackRepository) : ViewModel(){

    val mPath = spRepo.getAllPath()
    private val dsRepo = DataStoreRepository()
    private val mediaPlaybackState = MediaPlaybackState()

    private fun scanTrackList(mPath:List<String>): List<Track> {

        // 1. 음원 정보 주소
        val listUrl = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI // URI 값을 주면 나머지 데이터 모아옴
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
        val cursor = MyApplication.appContext.contentResolver?.query(listUrl,proj, null,
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
            if(mPath.isEmpty()){
                trackList.add(track)
                println("added")
            }else{
                for(a in 1..mPath.size){
                    if(fileDir.startsWith(mPath[a-1])){             // mPath: 경로
                        trackList.add(track)
                    }
                }
                println("added")
            }
        }

        cursor?.close()
        trackList.sortBy { it.title }       // 제목기준으로 정렬
        return trackList    // track 전체 반환
    }

    private fun insertScannedTracks(trackList:List<Track>){
        CoroutineScope(Dispatchers.IO).launch {
            for(index in 0..trackList.size-1) {
                trackRepo.insert(trackList[index])
            }
        }
    }
    var pathList = emptyList<String>()
    fun scanBtn(){
        mediaPlaybackState.reset()
        CoroutineScope(Dispatchers.IO).launch {
            trackRepo.deleteAll()
            insertScannedTracks(scanTrackList(pathList))
            playList = trackRepo.trackLibrary()
            isShuffled.postValue(false)
            dsRepo.saveIsShuffled(false)
        }
    }

    fun confirmBtn(path:Editable?){
        spRepo.insert(ScanPath(path.toString()))
    }
}
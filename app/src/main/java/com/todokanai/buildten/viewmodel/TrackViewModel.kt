package com.todokanai.buildten.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.todokanai.buildten.myobjects.Constants.ACTION_PAUSE_PLAY
import com.todokanai.buildten.repository.TrackRepository
import com.todokanai.buildten.room.Track
import com.todokanai.buildten.tools.MediaPlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(private val trackRepo: TrackRepository):ViewModel(){

    val trackListFlow = trackRepo.trackLibraryFlow      // 음악파일 목록 ->TrackFragment
    private val mediaPlaybackState = MediaPlaybackState()

    fun onItemClick(context: Context, track: Track){
        CoroutineScope(Dispatchers.IO).launch {
            trackRepo.updateCurrentTrack(track)
        }
        mediaPlaybackState.setTrack(track)

        context.sendBroadcast(Intent(ACTION_PAUSE_PLAY))
    }
}
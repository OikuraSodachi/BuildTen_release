package com.todokanai.buildten.myobjects

import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import com.todokanai.buildten.room.Track

object MyObjects {
    val mMediaPlayer = MediaPlayer()
    val isPlaying = MutableLiveData<Boolean>()
    val isLooping = MutableLiveData<Boolean>()
    val isShuffled = MutableLiveData<Boolean>()
    var shuffleSeed : Double = 0.0
    val currentTrack = MutableLiveData<Track>()
    var currentTrackValue : Track? = null
    var playList : List<Track> = emptyList()
}
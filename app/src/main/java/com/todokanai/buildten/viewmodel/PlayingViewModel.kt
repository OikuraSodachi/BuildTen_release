package com.todokanai.buildten.viewmodel

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.todokanai.buildten.myobjects.Constants.ACTION_PAUSE_PLAY
import com.todokanai.buildten.myobjects.Constants.ACTION_REPLAY
import com.todokanai.buildten.myobjects.Constants.ACTION_SHUFFLE
import com.todokanai.buildten.myobjects.Constants.ACTION_SKIP_TO_NEXT
import com.todokanai.buildten.myobjects.Constants.ACTION_SKIP_TO_PREVIOUS
import com.todokanai.buildten.myobjects.MyObjects
import com.todokanai.buildten.myobjects.MyObjects.isLooping
import com.todokanai.buildten.myobjects.MyObjects.isPlaying
import com.todokanai.buildten.myobjects.MyObjects.isShuffled
import com.todokanai.buildten.repository.TrackRepository
import com.todokanai.buildten.room.Track
import com.todokanai.buildten.tools.IconSetter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayingViewModel @Inject constructor(private val trackRepository: TrackRepository): ViewModel() {

    val currentTrack = MyObjects.currentTrack

    val isPlayingState = isPlaying

    val isLoopingState = isLooping

    val isShuffledState = isShuffled

    fun setPausePlayImage(isPlaying:Boolean?): Int {
        return IconSetter().setPausePlayImage(isPlaying)
    }
    fun setLoopingImage(isLooping:Boolean?): Int {
        return IconSetter().setLoopingImage(isLooping)
    }
    fun setShuffleImage(isShuffled:Boolean?): Int {
        return IconSetter().setShuffleImage(isShuffled)
    }
    fun getSeekbarMax(mediaPlayer:MediaPlayer):Int{
        return mediaPlayer.duration
    }
    fun getTotalTime(track:Track?):String{
        return SimpleDateFormat("mm:ss").format(track?.duration)
    }
    fun getCurrentProgress(mediaPlayer:MediaPlayer):String{
        return SimpleDateFormat("mm:ss").format(mediaPlayer.currentPosition)
    }
    fun getCurrentPosition(mediaPlayer:MediaPlayer):Int{
        return mediaPlayer.currentPosition
    }
    fun getTitle(track:Track?):String?{
        return track?.title
    }
    fun getAlbumArt(track: Track?): Uri? {

        return track?.getAlbumUri()
    }
    fun getArtistName(track:Track?):String?{
        return track?.artist
    }

    fun repeatButton(context:Context){
        context.sendBroadcast(Intent(ACTION_REPLAY))
    }
    fun previousButton(context:Context){
        context.sendBroadcast(Intent(ACTION_SKIP_TO_PREVIOUS))
    }
    fun playPauseButton(context:Context) {
        context.sendBroadcast(Intent(ACTION_PAUSE_PLAY))

    }
    fun nextButton(context:Context){
        context.sendBroadcast(Intent(ACTION_SKIP_TO_NEXT))
    }
    fun shuffleButton(context:Context){
        context.sendBroadcast(Intent(ACTION_SHUFFLE))
    }




    }
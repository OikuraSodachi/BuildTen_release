package com.todokanai.buildten.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.todokanai.buildten.myobjects.Constants.ACTION_PAUSE_PLAY
import com.todokanai.buildten.myobjects.Constants.ACTION_REPLAY
import com.todokanai.buildten.myobjects.Constants.ACTION_SHUFFLE
import com.todokanai.buildten.myobjects.Constants.ACTION_SKIP_TO_NEXT
import com.todokanai.buildten.myobjects.Constants.ACTION_SKIP_TO_PREVIOUS
import com.todokanai.buildten.myobjects.MyObjects.currentTrackValue
import com.todokanai.buildten.myobjects.MyObjects.playList
import com.todokanai.buildten.service.ForegroundPlayService
import com.todokanai.buildten.tools.MediaPlaybackState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackBroadcastReceiver : BroadcastReceiver() {

    private val mediaPlaybackState = MediaPlaybackState()

    // 컨트롤 계열 수렴지점?

    override fun onReceive(context: Context, intent: Intent) {
        val intentTrigger = Intent(context, ForegroundPlayService::class.java)
        when (intent.action) {
            ACTION_REPLAY -> {
                mediaPlaybackState.replay()

            }
            ACTION_SKIP_TO_PREVIOUS -> {
                currentTrackValue?.let { mediaPlaybackState.mPrev(playList, it) }

            }
            ACTION_PAUSE_PLAY -> {
                mediaPlaybackState.pausePlay()

            }
            ACTION_SKIP_TO_NEXT -> {
                currentTrackValue?.let { mediaPlaybackState.mNext(playList, it) }

            }
            ACTION_SHUFFLE -> {
                mediaPlaybackState.mShuffle()

            }
        }

        context.startForegroundService(intentTrigger)
    }
}
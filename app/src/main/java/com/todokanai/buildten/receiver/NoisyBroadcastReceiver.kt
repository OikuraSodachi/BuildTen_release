package com.todokanai.buildten.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.todokanai.buildten.myobjects.MyObjects.mMediaPlayer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoisyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY && mMediaPlayer.isPlaying) {
            mMediaPlayer.pause()
        }
    }
}
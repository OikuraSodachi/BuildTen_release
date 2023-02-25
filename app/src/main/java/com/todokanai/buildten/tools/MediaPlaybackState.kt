package com.todokanai.buildten.tools

import android.content.Intent
import android.media.AudioAttributes
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import com.todokanai.buildten.application.MyApplication
import com.todokanai.buildten.myobjects.MyObjects
import com.todokanai.buildten.myobjects.MyObjects.currentTrack
import com.todokanai.buildten.myobjects.MyObjects.currentTrackValue
import com.todokanai.buildten.myobjects.MyObjects.isLooping
import com.todokanai.buildten.myobjects.MyObjects.isShuffled
import com.todokanai.buildten.myobjects.MyObjects.playList
import com.todokanai.buildten.myobjects.MyObjects.shuffleSeed
import com.todokanai.buildten.repository.DataStoreRepository
import com.todokanai.buildten.repository.TrackRepository
import com.todokanai.buildten.room.MyDatabase
import com.todokanai.buildten.room.Track
import com.todokanai.buildten.service.ForegroundPlayService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class MediaPlaybackState() {

    private val myContext = MyApplication.appContext
    private val myDatabase = MyDatabase.getInstance(myContext)
    private val intentTrigger = Intent(myContext, ForegroundPlayService::class.java)
    private val mediaPlayer = MyObjects.mMediaPlayer
    private val isPlaying = MyObjects.isPlaying
    private val dsRepo = DataStoreRepository()
    private val trackRepo = TrackRepository(myDatabase.trackDao(),myDatabase.currentTrackDao())

    fun setMediaPlaybackState(state: Int) {
        val playbackState = PlaybackStateCompat.Builder()
            .run {
                val actions = if (state == PlaybackStateCompat.STATE_PLAYING) {
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SET_REPEAT_MODE or PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
                } else {
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SET_REPEAT_MODE or PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
                }
                setActions(actions)
            }
            //TODO
            .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        ForegroundPlayService.mediaSession.setPlaybackState(playbackState.build())
    }

    fun initMediaPlayer() {
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setMediaPlaybackState(PlaybackStateCompat.STATE_NONE)
            // setVolume(FULL_VOLUME, FULL_VOLUME)
        }
    }

    fun refreshNotification() {
        myContext.startForegroundService(intentTrigger)
    }           // Notification 업데이트

    fun replay() {
        if (isLooping.value == true) {
            dsRepo.saveIsLooping(false)
            isLooping.value = false
            mediaPlayer.isLooping = false
        } else {
            dsRepo.saveIsLooping(true)
            isLooping.value = true
            mediaPlayer.isLooping = true
        }

    }       // 반복재생                   --> onSetRepeatMode

    private fun mStart() {
        mediaPlayer.setOnCompletionListener { currentTrackValue?.let {  mNext(playList, it) } }
        mediaPlayer.start()
        isPlaying.value = true
        setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
        refreshNotification()
    }

    private fun mPause() {
        mediaPlayer.pause()
        isPlaying.value = false
        setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
        refreshNotification()
    }

    fun pausePlay() {
        if (mediaPlayer.isPlaying) {
            mPause()
        } else {
            mStart()
        }
    }       // 일시정지,재생

    fun reset() {
        mediaPlayer.reset()
        setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
    }               // mediaPlayer 비워두기

    fun setTrack(track: Track?) {
        track?.let {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(myContext, track.getTrackUri())
            mediaPlayer.prepare()
            mediaPlayer.isLooping = isLooping.value ?:false
            currentTrack.postValue(track)
            currentTrackValue = track

            CoroutineScope(Dispatchers.IO).launch {
                trackRepo.updateCurrentTrack(track)
            }
            refreshNotification()
        }
    }

    fun mShuffle() {
        if (isShuffled.value == true) {
            dsRepo.saveIsShuffled(false)
            isShuffled.value = false
            playList = playList.sortedBy { it.title }

            // 대충 재생목록 이름순 정렬
        } else {
            val newSeed = Math.random()
            shuffleSeed = newSeed
            dsRepo.saveRandomSeed(newSeed)
            dsRepo.saveIsLooping(true)
            isShuffled.value = true

            playList = playList.shuffled(Random(shuffleSeed.toLong()))
            // 대충 재생목록 셔플
        }

    }

    fun mPrev(playList:List<Track>,currentTrack:Track) {
        setTrack(getPrevTrack(playList,currentTrack))
        mStart()

    }

    fun mNext(playList: List<Track>, currentTrack: Track) {
        setTrack(getNextTrack(playList,currentTrack))
        mStart()
    }


    private fun getNextTrack(playList: List<Track>, currentTrack: Track): Track {
        val currentIndex = playList.indexOf(currentTrack)
        if (currentIndex == -1) {
            Toast.makeText(myContext, "재생목록 정보 불일치", Toast.LENGTH_SHORT).show()
        }
        if (currentIndex == playList.size - 1) {
            return playList.first()
        } else {
            return playList[currentIndex + 1]
        }
    }

    private fun getPrevTrack(playList: List<Track>, currentTrack: Track): Track {
        val currentIndex = playList.indexOf(currentTrack)
        if (currentIndex == -1) {
            Toast.makeText(myContext, "재생목록 정보 불일치", Toast.LENGTH_SHORT).show()
        }
        if (currentIndex == 0) {
            return playList.last()
        } else {
            return playList[currentIndex - 1]
        }
    }
}



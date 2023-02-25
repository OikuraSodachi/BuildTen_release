package com.todokanai.buildten.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaMetadata
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.todokanai.buildten.R
import com.todokanai.buildten.activity.MainActivity
import com.todokanai.buildten.application.MyApplication
import com.todokanai.buildten.myobjects.Constants.ACTION_PAUSE_PLAY
import com.todokanai.buildten.myobjects.Constants.ACTION_REPLAY
import com.todokanai.buildten.myobjects.Constants.ACTION_SHUFFLE
import com.todokanai.buildten.myobjects.Constants.ACTION_SKIP_TO_NEXT
import com.todokanai.buildten.myobjects.Constants.ACTION_SKIP_TO_PREVIOUS
import com.todokanai.buildten.myobjects.Constants.CHANNEL_ID
import com.todokanai.buildten.myobjects.Constants.DUCK_VOLUME
import com.todokanai.buildten.myobjects.Constants.FULL_VOLUME
import com.todokanai.buildten.myobjects.MyObjects
import com.todokanai.buildten.myobjects.MyObjects.currentTrack
import com.todokanai.buildten.myobjects.MyObjects.currentTrackValue
import com.todokanai.buildten.myobjects.MyObjects.isLooping
import com.todokanai.buildten.myobjects.MyObjects.isPlaying
import com.todokanai.buildten.myobjects.MyObjects.isShuffled
import com.todokanai.buildten.myobjects.MyObjects.playList
import com.todokanai.buildten.receiver.NoisyBroadcastReceiver
import com.todokanai.buildten.receiver.TrackBroadcastReceiver
import com.todokanai.buildten.repository.DataStoreRepository
import com.todokanai.buildten.repository.TrackRepository
import com.todokanai.buildten.room.MyDatabase
import com.todokanai.buildten.tools.IconSetter
import com.todokanai.buildten.tools.MediaPlaybackState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@AndroidEntryPoint
class ForegroundPlayService : MediaBrowserServiceCompat() {

    companion object {
        lateinit var mediaSession: MediaSessionCompat
    }

    private val mediaPlayer = MyObjects.mMediaPlayer
    private val myDatabase = MyDatabase.getInstance(MyApplication.appContext)
    private val dsRepo = DataStoreRepository()
    private val trackRepo = TrackRepository(myDatabase.trackDao(),myDatabase.currentTrackDao())
    private val mediaPlaybackState = MediaPlaybackState()
    var notiTitle : String? = null
    var notiArtist : String? = null
    var notiAlbumArt : Uri? = null

    val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener {
        when (it) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Permanent loss of audio focus
                // Pause playback immediately
                //mediaController.transportControls.pause()
                // Wait 30 seconds before stopping playback
                //handler.postDelayed(delayedStopRunnable, TimeUnit.SECONDS.toMillis(30))
                mediaPlayer.stop()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayer.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                mediaPlayer.setVolume(DUCK_VOLUME,
                    DUCK_VOLUME)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                    mediaPlayer.setVolume(FULL_VOLUME,
                        FULL_VOLUME)
                }
            }
        }
    }

    val audioManager by lazy { getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            onPrepare()
            Log.d("mediaSessionCallback","onPlay")
            if (!isAudioFocusGranted()) {
                return
            }
            registerReceiver(NoisyBroadcastReceiver(), noisyIntentFilter)
            mediaSession.isActive = true
            sendBroadcast(Intent(ACTION_PAUSE_PLAY))
        }
        override fun onStop() {
            Log.d("mediaSessionCallback","onStop")
            releaseAudioFocus()
            mediaPlaybackState.setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED)
            unregisterReceiver(NoisyBroadcastReceiver())
        }
        override fun onPause() {
            Log.d("mediaSessionCallback","onPause")
            sendBroadcast(Intent(ACTION_PAUSE_PLAY))
        }
        override fun onSetShuffleMode(shuffleMode: Int) {
            Log.d("mediaSessionCallback","onSetShuffleMode")
            sendBroadcast(Intent(ACTION_SHUFFLE))
            super.onSetShuffleMode(shuffleMode)
        }
        override fun onSkipToPrevious() {
            Log.d("mediaSessionCallback","onSkipToPrevious")
            sendBroadcast(Intent(ACTION_SKIP_TO_PREVIOUS))
            super.onSkipToPrevious()
        }
        override fun onSkipToNext() {
            Log.d("mediaSessionCallback","onSkipToNext")
            sendBroadcast(Intent(ACTION_SKIP_TO_NEXT))
            super.onSkipToNext()

        }
        override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
            Log.d("mediaSessionCallback","onMediaButtonEvent: ${mediaButtonEvent?.action.toString()}")
            return super.onMediaButtonEvent(mediaButtonEvent)
        }
        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            Log.d("mediaSessionCallback","onPlayFromMediaId")
        }
        override fun onSetRepeatMode(repeatMode: Int) {
            Log.d("mediaSessionCallback","onSetRepeatMode")
            sendBroadcast(Intent(ACTION_REPLAY))
            super.onSetRepeatMode(repeatMode)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("service","onCreate")
        registerReceiver(TrackBroadcastReceiver(), IntentFilter(ACTION_REPLAY))
        registerReceiver(TrackBroadcastReceiver(), IntentFilter(ACTION_SKIP_TO_PREVIOUS))
        registerReceiver(TrackBroadcastReceiver(), IntentFilter(ACTION_PAUSE_PLAY))
        registerReceiver(TrackBroadcastReceiver(), IntentFilter(ACTION_SKIP_TO_NEXT))
        registerReceiver(TrackBroadcastReceiver(), IntentFilter(ACTION_SHUFFLE))


        CoroutineScope(Dispatchers.IO).launch {
            val current = withContext(Dispatchers.Main){trackRepo.currentTrack()}
            val shuffler = withContext(Dispatchers.Main){dsRepo.isShuffled()}
            val seed = withContext(Dispatchers.Main){dsRepo.randomSeed()}
            currentTrack.postValue(current)
            currentTrackValue = current
            mediaPlaybackState.setTrack(current)
            isLooping.postValue(withContext(Dispatchers.Main){ dsRepo.isLooping()})
            isShuffled.postValue(withContext(Dispatchers.Main){dsRepo.isShuffled()})
            if(shuffler) {
                playList = trackRepo.trackLibrary().shuffled(Random(seed.toLong()))
            } else {
                playList = trackRepo.trackLibrary()

            }
        }


        currentTrack.observeForever(){
            it?.let {
                notiTitle = it.title

                notiArtist = it.artist

                notiAlbumArt = it.getAlbumUri()
            }
        }

        isLooping.observeForever(){
            mediaPlayer.isLooping = it
            dsRepo.saveIsLooping(it)
            mediaPlaybackState.refreshNotification()
        }

        isShuffled.observeForever(){
            dsRepo.saveIsShuffled(it)
            mediaPlaybackState.refreshNotification()
        }

        initMediaSession()
        mediaPlaybackState.initMediaPlayer()



    }

    fun initMediaSession() {
        mediaSession = MediaSessionCompat(applicationContext, "MediaSession").apply {
            setCallback(mediaSessionCallback)
            this@ForegroundPlayService.sessionToken = sessionToken
        }
    }

    fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_NONE             //  알림의 중요도
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(serviceChannel)
    }                  // 서비스 채널 생성

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("service","onStartCommand")
        createNotificationChannel()
        MediaButtonReceiver.handleIntent(mediaSession, intent)

        val mainOpenIntent = Intent(this, MainActivity::class.java)
        val mainIntent = PendingIntent.getActivity(this,0, Intent(mainOpenIntent), PendingIntent.FLAG_IMMUTABLE)

        val repeatIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_REPLAY), PendingIntent.FLAG_IMMUTABLE)
        val prevIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_SKIP_TO_PREVIOUS), PendingIntent.FLAG_IMMUTABLE)
        val pauseplayIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_PAUSE_PLAY), PendingIntent.FLAG_IMMUTABLE)
        val nextIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_SKIP_TO_NEXT), PendingIntent.FLAG_IMMUTABLE)
        val shuffleIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_SHUFFLE), PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = NotificationManagerCompat.from(this)

        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, "$notiTitle")
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "$notiArtist")
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, notiAlbumArt.toString())
                .build()
        )

        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID)       // 알림바에 띄울 알림을 만듬
                .setContentTitle("null Title Noti") // 알림의 제목
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(NotificationCompat.Action(IconSetter().setLoopingImage(isLooping.value), "REPEAT", repeatIntent))
                .addAction(NotificationCompat.Action(R.drawable.ic_baseline_skip_previous_24,"PREV",prevIntent))
                .addAction(NotificationCompat.Action(IconSetter().setPausePlayImage(isPlaying.value), "pauseplay", pauseplayIntent))
                .addAction(NotificationCompat.Action(R.drawable.ic_baseline_skip_next_24,"NEXT",nextIntent))
                .addAction(NotificationCompat.Action(IconSetter().setShuffleImage(isShuffled.value), "SHUFFLE", shuffleIntent))
                .setContentIntent(mainIntent)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 2, 3)     // 확장하지 않은상태 알림에서 쓸 기능의 배열번호
                        .setMediaSession(mediaSession.sessionToken)
                )
                .setOngoing(true)
                .build()
        notificationManager.notify(1,notification)

        startForeground(1, notification)              // 지정된 알림을 실행

        return super.onStartCommand(intent, flags, startId)
    }  // 서비스 활동개시
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>,
    ) {
        result.sendResult(mutableListOf())
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?,
    ): BrowserRoot? {
        if (clientPackageName == packageName) {
            return BrowserRoot("MediaSessionExperiment", null)
        }
        return null
    }

    fun isAudioFocusGranted(): Boolean {
        val requestResult = audioManager.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN)
        return requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun releaseAudioFocus() {
        audioManager.abandonAudioFocus(audioFocusChangeListener)
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        releaseAudioFocus()
        mediaSession.release()
        super.onDestroy()
    }
}
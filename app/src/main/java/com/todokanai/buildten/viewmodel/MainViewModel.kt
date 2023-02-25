package com.todokanai.buildten.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.todokanai.buildten.repository.DataStoreRepository
import com.todokanai.buildten.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class MainViewModel @Inject constructor(private val trackRepo : TrackRepository): ViewModel() {
    private val dsRepo = DataStoreRepository()

    fun launchForeground(context:Context,intentService:Intent){

        ContextCompat.startForegroundService(context, intentService)
    }

    fun exit(activity:Activity,intentService: Intent){
        finishAffinity(activity)
        activity.stopService(intentService)      // 서비스 종료
        System.runFinalization()
        exitProcess(0)
    }

}
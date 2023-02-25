package com.todokanai.buildten.repository

import com.todokanai.buildten.room.CurrentTrackDao
import com.todokanai.buildten.room.Track
import com.todokanai.buildten.room.TrackDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(private val trackDao: TrackDao,private val currentTrackDao: CurrentTrackDao){

    val trackLibraryFlow = trackDao.getAll()

    suspend fun trackLibrary() = withContext(Dispatchers.IO){ trackDao.getAllNonFlow()}        // withContext 성공사례?

    fun playList() = trackDao.getAll()

    suspend fun insert(track : Track) = trackDao.insert(track)

    suspend fun deleteAll() = trackDao.deleteAll()

    //---------------

//    val currentTrack : Flow<CurrentTrack?> = currentTrackDao.getByIndex(1)

    suspend fun currentTrack() = withContext(Dispatchers.IO){currentTrackDao.getByIndexNonFlow(1)?.toTrack()}



    suspend fun updateCurrentTrack(track:Track) {
        currentTrackDao.deleteAll()
        currentTrackDao.insert(track.toCurrentTrack())
    }

    //--------

    fun test(trackList:List<Track>,track:Track){
        println("pos: ${trackList.indexOf(track)}")

    }

}
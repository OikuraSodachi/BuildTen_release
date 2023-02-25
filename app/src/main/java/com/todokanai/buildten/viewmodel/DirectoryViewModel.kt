package com.todokanai.buildten.viewmodel

import androidx.lifecycle.ViewModel
import com.todokanai.buildten.repository.ScanPathRepository
import com.todokanai.buildten.room.ScanPath
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DirectoryViewModel @Inject constructor(private val spRepo : ScanPathRepository ) : ViewModel(){

    val pathListFlow = spRepo.getAllPath()

    suspend fun pathList() = spRepo.getAll()

    fun onItemClick(scanPath: ScanPath){
        spRepo.deleteByPath(scanPath.scanPath!!)
    }
}
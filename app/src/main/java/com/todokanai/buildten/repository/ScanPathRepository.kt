package com.todokanai.buildten.repository

import com.todokanai.buildten.room.ScanPath
import com.todokanai.buildten.room.ScanPathDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanPathRepository @Inject constructor(private val scanPathDao : ScanPathDao){

    suspend fun getAll() = withContext(Dispatchers.Main){scanPathDao.getAllPathNonFlow()}

    fun getAllPath() : Flow<List<ScanPath>> =
        scanPathDao.getAllPath().map { list->
            list.map{ScanPath(it)}
        }

    fun insert(scanPath: ScanPath) {
        CoroutineScope(Dispatchers.IO).launch {
            scanPathDao.insert(scanPath)
        }
    }

    fun deleteItem(scanPath:ScanPath){
        CoroutineScope(Dispatchers.IO).launch{
            scanPathDao.deleteItem(scanPath)
        }
    }


    fun deleteByPath(string:String){
        CoroutineScope(Dispatchers.IO).launch {
            scanPathDao.deleteByPath(string)
        }
    }

    fun deleteAll(){
        CoroutineScope(Dispatchers.IO).launch {
            scanPathDao.deleteAll()
        }
    }

}
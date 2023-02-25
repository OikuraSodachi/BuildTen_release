package com.todokanai.buildten.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.todokanai.buildten.application.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DataStoreRepository {
    companion object{
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mydatastore")
        val DATASTORE_STRING = stringPreferencesKey("datastore_string")
        val DATASTORE_IS_LOOPING = booleanPreferencesKey("datastore_is_looping")
        val DATASTORE_IS_SHUFFLED = booleanPreferencesKey("datastore_is_shuffled")
        val DATASTORE_RANDOM_SEED = doublePreferencesKey("datastore_random_seed")
        val DATASTORE_CURRENT_URI = stringPreferencesKey("datastore_current_uri")
    }
    private val myContext = MyApplication.appContext

    fun saveString( value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            myContext.dataStore.edit {
                it[DATASTORE_STRING] = value
                Log.d("datastore", "datastore_string insert: $value")
            }
        }
    }

    val dataStoreString: Flow<String?> = myContext.dataStore.data.map{
        it[DATASTORE_STRING]
    }
    //------------

    fun saveIsLooping( isLooping : Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            myContext.dataStore.edit {
                it[DATASTORE_IS_LOOPING] = isLooping
            }
        }
    }

    val isLooping : Flow<Boolean?> = myContext.dataStore.data.map {
        it[DATASTORE_IS_LOOPING]
    }

    suspend fun isLooping() : Boolean {
        return myContext.dataStore.data.first()[DATASTORE_IS_LOOPING] ?:false
    }


    //--------------------

    fun saveIsShuffled( isShuffled : Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            myContext.dataStore.edit {
                it[DATASTORE_IS_SHUFFLED] = isShuffled
            }
        }
    }

    val isShuffled : Flow<Boolean?> = myContext.dataStore.data.map {
        it[DATASTORE_IS_SHUFFLED]
    }

    suspend fun isShuffled() : Boolean {
        return myContext.dataStore.data.first()[DATASTORE_IS_SHUFFLED] ?:false
    }

    fun saveRandomSeed(seed:Double){
        CoroutineScope(Dispatchers.IO).launch {
            myContext.dataStore.edit {
                it[DATASTORE_RANDOM_SEED] = seed
            }
        }
    }

    val randomSeed : Flow<Double?> = myContext.dataStore.data.map {
        it[DATASTORE_RANDOM_SEED]
    }

    suspend fun randomSeed() : Double {
        return myContext.dataStore.data.first()[DATASTORE_RANDOM_SEED] ?:0.0
    }




}
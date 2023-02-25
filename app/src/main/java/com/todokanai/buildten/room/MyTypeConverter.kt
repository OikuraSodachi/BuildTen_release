package com.todokanai.buildten.room

import androidx.room.TypeConverter
import com.google.gson.Gson

class MyTypeConverter {
    @TypeConverter
    fun listToJson(value: List<Track>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value:String) = Gson().fromJson(value,Array<Track>::class.java).toList()
}
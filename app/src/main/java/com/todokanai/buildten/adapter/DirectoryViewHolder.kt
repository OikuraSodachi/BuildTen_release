package com.todokanai.buildten.adapter

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.todokanai.buildten.R
import com.todokanai.buildten.room.ScanPath

class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val pathText = itemView.findViewById<TextView>(R.id.directoryTextView)
    val directoryDeleteBtn = itemView.findViewById<ImageButton>(R.id.directoryDeleteButton)


    fun setDirectory(scanPath: ScanPath?) {
        pathText.text = scanPath?.scanPath
    }
}
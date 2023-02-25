package com.todokanai.buildten.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.todokanai.buildten.R
import com.todokanai.buildten.room.Track

class TrackRecyclerAdapter(private val onItemClicked:(track:Track)->Unit) : RecyclerView.Adapter<TrackViewHolder>() {
    var trackList = emptyList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_recycler, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(trackViewHolder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        trackViewHolder.setTrack(track)
        trackViewHolder.itemView.setOnClickListener {
            onItemClicked(track)     // TrackFragment에서 감지(detect) 할수 있게 하는 트리거
                                        // 대충 뷰모델과 연관없는 내용
        }
        trackViewHolder.imageAlbum.setOnClickListener {
            onItemClicked(track)
        }

    }
}
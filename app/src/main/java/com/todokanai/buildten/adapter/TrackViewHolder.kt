package com.todokanai.buildten.adapter

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.todokanai.buildten.R
import com.todokanai.buildten.room.Track
import java.text.SimpleDateFormat

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageAlbum = itemView.findViewById<ImageView>(R.id.imageAlbum)
    private val textArtist = itemView.findViewById<TextView>(R.id.textArtist)
    private val textTitle = itemView.findViewById<TextView>(R.id.textTitle)
    private val textDuration = itemView.findViewById<TextView>(R.id.textDuration)
    var getUri : Uri? = null

    fun setTrack(track: Track) {
        imageAlbum.setImageURI(track.getAlbumUri())     //앨범이미지 투영
        textArtist.text = track.artist
        textTitle.text = "${track.title}"
        textDuration.text = SimpleDateFormat("mm:ss").format(track.duration)
        // 홀더에 내용 추가
        this.getUri = track.getTrackUri()
    }
}
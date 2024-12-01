package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.Date
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameView: TextView = itemView.findViewById(R.id.trackName)
    private val artistNameView: TextView = itemView.findViewById(R.id.artistName)
    private val trackTimeView: TextView = itemView.findViewById(R.id.trackTime)
    private val artworkUrl100View: ImageView = itemView.findViewById(R.id.trackImg)


    fun bind(track: Track) {
        trackNameView.text = track.trackName
        artistNameView.text = track.artistName
        val formattedTime =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        trackTimeView.text = formattedTime

        Glide.with(itemView.context).load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .centerCrop()
            .into(artworkUrl100View)
        artistNameView.requestLayout()
    }
}
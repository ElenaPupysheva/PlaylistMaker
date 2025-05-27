package com.practicum.playlistmaker.player.ui

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist

class BottomSheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val playlistImage: ImageView = itemView.findViewById(R.id.playlist_image)
    private val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
    private val trackCount: TextView = itemView.findViewById(R.id.track_count)

    fun bind(playlist: Playlist) {
        playlistName.text = playlist.name
        trackCount.text = "${playlist.trackCount} треков"

        if (playlist.imagePath.isNotBlank()) {
            playlistImage.setImageURI(Uri.parse(playlist.imagePath))
        } else {
            playlistImage.setImageResource(R.drawable.placeholder)
        }
    }
}

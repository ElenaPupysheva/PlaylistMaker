package com.practicum.playlistmaker.media.ui

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist


class PlaylistsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val title: TextView = itemView.findViewById(R.id.title)
    private val description: TextView = itemView.findViewById(R.id.description)
    private val playlistPicture: ImageView = itemView.findViewById(R.id.playlistPicture)

    fun bind(playlist: Playlist) {
        title.text = playlist.name
        description.text = playlist.description
        if (playlist.imagePath.isNotEmpty()) {
            playlistPicture.setImageURI(Uri.parse(playlist.imagePath))
        } else {
            playlistPicture.setImageResource(R.drawable.placeholder)
        }
    }
}

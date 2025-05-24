package com.practicum.playlistmaker.media.ui

import com.practicum.playlistmaker.media.ui.PlaylistsViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist

class PlaylistsAdapter(private val playlists: List<Playlist>) :
    RecyclerView.Adapter<PlaylistsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_view, parent, false)
        return PlaylistsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size
}

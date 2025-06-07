package com.practicum.playlistmaker.media.ui

import com.practicum.playlistmaker.media.ui.PlaylistsViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.domain.models.Playlist

class PlaylistsAdapter(
    private val playlists: List<Playlist>,
    private val onItemClick: (Long) -> Unit
) :
    RecyclerView.Adapter<PlaylistsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val binding = PlaylistViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onItemClick(playlist.id)
        }
    }

    override fun getItemCount(): Int = playlists.size


}

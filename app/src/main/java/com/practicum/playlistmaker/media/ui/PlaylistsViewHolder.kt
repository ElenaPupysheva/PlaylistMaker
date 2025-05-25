package com.practicum.playlistmaker.media.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistViewBinding
import com.practicum.playlistmaker.domain.models.Playlist
import java.io.File

class PlaylistsViewHolder(private val binding: PlaylistViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        binding.title.text = playlist.name

        val context = binding.root.context
        val count = playlist.trackCount
        binding.trackCount.text = context.resources.getQuantityString(
            R.plurals.tracks_count, count, count
        )

        if (playlist.imagePath.isNotEmpty()) {
            val file = File(playlist.imagePath)
            Glide.with(context)
                .load(file)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(binding.playlistPicture)
        } else {
            binding.playlistPicture.setImageResource(R.drawable.placeholder)
        }
    }
}

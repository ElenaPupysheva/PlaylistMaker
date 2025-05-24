package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.domain.PlaylistInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BottomSheetPlaylistsAdapter(
    private val playlists: List<Playlist>,
    private val currentTrack: Track,
    private val playlistInteractor: PlaylistInteractor,
    private val bottomSheetBehavior: BottomSheetBehavior<*>
) : RecyclerView.Adapter<BottomSheetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bottomsheet_view, parent, false)
        return BottomSheetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)

        holder.itemView.setOnClickListener {
            if (playlist.trackIds.contains(currentTrack.trackId.toLong())) {
                Toast.makeText(
                    holder.itemView.context,
                    "Трек уже добавлен в плейлист ${playlist.name}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val updatedPlaylist = playlist.copy(
                    trackIds = playlist.trackIds + currentTrack.trackId.toLong(),
                    trackCount = playlist.trackCount + 1
                )

                CoroutineScope(Dispatchers.IO).launch {
                    playlistInteractor.updatePlaylist(updatedPlaylist)
                    playlistInteractor.saveTrackToPlaylistTracks(currentTrack)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            holder.itemView.context,
                            "Добавлено в плейлист ${playlist.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = playlists.size
}
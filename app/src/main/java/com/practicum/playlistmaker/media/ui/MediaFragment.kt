package com.practicum.playlistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.EXTRA_TRACK
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.presentation.FavoritesViewModel
import com.practicum.playlistmaker.media.presentation.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragment : Fragment() {

    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val nav = findNavController()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PlaylistMakerTheme {
                    MediaCompose(
                        favoritesViewModel = favoritesViewModel,
                        playlistsViewModel = playlistsViewModel,
                        onOpenPlayer = { track: Track ->
                            startActivity(
                                Intent(requireContext(), PlayerActivity::class.java)
                                    .putExtra(EXTRA_TRACK, Gson().toJson(track))
                            )
                        },
                        onOpenDetails = { playlistId: Long ->
                            nav.navigate(
                                R.id.detailedFragment,
                                Bundle().apply { putLong("playlistId", playlistId) }
                            )
                        },
                        onCreateNewPlaylist = {
                            nav.navigate(R.id.newPlaylistFragment)
                        }
                    )
                }
            }
        }
    }
}

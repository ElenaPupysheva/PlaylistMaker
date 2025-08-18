package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.presentation.PlaylistsViewModel
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private val vm: PlaylistsViewModel by viewModel()

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
                    PlaylistsCompose(
                        viewModel = vm,
                        onCreateNew = { nav.navigate(R.id.newPlaylistFragment) },
                        onOpenDetails = { id ->
                            nav.navigate(
                                R.id.detailedFragment,
                                Bundle().apply { putLong("playlistId", id) }
                            )
                        }
                    )
                }
            }
        }
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }
}
package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.media.domain.PlaylistInteractor
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlaylistsAdapter
    private val playlistInteractor: PlaylistInteractor by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = PlaylistsAdapter(emptyList())
        binding.playlistsRecyclerView.adapter = adapter

        binding.newPlaylist.setOnClickListener {
            findNavController().navigate(com.practicum.playlistmaker.R.id.newPlaylistFragment)
        }

        loadPlaylists()
    }

    override fun onResume() {
        super.onResume()
        loadPlaylists()
    }

    private fun loadPlaylists() {
        lifecycleScope.launch {
            val playlists: List<Playlist> = playlistInteractor.getAllPlaylists()
            if (playlists.isEmpty()) {
                binding.placeholder.visibility = View.VISIBLE
                binding.playlistsRecyclerView.visibility = View.GONE
            } else {
                binding.placeholder.visibility = View.GONE
                binding.playlistsRecyclerView.visibility = View.VISIBLE
                adapter = PlaylistsAdapter(playlists)
                binding.playlistsRecyclerView.adapter = adapter
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): PlaylistFragment {
            return PlaylistFragment()
        }
    }
}


package com.practicum.playlistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.FragmentFavoriteBinding
import com.practicum.playlistmaker.domain.models.EXTRA_TRACK
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.presentation.FavoriteState
import com.practicum.playlistmaker.media.presentation.FavoritesViewModel
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModel()

    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter(emptyList())
        adapter.setOnTrackClickListener { track ->
            viewModel.clickDebounce(track)
        }

        binding.recyclerFavourite.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFavourite.adapter = adapter

        viewModel.loadFavorites()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteState.Loading -> {
                    binding.recyclerFavourite.visibility = View.GONE
                    binding.placeholder.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }

                is FavoriteState.Empty -> {
                    binding.recyclerFavourite.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    binding.placeholder.visibility = View.VISIBLE
                }

                is FavoriteState.Content -> {
                    binding.progressBar.visibility = View.GONE
                    binding.placeholder.visibility = View.GONE
                    binding.recyclerFavourite.visibility = View.VISIBLE
                    adapter.updateTracks(state.tracks)
                }
            }
        }

    viewModel.onTrackClickTrigger.observe(viewLifecycleOwner) { track ->
            openPlayer(track)
        }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra(EXTRA_TRACK, Gson().toJson(track))
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }
}

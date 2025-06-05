package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.databinding.FragmentDetailedPlaylistBinding
import com.practicum.playlistmaker.media.presentation.DetailedPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailedFragment : Fragment() {

    private var _binding: FragmentDetailedPlaylistBinding? = null
    private val viewModel: DetailedPlaylistViewModel by viewModel()

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailedPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistId = arguments?.getLong("playlistId") ?: return

        viewModel.loadPlaylistById(playlistId)
        val toolbar = binding.toolbarPlayer
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.musicTrackName.text = "Best songs 2021"
        binding.playerYear.text = "2022"
        binding.totalTime.text = "300 минут"
        binding.totalTracks.text = "98 треков"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.practicum.playlistmaker.media.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentDetailedPlaylistBinding
import com.practicum.playlistmaker.databinding.ItemBottomsheetPlaylistBinding
import com.practicum.playlistmaker.domain.models.EXTRA_TRACK
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.media.presentation.DetailedPlaylistViewModel
import com.practicum.playlistmaker.player.ui.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailedFragment : Fragment() {

    private var _binding: FragmentDetailedPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailedPlaylistViewModel by viewModel()

    private var menuPlaylistBinding: ItemBottomsheetPlaylistBinding? = null
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var adapter: PlaylistTracksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailedPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarPlayer.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        val playlistId = arguments?.getLong("playlistId") ?: return
        viewModel.loadPlaylistById(playlistId)

        val playlistTracksBottomSheet = binding.playlistsBottomSheet
        val playlistBottomSheetBehavior = BottomSheetBehavior.from(playlistTracksBottomSheet)
        playlistBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        adapter = PlaylistTracksAdapter(
            tracks = emptyList(),
            onTrackClick = { track -> openPlayer(track) },
            onTrackLongClick = { track -> showDeleteDialog(track) }
        )

        binding.tracksRecyclerBottomSheet.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRecyclerBottomSheet.adapter = adapter

        viewModel.state.observe(viewLifecycleOwner) { playlist ->
            binding.musicTrackName.text = playlist.name
            binding.playerYear.text = playlist.description
            binding.totalTracks.text = "${playlist.trackCount} треков"

            if (playlist.imagePath.isNotEmpty()) {
                Glide.with(requireContext())
                    .load(File(playlist.imagePath))
                    .into(binding.musicTrackCover)
            } else {
                binding.musicTrackCover.setImageResource(R.drawable.placeholder)
            }
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.updateData(tracks)
            binding.totalTime.text = if (tracks.isNullOrEmpty()) {
                "0 минут"
            } else {
                val totalDurationMillis = tracks.sumOf { it.trackTimeMillis }
                val totalMinutes = totalDurationMillis / (1000 * 60)
                "$totalMinutes минут"
            }
        }

        val container: FrameLayout =
            binding.menuBottomSheet.findViewById(R.id.playlist_info_container)
        menuPlaylistBinding = ItemBottomsheetPlaylistBinding.inflate(
            LayoutInflater.from(requireContext()), container, false
        )
        container.addView(menuPlaylistBinding!!.root)

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
            peekHeight = 0
        }
        binding.menuBottomSheet.visibility = View.GONE
        binding.overlay.visibility = View.GONE

        binding.moreButtton.setOnClickListener {
            val playlist = viewModel.state.value ?: return@setOnClickListener
            updateMenuContent(playlist)
            binding.menuBottomSheet.visibility = View.VISIBLE
            binding.overlay.visibility = View.VISIBLE
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.overlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.overlay.visibility = View.GONE
            binding.menuBottomSheet.visibility = View.GONE
        }

        binding.menuEdit.setOnClickListener {
            val playlist = viewModel.state.value ?: return@setOnClickListener
            val bundle = Bundle().apply { putParcelable("playlist", playlist) }
            findNavController().navigate(R.id.newPlaylistFragment, bundle)

            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.overlay.visibility = View.GONE
            binding.menuBottomSheet.visibility = View.GONE
        }

        binding.menuDelete.setOnClickListener {
            val playlist = viewModel.state.value ?: return@setOnClickListener

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Удалить плейлист")
                .setMessage("Вы уверены, что хотите удалить этот плейлист?")
                .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Удалить") { dialog, _ ->
                    viewModel.deletePlaylist(playlist) {
                        dialog.dismiss()
                        findNavController().navigateUp()
                        Toast.makeText(requireContext(), "Плейлист удалён", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .show()

            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.overlay.visibility = View.GONE
            binding.menuBottomSheet.visibility = View.GONE
        }

        binding.menuShare.setOnClickListener {
            sharePlaylist()
        }

        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }
    }

    private fun updateMenuContent(playlist: Playlist) {
        menuPlaylistBinding?.apply {
            playlistName.text = playlist.name
            trackCount.text = "${playlist.trackCount} треков"
            if (playlist.imagePath.isNotEmpty()) {
                Glide.with(requireContext())
                    .load(File(playlist.imagePath))
                    .into(playlistImage)
            } else {
                playlistImage.setImageResource(R.drawable.placeholder)
            }
        }
    }

    private fun showDeleteDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить трек")
            .setMessage("Вы уверены, что хотите удалить трек из плейлиста?")
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Удалить") { dialog, _ ->
                viewModel.removeTrack(track)
                dialog.dismiss()
            }
            .show()
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(requireContext(), PlayerActivity::class.java)
        intent.putExtra(EXTRA_TRACK, Gson().toJson(track))
        startActivity(intent)
    }

    private fun sharePlaylist() {
        val playlist = viewModel.state.value
        val tracks = viewModel.tracks.value.orEmpty()

        if (playlist != null && tracks.isNotEmpty()) {
            val shareText = buildShareText(playlist, tracks)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, "Поделиться плейлистом"))
        } else {
            Toast.makeText(requireContext(), "Плейлист пуст", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildShareText(playlist: Playlist, tracks: List<Track>): String {
        val builder = StringBuilder()
        builder.appendLine(playlist.name)
        builder.appendLine(playlist.description)
        builder.appendLine("${tracks.size} треков")

        val formatter = java.text.SimpleDateFormat("mm:ss", java.util.Locale.getDefault())
        tracks.forEachIndexed { index, track ->
            val duration = formatter.format(track.trackTimeMillis)
            builder.appendLine("${index + 1}. ${track.artistName} - ${track.trackName} ($duration)")
        }

        return builder.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        menuPlaylistBinding = null
        _binding = null
    }
}


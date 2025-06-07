package com.practicum.playlistmaker.media.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewplaylistBinding
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.media.domain.PlaylistInteractor
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream
import android.graphics.ImageDecoder
import androidx.navigation.fragment.findNavController

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewplaylistBinding? = null
    private val binding get() = _binding!!
    private var editingPlaylist: Playlist? = null

    private val playlistInteractor: PlaylistInteractor by inject()
    private var isModified = false
    private var savedImagePath: String? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                binding.musicTrackCover.setImageURI(uri)
                binding.iconOverlay.visibility = View.GONE
                savedImagePath = saveImageToPrivateStorage(uri)
                isModified = true
            } ?: Log.d("PhotoPicker", "No media selected")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewplaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editingPlaylist = arguments?.getParcelable("playlist")

        val isEditMode = editingPlaylist != null
        if (isEditMode) {
            val playlist = editingPlaylist!!
            savedImagePath = playlist.imagePath
            binding.toolbar.title = getString(R.string.menu_edit)
            binding.createButton.text = getString(R.string.save)
            binding.editNameField.setText(playlist.name)
            binding.editDescriptionField.setText(playlist.description)
            if (playlist.imagePath.isNotEmpty()) {
                binding.musicTrackCover.setImageURI(File(playlist.imagePath).toUri())
                binding.iconOverlay.visibility = View.GONE
            }
        } else {
            savedImagePath = null
            binding.toolbar.title = getString(R.string.new_playlist)
            binding.createButton.text = getString(R.string.create)
            binding.musicTrackCover.setImageResource(R.drawable.rounded_rectangle)
            binding.iconOverlay.visibility = View.VISIBLE
            binding.createButton.isEnabled = false
        }

        binding.editNameField.doOnTextChanged { text, _, _, _ ->
            binding.createButton.isEnabled = !text.isNullOrBlank()
            isModified = true
        }
        binding.editDescriptionField.doOnTextChanged { _, _, _, _ ->
            isModified = true
        }
        binding.imageFrame.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.toolbar.setNavigationOnClickListener {
            handleExit()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleExit()
                }
            }
        )

        binding.createButton.setOnClickListener {
            val name = binding.editNameField.text.toString()
            val description = binding.editDescriptionField.text.toString()

            val updatedPlaylist = editingPlaylist?.copy(
                name = name,
                description = description,
                imagePath = savedImagePath ?: ""
            ) ?: Playlist(
                name = name,
                description = description,
                imagePath = savedImagePath ?: "",
                trackIds = emptyList(),
                trackCount = 0
            )

            viewLifecycleOwner.lifecycleScope.launch {
                if (isEditMode) {
                    playlistInteractor.updatePlaylist(updatedPlaylist)
                    Toast.makeText(requireContext(), "Изменения сохранены", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    playlistInteractor.savePlaylist(updatedPlaylist)
                    Toast.makeText(
                        requireContext(),
                        "Плейлист \"${updatedPlaylist.name}\" создан",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                isModified = false
                findNavController().navigateUp()
            }
        }
    }

    private fun handleExit() {
        if (isModified) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.areDone)
                .setMessage(R.string.lostData)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.finish) { _, _ ->
                    findNavController().navigateUp()
                }
                .show()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        val context = requireContext()
        val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        val fileName = "playlist_cover_${System.currentTimeMillis()}.jpg"
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!dir.exists()) dir.mkdirs()
        val imageFile = File(dir, fileName)

        FileOutputStream(imageFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return imageFile.absolutePath
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

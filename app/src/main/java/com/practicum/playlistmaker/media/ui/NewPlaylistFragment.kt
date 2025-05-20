package com.practicum.playlistmaker.media.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Build
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.databinding.FragmentNewplaylistBinding
import java.io.File
import java.io.FileOutputStream
import android.graphics.ImageDecoder
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.media.domain.PlaylistInteractor
import org.koin.android.ext.android.inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewplaylistBinding? = null
    private val binding get() = _binding!!

    private var isModified = false
    private var playlistName: String = ""
    private val playlistInteractor: PlaylistInteractor by inject()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.musicTrackCover.setImageURI(uri)
                binding.iconOverlay.visibility = View.GONE
                saveImageToPrivateStorage(uri)
                isModified = true
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
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

        binding.editName.doOnTextChanged { text, _, _, _ ->
            binding.createButton.isEnabled = !text.isNullOrBlank()
            isModified = true
            playlistName = text.toString()
        }

        binding.editDescription.doOnTextChanged { _, _, _, _ ->
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
            })

        val savedFile = getSavedImageFile()
        if (savedFile.exists()) {
            binding.musicTrackCover.setImageURI(savedFile.toUri())
            binding.iconOverlay.visibility = View.GONE
        } else {
            binding.iconOverlay.visibility = View.VISIBLE
        }


        binding.createButton.setOnClickListener {
            val playlist = Playlist(
                name = binding.editName.text.toString(),
                description = binding.editDescription.text.toString(),
                imagePath = getSavedImageFile().absolutePath,
                trackIds = emptyList(),
                trackCount = 0
            )

            viewLifecycleOwner.lifecycleScope.launch {
                playlistInteractor.savePlaylist(playlist)
                Toast.makeText(
                    requireContext(),
                    "Плейлист \"${playlist.name}\" создан",
                    Toast.LENGTH_SHORT
                ).show()
                isModified = false
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun handleExit() {
        if (isModified) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Завершить создание плейлиста?")
                .setMessage("Все несохраненные данные будут потеряны")
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Завершить") { _, _ ->
                    parentFragmentManager.popBackStack()
                }
                .show()
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        try {
            val context = requireContext()
            val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }

            val imageFile = getSavedImageFile()
            imageFile.parentFile?.mkdirs()

            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            Log.d("PhotoSave", "Image saved: ${imageFile.absolutePath}")
        } catch (e: Exception) {
            Log.e("PhotoSave", "Failed to save image: ${e.message}", e)
        }
    }

    private fun getSavedImageFile(): File {
        val dir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        return File(dir, "first_cover.jpg")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): NewPlaylistFragment {
            return NewPlaylistFragment()
        }
    }
}

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.databinding.FragmentNewplaylistBinding
import java.io.File
import java.io.FileOutputStream
import android.graphics.ImageDecoder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.media.domain.PlaylistInteractor
import org.koin.android.ext.android.inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewplaylistBinding? = null
    private val playlistInteractor: PlaylistInteractor by inject()

    private var isModified = false
    private var playlistName: String = ""
    private var savedImagePath: String? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                _binding?.let { binding ->
                binding.musicTrackCover.setImageURI(uri)
                binding.iconOverlay.visibility = View.GONE
                }
                savedImagePath = saveImageToPrivateStorage(uri)
                isModified = true
            } ?: Log.d("PhotoPicker", "No media selected")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewplaylistBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.let { binding ->
            savedImagePath = null
            binding.musicTrackCover.setImageResource(R.drawable.rounded_rectangle)
            binding.iconOverlay.visibility = View.VISIBLE

            binding.editNameField.doOnTextChanged { text, _, _, _ ->
                binding.createButton.isEnabled = !text.isNullOrBlank()
                isModified = true
                playlistName = text.toString()
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
                val playlist = Playlist(
                    name = binding.editNameField.text.toString(),
                    description = binding.editDescriptionField.text.toString(),
                    imagePath = savedImagePath ?: "",
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
    }

    private fun handleExit() {
        if (isModified) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.areDone)
                .setMessage(R.string.lostData)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.finish) { _, _ ->
                    parentFragmentManager.popBackStack()
                }
                .show()
        } else {
            parentFragmentManager.popBackStack()
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

    companion object {
        fun newInstance(): NewPlaylistFragment = NewPlaylistFragment()
    }
}

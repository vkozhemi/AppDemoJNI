package com.vkozhemi.appdemo.ui.fragments

import android.R
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.vkozhemi.appdemo.databinding.FragmentSaveBinding
import com.vkozhemi.appdemo.viewmodel.ImagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SaveFragment : Fragment() {

    private var _binding: FragmentSaveBinding? = null
    private val binding get() = _binding
    private val imagesViewModel: ImagesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSaveBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        if (imagesViewModel.isInitialized()) {
            binding?.preview?.setImageBitmap(imagesViewModel.currentCommitted())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val formats = listOf("PNG", "JPEG")
        binding?.formatSpinner?.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item, formats
        )

        if (binding?.fileName?.text.isNullOrBlank()) {
            binding?.fileName?.setText("edited_${System.currentTimeMillis()}")
        }

        binding?.btnSave?.setOnClickListener {
            val nameBase =
                binding?.fileName?.text?.toString()?.trim().takeUnless { it.isNullOrEmpty() }
                    ?: "edited"
            val fmt = when (binding?.formatSpinner?.selectedItem as String) {
                "JPEG" -> Bitmap.CompressFormat.JPEG
                else -> Bitmap.CompressFormat.PNG
            }
            val displayName =
                if (fmt == Bitmap.CompressFormat.JPEG) "$nameBase.jpg" else "$nameBase.png"

            lifecycleScope.launch {
                val uri = withContext(Dispatchers.IO) {
                    imagesViewModel.saveCommittedToGallery(displayName, fmt)
                }
                if (uri != null) {
                    Toast.makeText(requireContext(), "Saved to gallery", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Save failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
}
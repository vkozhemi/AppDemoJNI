package com.vkozhemi.appdemo.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vkozhemi.appdemo.databinding.FragmentPickBinding
import com.vkozhemi.appdemo.ui.MainActivity
import com.vkozhemi.appdemo.viewmodel.ImagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PickFragment : Fragment() {

    private var _binding: FragmentPickBinding? = null
    private val binding get() = _binding
    private val imagesViewModel: ImagesViewModel by activityViewModels()

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult
        val isImageLoaded = imagesViewModel.setSourceFromUri(uri)
        if (isImageLoaded) {
            binding?.preview?.setImageBitmap(imagesViewModel.currentCommitted())
            (activity as? MainActivity)?.goToPage(1)
        } else {
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPickBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.btnPick?.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding?.preview?.setImageBitmap(imagesViewModel.currentCommitted())
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
}
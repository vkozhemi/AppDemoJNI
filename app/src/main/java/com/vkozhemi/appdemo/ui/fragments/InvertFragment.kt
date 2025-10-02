package com.vkozhemi.appdemo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vkozhemi.appdemo.databinding.FragmentInvertBinding
import com.vkozhemi.appdemo.viewmodel.ImagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvertFragment : Fragment() {

    private var _binding: FragmentInvertBinding? = null
    private val binding get() = _binding
    private val imagesViewModel: ImagesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInvertBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        binding?.image?.setImageBitmap(imagesViewModel.beginEdit())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.btnApply?.setOnClickListener {
            if (imagesViewModel.invertInPlace()) {
                binding?.image?.invalidate()
            }
        }
        binding?.btnReset?.setOnClickListener {
            imagesViewModel.discard()
            binding?.image?.setImageBitmap(imagesViewModel.beginEdit())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
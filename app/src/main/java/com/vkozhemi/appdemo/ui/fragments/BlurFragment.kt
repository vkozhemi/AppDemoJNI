package com.vkozhemi.appdemo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.vkozhemi.appdemo.databinding.FragmentBlurBinding
import com.vkozhemi.appdemo.viewmodel.ImagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BlurFragment : Fragment() {
    private var _binding: FragmentBlurBinding? = null
    private val binding get() = _binding
    private val imagesViewModel: ImagesViewModel by activityViewModels()
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBlurBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        binding?.image?.setImageBitmap(imagesViewModel.beginEdit())
        binding?.blurLabel?.text = "Blur: ${binding?.blurSeek?.progress}"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.blurSeek?.max = 25
        binding?.blurSeek?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, p: Int, fromUser: Boolean) {
                binding?.blurLabel?.text = "Blur: $p"
                debounce(p)
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {
                binding?.blurSeek?.progress?.let { debounce(it) }
            }
        })
    }

    private fun debounce(radius: Int) {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            delay(120)

            // Show fresh copy before applying blur
            val staged = imagesViewModel.stageFreshFromCommitted()
            binding?.image?.setImageBitmap(staged)

            // Apply blur and invalidate
            if (imagesViewModel.boxBlurInPlace(radius)) {
                binding?.image?.invalidate()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
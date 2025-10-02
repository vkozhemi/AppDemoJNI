package com.vkozhemi.appdemo.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.vkozhemi.appdemo.R
import com.vkozhemi.appdemo.ui.fragments.BlurFragment
import com.vkozhemi.appdemo.ui.fragments.InvertFragment
import com.vkozhemi.appdemo.ui.fragments.PickFragment
import com.vkozhemi.appdemo.ui.fragments.SaveFragment
import com.vkozhemi.appdemo.viewmodel.ImagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val imagesViewModel: ImagesViewModel by viewModels()
    private lateinit var pager: ViewPager2
    private var lastPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!imagesViewModel.isInitialized()) {
            imagesViewModel.setSourceFromAssets("generic_kanzi_blog_cover.png")
        }

        pager = findViewById(R.id.pager)
        pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 4
            override fun createFragment(position: Int) = when (position) {
                0 -> PickFragment()
                1 -> InvertFragment()
                2 -> BlurFragment()
                else -> SaveFragment()
            }
        }

        // Commit forward, discard back
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position > lastPage) {
                    imagesViewModel.commit()
                } else if (position < lastPage) {
                    imagesViewModel.discard()
                }
                lastPage = position
            }
        })
    }

    fun goToPage(index: Int) {
        pager.currentItem = index
    }

}
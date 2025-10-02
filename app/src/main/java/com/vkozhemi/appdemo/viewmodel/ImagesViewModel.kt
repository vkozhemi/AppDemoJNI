package com.vkozhemi.appdemo.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.vkozhemi.appdemo.data.ImagesRepository
import com.vkozhemi.mylibrary.NativeFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val repo: ImagesRepository,
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    private lateinit var committed: Bitmap
    private var staged: Bitmap? = null
    private var initialized = false

    fun isInitialized() = initialized

    fun setSource(bmp: Bitmap) {
        committed = bmp.copy(Bitmap.Config.ARGB_8888, true)
        staged = null
        initialized = true
    }

    fun beginEdit(): Bitmap {
        check(initialized) { "Source image not set" }
        staged = committed.copy(Bitmap.Config.ARGB_8888, true)
        return requireNotNull(staged) { "Failed to create staged bitmap" }
    }

    fun stageFreshFromCommitted(): Bitmap {
        check(initialized) { "Source image not set" }
        staged = committed.copy(Bitmap.Config.ARGB_8888, true)
        return requireNotNull(staged) { "Failed to create staged bitmap" }
    }

    fun currentCommitted(): Bitmap = committed

    fun commit() {
        staged?.let { committed = it }
        staged = null
    }

    fun discard() {
        staged = null
    }

    fun invertInPlace(): Boolean =
        staged?.let { NativeFilters.applyInvert(it) } ?: false

    fun boxBlurInPlace(radius: Int): Boolean =
        staged?.let { NativeFilters.applyBlur(it, radius) } ?: false

    fun setSourceFromAssets(fileName: String): Boolean {
        val bmp = repo.loadFromAssets(fileName) ?: return false
        setSource(bmp)
        return true
    }

    fun setSourceFromUri(uri: Uri): Boolean {
        val bmp = repo.loadFromUri(uri) ?: return false
        val ensured = if (bmp.config == Bitmap.Config.ARGB_8888 && bmp.isMutable) {
            bmp
        } else {
            bmp.copy(Bitmap.Config.ARGB_8888, true)
        }
        setSource(ensured)
        return true
    }

    fun saveCommittedToGallery(
        displayName: String,
        format: Bitmap.CompressFormat
    ) = repo.saveToGallery(committed, displayName, format)
}

package com.vkozhemi.appdemo.data

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.WorkerThread

/**
 * Handles loading and saving images
 */
class ImagesRepository(private val app: Application) {

    /**
     * Load picture from application assets.
     * @param name - name of picture file in assets directory.
     * @return {@link Bitmap} if picture is successfully loaded,
     *      or {@code null} if picture was not found
     */
    @WorkerThread
    fun loadFromAssets(name: String): Bitmap? = runCatching {
        app.assets.open(name).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
                ?.copy(Bitmap.Config.ARGB_8888, true)
        }
    }.getOrNull()

    /**
     * Load Bitmap from SAF/MediaStore Uri.
     * @param uri - Uri of image to load
     * @return mutable {@link Bitmap} in ARGB_8888 format,
     *              or {@code null} if decoding fails
     */
    @WorkerThread
    fun loadFromUri(uri: Uri): Bitmap? = runCatching {
        val resolver = app.contentResolver

        val src = ImageDecoder.createSource(resolver, uri)
        val raw: Bitmap? =
            ImageDecoder.decodeBitmap(src) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        raw ?: return@runCatching null
    }.getOrNull()

    /**
     * Save [bmp] into Gallery via MediaStore.
     *
     * @param bmp bitmap to save
     * @param displayName file name without extension (e.g., "image_001")
     * @param format compression format (JPEG, PNG, WEBP)
     * @param relativeDir target relative path within external storage (must end with '/')
     * @return content {@link Uri} of saved image,
     *             or {@code null} if saving fails
     */
    @WorkerThread
    fun saveToGallery(
        bmp: Bitmap,
        displayName: String,
        format: Bitmap.CompressFormat,
        relativeDir: String = "Pictures/AAOSDemo/"
    ): Uri? = runCatching {
        val mimeType = when (format) {
            Bitmap.CompressFormat.JPEG -> "image/jpeg"
            Bitmap.CompressFormat.PNG -> "image/png"
            Bitmap.CompressFormat.WEBP,
            Bitmap.CompressFormat.WEBP_LOSSLESS,
            Bitmap.CompressFormat.WEBP_LOSSY -> "image/webp"

            else -> "image/jpeg"
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.WIDTH, bmp.width)
            put(MediaStore.MediaColumns.HEIGHT, bmp.height)
            put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativeDir)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val resolver = app.contentResolver
        val uri = resolver.insert(collection, contentValues) ?: return@runCatching null

        try {
            resolver.openOutputStream(uri)?.use { out ->
                if (!bmp.compress(format, 100, out)) {
                    error("compress failed")
                }
            } ?: error("openOutputStream returned null")
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            return@runCatching null
        }

        contentValues.clear()
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)
        uri
    }.getOrNull()
}
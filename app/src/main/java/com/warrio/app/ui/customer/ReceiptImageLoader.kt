package com.warrio.app.ui.customer

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.IOException

internal fun loadReceiptImage(
    resolver: ContentResolver,
    uri: Uri,
    maxDimension: Int = 1600,
): Bitmap {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        inSampleSize = 1
    }
    resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    if (options.outWidth <= 0 || options.outHeight <= 0) throw IOException("Unreadable image")

    // Bound preview memory before allocating pixels, including for very large photos.
    options.inSampleSize = receiptSampleSize(options.outWidth, options.outHeight, maxDimension)
    options.inJustDecodeBounds = false
    val orientation = try {
        resolver.openInputStream(uri)?.use {
            ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL
    } catch (_: IOException) {
        ExifInterface.ORIENTATION_NORMAL
    }
    val bitmap = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
        ?: throw IOException("Unreadable image")
    val matrix = Matrix().apply {
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> setScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                setRotate(90f)
                postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                setRotate(270f)
                postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> setRotate(270f)
        }
    }
    if (matrix.isIdentity) return bitmap
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).also {
        if (it !== bitmap) bitmap.recycle()
    }
}

internal fun receiptSampleSize(width: Int, height: Int, maxDimension: Int): Int {
    require(width > 0 && height > 0 && maxDimension > 0)
    var sampleSize = 1
    while ((maxOf(width, height).toLong() + sampleSize - 1) / sampleSize > maxDimension) {
        check(sampleSize <= Int.MAX_VALUE / 2) { "Image dimensions are too large" }
        sampleSize *= 2
    }
    return sampleSize
}

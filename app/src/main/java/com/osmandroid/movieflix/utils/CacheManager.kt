package com.osmandroid.movieflix.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.*
import java.io.*
import java.net.URL


class CacheManager {
    companion object {
        private fun getImage(context: Context, fileName: String): Bitmap? {
            val file = File(context.cacheDir, fileName)
            return try {
                BitmapFactory.decodeStream(FileInputStream(file))
            } catch (e: FileNotFoundException) {
                null
            }
        }

        private fun putImage(context: Context, fileName: String, bitmap: Bitmap) {
            val file = File(context.cacheDir.toString(), fileName)
            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        private fun URL.toBitmap(): Bitmap? {
            return try {
                BitmapFactory.decodeStream(openStream())
            } catch (e: IOException) {
                null
            }
        }

        fun setImage(context: Context, view: ImageView, fileName: String, baseUrl: String) {
            val posterBitmap: Bitmap? = getImage(context, fileName)
            if (posterBitmap != null) {
                GlobalScope.launch(Dispatchers.Main) {
                    view.setImageBitmap(posterBitmap)
                }
            } else {
                val url = URL(baseUrl + fileName)
                val result: Deferred<Bitmap?> = GlobalScope.async {
                    url.toBitmap()
                }
                GlobalScope.launch(Dispatchers.Main) {
                    val bitmap: Bitmap? = result.await()
                    bitmap?.apply {
                        view.setImageBitmap(bitmap)
                        putImage(context, fileName, bitmap)
                    }
                }
            }
        }

    }
}
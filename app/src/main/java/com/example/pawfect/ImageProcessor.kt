package com.example.pawfect

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ImageProcessor {
    companion object {
        fun encodeImageToBase64(bitmap: Bitmap): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        fun decodeBase64ToBitmap(base64String: String): Bitmap? {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }

        fun imageBitmapToBitmap(imageBitmap: ImageBitmap?): Bitmap? {
           if (imageBitmap != null) {
                val bitmapWidth = imageBitmap.width
                val bitmapHeight = imageBitmap.height
                val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)

                val pixelArray = IntArray(bitmapWidth * bitmapHeight)
                imageBitmap.readPixels(pixelArray)
                bitmap.setPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight)

                return bitmap
           }
            return null
        }


        fun getCachedImageFile(context: Context, imageUrl: String): File {
            val cacheDir = File(context.cacheDir, "image_cache")
            if (!cacheDir.exists()) cacheDir.mkdirs()

            val fileName = imageUrl.hashCode().toString() + ".jpg"
            return File(cacheDir, fileName)
        }


        suspend fun downloadAndCacheImage(context: Context, imageUrl: String): Bitmap? {
            return withContext(Dispatchers.IO) {
                val cachedFile = getCachedImageFile(context, imageUrl)

                if (cachedFile.exists()) {
                    return@withContext BitmapFactory.decodeFile(cachedFile.absolutePath)
                }

                try {
                    val url = URL(imageUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val inputStream: InputStream = connection.inputStream
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    FileOutputStream(cachedFile).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }

                    bitmap
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        fun saveBitmapToCacheAndGetUri(context: Context, bitmap: Bitmap): Uri? {
            return try {
                val file = File(context.cacheDir, "ai_generated_offspring_image.jpg")
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                Uri.fromFile(file)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }
}
package com.example.pawfect

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import java.io.ByteArrayOutputStream


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
    }
}
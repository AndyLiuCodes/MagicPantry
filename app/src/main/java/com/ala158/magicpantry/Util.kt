package com.ala158.magicpantry

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File

object Util {
    // Obtained getBitmap() from Xing-Dong Yang in the code example
    // from https://www.sfu.ca/~xingdong/Teaching/CMPT362/lecture14/lecture14.html
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getBitmap(context: Context, imgUri: Uri, imagePath: File): Bitmap {
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        matrix.setRotate(getRotationAmount(File(imagePath.toURI())))
        val ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return ret
    }

    // After taking photos on my phone, the image would rotate itself. This function is to rotate the
    // image back to the correct position. Adapted from
    // https://stackoverflow.com/questions/68008829/all-images-taken-with-android-camerax-have-incorrect-orientation-in-their-exif-d
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getRotationAmount(imageFile: File): Float {
        try {
            val ei = ExifInterface(imageFile)
            val imageOrientation =
                ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            if (imageOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90f
            } else if (imageOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180f
            } else if (imageOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270f
            } else {
                return 0f
            }
        } catch (e: Exception) {
            println(e)
            // If any issues occur with the viewing the Exif data, then no rotation will occur
            return 0f
        }
    }
}
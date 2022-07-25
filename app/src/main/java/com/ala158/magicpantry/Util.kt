package com.ala158.magicpantry

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ala158.magicpantry.database.MagicPantryDatabase
import com.ala158.magicpantry.repository.IngredientRepository
import com.ala158.magicpantry.repository.NotificationRepository
import com.ala158.magicpantry.repository.RecipeRepository
import com.ala158.magicpantry.repository.ShoppingListItemRepository
import com.ala158.magicpantry.viewModel.*
import java.io.File
import java.io.FileInputStream

object Util {
    // Obtained getBitmap() from Xing-Dong Yang in the code example
    // from https://www.sfu.ca/~xingdong/Teaching/CMPT362/lecture14/lecture14.html
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getBitmap(context: Context, imgUri: Uri, imagePath: File): Bitmap {
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        matrix.setRotate(getRotationAmount(File(imagePath.toURI())))
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // After taking photos on my phone, the image would rotate itself. This function is to rotate the
    // image back to the correct position. Adapted from
    // https://stackoverflow.com/questions/68008829/all-images-taken-with-android-camerax-have-incorrect-orientation-in-their-exif-d
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getRotationAmount(imageFile: File): Float {
        try {
            val fileInputStream = FileInputStream(imageFile)
            val ei = androidx.exifinterface.media.ExifInterface(fileInputStream)
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

    // Create util methods by passing in viewModelClass and which of the
    // 4 data types it's derived from
    enum class DataType { INGREDIENT, RECIPE, SHOPPING_LIST_ITEM, NOTIFICATION }

    fun <T : ViewModel> createViewModel(
        context: Context,
        viewModelClass: Class<T>,
        dataType: DataType
    ): T {
        val database = MagicPantryDatabase.getInstance(context)

        when (dataType) {
            DataType.INGREDIENT -> {
                val ingredientRepository = IngredientRepository(database.ingredientDAO)
                return ViewModelProvider(
                    context as ViewModelStoreOwner,
                    IngredientViewModelFactory(ingredientRepository)
                ).get(viewModelClass)
            }
            DataType.RECIPE -> {
                val recipeRepository = RecipeRepository(database.recipeDAO)
                return ViewModelProvider(
                    context as ViewModelStoreOwner,
                    RecipeViewModelFactory(recipeRepository)
                ).get(viewModelClass)
            }
            DataType.SHOPPING_LIST_ITEM -> {
                val shoppingListItemRepository =
                    ShoppingListItemRepository(database.shoppingListItemDAO)
                return ViewModelProvider(
                    context as ViewModelStoreOwner,
                    ShoppingListItemViewModelFactory(shoppingListItemRepository)
                ).get(viewModelClass)
            }
            else -> {
                val notificationRepository = NotificationRepository(database.notificationDAO)
                return ViewModelProvider(
                    context as ViewModelStoreOwner,
                    NotificationViewModelFactory(notificationRepository)
                ).get(viewModelClass)
            }
        }
    }
}
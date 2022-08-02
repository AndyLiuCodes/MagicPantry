@file:Suppress("DEPRECATION")

package com.ala158.magicpantry.ui.recipes

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.AddRecipeArrayAdapter
import com.ala158.magicpantry.data.Recipe
import com.ala158.magicpantry.data.RecipeWithRecipeItems
import com.ala158.magicpantry.viewModel.RecipeViewModel
import java.io.ByteArrayOutputStream
import java.io.File

class AddRecipeActivity : AppCompatActivity() {
    private lateinit var imageUri : Uri
    private var bitmap : Bitmap? = null

    private val requestCamera = 1000
    private val requestGallery = 2000
    private lateinit var cameraBtn: Button

    private lateinit var sharedPrefFile : SharedPreferences

    private var imageToScan: File? = null
    private var imageView: ImageView? = null

    private lateinit var title : TextView
    private lateinit var cookTime : TextView
    private lateinit var servings : TextView
    private lateinit var description : TextView
    private lateinit var ingredients : ListView

    private lateinit var recipeViewModel : RecipeViewModel

    private var recipeArray = arrayOf<RecipeWithRecipeItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        //set up shared pref
        sharedPrefFile = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val edit = sharedPrefFile.edit()

        //start up database
        recipeViewModel = Util.createViewModel(
            this,
            RecipeViewModel::class.java,
            Util.DataType.RECIPE
        )

        imageView = findViewById(R.id.add_recipe_img)
        cameraBtn = findViewById(R.id.btn_add_recipe_pic)

        title = findViewById(R.id.add_recipe_edit_recipe_title)
        cookTime = findViewById(R.id.add_recipe_edit_recipe_cook_time)
        servings = findViewById(R.id.add_recipe_edit_recipe_servings)
        description = findViewById(R.id.add_recipe_edit_recipe_description)
        ingredients = findViewById(R.id.add_recipe_ingredient_listView)

        recipeViewModel.allRecipes.observe(this) {
            val myList = it.toTypedArray()

            //if not empty
            if (myList.isNotEmpty()) {
                recipeArray = myList
            }
        }

        val adapter = AddRecipeArrayAdapter(this, recipeArray, recipeViewModel)
        ingredients.adapter = adapter

        ingredients.setOnItemClickListener { parent: AdapterView<*>, _: View, _: Int, _: Long->
            println("debug: $parent")
        }

        cameraBtn.setOnClickListener {
            val choices = arrayOf("Open Camera", "Select from Gallery")

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                .setCancelable(true)

                // set 2 choices, open camera or open gallery
                .setItems(choices)
                { _: DialogInterface, myId: Int ->
                    if (myId == 0) {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                        // store image once it is taken. includes a file name and date/time taken
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.TITLE, "MyPicture")
                        values.put(
                            MediaStore.Images.Media.DESCRIPTION,
                            "Photo taken on " + System.currentTimeMillis()
                        )
                        imageUri = this.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                        )!!

                        // open camera and add image to photo gallery if one is taken
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        startActivityForResult(cameraIntent, requestCamera)
                    } else {
                        // open photo gallery to choose an image
                        val galleryIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, requestGallery)
                    }
                }
            val alert: AlertDialog = builder.create()
            alert.show()
        }

        val addIngredientBtn = findViewById<Button>(R.id.add_recipe_btn_add_ingredient_to_recipe)
        addIngredientBtn.setOnClickListener {
            edit.putString("recipe_title", title.text.toString())
            edit.putString("recipe_cookTime", cookTime.text.toString())
            edit.putString("recipe_servings", servings.text.toString())
            edit.putString("recipe_description", description.text.toString())
            edit.apply()

            val intent = Intent(this, AddIngredientToRecipeActivity::class.java)
            startActivity(intent)
        }

        val cancelBtn = findViewById<Button>(R.id.add_recipe_btn_cancel_recipe)
        cancelBtn.setOnClickListener {
            onBackPressed()
        }

        val addBtn = findViewById<Button>(R.id.add_recipe_btn_add_recipe)
        addBtn.setOnClickListener {
            updateDatabase()
            onBackPressed()
        }
    }

    // when camera or gallery chosen, update photo
    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("CommitPrefEdits")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if camera selected, check request code
        if (requestCode == requestCamera && resultCode == Activity.RESULT_OK) {

            // get image uri, convert it to bitmap and rotate if necessary, then
            // set imageBitmap to display it
            // Help from https://developer.android.com/training/data-storage/shared/media
            // TODO change how we do our URIs since this logic to fetch the image is slow due to db query
            var imagePath = ""
            val projections = arrayOf(MediaStore.Images.Media.DATA)
            this.contentResolver.query(
                imageUri,
                projections,
                null,
                null,
                null
            )?.use { cursor ->
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                imagePath = cursor.getString(columnIndex)
            }

            imageToScan = File(imagePath)
            val myBitmap = Util.getBitmap(this, imageUri, imageToScan!!)

            imageView!!.setImageBitmap(myBitmap)

            bitmap = myBitmap
        }

        // if gallery selected, check request code
        else if (requestCode == requestGallery && resultCode == Activity.RESULT_OK && data != null) {

            // get image uri and set imageBitmap to display it
            val myData = data.data

            // get image uri, convert it to bitmap and rotate if necessary, then
            // set imageBitmap to display it
            // Help from https://developer.android.com/training/data-storage/shared/media
            var imagePath = ""
            val projections = arrayOf(MediaStore.Images.Media.DATA)
            this.contentResolver.query(
                myData!!,
                projections,
                null,
                null,
                null
            )?.use { cursor ->
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                cursor.moveToFirst()
                imagePath = cursor.getString(columnIndex)
            }

            imageToScan = File(imagePath)
            val myBitmap = Util.getBitmap(this, myData, imageToScan!!)

            imageView!!.setImageBitmap(myBitmap)

            bitmap = myBitmap
        }
    }

    //update database
    private fun updateDatabase() {
        val recipe = Recipe()
        recipe.description = description.text.toString()
        recipe.servings = if (servings.text.toString() == "") {
            0
        }
        else {
            servings.text.toString().toInt()
        }
        recipe.timeToCook = if (cookTime.text.toString() == "") {
            0
        }
        else {
            cookTime.text.toString().toInt()
        }
        recipe.title = title.text.toString()
        recipe.imageUri = if (bitmap != null) {
            getImageUri(this, bitmap!!).toString()
        }
        else {
            ""
        }
        recipeViewModel.insert(recipe)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    override fun onResume() {
        super.onResume()
        val edit = sharedPrefFile.edit()

        if (sharedPrefFile.contains("recipe_title")) {
            title.text = sharedPrefFile.getString("recipe_title", "")
            edit.remove("recipe_title").apply()
        }
        if (sharedPrefFile.contains("recipe_cookTime")) {
            cookTime.text = sharedPrefFile.getString("recipe_cookTime", "")
            edit.remove("recipe_cookTime").apply()
        }
        if (sharedPrefFile.contains("recipe_servings")) {
            servings.text = sharedPrefFile.getString("recipe_servings", "")
            edit.remove("recipe_servings").apply()
        }
        if (sharedPrefFile.contains("recipe_description")) {
            description.text = sharedPrefFile.getString("recipe_description", "")
            edit.remove("recipe_description").apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Delete image once we are done with it
        if (imageToScan != null && imageToScan!!.exists())
            imageToScan!!.delete()
    }
}
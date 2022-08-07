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
import android.os.Environment
import android.os.Parcel
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.AddRecipeArrayAdapter
import com.ala158.magicpantry.data.Recipe
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.RecipeItemAndIngredient
import com.ala158.magicpantry.dialogs.ChangeRecipeIngredientAmountDialog
import com.ala158.magicpantry.ui.ingredientlistadd.IngredientListAddActivity
import com.ala158.magicpantry.ui.manualingredientinput.edit.ReviewIngredientsEditActivity
import com.ala158.magicpantry.ui.receiptscanner.ReceiptScannerActivity
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.RecipeItemViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AddRecipeActivity :
    AppCompatActivity(),
    AddRecipeArrayAdapter.OnRecipeEditAmountChangeClickListener,
    AddRecipeArrayAdapter.OnRecipeItemDeleteClickListener {
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeItemViewModel: RecipeItemViewModel

    private lateinit var imageUri: Uri
    private var bitmap: Bitmap? = null

    private var isRecipeNameValid = true
    private lateinit var recipeNameLabel: TextView

    private val tag = "MagicPantry"
    private var recipeName = "Recipe"
    private var filePath = ""

    private val requestCamera = 1000
    private val requestGallery = 2000
    private lateinit var cameraBtn: Button

    private lateinit var sharedPrefFile: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    private var recipeImage: File? = null
    private var imageView: ImageView? = null

    private lateinit var title: TextView
    private lateinit var cookTime: TextView
    private lateinit var servings: TextView
    private lateinit var description: TextView
    private lateinit var ingredients: ListView

    private var ingredientToBeAdded = ArrayList<RecipeItemAndIngredient>()
    private lateinit var addIngredientToRecipeLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: AddRecipeArrayAdapter

    private var finishBtnClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        //set up shared pref
        sharedPrefFile = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        edit = sharedPrefFile.edit()

        //start up database

        ingredientViewModel = Util.createViewModel(
            this,
            IngredientViewModel::class.java,
            Util.DataType.INGREDIENT
        )

        recipeViewModel = Util.createViewModel(
            this,
            RecipeViewModel::class.java,
            Util.DataType.RECIPE
        )

        recipeItemViewModel = Util.createViewModel(
            this,
            RecipeItemViewModel::class.java,
            Util.DataType.RECIPE_ITEM
        )

        imageView = findViewById(R.id.add_recipe_img)
        cameraBtn = findViewById(R.id.btn_add_recipe_pic)

        title = findViewById(R.id.add_recipe_edit_recipe_title)
        cookTime = findViewById(R.id.add_recipe_edit_recipe_cook_time)
        servings = findViewById(R.id.add_recipe_edit_recipe_servings)
        description = findViewById(R.id.add_recipe_edit_recipe_description)
        ingredients = findViewById(R.id.add_recipe_ingredient_listView)

        recipeNameLabel = findViewById(R.id.add_recipe_title)

        // https://stackoverflow.com/questions/35634023/how-can-i-have-a-listview-inside-a-nestedscrollview
        ingredients.isNestedScrollingEnabled = true

        addIngredientToRecipeLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                    if (it.data!!.hasExtra(ADDED_INGREDIENTS_KEY)) {
                        val newlyAddedIngredients =
                            it.data!!.getSerializableExtra(ADDED_INGREDIENTS_KEY)
                        val existingAddedIngredients: ArrayList<RecipeItemAndIngredient> =
                            recipeViewModel.addedRecipeItemAndIngredient.value!!
                        existingAddedIngredients.addAll(newlyAddedIngredients as ArrayList<RecipeItemAndIngredient>)
                        recipeViewModel.addedRecipeItemAndIngredient.value =
                            existingAddedIngredients
                    }
                }
            }

        adapter = AddRecipeArrayAdapter(this, ingredientToBeAdded, this, this)
        ingredients.adapter = adapter

        updateListViewSize(ingredientToBeAdded.size, ingredients)

        recipeViewModel.addedRecipeItemAndIngredient.observe(this) {
            ingredientToBeAdded.clear()
            ingredientToBeAdded.addAll(it)
            adapter.notifyDataSetChanged()

            updateListViewSize(it.size, ingredients)
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

                        if (title.text.toString().trim().isNotEmpty()) {
                            recipeName = title.text.toString()
                        }

                        // store image once it is taken. includes a file name and date/time taken
                        val values = ContentValues()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val file =
                                File(Environment.getExternalStorageDirectory().toString() + "/$tag/")
                            if (!file.exists()) {
                                file.mkdirs()
                            }
                            values.put(MediaStore.Images.Media.TITLE, recipeName)
                            values.put(MediaStore.Images.Media.DATA, recipeName)
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$tag/")
                            values.put(
                                MediaStore.Images.Media.DESCRIPTION,
                                "Photo taken on " + System.currentTimeMillis()
                            )
                        }
                        else {
                            values.put(MediaStore.Images.Media.TITLE, recipeName)
                            values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis())
                        }
                        imageUri = this.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                        )!!

                        filePath = imageUri.path!!

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

            val intent = Intent(this, IngredientListAddActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(Util.INGREDIENT_ADD_LIST, Util.INGREDIENT_ADD_RECIPE)
            val idToFilter = ArrayList<Int>()
            val iterator = recipeViewModel.addedRecipeItemAndIngredient.value!!.listIterator()
            // Get all the ids to filter from the IngredientListAddActivity
            for (recipeItemAndIngredient in iterator) {
                idToFilter.add(recipeItemAndIngredient.ingredient.ingredientId.toInt())
            }
            // Send the Ids t o filter to the IngredientListAddActivity
            bundle.putIntegerArrayList(IDS_TO_FILTER_KEY, idToFilter)
            intent.putExtras(bundle)
            addIngredientToRecipeLauncher.launch(intent)
        }

        val cancelBtn = findViewById<Button>(R.id.add_recipe_btn_cancel_recipe)
        cancelBtn.setOnClickListener {
            edit.remove("recipe_image").apply()

            // Delete image once we are done with it
            ReceiptScannerActivity().deleteImageFromGallery(filePath)
            finish()
        }

        val addBtn = findViewById<Button>(R.id.add_recipe_btn_add_recipe)
        addBtn.setOnClickListener {
            finishBtnClicked = true

            // check if title entered
            var errorMsg = ""

            if (title.text.toString().trim() == "") {
                errorMsg += "• The ingredient name cannot be empty"
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                recipeNameLabel.setTextColor(resources.getColor(R.color.mp_red, null))
                isRecipeNameValid = false
            }
            else {
                updateDatabase()
                edit.remove("edit_recipe_image").apply()
                Toast.makeText(this, "Recipe Saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onRecipeEditAmountChangeClick(recipeItem: RecipeItem) {
        // Open dialog to record amount
        val onRecipeIngredientAmountChangeDialog = ChangeRecipeIngredientAmountDialog()
        val dialogData = Bundle()
        dialogData.putParcelable(
            ChangeRecipeIngredientAmountDialog.DIALOG_CHANGE_RECIPE_INGREDIENT_AMOUNT_LISTENER_KEY,
            object : ChangeRecipeIngredientAmountDialog.ChangeRecipeIngredientAmountDialogListener {
                override fun onChangeRecipeIngredientAmountConfirm(amount: Double) {
                    CoroutineScope(Dispatchers.IO).launch {
                        recipeViewModel.updateRecipeItemAmount(recipeItem, amount)
                        withContext(Dispatchers.Main) {
                            // Refresh the live data to trigger the observer to update
                            recipeViewModel.addedRecipeItemAndIngredient.value =
                                recipeViewModel.addedRecipeItemAndIngredient.value
                        }
                    }
                }

                override fun describeContents(): Int {
                    return 0
                }

                override fun writeToParcel(dest: Parcel?, flags: Int) {
                    return
                }
            })
        onRecipeIngredientAmountChangeDialog.arguments = dialogData
        onRecipeIngredientAmountChangeDialog.show(
            supportFragmentManager,
            "Change recipe ingredient amount"
        )
    }

    override fun onRecipeItemDelete(deleteTarget: RecipeItemAndIngredient) {
        var foundIdx = -1
        for (idx in  0 until recipeViewModel.addedRecipeItemAndIngredient.value!!.size) {
            val recipeItemAndIngredientEntry = recipeViewModel.addedRecipeItemAndIngredient.value!![idx]
            if (recipeItemAndIngredientEntry.ingredient.ingredientId == deleteTarget.ingredient.ingredientId) {
                foundIdx = idx
                break
            }
        }

        if (foundIdx != -1) {
            recipeViewModel.addedRecipeItemAndIngredient.value!!.removeAt(foundIdx)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show()
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
            ReceiptScannerActivity().deleteImageFromGallery(filePath)

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

            recipeImage = File(imagePath)
            val myBitmap = Util.getBitmap(this, imageUri, recipeImage!!)

            imageView!!.setImageBitmap(myBitmap)

            bitmap = myBitmap

            imageUri = Uri.parse(imagePath)
            filePath = imageUri.path!!
        }

        // if gallery selected, check request code
        else if (requestCode == requestGallery && resultCode == Activity.RESULT_OK && data != null) {
            ReceiptScannerActivity().deleteImageFromGallery(filePath)

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

            recipeImage = File(imagePath)
            val myBitmap = Util.getBitmap(this, myData, recipeImage!!)

            imageView!!.setImageBitmap(myBitmap)

            bitmap = myBitmap

            imageUri = Uri.parse(imagePath)
            filePath = imageUri.path!!
        }
        val recipeImageString = if (bitmap != null) {
            imageUri.path.toString()
        } else {
            ""
        }
        edit.putString("recipe_image", recipeImageString).apply()
    }

    //update size of listView
    fun updateListViewSize(size: Int, listView: ListView) {
        val cellSize = 170
        val list: ViewGroup.LayoutParams = listView.layoutParams
        list.height = cellSize * size
        listView.layoutParams = list
    }

    //update database
    private fun updateDatabase() {
        val recipe = Recipe()
        recipe.description = description.text.toString()
        recipe.servings = if (servings.text.toString() == "") {
            0
        } else {
            servings.text.toString().toInt()
        }
        recipe.timeToCook = if (cookTime.text.toString() == "") {
            0
        } else {
            cookTime.text.toString().toInt()
        }
        recipe.title = title.text.toString()
        recipe.imageUri = if (sharedPrefFile.contains("recipe_image")) {
            sharedPrefFile.getString("recipe_image", "").toString()
        } else {
            ""
        }
        recipeViewModel.insert(recipe, recipeItemViewModel, recipeViewModel, ingredientViewModel)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ReviewIngredientsEditActivity.IS_INGREDIENT_NAME_VALID_KEY, isRecipeNameValid)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            isRecipeNameValid = savedInstanceState.getBoolean(ReviewIngredientsEditActivity.IS_INGREDIENT_NAME_VALID_KEY)
            if (!isRecipeNameValid)
                recipeNameLabel.setTextColor(resources.getColor(R.color.mp_red, null))
        }
    }

    override fun onResume() {
        super.onResume()

        if (sharedPrefFile.contains("recipe_title")) {
            title.text = sharedPrefFile.getString("recipe_title", "")
            edit.remove("recipe_title").apply()
        }
        if (sharedPrefFile.contains("recipe_image")) {
            imageView!!.setImageURI(Uri.parse(sharedPrefFile.getString("recipe_image", "")))
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

        edit.remove("edit_recipe_image").apply()

        if (!finishBtnClicked) {
            // Delete image once we are done with it
            ReceiptScannerActivity().deleteImageFromGallery(filePath)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        edit.remove("recipe_image").apply()

        // Delete image once we are done with it
        ReceiptScannerActivity().deleteImageFromGallery(filePath)
    }

    companion object {
        const val ADDED_INGREDIENTS_KEY = "ADDED_INGREDIENTS_KEY"
        const val IDS_TO_FILTER_KEY = "IDS_TO_FILTER_KEY"
    }
}
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
import android.os.Parcel
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.EditRecipeArrayAdapter
import com.ala158.magicpantry.data.Recipe
import com.ala158.magicpantry.data.RecipeItem
import com.ala158.magicpantry.data.RecipeItemAndIngredient
import com.ala158.magicpantry.data.RecipeWithRecipeItems
import com.ala158.magicpantry.dialogs.ChangeRecipeIngredientAmountDialog
import com.ala158.magicpantry.ui.ingredientlistadd.IngredientListAddActivity
import com.ala158.magicpantry.ui.receiptscanner.ReceiptScannerActivity
import com.ala158.magicpantry.viewModel.RecipeItemViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class EditRecipeActivity : AppCompatActivity(), EditRecipeArrayAdapter.OnRecipeEditAmountChangeClickListener {
    private lateinit var imageUri: Uri
    private var bitmap: Bitmap? = null

    private val tag = "MagicPantry"
    private var recipeName = "Recipe"
    private var filePath = ""

    private val requestCamera = 1100
    private val requestGallery = 2200
    private lateinit var cameraBtn: Button

    private lateinit var sharedPrefFile : SharedPreferences
    private lateinit var edit : SharedPreferences.Editor

    private var recipeImage: File? = null
    private var imageView: ImageView? = null

    private lateinit var title : TextView
    private lateinit var cookTime : TextView
    private lateinit var servings : TextView
    private lateinit var description : TextView
    private lateinit var ingredients : ListView

    private lateinit var recipeViewModel : RecipeViewModel
    private lateinit var recipeItemViewModel: RecipeItemViewModel
    private lateinit var adapter: EditRecipeArrayAdapter

    private var recipeArray = arrayOf<RecipeWithRecipeItems>()
    private var pos = 0
    private var id = 0L

    private var newUri = ""

    private var ingredientToBeAdded = ArrayList<RecipeItemAndIngredient>()
    private lateinit var addIngredientToRecipeLauncher: ActivityResultLauncher<Intent>
    private var isFirstStart = true
    private var finishBtnClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)

        if (savedInstanceState != null) {
            isFirstStart = savedInstanceState.getBoolean(IS_FIRST_START_KEY)
        }

        pos = intent.getIntExtra("RecipeChosen", -1)

        //set up shared pref
        sharedPrefFile = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        edit = sharedPrefFile.edit()

        //start up database
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

        imageView = findViewById(R.id.edit_recipe_edit_recipe_img)
        cameraBtn = findViewById(R.id.edit_recipe_btn_edit_recipe_pic)

        title = findViewById(R.id.edit_recipe_edit_recipe_title)
        cookTime = findViewById(R.id.edit_recipe_edit_recipe_cook_time)
        servings = findViewById(R.id.edit_recipe_edit_recipe_servings)
        description = findViewById(R.id.edit_recipe_edit_recipe_description)
        ingredients = findViewById(R.id.edit_recipe_edit_ingredient_listView)

        // https://stackoverflow.com/questions/35634023/how-can-i-have-a-listview-inside-a-nestedscrollview
        ingredients.isNestedScrollingEnabled = true

        addIngredientToRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                if (it.data!!.hasExtra(AddRecipeActivity.ADDED_INGREDIENTS_KEY)) {
                    val newlyAddedIngredients = it.data!!.getSerializableExtra(AddRecipeActivity.ADDED_INGREDIENTS_KEY)
                    val existingAddedIngredients: ArrayList<RecipeItemAndIngredient> =
                        recipeViewModel.addedRecipeItemAndIngredient.value!!
                    existingAddedIngredients.addAll(newlyAddedIngredients as ArrayList<RecipeItemAndIngredient>)
                    recipeViewModel.addedRecipeItemAndIngredient.value = existingAddedIngredients
                }
            }
        }

        adapter = EditRecipeArrayAdapter(this, ingredientToBeAdded, this)
        ingredients.adapter = adapter

        // Update ingredient list when user adds new ingredients
        recipeViewModel.addedRecipeItemAndIngredient.observe(this) {
            adapter.replaceRecipeIngredients(it)
            adapter.notifyDataSetChanged()

        }

        recipeViewModel.allRecipes.observe(this) {
            val myList = it.toTypedArray()

            //if not empty
            if (myList.isNotEmpty()) {
                recipeArray = myList

                //get id of recipe to edit or delete
                recipeViewModel.originalRecipeData = recipeArray[pos]
                id = recipeViewModel.originalRecipeData!!.recipe.recipeId

                // perform logic on first start of activity to help handle orientation change replacing data
                //set textViews and imageView
                newUri = recipeArray[pos].recipe.imageUri
                if (newUri == "") {
                    imageView!!.setImageResource(R.drawable.magic_pantry_app_logo)
                }
                else {
                    imageView!!.setImageURI(newUri.toUri())
                }
                edit.putString("edit_recipe_image", newUri).apply()

                if (isFirstStart) {
                    // Get all the ids of the existing original ingredients
                    for (recipeIngredient in recipeViewModel.originalRecipeData!!.recipeItems) {
                        recipeViewModel.originalRecipeIngredientIdSet.add(recipeIngredient.ingredient.ingredientId)
                    }

                    title.text = recipeArray[pos].recipe.title
                    cookTime.text = recipeArray[pos].recipe.timeToCook.toString()
                    servings.text = recipeArray[pos].recipe.servings.toString()
                    description.text = recipeArray[pos].recipe.description

                    recipeViewModel.addedRecipeItemAndIngredient.value!!.addAll(recipeViewModel.originalRecipeData!!.recipeItems)
                    adapter.replaceRecipeIngredients(recipeViewModel.addedRecipeItemAndIngredient.value!!)
                    adapter.notifyDataSetChanged()

                    isFirstStart = false
                }
            }
        }

        cameraBtn.setOnClickListener {
            val choices = arrayOf("Open Camera", "Select from Gallery")

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                .setCancelable(true)

                // set 2 choices, open camera or open gallery
                .setItems(choices)
                { _: DialogInterface, myId: Int ->
                    if (myId == 0) {
                        // store image once it is taken. includes a file name and date/time taken
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                        if (title.text.toString().trim().isNotEmpty()) {
                            recipeName = title.text.toString()
                        }

                        val file =
                            File(Environment.getExternalStorageDirectory().toString() + "/$tag/")
                        if (!file.exists()) {
                            file.mkdirs()
                        }

                        // store image once it is taken. includes a file name and date/time taken
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.TITLE, recipeName)
                        values.put(MediaStore.Images.Media.DATA, recipeName)
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$tag/")
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

        val addIngredientBtn = findViewById<Button>(R.id.edit_recipe_btn_add_ingredient_to_recipe)
        addIngredientBtn.setOnClickListener {
            edit.putString("edit_recipe_title", title.text.toString())
            edit.putString("edit_recipe_cookTime", cookTime.text.toString())
            edit.putString("edit_recipe_servings", servings.text.toString())
            edit.putString("edit_recipe_description", description.text.toString())
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
            bundle.putIntegerArrayList(AddRecipeActivity.IDS_TO_FILTER_KEY, idToFilter)
            intent.putExtras(bundle)
            addIngredientToRecipeLauncher.launch(intent)
        }

        val cancelBtn = findViewById<Button>(R.id.edit_recipe_btn_cancel_recipe)
        cancelBtn.setOnClickListener {
            edit.remove("recipe_image").apply()

            // Delete image once we are done with it
            ReceiptScannerActivity().deleteImageFromGallery(filePath)
            finish()
        }

        val doneBtn = findViewById<Button>(R.id.edit_recipe_btn_add_recipe)
        doneBtn.setOnClickListener {
            finishBtnClicked = true

            val recipeTitle = title.text.toString()
            if(recipeTitle.trim().isEmpty()) {
                Toast.makeText(this, "Please enter in a recipe title", Toast.LENGTH_SHORT).show()
            } else {
                updateDatabase()
                edit.remove("edit_recipe_image").apply()
                Toast.makeText(this, "Recipe Saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_FIRST_START_KEY, isFirstStart)
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
        onRecipeIngredientAmountChangeDialog.show(supportFragmentManager, "Change recipe ingredient amount")
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
        }
        else {
            ""
        }
        edit.putString("edit_recipe_image", recipeImageString).apply()
    }

    //update database
    private fun updateDatabase() {
        newUri = if (sharedPrefFile.contains("edit_recipe_image")) {
            sharedPrefFile.getString("edit_recipe_image", "").toString()
        }
        else {
            ""
        }
        val newServings = if (servings.text.toString() == "") {
            0
        }
        else {
            servings.text.toString().toInt()
        }
        val newCookTime = if (cookTime.text.toString() == "") {
            0
        }
        else {
            cookTime.text.toString().toInt()
        }
        val updatedRecipe = Recipe(id, title.text.toString(), newUri, newServings, newCookTime, description.text.toString(), 0)
        recipeViewModel.updateRecipeWithRecipeItems(updatedRecipe, recipeItemViewModel)
    }

    //add delete button
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_recipe_delete_menu, menu)
        return true
    }

    //make button clickable
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //if delete clicked
        if (id == R.id.deleteButton) {
            //delete item from database
            deleteCurrItem()

            //return to RecipesFragment
            val local = Intent()
            local.action = "FINISH"
            sendBroadcast(local)

            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //delete current item from database
    private fun deleteCurrItem() {
        //remove observer from livedata
        recipeViewModel.allRecipes.removeObservers(this)

        //delete item from viewModel
        for (i in 0 until recipeViewModel.originalRecipeData!!.recipeItems.size) {
            recipeItemViewModel.delete(recipeViewModel.originalRecipeData!!.recipeItems[i].recipeItem)
        }
        recipeViewModel.delete(recipeViewModel.originalRecipeData!!.recipe)
    }

    override fun onResume() {
        super.onResume()

        if (sharedPrefFile.contains("edit_recipe_title")) {
            title.text = sharedPrefFile.getString("edit_recipe_title", "")
            edit.remove("edit_recipe_title").apply()
        }
        if (sharedPrefFile.contains("edit_recipe_image")) {
            val savedUri = sharedPrefFile.getString("edit_recipe_image", "")
            if (savedUri == "") {
                imageView!!.setImageResource(R.drawable.magic_pantry_app_logo)
            }
            else {
                imageView!!.setImageURI(Uri.parse(savedUri))
            }
        }
        if (sharedPrefFile.contains("edit_recipe_cookTime")) {
            cookTime.text = sharedPrefFile.getString("edit_recipe_cookTime", "")
            edit.remove("edit_recipe_cookTime").apply()
        }
        if (sharedPrefFile.contains("edit_recipe_servings")) {
            servings.text = sharedPrefFile.getString("edit_recipe_servings", "")
            edit.remove("edit_recipe_servings").apply()
        }
        if (sharedPrefFile.contains("edit_recipe_description")) {
            description.text = sharedPrefFile.getString("edit_recipe_description", "")
            edit.remove("edit_recipe_description").apply()
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

        edit.remove("edit_recipe_image").apply()

        // Delete image once we are done with it
        ReceiptScannerActivity().deleteImageFromGallery(filePath)
    }

    companion object {
        const val IS_FIRST_START_KEY = "IS_FIRST_START_KEY"
    }
}
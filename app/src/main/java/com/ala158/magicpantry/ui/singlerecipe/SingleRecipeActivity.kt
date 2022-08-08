package com.ala158.magicpantry.ui.singlerecipe

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.ala158.magicpantry.R
import com.ala158.magicpantry.UpdateDB
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.RecipeIngredientArrayAdapter
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.Notification
import com.ala158.magicpantry.data.RecipeWithRecipeItems
import com.ala158.magicpantry.dialogs.AddMissingIngredientsToShoppingListDialog
import com.ala158.magicpantry.ui.notifications.LowIngredientActivity
import com.ala158.magicpantry.ui.recipes.EditRecipeActivity
import com.ala158.magicpantry.viewModel.*
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.RecipeItemViewModel
import com.ala158.magicpantry.viewModel.RecipeViewModel
import com.ala158.magicpantry.viewModel.ShoppingListItemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class SingleRecipeActivity : AppCompatActivity(),
    AddMissingIngredientsToShoppingListDialog.AddMissingIngredientDialogListener {

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeItemViewModel: RecipeItemViewModel
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var shoppingListItemViewModel: ShoppingListItemViewModel
    private lateinit var recipeName: TextView
    private lateinit var recipeImage: ImageView
    private lateinit var recipeDescription: TextView
    private lateinit var ingredientListView: ListView
    private lateinit var cookingTimeView: TextView
    private lateinit var numOfServingsView: TextView
    private lateinit var isAbleToCookImageView: ImageView
    private lateinit var isAbleToCookTextView: TextView
    private lateinit var editRecipeButton: Button
    private lateinit var addIngredientsButton: Button
    private lateinit var cookNowButton: Button
    private lateinit var recipeIngredientArrayAdapter: RecipeIngredientArrayAdapter

    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var recipeWithRecipeItems: RecipeWithRecipeItems
    private var isCookable = false

    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var notificationManager: NotificationManager
    private var lowIngredients: MutableList<Ingredient> = java.util.ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_recipe)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
        ingredientViewModel = Util.createViewModel(
            this,
            IngredientViewModel::class.java,
            Util.DataType.INGREDIENT
        )

        shoppingListItemViewModel = Util.createViewModel(
            this,
            ShoppingListItemViewModel::class.java,
            Util.DataType.SHOPPING_LIST_ITEM
        )


        notificationViewModel = Util.createViewModel(
            this,
            NotificationViewModel::class.java,
            Util.DataType.NOTIFICATION
        )
        createNotificationChannel()

        recipeName = findViewById(R.id.singleRecipeName)
        recipeImage = findViewById(R.id.singleRecipeImage)
        recipeDescription = findViewById(R.id.recipe_description)
        ingredientListView = findViewById(R.id.listview_all_recipe_ingredients)
        cookingTimeView = findViewById(R.id.CookingTime)
        numOfServingsView = findViewById(R.id.NoOfServings)
        isAbleToCookImageView = findViewById(R.id.enoughIngredientsImage)
        isAbleToCookTextView = findViewById(R.id.EnoughIngredientsText)
        editRecipeButton = findViewById(R.id.edit_recipe_button)
        cookNowButton = findViewById(R.id.cook_now_btn)
        addIngredientsButton = findViewById(R.id.add_ingredient_to_recipe_button)

        // https://stackoverflow.com/questions/35634023/how-can-i-have-a-listview-inside-a-nestedscrollview
        ingredientListView.isNestedScrollingEnabled = true

        val id = intent.getIntExtra("RECIPE_KEY", -1)
        val id2 = intent.getIntExtra("RECIPE_KEY_COOKABLE", -1)
        recipeIngredientArrayAdapter = RecipeIngredientArrayAdapter(this, ArrayList())
        ingredientListView.adapter = recipeIngredientArrayAdapter

        recipeViewModel.allRecipes.observe(this) {
            recipeViewModel.updateCurrentCookable()
            if (id != -1) {
                recipeWithRecipeItems = it[id]
                recipeName.text = recipeWithRecipeItems.recipe.title
                if (recipeWithRecipeItems.recipe.imageUri == "") {
                    recipeImage.setImageResource(R.drawable.magic_pantry_app_logo)
                } else {
                    recipeImage.setImageURI(recipeWithRecipeItems.recipe.imageUri.toUri())
                }
                recipeDescription.text = recipeWithRecipeItems.recipe.description
                cookingTimeView.text = "${recipeWithRecipeItems.recipe.timeToCook} mins"
                numOfServingsView.text = "${recipeWithRecipeItems.recipe.servings} servings"
                if (recipeWithRecipeItems.recipe.numMissingIngredients == 0) {
                    isCookable = true
                    isAbleToCookImageView.setImageResource(R.drawable.ic_baseline_check_box_24)
                    isAbleToCookTextView.text = "Ready to cook!"
                    cookNowButton.visibility = View.VISIBLE
                } else {
                    isCookable = false
                    isAbleToCookImageView.setImageResource(R.drawable.low_stock)
                    isAbleToCookTextView.text =
                        "Missing ${recipeWithRecipeItems.recipe.numMissingIngredients} ingredients"
                    cookNowButton.visibility = View.INVISIBLE
                }
                recipeIngredientArrayAdapter.replaceRecipeIngredients(recipeWithRecipeItems.recipeItems)
                makeListViewShowThreeItems(ingredientListView)
                recipeIngredientArrayAdapter.notifyDataSetChanged()
            }
        }
        recipeViewModel.cookableRecipes.observe(this) {
            if (id2 != -1) {
                var recipeWithIngredients: RecipeWithRecipeItems = it[id2]
                recipeName.text = recipeWithIngredients.recipe.title
                cookingTimeView.text = "${recipeWithIngredients.recipe.timeToCook} mins"
                numOfServingsView.text = "${recipeWithIngredients.recipe.servings} servings"
                if (recipeWithIngredients.recipe.numMissingIngredients == 0) {
                    isAbleToCookImageView.setImageResource(R.drawable.ic_baseline_check_box_24)
                    isAbleToCookTextView.text = "Ready to cook!"
                } else {
                    isAbleToCookImageView.setImageResource(R.drawable.low_stock)
                    isAbleToCookTextView.text =
                        "Missing ${recipeWithIngredients.recipe.numMissingIngredients} ingredients"
                }
                recipeIngredientArrayAdapter.replaceRecipeIngredients(it[id2].recipeItems)
                makeListViewShowThreeItems(ingredientListView)
                recipeIngredientArrayAdapter.notifyDataSetChanged()
            }
        }

        ingredientViewModel.allIngredientsLiveData.observe(this) {
            //recipeIngredientArrayAdapter.replaceAllIngredients(it)
            recipeIngredientArrayAdapter.notifyDataSetChanged()
        }
        notificationViewModel.newNotificationId.observe(this) {
            if (it != 0L) {
                sendNotification(it)
            }
        }

        editRecipeButton.setOnClickListener {
            val intent = Intent(this, EditRecipeActivity::class.java)
            intent.putExtra("RecipeChosen", id)
            startActivity(intent)
        }

        addIngredientsButton.setOnClickListener {
            if (recipeWithRecipeItems.recipeItems.isEmpty()) {
                Toast.makeText(
                    this,
                    "There are no ingredients in the recipe to add to shopping list",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (isCookable) {
                Toast.makeText(
                    this,
                    "Recipe is cookable. There are no missing ingredients to add to shopping list",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Open dialog for confirmation to add all missing ingredients to shopping list
                val addMissingIngredientsToShoppingListDialog =
                    AddMissingIngredientsToShoppingListDialog()
                addMissingIngredientsToShoppingListDialog.show(
                    supportFragmentManager,
                    "Add Missing Ingredients"
                )
            }
        }

        cookNowButton.setOnClickListener {
            if (id != -1) {
                CoroutineScope(Dispatchers.IO).launch {
                    UpdateDB.consumeIngredients(recipeWithRecipeItems, ingredientViewModel)
                    // List of updated ingredient Ids that have been consumed
                    val updatedIngredientsIds =
                        recipeWithRecipeItems.recipeItems.map { it.ingredient.ingredientId }
                    UpdateDB.postUpdatesAfterModifyIngredient(
                        updatedIngredientsIds,
                        ingredientViewModel,
                        recipeItemViewModel,
                        recipeViewModel
                    )
                    insertNotification(recipeWithRecipeItems)
                }

                Toast.makeText(this, "Recipe Cooked!", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                finish()
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction("FINISH")
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun insertNotification(recipe: RecipeWithRecipeItems) {
        val notification = Notification()
        notification.date = Calendar.getInstance()
        val recipeItems = recipe.recipeItems
        for (item in recipeItems) {
            if (item.ingredient.isNotify) {
                if (item.ingredient.amount <= item.ingredient.notifyThreshold) {
                    lowIngredients.add(item.ingredient)
                }
            }
        }
        if (lowIngredients.size > 0) {
            if (lowIngredients.size == 1) {
                notification.description = "Low On ${lowIngredients[0].name}"
            } else {
                notification.description = "Low On ${lowIngredients.size} ingredients"
            }
            notificationViewModel.insert(notification, lowIngredients)
        }
    }

    override fun onConfirmationClick(isConfirm: Boolean) {
        // Add the missing ingredients in the recipe to the shopping list
        if (isConfirm) {
            shoppingListItemViewModel.insertMissingRecipeIngredients(recipeWithRecipeItems.recipeItems)
            Toast.makeText(
                this,
                "Added missing ingredients to shopping list!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "LowIngredients"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("lowIngredients", name, importance)
            // Register the channel with the system
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(l: Long) {
        val resultIntent = Intent(this, LowIngredientActivity::class.java).apply {
            putExtra("NotificationId", l)
        }
        // Create the TaskStackBuilder
        //https://stackoverflow.com/questions/7370324/notification-passes-old-intent-extras Used this to help with notifications erasing old notifications intents
        val iUniqueId = (System.currentTimeMillis() and 0xfffffff).toInt()
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(
                iUniqueId,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        var builder: NotificationCompat.Builder? = null
        if (lowIngredients.size == 1) {
            builder = NotificationCompat.Builder(this, "lowIngredients")
                .setSmallIcon(R.drawable.magic_pantry_app_logo)
                .setContentTitle("You are low on ${lowIngredients[0].name}")
                .setContentText("Click here to view")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
        } else if (lowIngredients.size > 1) {
            builder = NotificationCompat.Builder(this, "lowIngredients")
                .setSmallIcon(R.drawable.magic_pantry_app_logo)
                .setContentTitle("You are low on ${lowIngredients.size} ingredients")
                .setContentText("Click here to view")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
        }
        with(NotificationManagerCompat.from(this)) {
            if (builder != null) {
                notify(l.toInt(), builder.build())
            }
        }
    }

    // https://stackoverflow.com/questions/12212890/disable-scrolling-of-a-listview-contained-within-a-scrollview
    private fun makeListViewShowThreeItems(listView: ListView) {
        val cellSize = 200
        val listViewAdapter = listView.adapter ?: return

        var listViewHeight = cellSize * listViewAdapter.count

        if (listViewAdapter.count > 3) {
            listViewHeight = cellSize * 3
        }

        val listViewParams = listView.layoutParams
        listViewParams.height = listViewHeight
        listView.layoutParams = listViewParams
        listView.requestLayout()
    }

    // https://stackoverflow.com/a/27212978 for adding back button in title bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }
}
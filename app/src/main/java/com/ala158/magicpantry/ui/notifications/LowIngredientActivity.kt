package com.ala158.magicpantry.ui.notifications

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.LowIngredientArrayAdapter
import com.ala158.magicpantry.data.NotificationWithIngredients
import com.ala158.magicpantry.dialogs.AddMissingIngredientsToShoppingListDialog
import com.ala158.magicpantry.viewModel.NotificationViewModel
import com.ala158.magicpantry.viewModel.ShoppingListItemViewModel
import java.text.SimpleDateFormat
import java.util.*

class LowIngredientActivity : AppCompatActivity(),
    AddMissingIngredientsToShoppingListDialog.AddMissingIngredientDialogListener {
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var shoppingListItemViewModel: ShoppingListItemViewModel

    private lateinit var lowIngredientAdapter: LowIngredientArrayAdapter
    private lateinit var headerTextView: TextView
    private lateinit var dateTimeTextView: TextView
    private lateinit var ingredientsListView: ListView
    private lateinit var addAllBtn: Button

    private lateinit var currNotification: NotificationWithIngredients

    private var pos = 0
    private var id = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_low_ingredient)

        id = intent.getLongExtra("NotificationId", -1L)
        pos = intent.getIntExtra("NotificationPosition", 0)

        headerTextView = findViewById(R.id.header_notifications)
        dateTimeTextView = findViewById(R.id.low_ingredient_date)
        ingredientsListView = findViewById(R.id.notifications_list_view)
        addAllBtn = findViewById(R.id.btn_add_ingredient_notifications)

        val dateFormat = SimpleDateFormat("MMM dd, yyyy  hh:mmaa")
        val formattedDate: String = dateFormat.format(Date()).toString()
        dateTimeTextView.text = formattedDate

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

        lowIngredientAdapter =
            LowIngredientArrayAdapter(this, ArrayList(), shoppingListItemViewModel)
        ingredientsListView.adapter = lowIngredientAdapter

        notificationViewModel.allNotifications.observe(this) { notifications ->
            if (notifications.isNotEmpty()) {
                currNotification = notifications[pos]
                lowIngredientAdapter.replace(currNotification.ingredients)

                if (lowIngredientAdapter.validIngredients.isEmpty()) {
                    addAllBtn.visibility = View.GONE
                    headerTextView.text = "All stocked up!"
                } else {
                    addAllBtn.visibility = View.VISIBLE
                    headerTextView.text = "Low stock on..."
                }

                if (!currNotification.notification.isRead) {
                    currNotification.notification.isRead = true
                    notificationViewModel.updateRead(currNotification.notification)
                }

                if (id != -1L) {
                    notificationViewModel.getById(id).observe(this) {
                        val ingredients = it.ingredients
                        lowIngredientAdapter.replace(ingredients)
                        lowIngredientAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        shoppingListItemViewModel = Util.createViewModel(
            this,
            ShoppingListItemViewModel::class.java,
            Util.DataType.SHOPPING_LIST_ITEM
        )

        val addAllBtn = findViewById<Button>(R.id.btn_add_ingredient_notifications)
        addAllBtn.setOnClickListener {
            saveAllToShoppingList()
        }
    }

    private fun saveAllToShoppingList() {
        // Open dialog for confirmation to add all missing ingredients to shopping list
        val addMissingIngredientsToShoppingListDialog = AddMissingIngredientsToShoppingListDialog()
        addMissingIngredientsToShoppingListDialog.show(
            supportFragmentManager,
            "Add Missing Ingredients"
        )
    }

    override fun onConfirmationClick(isConfirm: Boolean) {
        // Add the missing ingredients from the notification to the shopping list
        if (isConfirm) {
            shoppingListItemViewModel.insertLowIngredientsFromNotifications(
                lowIngredientAdapter.validIngredients
            )
            Toast.makeText(
                this,
                "Added low stock ingredients to shopping list!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
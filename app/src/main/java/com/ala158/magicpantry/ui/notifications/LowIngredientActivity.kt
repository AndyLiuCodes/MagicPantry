package com.ala158.magicpantry.ui.notifications

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.LowIngredientArrayAdapter
import com.ala158.magicpantry.data.Notification
import com.ala158.magicpantry.viewModel.IngredientViewModel
import com.ala158.magicpantry.viewModel.NotificationViewModel
import com.ala158.magicpantry.viewModel.ShoppingListItemViewModel
import java.text.SimpleDateFormat
import java.util.*

class LowIngredientActivity : AppCompatActivity() {
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var shoppingListItemViewModel: ShoppingListItemViewModel

    private lateinit var ingredientList : ListView
    private var ingredients = arrayOf("bread", "milk")

    private var pos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_low_ingredient)

        pos = intent.getIntExtra("NotificationPosition", -1)

        val dateTime = findViewById<TextView>(R.id.low_ingredient_date)

        val dateFormat = SimpleDateFormat("MMM dd, yyyy  hh:mmaa")
        val formattedDate: String = dateFormat.format(Date()).toString()
        dateTime.text = formattedDate

        notificationViewModel = Util.createViewModel(
            this,
            NotificationViewModel::class.java,
            Util.DataType.NOTIFICATION
        )

        var notificationToUpdate: Notification? = null
        notificationViewModel.allNotifications.observe(this) {
            val myList = it.toTypedArray()

            //if not empty
            if (myList.isNotEmpty()) {
                notificationToUpdate = myList[pos].notification
            }
        }
        if (notificationToUpdate != null) {
            notificationViewModel.updateRead(
                Notification(
                    notificationToUpdate!!.notificationId,
                    notificationToUpdate!!.date,
                    notificationToUpdate!!.description,
                    true
                )
            )
        }

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

        ingredientList = findViewById(R.id.notifications_list_view)
        val adapter = LowIngredientArrayAdapter(this, ingredients)
        ingredientList.adapter = adapter

        val addAllBtn = findViewById<Button>(R.id.btn_add_ingredient_notifications)
        addAllBtn.setOnClickListener {
            saveToShoppingList()
        }
    }

    private fun saveToShoppingList() {
        //TODO:
    }
}
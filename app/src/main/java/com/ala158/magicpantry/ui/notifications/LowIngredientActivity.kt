package com.ala158.magicpantry.ui.notifications

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.arrayAdapter.LowIngredientArrayAdapter
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.Notification
import com.ala158.magicpantry.data.NotificationWithIngredients
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
    private lateinit var lowIngredientListArrayAdapter: LowIngredientArrayAdapter
    private lateinit var notifications:List<NotificationWithIngredients>

    private var pos = 0
    private var id = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_low_ingredient)

        pos = intent.getIntExtra("NotificationPosition", -1)
        id = intent.getLongExtra("NotificationId", -1L )
        val dateTime = findViewById<TextView>(R.id.low_ingredient_date)

        val dateFormat = SimpleDateFormat("MMM dd, yyyy  hh:mmaa")
        val formattedDate: String = dateFormat.format(Date()).toString()
        dateTime.text = formattedDate

        notificationViewModel = Util.createViewModel(
            this,
            NotificationViewModel::class.java,
            Util.DataType.NOTIFICATION
        )
        ingredientList = findViewById(R.id.notifications_list_view)
        lowIngredientListArrayAdapter = LowIngredientArrayAdapter(this,ArrayList())
        ingredientList.adapter = lowIngredientListArrayAdapter
        var notificationToUpdate: Notification? = null
        notificationViewModel.allNotifications.observe(this) {
            val myList = it.toTypedArray()

            //if not empty
            if (myList.isNotEmpty()) {
                if(pos != -1) {
                    notificationToUpdate = myList[pos].notification
                    lowIngredientListArrayAdapter.replace(myList[pos].ingredients)
                    lowIngredientListArrayAdapter.notifyDataSetChanged()
                }
                if(id != -1L){
                    notificationViewModel.getById(id).observe(this){
                        val notification = it.notification
                        val ingredients =it.ingredients
                        lowIngredientListArrayAdapter.replace(ingredients)
                        lowIngredientListArrayAdapter.notifyDataSetChanged()
                    }
                }
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

        val addAllBtn = findViewById<Button>(R.id.btn_add_ingredient_notifications)
        addAllBtn.setOnClickListener {
            saveToShoppingList()
        }
    }

    private fun saveToShoppingList() {
        //TODO:
    }
}
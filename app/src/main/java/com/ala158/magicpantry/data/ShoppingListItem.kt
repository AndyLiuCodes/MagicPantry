package com.ala158.magicpantry.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list_item")
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true)
    var shoppingListItemId: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "amount")
    var amount: Int = 0,

    @ColumnInfo(name = "is_item_bought")
    var isItemBought: Boolean = false,
)

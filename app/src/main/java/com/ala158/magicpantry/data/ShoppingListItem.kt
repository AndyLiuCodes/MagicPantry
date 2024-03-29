package com.ala158.magicpantry.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// 1-1 Relationship REF: https://stackoverflow.com/questions/48079168/room-database-with-one-to-one-relation:w
@Entity(
    tableName = "shopping_list_item",
    foreignKeys = [ForeignKey(
        entity = Ingredient::class,
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE,
        parentColumns = ["ingredientId"],
        childColumns = ["related_ingredient_id"]
    )]
)
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true)
    var shoppingListItemId: Long = 0L,

    @ColumnInfo(name = "item_amount")
    var itemAmount: Double = 0.0,

    @ColumnInfo(name = "is_item_bought")
    var isItemBought: Boolean = false,

    @ColumnInfo(name = "related_ingredient_id", index = true)
    var relatedIngredientId: Long = 0L,
) {
    constructor(amount: Double, isItemBought: Boolean) : this() {
        this.itemAmount = amount
        this.isItemBought = isItemBought
    }

    constructor(amount: Double, isItemBought: Boolean, relatedIngredientId: Long) : this() {
        this.itemAmount = amount
        this.isItemBought = isItemBought
        this.relatedIngredientId = relatedIngredientId
    }
}

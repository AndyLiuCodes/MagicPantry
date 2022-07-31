package com.ala158.magicpantry.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_item",
    foreignKeys = [ForeignKey(
        entity = Ingredient::class,
        onUpdate = ForeignKey.CASCADE,
        parentColumns = ["ingredientId"],
        childColumns = ["related_ingredient_id"]
    )]
    )
data class RecipeItem(
    @PrimaryKey(autoGenerate = true)
    var recipeItemId: Long = 0L,

    @ColumnInfo(name = "recipe_amount")
    var recipeAmount: Int = 0,

    @ColumnInfo(name = "recipe_unit")
    var recipeUnit: String = "",

    @ColumnInfo(name = "recipe_is_enough")
    var recipeIsEnough: Boolean = false,

    @ColumnInfo(name = "related_ingredient_id")
    var relatedIngredientId: Long = 0L
)

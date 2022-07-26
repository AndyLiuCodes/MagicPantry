package com.ala158.magicpantry.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    var recipeId: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "image_uri")
    var imageUri: String = "",

    @ColumnInfo(name = "servings")
    var servings: Int = 0,

    @ColumnInfo(name = "time_to_cook")
    var timeToCook: Int = 0,

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "num_missing_ingredients")
    var numMissingIngredients: Int = 0,
){
    constructor(title: String, servings: Int, timeToCook: Int, description: String ) : this() {
        this.title = title
        this.servings = servings
        this.timeToCook = timeToCook
        this.description = description
    }
}

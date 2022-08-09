package com.ala158.magicpantry.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe_item",
    foreignKeys = [
        ForeignKey(
            entity = Ingredient::class,
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
            parentColumns = ["ingredientId"],
            childColumns = ["related_ingredient_id"]
        ),
        ForeignKey(
            entity = Recipe::class,
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
            parentColumns = ["recipeId"],
            childColumns = ["related_recipe_id"]
        )
    ]
)
data class RecipeItem(
    @PrimaryKey(autoGenerate = true)
    var recipeItemId: Long = 0L,

    @ColumnInfo(name = "recipe_amount")
    var recipeAmount: Double = 0.0,

    @ColumnInfo(name = "recipe_unit")
    var recipeUnit: String = "",

    @ColumnInfo(name = "recipe_is_enough")
    var recipeIsEnough: Boolean = false,

    @ColumnInfo(name = "related_ingredient_id", index = true)
    var relatedIngredientId: Long = 0L,

    @ColumnInfo(name = "related_recipe_id", index = true)
    var relatedRecipeId: Long = 0L
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readString() ?: "unit",
        parcel.readByte() != 0.toByte(),
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(recipeItemId)
        parcel.writeDouble(recipeAmount)
        parcel.writeString(recipeUnit)
        parcel.writeByte(if (recipeIsEnough) 1 else 0)
        parcel.writeLong(relatedIngredientId)
        parcel.writeLong(relatedRecipeId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecipeItem> {
        override fun createFromParcel(parcel: Parcel): RecipeItem {
            return RecipeItem(parcel)
        }

        override fun newArray(size: Int): Array<RecipeItem?> {
            return arrayOfNulls(size)
        }
    }
}

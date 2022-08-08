package com.ala158.magicpantry.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation

data class RecipeItemAndIngredient(
    @Embedded val recipeItem: RecipeItem,
    @Relation(
        parentColumn = "related_ingredient_id",
        entityColumn = "ingredientId"
    )
    val ingredient: Ingredient,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(RecipeItem::class.java.classLoader)!!,
        parcel.readParcelable(Ingredient::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(recipeItem, flags)
        parcel.writeParcelable(ingredient, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecipeItemAndIngredient> {
        override fun createFromParcel(parcel: Parcel): RecipeItemAndIngredient {
            return RecipeItemAndIngredient(parcel)
        }

        override fun newArray(size: Int): Array<RecipeItemAndIngredient?> {
            return arrayOfNulls(size)
        }
    }
}

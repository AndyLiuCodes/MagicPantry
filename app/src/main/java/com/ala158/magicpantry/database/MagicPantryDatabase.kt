package com.ala158.magicpantry.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ala158.magicpantry.dao.IngredientDAO
import com.ala158.magicpantry.dao.RecipeDAO
import com.ala158.magicpantry.data.Ingredient
import com.ala158.magicpantry.data.Recipe

@Database(entities = [Ingredient::class, Recipe::class], version = 2)
abstract class MagicPantryDatabase : RoomDatabase() {
    abstract val ingredientDAO: IngredientDAO
    abstract val recipeDAO: RecipeDAO

    companion object {
        @Volatile
        private var INSTANCE: MagicPantryDatabase? = null

        fun getInstance(context: Context): MagicPantryDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext, MagicPantryDatabase::class.java,
                        "magic_pantry"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
package com.ala158.magicpantry.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ala158.magicpantry.converters.Converters
import com.ala158.magicpantry.dao.*
import com.ala158.magicpantry.data.*

@Database(
    entities = [Ingredient::class, Recipe::class, RecipeItem::class, ShoppingListItem::class, Notification::class, IngredientNotificationCrossRef::class],
    version = 12
)
@TypeConverters(Converters::class)
abstract class MagicPantryDatabase : RoomDatabase() {
    abstract val ingredientDAO: IngredientDAO
    abstract val recipeDAO: RecipeDAO
    abstract val recipeItemDAO: RecipeItemDAO
    abstract val shoppingListItemDAO: ShoppingListItemDAO
    abstract val notificationDAO: NotificationDAO

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
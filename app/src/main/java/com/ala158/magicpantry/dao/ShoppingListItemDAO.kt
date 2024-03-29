package com.ala158.magicpantry.dao

import androidx.room.*
import com.ala158.magicpantry.data.ShoppingListItem
import com.ala158.magicpantry.data.ShoppingListItemAndIngredient
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListItemDAO {
    @Transaction
    @Query("SELECT * FROM shopping_list_item, ingredient WHERE related_ingredient_id = ingredientId")
    fun getAllShoppingListItems(): Flow<List<ShoppingListItemAndIngredient>>

    @Query("SELECT * FROM shopping_list_item, ingredient WHERE related_ingredient_id = :ingredientId")
    suspend fun getShoppingListItemAndIngredientByIngredientId(ingredientId: Long): ShoppingListItemAndIngredient

    @Query("SELECT * FROM shopping_list_item WHERE related_ingredient_id = :ingredientId")
    suspend fun getShoppingListItemByIngredientId(ingredientId: Long): ShoppingListItem?

    @Query("SELECT * FROM shopping_list_item, ingredient WHERE related_ingredient_id = ingredientId AND  is_item_bought = 1")
    fun getBoughtShoppingListItemsSync(): List<ShoppingListItemAndIngredient>

    @Insert
    suspend fun insertShoppingListItem(item: ShoppingListItem)

    @Update
    suspend fun updateShoppingListItem(shoppingListItem: ShoppingListItem)

    @Query("DELETE FROM shopping_list_item where shoppingListItemId = :key")
    suspend fun deleteShoppingListItemById(key: Long)

    @Query("DELETE FROM shopping_list_item where is_item_bought = 1")
    suspend fun deleteAllIsBoughtShoppingListItems()

}
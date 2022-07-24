package com.ala158.magicpantry.dao

import androidx.room.*
import com.ala158.magicpantry.data.ShoppingListItemAndIngredient
import com.ala158.magicpantry.data.ShoppingListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListItemDAO {
    @Insert
    suspend fun insertShoppingListItem(item: ShoppingListItem)

    @Query("SELECT * FROM shopping_list_item where shoppingListItemId = :key")
    suspend fun getShoppingListItem(key: Long): ShoppingListItem

    @Query("SELECT * FROM shopping_list_item")
    fun getAllShoppingListItems(): Flow<List<ShoppingListItem>>

    @Transaction
    @Query("SELECT * FROM shopping_list_item where shoppingListItemId = :key")
    fun getIngredientAndShoppingListItem(key: Long): Flow<ShoppingListItemAndIngredient>

    @Delete
    suspend fun deleteShoppingListItem(shoppingListItem: ShoppingListItem)

    @Update
    suspend fun updateShoppingListItem(shoppingListItem: ShoppingListItem)

    @Query("DELETE FROM shopping_list_item where shoppingListItemId = :key")
    suspend fun deleteShoppingListItemById(key: Long)

}
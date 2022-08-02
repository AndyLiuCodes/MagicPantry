package com.ala158.magicpantry.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredient")
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    var ingredientId: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "amount")
    var amount: Int = 0,

    @ColumnInfo(name = "unit")
    var unit: String = "unit",

    @ColumnInfo(name = "price")
    var price: Double = 0.0,

    @ColumnInfo(name = "is_notify")
    var isNotify: Boolean = false,

    @ColumnInfo(name = "notify_threshold")
    var notifyThreshold: Int = 0,
) {
    constructor(name: String, amount: Int, unit: String, price: Double) : this() {
        this.name = name
        this.amount = amount
        this.unit = unit
        this.price = price
    }

    constructor(name: String, amount: Int, unit: String, price: Double, notifyThreshold: Int): this() {
        this.name = name
        this.amount = amount
        this.unit = unit
        this.price = price
        this.notifyThreshold = notifyThreshold
    }
}

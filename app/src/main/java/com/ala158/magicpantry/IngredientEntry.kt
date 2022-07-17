package com.ala158.magicpantry

class IngredientEntry {
    private var name: String = ""
    private var amount: Int = 0
    private var unit: String = "unit"
    private var price: Double = 0.0

    fun getName() : String {
        return name
    }

    fun setName(newName: String) {
        name = newName
    }

    fun getAmount() : Int {
        return amount
    }

    fun setAmount(newAmount: Int) {
        amount = newAmount
    }

    fun getUnit() : String {
        return unit
    }

    fun setUnit(newUnit: String) {
        unit = newUnit
    }

    fun getPrice(): Double {
        return price
    }

    fun setPrice(newPrice: Double) {
        price = newPrice
    }
}
package com.ala158.magicpantry

class IngredientEntry {
    private var id: Long = -1L
    private var name: String = ""
    private var amount: Double = 0.0
    private var unit: String = "unit"
    private var price: Double = 0.0
    private var isNotify: Boolean = false
    private var notifyThreshold: Int = 0

    fun getId() : Long {
        return id
    }

    fun setId(newId: Long) {
        id = newId
    }
    fun getName() : String {
        return name
    }

    fun setName(newName: String) {
        name = newName
    }

    fun getAmount() : Double {
        return amount
    }

    fun setAmount(newAmount: Double) {
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

    fun getIsNotify() : Boolean {
        return isNotify
    }

    fun setIsNotify(newIsNotify: Boolean) {
        isNotify = newIsNotify
    }

    fun getNotifyThreshold() : Int {
        return notifyThreshold
    }

    fun setNotifyThreshold(newNotifyThreshold : Int) {
        notifyThreshold = newNotifyThreshold
    }
}
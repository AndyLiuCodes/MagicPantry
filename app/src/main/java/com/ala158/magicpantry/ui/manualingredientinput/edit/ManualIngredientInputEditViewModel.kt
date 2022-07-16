package com.ala158.magicpantry.ui.manualingredientinput.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ManualIngredientInputEditViewModel : ViewModel() {

    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name
    fun setName(newName: String) {
        _name.value = newName
    }

    private val _amount = MutableLiveData(0)
    val amount: LiveData<Int> = _amount
    fun setAmount(newAmount: Int) {
        _amount.value = newAmount
    }

    private val _unit = MutableLiveData("")
    val unit: LiveData<String> = _unit
    fun setUnit(newUnit: String) {
        _unit.value = newUnit
    }

    private val _price = MutableLiveData(0.0)
    val price: LiveData<Double> = _price
    fun setPrice(newPrice: Double) {
        _price.value = newPrice
    }
}
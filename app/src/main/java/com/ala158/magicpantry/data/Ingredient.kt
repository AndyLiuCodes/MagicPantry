package com.ala158.magicpantry.data

import android.os.Parcel
import android.os.Parcelable
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
    var amount: Double = 0.0,

    @ColumnInfo(name = "unit")
    var unit: String = "unit",

    @ColumnInfo(name = "price")
    var price: Double = 0.0,

    @ColumnInfo(name = "is_notify")
    var isNotify: Boolean = false,

    @ColumnInfo(name = "notify_threshold")
    var notifyThreshold: Double = 0.0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "unit",
        parcel.readDouble(),
        parcel.readByte() != 0.toByte(),
        parcel.readDouble()
    )

    constructor(name: String, amount: Double, unit: String, price: Double) : this() {
        this.name = name
        this.amount = amount
        this.unit = unit
        this.price = price
    }

    constructor(name: String, amount: Double, unit: String, price: Double, notifyThreshold: Double): this() {
        this.name = name
        this.amount = amount
        this.unit = unit
        this.price = price
        this.notifyThreshold = notifyThreshold
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(ingredientId)
        parcel.writeString(name)
        parcel.writeDouble(amount)
        parcel.writeString(unit)
        parcel.writeDouble(price)
        parcel.writeByte(if (isNotify) 1 else 0)
        parcel.writeDouble(notifyThreshold)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ingredient> {
        override fun createFromParcel(parcel: Parcel): Ingredient {
            return Ingredient(parcel)
        }

        override fun newArray(size: Int): Array<Ingredient?> {
            return arrayOfNulls(size)
        }
    }
}

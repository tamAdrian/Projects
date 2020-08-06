package com.example.restaurantapplication.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RestaurantMenu(
    var name: String?,
    var restaurant: String,
    var foodImageURL: String,
    var category: String,
    var price: Float,
    var quantity: Int = 0,
    var description: String
) : Parcelable {
    constructor() : this("", "", "", "", 0f, 0, "")

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (javaClass != other?.javaClass)
            return false
        other as RestaurantMenu
        if (name == other.name && restaurant == other.restaurant && category == other.category) {
            return true
        }
        return false
    }
}
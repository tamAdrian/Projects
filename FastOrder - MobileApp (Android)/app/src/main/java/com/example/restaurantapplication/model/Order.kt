package com.example.restaurantapplication.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlin.collections.ArrayList

@Parcelize
data class Order(
    var id: String,
    var authUser: String?,
    var orders: ArrayList<RestaurantMenu>,
    var totalPrice: Float,
    var totalMenuItems: Int,
    var active: Boolean,
    var restaurantName: String
) : Parcelable {
    constructor() : this("", "", ArrayList<RestaurantMenu>(),0f, 0, false, "")

    override fun toString(): String {
        return "{ ${orders.size} , tPrice: $totalPrice, menuItems: $totalMenuItems }"
    }
}
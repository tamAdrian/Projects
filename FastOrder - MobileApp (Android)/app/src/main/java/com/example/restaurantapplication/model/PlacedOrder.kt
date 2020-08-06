package com.example.restaurantapplication.model

import android.os.Parcelable
import com.example.restaurantapplication.utils.NEPRELUATA
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlacedOrder(
    var id: String?,
    var order: Order,
    var status: String,
    var date: String?,
    var hour: String?,
    var numberOfPeople: String?,
    var fcmToken: String?,
    var restaurantName: String
) : Parcelable {
    constructor() : this("", Order(), NEPRELUATA, "", "", "", "", "")
}

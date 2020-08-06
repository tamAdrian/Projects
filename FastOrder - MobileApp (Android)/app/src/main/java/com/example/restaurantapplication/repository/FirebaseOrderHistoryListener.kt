package com.example.restaurantapplication.repository

import com.example.restaurantapplication.model.PlacedOrder

interface FirebaseOrderHistoryListener {
    fun onSuccessGetPlacedOrdersByUserId(placedOrders: ArrayList<PlacedOrder>)
    fun onError(error: String)
}
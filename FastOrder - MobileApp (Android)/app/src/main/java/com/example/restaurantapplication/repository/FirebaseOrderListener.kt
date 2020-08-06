package com.example.restaurantapplication.repository

import com.example.restaurantapplication.model.Order
import com.example.restaurantapplication.model.RestaurantMenu

interface FirebaseOrderListener {
    fun onSuccessLastOrder(order: Order?)
    fun onSuccessUpdateOrder(orders: ArrayList<RestaurantMenu>)
    fun onSuccessChangeOrderStatus()
    fun onError(error: String)
}
package com.example.restaurantapplication.repository

import com.example.restaurantapplication.model.RestaurantMenu

interface FirebaseMenusListener {
    fun onSuccess(map: Map<String, List<RestaurantMenu>>)
    fun onError(error: String)
}
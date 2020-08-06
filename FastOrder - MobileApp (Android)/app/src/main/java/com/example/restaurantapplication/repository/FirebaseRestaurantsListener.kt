package com.example.restaurantapplication.repository

import com.example.restaurantapplication.model.Restaurant

interface FirebaseRestaurantsListener {
    fun onSuccess(list: List<Restaurant>)
    fun onSuccess(restaurant: Restaurant)
    fun onNoSearchResult()
    fun onError(error: String)
}
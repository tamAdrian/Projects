package com.example.restaurantapplication.model

data class Restaurant(
    var name: String,
    var imageURL: String,
    var latitude: Float,
    var longitude: Float,
    var description: String
) {
    constructor() : this("", "", 0f, 0f, "")
}
package com.example.restaurantapplication.todo.restaurantPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantapplication.model.Restaurant
import com.example.restaurantapplication.repository.FirebaseRestaurantsListener
import com.example.restaurantapplication.repository.RestaurantRepository

class RestaurantPageViewModel : ViewModel() {

    private val mutableRestaurant = MutableLiveData<Restaurant>()
    private val mutableException = MutableLiveData<String>().apply { value = "" }
    private val restaurantRepository = RestaurantRepository()

    val restaurant: LiveData<Restaurant> = mutableRestaurant
    val exception: LiveData<String> = mutableException

    fun getRestaurantByName(restaurantName: String) {
        restaurantRepository.getRestaurantByName(object : FirebaseRestaurantsListener {
            override fun onSuccess(list: List<Restaurant>) {

            }

            override fun onSuccess(restaurant: Restaurant) {
                mutableRestaurant.value = restaurant
            }

            override fun onNoSearchResult() {}

            override fun onError(error: String) {
                mutableException.value = error
            }

        }, restaurantName)
    }
}

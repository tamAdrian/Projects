package com.example.restaurantapplication.todo.restaurants

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantapplication.model.Restaurant
import com.example.restaurantapplication.repository.RestaurantRepository
import com.example.restaurantapplication.repository.FirebaseRestaurantsListener

class RestaurantListViewModel : ViewModel() {
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<String>().apply { value = "" }
    private val mutableNoSearchResult = MutableLiveData<Boolean>().apply { value = false }
    private val mutableRestaurants = MutableLiveData<List<Restaurant>>()
    private val restaurantRepository = RestaurantRepository()

    val loading: LiveData<Boolean> = mutableLoading
    val exception: LiveData<String> = mutableException
    val restaurants: LiveData<List<Restaurant>> = mutableRestaurants
    val noSearchResult: LiveData<Boolean> = mutableNoSearchResult

    init {
        loadRestaurants()
    }

    private fun loadRestaurants() {
        mutableLoading.value = true
        restaurantRepository.getAll(object :
            FirebaseRestaurantsListener {

            override fun onSuccess(restaurant: Restaurant) {
            }

            override fun onNoSearchResult() {}

            override fun onSuccess(list: List<Restaurant>) {
                mutableRestaurants.value = list
                mutableLoading.value = false
            }

            override fun onError(error: String) {
                mutableException.value = error
                mutableLoading.value = false
            }
        })
    }


    fun filterRestaurants(restaurantName: String?) {
        mutableLoading.value = true
        restaurantRepository.filterRestaurants(object :
            FirebaseRestaurantsListener {
            override fun onSuccess(restaurant: Restaurant) {}

            override fun onNoSearchResult() {
                mutableNoSearchResult.value = true
                mutableRestaurants.value = ArrayList<Restaurant>() // empty array
                mutableLoading.value = false
                mutableNoSearchResult.value = false
            }

            override fun onSuccess(list: List<Restaurant>) {
                mutableRestaurants.value = list
                mutableLoading.value = false
            }

            override fun onError(error: String) {
                mutableException.value = error
                mutableLoading.value = false
            }
        }, restaurantName)
    }

}

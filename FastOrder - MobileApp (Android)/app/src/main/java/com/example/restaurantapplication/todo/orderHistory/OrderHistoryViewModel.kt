package com.example.restaurantapplication.todo.orderHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantapplication.model.PlacedOrder
import com.example.restaurantapplication.repository.FirebaseOrderHistoryListener
import com.example.restaurantapplication.repository.PlacedOrderRepository
import com.google.firebase.auth.FirebaseAuth

class OrderHistoryViewModel : ViewModel() {
    private val mutablePlacedOrdersHistory = MutableLiveData<ArrayList<PlacedOrder>>()
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<String>().apply { value = "" }
    private val placedOrderRepository = PlacedOrderRepository()
    private var authId: String? = ""

    val placedOrdersHistory: LiveData<ArrayList<PlacedOrder>> = mutablePlacedOrdersHistory
    val loading: LiveData<Boolean> = mutableLoading
    val exception: LiveData<String> = mutableException

    fun getAuthUser() {
        authId = FirebaseAuth.getInstance().currentUser?.uid
    }

    fun getPlacedOrderHistoryByUser() {
        mutableLoading.value = true

        placedOrderRepository.getPlacedOrdersHistoryByUser(object : FirebaseOrderHistoryListener {
            override fun onSuccessGetPlacedOrdersByUserId(placedOrders: ArrayList<PlacedOrder>) {
                mutablePlacedOrdersHistory.value = placedOrders
                mutableLoading.value = false
            }

            override fun onError(error: String) {
                mutableException.value = error
                mutableLoading.value = false
            }
        }, authId)
    }
}
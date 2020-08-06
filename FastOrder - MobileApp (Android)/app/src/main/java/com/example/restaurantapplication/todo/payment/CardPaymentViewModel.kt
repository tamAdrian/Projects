package com.example.restaurantapplication.todo.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantapplication.model.Order
import com.example.restaurantapplication.model.PlacedOrder
import com.example.restaurantapplication.model.RestaurantMenu
import com.example.restaurantapplication.repository.FirebaseOrderListener
import com.example.restaurantapplication.repository.OrderRepository
import com.example.restaurantapplication.repository.PlacedOrderRepository

class CardPaymentViewModel : ViewModel() {
    private val mutableException = MutableLiveData<String>().apply { value = "" }
    private val placedOrderRepository = PlacedOrderRepository()
    private val orderRepository = OrderRepository()

    val exception: LiveData<String> = mutableException

    fun insertPlacedOrder(placedOrder: PlacedOrder) {
        placedOrderRepository.insert(placedOrder)
    }

    fun updateOrderStatus(order: Order) {
        orderRepository.updateOrderStatus(object : FirebaseOrderListener {
            override fun onSuccessLastOrder(order: Order?) {}

            override fun onSuccessUpdateOrder(orders: ArrayList<RestaurantMenu>) {}

            override fun onSuccessChangeOrderStatus() {}

            override fun onError(error: String) {
                mutableException.value = error
            }

        }, order)
    }
}
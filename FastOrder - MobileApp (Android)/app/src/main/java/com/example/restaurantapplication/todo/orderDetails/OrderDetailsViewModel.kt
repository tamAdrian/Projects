package com.example.restaurantapplication.todo.orderDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.restaurantapplication.model.Order
import com.example.restaurantapplication.model.RestaurantMenu
import com.example.restaurantapplication.repository.FirebaseOrderListener
import com.example.restaurantapplication.repository.OrderRepository

class OrderDetailsViewModel : ViewModel() {
    private val mutableActiveOrder = MutableLiveData<Order>()
    private val mutableEmptyOrder = MutableLiveData<Boolean>().apply { value = false }
    private val mutableDate = MutableLiveData<String>().apply { value = "" }
    private val mutableTime = MutableLiveData<String>().apply { value = "" }
    private val mutableNumberOfPeople = MutableLiveData<String>().apply { value = "1" }
    private val orderRepository = OrderRepository()

    val activeOrder: LiveData<Order> = mutableActiveOrder
    val emptyOrder: LiveData<Boolean> = mutableEmptyOrder
    val date: LiveData<String> = mutableDate
    val time: LiveData<String> = mutableTime
    val numberOfPeople: LiveData<String> = mutableNumberOfPeople

    val mediatorLiveData = MediatorLiveData<Boolean>()

    init {

        mediatorLiveData.addSource(mutableDate, Observer {
            mediatorLiveData.value = mutableTime.value != ""
        })

        mediatorLiveData.addSource(mutableTime, Observer {
            mediatorLiveData.value = mutableDate.value != ""
        })
    }

    fun initActiveOrder(argsOrder: Order) {
        mutableActiveOrder.value = argsOrder
    }

    fun chooseDate(date: String) {
        mutableDate.value = date
    }

    fun chooseTime(time: String) {
        mutableTime.value = time
    }

    fun chooseNumberOfPeople(numberOfPeople: String) {
        mutableNumberOfPeople.value = numberOfPeople
    }

    private fun insertToActiveOrder(
        menuItem: ArrayList<RestaurantMenu>,
        totalPrice: Float,
        totalMenuItems: Int,
        activeOrderId: String
    ) {
        orderRepository.updateOrder(object : FirebaseOrderListener {

            override fun onSuccessLastOrder(order: Order?) {}

            override fun onSuccessUpdateOrder(orders: ArrayList<RestaurantMenu>) {
                val updatedOrder = mutableActiveOrder.value?.copy()
                updatedOrder?.orders = orders
                mutableActiveOrder.value = updatedOrder
            }

            override fun onSuccessChangeOrderStatus() {}

            override fun onError(error: String) {}

        }, menuItem, totalPrice, totalMenuItems, activeOrderId)
    }

    fun addQuantity(restaurantItem: RestaurantMenu) {
        val restaurantItems = ArrayList(mutableActiveOrder.value?.orders.orEmpty())
        if (restaurantItems.contains(restaurantItem)) {
            val position = restaurantItems.indexOf(restaurantItem)
            restaurantItems[position].quantity++
        }

        mutableActiveOrder.value?.let {
            it.totalPrice += restaurantItem.price
            it.totalMenuItems += 1
            it.orders = restaurantItems

            insertToActiveOrder(it.orders, it.totalPrice, it.totalMenuItems, it.id)
        }
    }

    fun removeQuantity(restaurantItem: RestaurantMenu) {
        val restaurantItems = ArrayList(mutableActiveOrder.value?.orders.orEmpty())
        if (restaurantItems.contains(restaurantItem)) {
            val position = restaurantItems.indexOf(restaurantItem)
            restaurantItems[position].quantity--

            if (restaurantItems[position].quantity <= 0) {
                restaurantItems.removeAt(position)
            }
        }

        mutableActiveOrder.value?.let {
            it.totalPrice -= restaurantItem.price
            it.totalMenuItems -= 1
            it.orders = restaurantItems

            insertToActiveOrder(it.orders, it.totalPrice, it.totalMenuItems, it.id)
        }

        if (restaurantItems.isEmpty()) {
            mutableEmptyOrder.value = true
        }
    }
}

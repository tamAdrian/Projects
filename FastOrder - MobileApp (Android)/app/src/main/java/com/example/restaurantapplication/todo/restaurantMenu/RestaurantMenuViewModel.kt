package com.example.restaurantapplication.todo.restaurantMenu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantapplication.model.Order
import com.example.restaurantapplication.model.RestaurantMenu
import com.example.restaurantapplication.repository.FirebaseMenusListener
import com.example.restaurantapplication.repository.FirebaseOrderListener
import com.example.restaurantapplication.repository.MenusRepository
import com.example.restaurantapplication.repository.OrderRepository

class RestaurantMenuViewModel : ViewModel() {
    private val mutableMenus = MutableLiveData<Map<String, List<RestaurantMenu>>>()
    private val mutableException = MutableLiveData<String>().apply { value = "" }
    private val mutableActiveOrder = MutableLiveData<Order>()
    private val menusRepository = MenusRepository()
    private val orderRepository = OrderRepository()
    private val mutableIsOrderActive = MutableLiveData<Boolean>().apply { value = false }

    val restaurantMenu: LiveData<Map<String, List<RestaurantMenu>>> = mutableMenus
    val activeOrder: LiveData<Order> = mutableActiveOrder
    val exception: LiveData<String> = mutableException
    val isOrderActive: LiveData<Boolean> = mutableIsOrderActive

    fun getRestaurantNameGroupedByCategory(restaurantName: String) {
        menusRepository.getRestaurantNameGroupedByCategory(object : FirebaseMenusListener {
            override fun onSuccess(map: Map<String, List<RestaurantMenu>>) {
                mutableMenus.value = map
            }

            override fun onError(error: String) {
                mutableException.value = error
            }

        }, restaurantName)
    }

    private fun insertNewOrder(order: Order) {
        orderRepository.insert(order)
        mutableActiveOrder.value = order
        mutableIsOrderActive.value = true
    }

    fun getLastOrder(authUser: String?, restaurantName: String) {
        orderRepository.getLastOrder(object : FirebaseOrderListener {

            override fun onSuccessUpdateOrder(orders: ArrayList<RestaurantMenu>) {}

            override fun onSuccessChangeOrderStatus() {}

            override fun onSuccessLastOrder(order: Order?) {
                mutableActiveOrder.value = order
                val totalPrice = mutableActiveOrder.value?.totalPrice ?: 0f
                mutableIsOrderActive.value = totalPrice > 0f
            }

            override fun onError(error: String) {
                mutableException.value = error
            }

        }, authUser, restaurantName)
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
                mutableIsOrderActive.value = true
            }

            override fun onSuccessChangeOrderStatus() {}

            override fun onError(error: String) {
                mutableException.value = error
            }

        }, menuItem, totalPrice, totalMenuItems, activeOrderId)
    }

    private fun createNewOrder(selectedMenuItem: RestaurantMenu, loggedUser: String?): Order {
        selectedMenuItem.quantity = 1
        val menuItems = arrayListOf(selectedMenuItem)
        return Order(
            "",
            loggedUser,
            menuItems,
            selectedMenuItem.price,
            1,
            true,
            selectedMenuItem.restaurant
        )
    }

    private fun addMenuItemToActiveOrder(selectedMenuItem: RestaurantMenu) {
        val orderedItems: ArrayList<RestaurantMenu> =
            ArrayList(mutableActiveOrder.value?.orders.orEmpty())

        if (orderedItems.contains(selectedMenuItem)) {
            val position = orderedItems.indexOf(selectedMenuItem)
            orderedItems[position].quantity = orderedItems[position].quantity + 1
        } else {
            selectedMenuItem.quantity = 1
            orderedItems.add(selectedMenuItem)
        }

        mutableActiveOrder.value?.apply {
            totalPrice += selectedMenuItem.price
            totalMenuItems += 1
            orders = orderedItems
        }
    }

    fun handleOrder(selectedMenuItem: RestaurantMenu, loggedUser: String?) {
        if (mutableActiveOrder.value == null) {
            val order = createNewOrder(selectedMenuItem, loggedUser)
            insertNewOrder(order)
        } else {
            addMenuItemToActiveOrder(selectedMenuItem) //update order
            mutableActiveOrder.value?.let {
                insertToActiveOrder(it.orders, it.totalPrice, it.totalMenuItems, it.id)
            }
        }

    }
}

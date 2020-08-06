package com.example.restaurantapplication.repository

import com.example.restaurantapplication.model.Order
import com.example.restaurantapplication.model.RestaurantMenu
import com.example.restaurantapplication.utils.ORDERS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderRepository {
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var orderDatabaseReference: DatabaseReference =
        firebaseDatabase.reference.child(ORDERS)

    fun insert(order: Order) {
        val id = orderDatabaseReference.push().key
        if (id != null) {
            order.id = id
            orderDatabaseReference.child(id).setValue(order)
        }
    }

    fun getLastOrder(
        firebaseOrderListener: FirebaseOrderListener,
        authUser: String?,
        restaurantName: String
    ) {
        val query = orderDatabaseReference.orderByChild("authUser")
            .equalTo(authUser)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                firebaseOrderListener.onError(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children) {
                    (snapshot.getValue(Order::class.java))?.apply {
                        //check if we have an unplaced order for this restaurant
                        if (this.active && this.restaurantName == restaurantName) {
                            firebaseOrderListener.onSuccessLastOrder(this)
                        }
                    }
                }
            }

        })
    }

    fun updateOrder(
        firebaseOrderListener: FirebaseOrderListener,
        menuItems: ArrayList<RestaurantMenu>,
        totalPrice: Float,
        totalMenuItems: Int,
        activeOrderId: String
    ) {
        val newOrderData = HashMap<String, Any>()
        newOrderData["orders"] = menuItems
        newOrderData["totalPrice"] = totalPrice
        newOrderData["totalMenuItems"] = totalMenuItems

        orderDatabaseReference.child(activeOrderId).updateChildren(newOrderData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseOrderListener.onSuccessUpdateOrder(menuItems)
                } else if (task.isCanceled) {
                    firebaseOrderListener.onError(task.exception.toString())
                }
            }
    }

    fun updateOrderStatus(firebaseOrderListener: FirebaseOrderListener, order: Order) {
        orderDatabaseReference.child(order.id).child("active").setValue(false)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseOrderListener.onSuccessChangeOrderStatus()
                } else if (task.isCanceled) {
                    firebaseOrderListener.onError(task.exception.toString())
                }
            }
    }
}
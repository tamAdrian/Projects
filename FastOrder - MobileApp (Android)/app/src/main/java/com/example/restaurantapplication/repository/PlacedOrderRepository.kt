package com.example.restaurantapplication.repository

import com.example.restaurantapplication.model.PlacedOrder
import com.example.restaurantapplication.utils.ORDER
import com.example.restaurantapplication.utils.PLACED_ORDERS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date

class PlacedOrderRepository {

    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var placedOrderDatabaseReference: DatabaseReference =
        firebaseDatabase.reference.child(PLACED_ORDERS)

    fun insert(placedOrder: PlacedOrder) {
        val id = placedOrderDatabaseReference.push().key
        if (id != null) {
            placedOrder.id = id
            placedOrderDatabaseReference.child(id).setValue(placedOrder)
        }
    }

    fun getPlacedOrdersHistoryByUser(
        firebaseOrderHistoryListener: FirebaseOrderHistoryListener,
        authId: String?
    ) {
        val query = placedOrderDatabaseReference.orderByChild(ORDER)

        query.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                var placedOrders = ArrayList<PlacedOrder>()
                for (snapshot in p0.children) {
                    (snapshot.getValue(PlacedOrder::class.java))?.apply {
                        if (this.order.authUser == authId) {
                            placedOrders.add(this)
                        }
                    }
                }
                placedOrders = ArrayList(placedOrders.sortedByDescending { Date(it.date) })
                firebaseOrderHistoryListener.onSuccessGetPlacedOrdersByUserId(placedOrders)
            }

            override fun onCancelled(p0: DatabaseError) {
                firebaseOrderHistoryListener.onError(p0.message)
            }
        })
    }
}
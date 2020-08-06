package com.example.restaurantapplication.repository

import com.example.restaurantapplication.model.RestaurantMenu
import com.example.restaurantapplication.utils.MENUS
import com.example.restaurantapplication.utils.RESTAURANT
import com.google.firebase.database.*

class MenusRepository {
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var menusDatabaseReference: DatabaseReference =
        firebaseDatabase.reference.child(MENUS)

    fun getRestaurantNameGroupedByCategory(
        firebaseMenusListener: FirebaseMenusListener,
        restaurantName: String
    ) {
        val query = menusDatabaseReference.orderByChild(RESTAURANT).equalTo(restaurantName)

        query.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val restaurantMenu = ArrayList<RestaurantMenu>()
                for (snapshot in p0.children) {
                    (snapshot.getValue(RestaurantMenu::class.java))?.apply {
                        restaurantMenu.add(this)
                    }
                }
                val groupByCategoryMenu: Map<String, List<RestaurantMenu>> = restaurantMenu
                    .groupBy({ it.category }, { it })

                firebaseMenusListener.onSuccess(groupByCategoryMenu)
            }

            override fun onCancelled(p0: DatabaseError) {
                firebaseMenusListener.onError(p0.message)
            }
        })
    }
}
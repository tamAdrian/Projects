package com.example.restaurantapplication.repository

import com.example.restaurantapplication.model.Restaurant
import com.example.restaurantapplication.utils.NAME
import com.example.restaurantapplication.utils.RESTAURANTS
import com.google.firebase.database.*

class RestaurantRepository {
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var restaurantsDatabaseReference: DatabaseReference =
        firebaseDatabase.reference.child(RESTAURANTS)

    fun getAll(firebaseRestaurantsListener: FirebaseRestaurantsListener) {
        restaurantsDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val databaseRestaurants = ArrayList<Restaurant>()
                for (snapshot in p0.children) {
                    (snapshot.getValue(Restaurant::class.java))?.apply {
                        databaseRestaurants.add(this)
                    }
                }
                firebaseRestaurantsListener.onSuccess(databaseRestaurants)
            }

            override fun onCancelled(p0: DatabaseError) {
                firebaseRestaurantsListener.onError(p0.message)
            }
        })
    }

    fun filterRestaurants(
        firebaseRestaurantsListener: FirebaseRestaurantsListener,
        restaurantName: String?
    ) {

        restaurantsDatabaseReference.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val filteredRestaurants = ArrayList<Restaurant>()
                if (restaurantName == null) {
                    //show all restaurants
                } else {
                    var noResult = true

                    for (snapshot in p0.children) {
                        snapshot.getValue(Restaurant::class.java)?.apply {
                            if (this.name.toLowerCase().contains(restaurantName)) {
                                filteredRestaurants.add(this)
                                noResult = false
                            }
                        }
                    }

                    if (noResult) {
                        firebaseRestaurantsListener.onNoSearchResult()
                    } else {
                        firebaseRestaurantsListener.onSuccess(filteredRestaurants)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                firebaseRestaurantsListener.onError(p0.message)
            }
        })
    }

    fun getRestaurantByName(
        firebaseRestaurantsListener: FirebaseRestaurantsListener,
        restaurantName: String
    ) {
        val query = restaurantsDatabaseReference.orderByChild(NAME).equalTo(restaurantName)
            .limitToFirst(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children) {
                    (snapshot.getValue(Restaurant::class.java))?.apply {
                        firebaseRestaurantsListener.onSuccess(this)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                firebaseRestaurantsListener.onError(p0.message)
            }
        })
    }
}
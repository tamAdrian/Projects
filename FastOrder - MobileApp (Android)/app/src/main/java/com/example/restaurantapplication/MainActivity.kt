package com.example.restaurantapplication

import android.os.Bundle
import com.example.restaurantapplication.todo.internetConnection.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}

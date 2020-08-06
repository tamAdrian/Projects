package com.example.restaurantapplication.todo.internetConnection

import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurantapplication.R
import com.google.android.material.snackbar.Snackbar


//THIS IS THE BASE ACTIVITY OF ALL ACTIVITIES OF THE APPLICATION.
open class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private var mSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }


    private fun showMessage(isConnected: Boolean) {
        if (!isConnected) {
            val messageToUser = "Nu exista conexiune la internet."
            mSnackBar = Snackbar.make(
                findViewById(R.id.root_layout),
                messageToUser,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction("HIDE") { mSnackBar?.dismiss() } //Assume "rootLayout" as the root layout of every activity.
            mSnackBar?.setActionTextColor(Color.GRAY)
            mSnackBar?.view?.setBackgroundColor(applicationContext.resources.getColor(R.color.colorPrimaryDark))
            mSnackBar?.show()
        } else {
            mSnackBar?.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()

        ConnectivityReceiver.connectivityReceiverListener = this
    }

    /**
     * Callback will be called when there is change
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }
}
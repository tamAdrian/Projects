package com.example.restaurantapplication.todo.orderHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.R
import com.example.restaurantapplication.model.PlacedOrder
import kotlinx.android.synthetic.main.order_history_view.view.date_and_time
import kotlinx.android.synthetic.main.order_history_view.view.restaurant_name
import kotlinx.android.synthetic.main.order_history_view.view.total_price

class OrderHistoryAdapter : ListAdapter<PlacedOrder, OrderHistoryAdapter.ViewHolder>(OrderHistoryDiffCallback()) {


    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val restaurantName: TextView = view.restaurant_name
        val dataAndTime: TextView = view.date_and_time
        val totalPrice: TextView = view.total_price
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val placedOrder = getItem(position)
        holder.restaurantName.text = placedOrder.restaurantName
        val dateAndTime = placedOrder.date + " " + placedOrder.hour
        holder.dataAndTime.text = dateAndTime
        holder.totalPrice.text = placedOrder.order.totalPrice.toString()
    }

    class OrderHistoryDiffCallback : DiffUtil.ItemCallback<PlacedOrder>() {
        override fun areItemsTheSame(oldItem: PlacedOrder, newItem: PlacedOrder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlacedOrder, newItem: PlacedOrder): Boolean {
            return oldItem == newItem
        }
    }
}

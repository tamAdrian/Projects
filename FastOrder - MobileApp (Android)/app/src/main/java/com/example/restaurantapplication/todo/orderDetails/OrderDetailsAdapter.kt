package com.example.restaurantapplication.todo.orderDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapplication.R
import com.example.restaurantapplication.model.RestaurantMenu
import kotlinx.android.synthetic.main.order_details_view.view.*

class OrderDetailsAdapter(
    private val itemClickListener: OnItemClickListener
) : ListAdapter<RestaurantMenu, OrderDetailsAdapter.ViewHolder>(RestaurantDiffCallback()) {

    interface OnItemClickListener {
        fun onAddClick(position: Int)
        fun onRemoveClick(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_details_view, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderedItem = getItem(position)
        holder.foodNameTextView.text = orderedItem.name.toString()
        holder.quantity.text = orderedItem.quantity.toString()
    }

    inner class ViewHolder(view: View, itemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(view) {
        val foodNameTextView: TextView = view.food_name
        val quantity: TextView = view.quantity

        init {
            view.add_button.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onAddClick(position)
                    val quantity = view.quantity.text.toString().toInt() + 1
                    view.quantity.text = quantity.toString()
                }
            }

            view.remove_button.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    var quantity = view.quantity.text.toString().toInt()
                    if (quantity != 0) {
                        itemClickListener.onRemoveClick(position)
                        quantity--
                    } else {
                        view.remove_button.isClickable = false
                    }
                    //update quantity
                    view.quantity.text = quantity.toString()
                }
            }
        }

    }

    class RestaurantDiffCallback : DiffUtil.ItemCallback<RestaurantMenu>() {
        override fun areItemsTheSame(oldItem: RestaurantMenu, newItem: RestaurantMenu): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: RestaurantMenu, newItem: RestaurantMenu): Boolean {
            return oldItem == newItem
        }
    }
}
package com.example.restaurantapplication.todo.restaurantMenu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.restaurantapplication.R
import com.example.restaurantapplication.model.RestaurantMenu
import kotlinx.android.synthetic.main.restaurant_menu_view.view.*

class RestaurantMenuAdapter(
    private val context: Context,
    private val itemClickListener: OnItemClickListener
) : ListAdapter<RestaurantMenu, RestaurantMenuAdapter.ViewHolder>(RestaurantDiffCallback()) {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_menu_view, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menuItem: RestaurantMenu = getItem(position)

        holder.name.text = menuItem.name

        Glide.with(context)
            .load(menuItem.foodImageURL)
            .placeholder(R.drawable.roundedlogo)
            .transition(DrawableTransitionOptions.withCrossFade(350))
            .into(holder.foodImage)


        val priceText: String =
            context.getString(R.string.food_price, menuItem.price.toString()) ?: "Pret"
        holder.price.text = priceText

        holder.description.text = menuItem.description

        if (menuItem.quantity > 0) {
            holder.counter.visibility = View.VISIBLE
            holder.counter.text = menuItem.quantity.toString()
        } else {
            holder.counter.visibility = View.GONE
            holder.counter.text = "0"
        }
    }

    inner class ViewHolder(view: View, itemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(view) {
        val foodImage: ImageView = view.food_image
        val name: TextView = view.food_name
        val description: TextView = view.description
        val price: TextView = view.price
        val counter: TextView = view.counter

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position)

                    view.counter.visibility = View.VISIBLE
                    var count = view.counter.text.toString().toInt()
                    count += 1
                    view.counter.text = count.toString()
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


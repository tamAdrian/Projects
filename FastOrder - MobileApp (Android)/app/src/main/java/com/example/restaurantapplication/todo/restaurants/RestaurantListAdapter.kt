package com.example.restaurantapplication.todo.restaurants

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
import com.example.restaurantapplication.R
import com.example.restaurantapplication.model.Restaurant
import kotlinx.android.synthetic.main.restaurant_view.view.*

class RestaurantListAdapter(
    private val context: Context,
    private val itemClickListener: OnItemClickListener
) : ListAdapter<Restaurant, RestaurantListAdapter.ViewHolder>(RestaurantDiffCallback()) {

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_view, parent, false)
        return ViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.restaurantNameTextView.text = item.name
        holder.restaurantMenuDescription.text = item.description
        
        Glide.with(context)
            .load(item.imageURL)
            .placeholder(R.drawable.roundedlogo)
            .fitCenter()
            .into(holder.restaurantImageView)
    }

    inner class ViewHolder(view: View, itemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(view) {
        val restaurantNameTextView: TextView = view.restaurant_name
        val restaurantImageView: ImageView = view.restaurant_image
        val restaurantMenuDescription: TextView = view.menu_description

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(
                        view,
                        position
                    )
                }
            }
        }
    }

    class RestaurantDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem == newItem
        }
    }
}


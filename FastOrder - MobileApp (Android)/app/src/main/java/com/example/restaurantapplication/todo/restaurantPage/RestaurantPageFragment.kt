package com.example.restaurantapplication.todo.restaurantPage

import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.restaurantapplication.R
import com.example.restaurantapplication.model.Restaurant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.restaurant_page_fragment.*

class RestaurantPageFragment : Fragment() {

    private lateinit var viewModel: RestaurantPageViewModel
    private lateinit var restaurant: Restaurant
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMaps: GoogleMap
    private val args by navArgs<RestaurantPageFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.restaurant_page_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RestaurantPageViewModel::class.java)

        val restaurantName = args.restaurantName

        setupViewModel(restaurantName)

        menu_image.setOnClickListener {
            this.findNavController()
                .navigate(
                    RestaurantPageFragmentDirections.actionRestaurantPageFragmentToRestaurantMenu(
                        restaurantName
                    )
                )
        }
    }

    private fun setupGoogleMaps() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMaps = it
            val location = LatLng(restaurant.latitude.toDouble(), restaurant.longitude.toDouble())
            googleMaps.addMarker(MarkerOptions().position(location).title(restaurant.name))
                .showInfoWindow()
            googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
            googleMaps.uiSettings.isScrollGesturesEnabled = false
        }
    }

    private fun setupViewModel(restaurantName: String) {
        viewModel.restaurant.observe(viewLifecycleOwner, Observer {
            restaurant = it

            Glide.with(this)
                .load(restaurant.imageURL)
                .transition(DrawableTransitionOptions.withCrossFade(350))
                .apply(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .into(restaurant_image)

            restaurant_name.text = restaurant.name.toUpperCase()
            setupToolbar()
            setupGoogleMaps()
        })

        viewModel.exception.observe(viewLifecycleOwner, Observer { exception ->
            if (exception != "") {
                Toast.makeText(context, exception, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.getRestaurantByName(restaurantName)
    }

    private fun setupToolbar() {
        collapsing_toolbar.setExpandedTitleColor(Color.TRANSPARENT)
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = restaurant.name
            setHasOptionsMenu(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
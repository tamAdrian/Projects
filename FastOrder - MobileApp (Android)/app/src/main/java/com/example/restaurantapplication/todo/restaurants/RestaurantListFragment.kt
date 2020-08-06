package com.example.restaurantapplication.todo.restaurants

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.restaurantapplication.R
import kotlinx.android.synthetic.main.restaurant_list_fragment.*
import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.example.restaurantapplication.MainActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.halcyonmobile.android.common.extensions.navigation.findSafeNavController

class RestaurantListFragment : Fragment() {

    private lateinit var viewModel: RestaurantListViewModel
    private lateinit var restaurantsAdapter: RestaurantListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.restaurant_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setToolbar(view)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setToolbar(view: View) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RestaurantListViewModel::class.java)
        setupViewModel()
        setupKeyboard()
    }

    private fun setupKeyboard() {
        relative_layout.setOnTouchListener { v, event ->
            relative_layout.hideKeyboard()
            false
        }

        restaurant_list.setOnTouchListener { v, event ->
            relative_layout.hideKeyboard()
            false
        }
    }

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun setupViewModel() {
        restaurantsAdapter =
            RestaurantListAdapter(
                requireContext(),
                object : RestaurantListAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val restaurantName = restaurantsAdapter.currentList[position].name

                        findNavController().navigate(
                            RestaurantListFragmentDirections.actionRestaurantListFragmentToRestaurantPageFragment(
                                restaurantName
                            )
                        )
                    }
                })

        restaurant_list.adapter = restaurantsAdapter

        viewModel.restaurants.observe(viewLifecycleOwner, Observer { restaurants ->
            restaurantsAdapter.submitList(restaurants)
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer { loading ->
            progress_bar.visibility = if (loading) View.VISIBLE else View.GONE
        })

        viewModel.exception.observe(viewLifecycleOwner, Observer { exception ->
            if (exception != "") {
                Toast.makeText(context, exception, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.noSearchResult.observe(viewLifecycleOwner, Observer { noResult ->
            if (noResult) {
                Toast.makeText(
                    context,
                    requireContext().getString(R.string.no_search_result),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.main_menu, menu)
        setupSearchView(menu)

        return super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupSearchView(menu: Menu) {
        val searchView: SearchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.queryHint = requireContext().getString(R.string.search_restaurants)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(context, query, Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterRestaurants(newText?.toLowerCase())
                return true
            }

        })
    }

    //delete last search
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchView: SearchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setQuery("", false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                AuthUI.getInstance().signOut(requireContext())
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                return true
            }
            R.id.placed_orders_history -> {
                findSafeNavController().navigate(
                    RestaurantListFragmentDirections.actionRestaurantListFragmentToOrderHistoryFragment()
                )
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

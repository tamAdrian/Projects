package com.example.restaurantapplication.todo.restaurantMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.restaurantapplication.R
import com.example.restaurantapplication.model.Order
import com.example.restaurantapplication.model.RestaurantMenu
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.menu_fragment.*
import kotlinx.android.synthetic.main.restaurant_menu_fragment.*
import kotlin.collections.ArrayList

class RestaurantMenuFragment : Fragment() {

    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    private lateinit var menuCollectionAdapter: MenuCollectionAdapter
    private lateinit var viewPager: ViewPager2

    private lateinit var viewModel: RestaurantMenuViewModel
    private lateinit var restaurantName: String

    private lateinit var mAuth: FirebaseAuth
    private lateinit var activeOrder: Order

    private val args by navArgs<RestaurantMenuFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.restaurant_menu_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fab_menu.isEnabled = false

        viewModel = ViewModelProviders.of(this).get(RestaurantMenuViewModel::class.java)

        setupToolbar(view)
        setupViewModel(view)

        relative_layout.setOnClickListener {
            this.findNavController().navigate(
                RestaurantMenuFragmentDirections.actionRestaurantMenuToOrderDetailsFragment(
                    restaurantName,
                    activeOrder
                )
            )
        }
    }

    private fun setupViewModel(view: View) {
        viewModel.restaurantMenu.observe(viewLifecycleOwner, Observer { menu ->
            setupViewPager(menu, view)

            if (menu.isEmpty()) {
                message.visibility = View.VISIBLE
            } else {
                message.visibility = View.GONE
            }
        })

        viewModel.exception.observe(viewLifecycleOwner, Observer { exception ->
            if (exception != "") {
                Toast.makeText(context, exception, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.activeOrder.observe(viewLifecycleOwner, Observer {
            activeOrder = it
            fab_menu.count = it.totalMenuItems
            total_price.text = it.totalPrice.toString()
        })

        viewModel.isOrderActive.observe(viewLifecycleOwner, Observer {
            relative_layout.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.getLastOrder(getLoggedUser(), restaurantName)
        viewModel.getRestaurantNameGroupedByCategory(restaurantName)
    }

    private fun setupViewPager(
        menu: Map<String, List<RestaurantMenu>>,
        view: View
    ) {
        menuCollectionAdapter = MenuCollectionAdapter(this, menu)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = menuCollectionAdapter

        val keys = menu.keys.toList()
        TabLayoutMediator(tab_layout, viewPager) { tab, position ->
            tab.text = keys[position]
        }.attach()
    }

    fun getLoggedUser(): String? {
        mAuth = FirebaseAuth.getInstance()
        return mAuth.currentUser?.uid
    }

    private fun setupToolbar(view: View) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        restaurantName = args.restaurantName
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.title = restaurantName
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
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

class MenuCollectionAdapter(
    fragment: Fragment,
    private val menu: Map<String, List<RestaurantMenu>>
) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = this.menu.keys.size

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val keys = menu.keys.toList()
        val list = menu[keys[position]] as? ArrayList<RestaurantMenu>
        val menuTab: ArrayList<RestaurantMenu> = list ?: error("")
        val args = Bundle()
        args.putParcelableArrayList(ARG_OBJECT, menuTab)
        val menuObjectFragment = MenuObjectFragment()
        menuObjectFragment.arguments = args
        return menuObjectFragment
    }
}

private const val ARG_OBJECT = "tabList"

// Instances of this class are fragments representing a single
// object in our collection.
class MenuObjectFragment : Fragment() {
    private lateinit var restaurantMenuAdapter: RestaurantMenuAdapter
    private lateinit var viewModel: RestaurantMenuViewModel
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.menu_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()

        restaurantMenuAdapter =
            RestaurantMenuAdapter(
                requireContext(),
                object : RestaurantMenuAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val selectedMenuItem = restaurantMenuAdapter.currentList[position]
                        if (viewModel != null) {
                            viewModel.handleOrder(selectedMenuItem, getLoggedUser())
                        }
                    }
                })
        recycler_view.adapter = restaurantMenuAdapter

        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            val menuItems: ArrayList<RestaurantMenu>? = getParcelableArrayList(ARG_OBJECT)
            persistanceForMenuItems(menuItems)
            restaurantMenuAdapter.submitList(menuItems)
        }
    }


    private fun persistanceForMenuItems(menuItems: ArrayList<RestaurantMenu>?) {
        if (viewModel != null) {
            val orders = viewModel.activeOrder.value?.orders
            if (orders != null) {
                menuItems?.map { item ->
                    if (orders.contains(item)) {
                        val position = orders.indexOf(item)
                        item.quantity = orders[position].quantity
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        parentFragment?.apply {
            viewModel = ViewModelProviders.of(this).get(RestaurantMenuViewModel::class.java)
        }
    }

    fun getLoggedUser(): String? {
        mAuth = FirebaseAuth.getInstance()
        return mAuth.currentUser?.uid
    }
}
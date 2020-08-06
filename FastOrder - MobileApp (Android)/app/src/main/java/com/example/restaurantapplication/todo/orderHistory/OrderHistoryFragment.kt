package com.example.restaurantapplication.todo.orderHistory

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.restaurantapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.order_history_fragment.order_history_list
import kotlinx.android.synthetic.main.order_history_fragment.progress_bar

class OrderHistoryFragment : Fragment() {

    private lateinit var viewModel: OrderHistoryViewModel
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.order_history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(view)
    }

    private fun setupToolbar(view: View) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.title = requireContext().getString(R.string.my_orders)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setHasOptionsMenu(true)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OrderHistoryViewModel::class.java)
        setupViewModel()
    }

    private fun setupViewModel() {
        orderHistoryAdapter = OrderHistoryAdapter()
        order_history_list.adapter = orderHistoryAdapter

        viewModel.getAuthUser()

        viewModel.placedOrdersHistory.observe(viewLifecycleOwner, Observer { placedOrdersHistory ->
            orderHistoryAdapter.submitList(placedOrdersHistory)
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer { loading ->
            progress_bar.visibility = if (loading) View.VISIBLE else View.GONE
        })

        viewModel.exception.observe(viewLifecycleOwner, Observer { exception ->
            if (exception != "") {
                Toast.makeText(context, exception, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.getPlacedOrderHistoryByUser()
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

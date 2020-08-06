package com.example.restaurantapplication.todo.orderDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.restaurantapplication.R
import com.example.restaurantapplication.model.PlacedOrder
import com.example.restaurantapplication.utils.NEPRELUATA
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.wdullaer.materialdatetimepicker.time.Timepoint
import kotlinx.android.synthetic.main.order_details_fragment.bottom_bar
import kotlinx.android.synthetic.main.order_details_fragment.date_image
import kotlinx.android.synthetic.main.order_details_fragment.date_text
import kotlinx.android.synthetic.main.order_details_fragment.hour_text
import kotlinx.android.synthetic.main.order_details_fragment.number_picker
import kotlinx.android.synthetic.main.order_details_fragment.order_list
import kotlinx.android.synthetic.main.order_details_fragment.order_price
import kotlinx.android.synthetic.main.order_details_fragment.time_image
import java.text.DateFormatSymbols
import java.util.Calendar

class OrderDetailsFragment : Fragment() {

    private lateinit var viewModel: OrderDetailsViewModel
    private lateinit var restaurantName: String
    private lateinit var orderDetailsAdapter: OrderDetailsAdapter
    private val args by navArgs<OrderDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OrderDetailsViewModel::class.java)

        orderDetailsAdapter = OrderDetailsAdapter(
            object : OrderDetailsAdapter.OnItemClickListener {
                override fun onAddClick(position: Int) {
                    viewModel.addQuantity(orderDetailsAdapter.currentList[position])
                }

                override fun onRemoveClick(position: Int) {
                    val menuItem = orderDetailsAdapter.currentList[position]
                    viewModel.removeQuantity(menuItem)
                }

            })

        viewModel.activeOrder.observe(viewLifecycleOwner, Observer {
            orderDetailsAdapter.submitList(it.orders)
            order_price.text = requireContext().getString(R.string.order_price, it.totalPrice.toString(), it.totalMenuItems.toString())
        })

        viewModel.emptyOrder.observe(viewLifecycleOwner, Observer { isEmpty ->
            if (isEmpty) {
                findNavController().navigateUp()
            }
        })

        viewModel.time.observe(viewLifecycleOwner, Observer { time ->
            if(time != "") {
                hour_text.text = time
            }
        })

        viewModel.date.observe(viewLifecycleOwner, Observer { date ->
            if(date != "") {
                date_text.text = date
            }
        })

        viewModel.mediatorLiveData.observe(viewLifecycleOwner, Observer {
            bottom_bar.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.initActiveOrder(args.activeOrder)

        order_list.adapter = orderDetailsAdapter

        bottom_bar.setOnClickListener {
            viewModel.activeOrder.value?.let {
                val placedOrder = PlacedOrder(
                    "",
                    it,
                    NEPRELUATA,
                    viewModel.date.value,
                    viewModel.time.value,
                    viewModel.numberOfPeople.value,
                    "fcm",
                    restaurantName
                )
                findNavController().navigate(OrderDetailsFragmentDirections.actionOrderDetailsFragmentToCardPaymentFragment(placedOrder))
            }

        }

        setupDatePicker()
        setupTimePicker()
        setupNumberPicker()
    }

    private fun setupNumberPicker() {
        number_picker.minValue = 1
        number_picker.maxValue = 6
        number_picker.setOnValueChangedListener { picker, oldVal, newVal ->
            viewModel.chooseNumberOfPeople(
                newVal.toString()
            )
        }
    }

    private fun setupTimePicker() {
        time_image.setOnClickListener {
            if (date_text.text == requireContext().getString(R.string.date)) {
                Toast.makeText(requireContext(), "Alegeti mai intai data.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                val tpd: TimePickerDialog = TimePickerDialog.newInstance(
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute, _ ->
                        var time = String()
                        time = if (minute == 0) {
                            "$hourOfDay:$minute" + 0
                        } else {
                            "$hourOfDay:$minute"
                        }

                        viewModel.chooseTime(time)
                    },
                    hour,
                    minute,
                    true
                )

                val selectedDate = date_text.text.split(" ")
                val selectedDay = selectedDate[0].toInt()

                if (selectedDay == calendar.get(Calendar.DAY_OF_MONTH) &&
                    hour >= 9
                ) {
                    //reservation for current day
                    val time = calendar.get(Calendar.MINUTE)
                    if (time > 30) {
                        //at least 30 min for accepting and preparing order
                        tpd.setMinTime(Timepoint(calendar.get(Calendar.HOUR_OF_DAY) + 1, 30))
                    } else {
                        //at least 30 min for accepting and preparing order
                        tpd.setMinTime(Timepoint(calendar.get(Calendar.HOUR_OF_DAY) + 1, 0))
                    }
                } else {
                    //reservation for another day
                    tpd.setMinTime(Timepoint(10, 0))
                }

                tpd.setMaxTime(Timepoint(22, 0))
                tpd.setTimeInterval(1, 30)

                tpd.setAccentColor("#00796b")
                tpd.setCancelColor("#ffffff")
                tpd.setOkColor("#ffffff")

                tpd.show(requireFragmentManager(), "TIME")

                tpd.setOnCancelListener {
                    Toast.makeText(
                        requireContext(),
                        "Nu uitati sa alegeti ora.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    private fun setupDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()
        val calendar = Calendar.getInstance()

        //block current day if time is after 10 pm and before 12 pm
        if (calendar.get(Calendar.HOUR_OF_DAY) in 21..23) {
            //block all dates before current day
            constraintsBuilder.setValidator(DateValidatorPointForward.from(calendar.timeInMillis))
            //roll calendar with one day
            calendar.add(Calendar.DATE, 1)
        } else {
            //block all dates before current day
            constraintsBuilder.setValidator(DateValidatorPointForward.now())
        }

        constraintsBuilder.setStart(calendar.timeInMillis)  //set start current day of the month
        calendar.add(Calendar.MONTH, 1)
        constraintsBuilder.setEnd(calendar.timeInMillis) //set end one month after
        calendar.add(Calendar.MONTH, -1)

        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setCalendarConstraints(constraintsBuilder.build())

        val materialDatePicker = builder.build()

        date_image.setOnClickListener {
            materialDatePicker.show(parentFragmentManager, "DATE")
        }

        materialDatePicker.addOnPositiveButtonClickListener { date ->
            val day =
                Calendar.getInstance().apply { timeInMillis = date }.get(Calendar.DAY_OF_MONTH)
            val month = Calendar.getInstance().apply { timeInMillis = date }.get(Calendar.MONTH)
            val year = Calendar.getInstance().apply { timeInMillis = date }.get(Calendar.YEAR)

            val monthName = DateFormatSymbols().shortMonths[month]
            val date = "$day $monthName, $year"
            viewModel.chooseDate(date)
        }

        materialDatePicker.addOnNegativeButtonClickListener {
            Toast.makeText(requireContext(), "Nu uitati sa alegeti data.", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun setupToolbar(view: View) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        restaurantName = args.restaurantName
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.title = restaurantName
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_24dp)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

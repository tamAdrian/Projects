package com.example.restaurantapplication.todo.payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.restaurantapplication.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.StripeIntent
import com.stripe.android.view.CardValidCallback
import kotlinx.android.synthetic.main.fragment_card_payment.card_input_widget
import kotlinx.android.synthetic.main.fragment_card_payment.pay_button
import kotlinx.android.synthetic.main.fragment_card_payment.progress_bar
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class CardPaymentFragment : Fragment() {

    // 10.0.2.2 is the Android emulator's alias to localhost
    private val backendUrl = "https://thesis-stripe-payment-server.herokuapp.com/"
    private val httpClient = OkHttpClient()
    private lateinit var publishableKey: String
    private lateinit var paymentIntentClientSecret: String
    private lateinit var stripe: Stripe

    private lateinit var viewModel: CardPaymentViewModel


    private val args by navArgs<CardPaymentFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        startCheckout()

        card_input_widget.postalCodeEnabled = false
        card_input_widget.setCardValidCallback(object : CardValidCallback {
            override fun onInputChanged(
                isValid: Boolean,
                invalidFields: Set<CardValidCallback.Fields>
            ) {
                pay_button.isEnabled = isValid
            }

        })

        viewModel = ViewModelProviders.of(this).get(CardPaymentViewModel::class.java)
        viewModel.exception.observe(viewLifecycleOwner, Observer { exception ->
            if (exception != "") {
                Toast.makeText(context, exception, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupToolbar(view: View) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.title = requireContext().getString(R.string.payment)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setHasOptionsMenu(true)
        }
    }

    private fun displayAlert(
        title: String,
        message: String
    ) {
        activity?.runOnUiThread {
            val builder = AlertDialog.Builder(requireActivity())
                .setTitle(title)
                .setMessage(message)

            builder.setPositiveButton("Ok", null)

            builder
                .create()
                .show()
        }
    }

    private fun startCheckout() {
        // Create a PaymentIntent by calling the sample server's /create-payment-intent endpoint.
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val totalPrice = args.placedOrder.order.totalPrice
        val requestJson = """
            {
                "currency": "ron",
                "items": "$totalPrice"
            }
            """
        val body = requestJson.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(backendUrl + "create-payment-intent")
            .post(body)
            .build()

        httpClient.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (isAdded) {
                        displayAlert(requireContext().getString(R.string.server_error), "Error: $e")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (isAdded) {
                        if (!response.isSuccessful) {
                            displayAlert(
                                requireContext().getString(R.string.unsuccessful_payment),
                                "Error: $response"
                            )
                        } else {
                            val responseData = response.body?.string()
                            val responseJson =
                                responseData?.let { JSONObject(it) } ?: JSONObject()

                            // the response from the server includes the Stripe publishable key and
                            // paymentIntent details.
                            publishableKey = responseJson.getString("publishableKey")
                            paymentIntentClientSecret = responseJson.getString("clientSecret")

                            // configure the SDK with your Stripe publishable key so that it can make
                            // requests to the Stripe API
                            stripe = Stripe(requireActivity().applicationContext, publishableKey)
                        }
                    }
                }
            })

        // hook up the pay button to the card widget and stripe instance
        pay_button.setOnClickListener {
            progress_bar.visibility = View.VISIBLE

            val cardInputWidget = card_input_widget
            cardInputWidget.paymentMethodCreateParams?.let { params ->
                val confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret)
                stripe.confirmPayment(this, confirmParams)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, object : ApiResultCallback<PaymentIntentResult> {
            override fun onSuccess(result: PaymentIntentResult) {
                val paymentIntent = result.intent
                val status = paymentIntent.status
                if (status == StripeIntent.Status.Succeeded) {
                    //insert placedOrder with device fcmToken
                    FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.d("CardPaymentFragment", "getInstanceId failed", task.exception)
                                return@OnCompleteListener
                            }

                            // get new Instance ID token
                            val token = task.result?.token

                            //place order to become visible for restaurant
                            args.placedOrder.fcmToken = token
                            viewModel.insertPlacedOrder(args.placedOrder)

                            //set order status to inactive
                            viewModel.updateOrderStatus(args.placedOrder.order)

                            findNavController().navigate(CardPaymentFragmentDirections.actionCardPaymentFragmentToRestaurantListFragment())
                        })

                    val message =
                        requireContext().getString(
                            R.string.reminder,
                            args.placedOrder.date,
                            args.placedOrder.hour
                        )
                    displayAlert(
                        requireContext().getString(R.string.successful_payment), message
                    )
                    progress_bar.visibility = View.GONE

                } else if (status == StripeIntent.Status.RequiresPaymentMethod) {
                    displayAlert(
                        requireContext().getString(R.string.unsuccessful_payment),
                        paymentIntent.lastPaymentError?.message.orEmpty()
                    )
                    progress_bar.visibility = View.GONE
                }
            }

            override fun onError(e: Exception) {
                displayAlert(
                    requireContext().getString(R.string.unsuccessful_payment),
                    e.toString()
                )
                progress_bar.visibility = View.GONE
            }
        })
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
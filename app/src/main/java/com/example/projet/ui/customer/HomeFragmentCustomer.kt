package com.example.projet.ui.customer

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projet.R
import com.example.projet.data.model.BookServiceRequest
import com.example.projet.data.model.Service
import com.example.projet.data.repository.AuthRepository
import com.example.projet.data.repository.CustomerRepository
import com.example.projet.databinding.FragmentCustomerHomeBinding
import com.example.projet.viewmodel.AuthViewModel
import com.example.projet.viewmodel.CustomerViewModel

class HomeFragmentCustomer : Fragment() {

    private var _binding: FragmentCustomerHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CustomerViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CustomerViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return CustomerViewModel(CustomerRepository()) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    private val authViewModel: AuthViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(AuthRepository()) as T
            }
        }
    }

    private lateinit var adapter: CustomerServicesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        // Load services when view is created
        viewModel.loadAllServices()

        // Set up click listener for My Bookings button
        binding.btnMyBookings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_customerReservationsFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = CustomerServicesAdapter { service ->
            showBookingDialog(service)
        }
        
        binding.recyclerViewServices.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeFragmentCustomer.adapter
        }
    }

    private fun showBookingDialog(service: Service) {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val dateInput = EditText(context).apply { hint = "Date (YYYY-MM-DD)" }
        val timeInput = EditText(context).apply { hint = "Time (HH:MM)" }
        val addressInput = EditText(context).apply { hint = "Address" }

        layout.addView(dateInput)
        layout.addView(timeInput)
        layout.addView(addressInput)

        AlertDialog.Builder(context)
            .setTitle("Book Service: ${service.title}")
            .setView(layout)
            .setPositiveButton("Book") { _, _ ->
                val date = dateInput.text.toString()
                val time = timeInput.text.toString()
                val address = addressInput.text.toString()

                if (date.isNotBlank() && time.isNotBlank() && address.isNotBlank()) {
                    performBooking(service, date, time, address)
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performBooking(service: Service, date: String, time: String, address: String) {
        val userId = authViewModel.userId.value
        if (userId.isNullOrEmpty()) {
            Toast.makeText(context, "You must be logged in to book a service", Toast.LENGTH_SHORT).show()
            return
        }

        val request = BookServiceRequest(
            serviceId = service.id,
            customerId = userId,
            date = date,
            time = time,
            address = address
        )

        viewModel.bookService(request)
    }

    private fun observeViewModel() {
        viewModel.services.observe(viewLifecycleOwner) { services ->
            adapter.submitList(services)
            
            if (services.isEmpty()) {
                binding.textViewEmpty.visibility = View.VISIBLE
                binding.recyclerViewServices.visibility = View.GONE
            } else {
                binding.textViewEmpty.visibility = View.GONE
                binding.recyclerViewServices.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }.onFailure { error ->
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

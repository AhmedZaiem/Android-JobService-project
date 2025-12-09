package com.example.projet.ui.customer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projet.data.repository.AuthRepository
import com.example.projet.data.repository.CustomerRepository
import com.example.projet.databinding.FragmentCustomerReservationsBinding
import com.example.projet.viewmodel.AuthViewModel
import com.example.projet.viewmodel.CustomerViewModel

class CustomerBookingsFragment : Fragment() {

    private var _binding: FragmentCustomerReservationsBinding? = null
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

    private lateinit var bookingsAdapter: CustomerBookingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerReservationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        bookingsAdapter = CustomerBookingsAdapter()
        binding.recyclerViewBookings.apply {
            adapter = bookingsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        // Observe user ID to fetch bookings
        authViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (!userId.isNullOrEmpty()) {
                viewModel.loadCustomerBookings(userId)
            } else {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe bookings list
        viewModel.bookings.observe(viewLifecycleOwner) { bookings ->
            bookingsAdapter.submitList(bookings)

            if (bookings.isEmpty()) {
                binding.textViewEmpty.visibility = View.VISIBLE
                binding.recyclerViewBookings.visibility = View.GONE
            } else {
                binding.textViewEmpty.visibility = View.GONE
                binding.recyclerViewBookings.visibility = View.VISIBLE
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.textViewEmpty.visibility = View.GONE
            }
        }

        // Observe errors
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onFailure { error ->
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("CustomerBookings", "Error loading bookings", error)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

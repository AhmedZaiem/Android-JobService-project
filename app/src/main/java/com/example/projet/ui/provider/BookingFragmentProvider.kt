package com.example.projet.ui.provider

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projet.data.repository.AuthRepository
import com.example.projet.data.repository.ProviderRepository
import com.example.projet.databinding.FragmentProviderBookingsBinding
import com.example.projet.viewmodel.AuthViewModel
import com.example.projet.viewmodel.ProviderViewModel
import com.example.projet.viewmodel.ProviderViewModelFactory

class BookingFragmentProvider : Fragment() {

    private var _binding: FragmentProviderBookingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProviderViewModel by viewModels {
        ProviderViewModelFactory(ProviderRepository())
    }

    private val authViewModel: AuthViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(AuthRepository()) as T
            }
        }
    }

    private lateinit var bookingsAdapter: BookingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProviderBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BookingFragment", "onViewCreated")

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        bookingsAdapter = BookingsAdapter(
            onAccept = { booking ->
                val providerId = authViewModel.userId.value
                if (!providerId.isNullOrEmpty()) {
                    viewModel.acceptBooking(booking.id, providerId)
                } else {
                    Toast.makeText(context, "Provider ID not found", Toast.LENGTH_SHORT).show()
                }
            },
            onReject = { booking ->
                val providerId = authViewModel.userId.value
                if (!providerId.isNullOrEmpty()) {
                    viewModel.rejectBooking(booking.id, providerId)
                } else {
                    Toast.makeText(context, "Provider ID not found", Toast.LENGTH_SHORT).show()
                }
            }
        )

        binding.recyclerView.apply {
            adapter = bookingsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        // Observe provider ID
        authViewModel.userId.observe(viewLifecycleOwner) { providerId ->
            Log.d("BookingFragment", "Auth ID observed: $providerId")
            if (!providerId.isNullOrEmpty()) {
                viewModel.loadBookings(providerId)
            } else {
                Log.w("BookingFragment", "User ID is null or empty")
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe bookings list
        viewModel.bookings.observe(viewLifecycleOwner) { bookings ->
            Log.d("BookingFragment", "Bookings list updated: size=${bookings.size}")
            bookingsAdapter.submitList(bookings)

            binding.recyclerView.visibility = if (bookings.isEmpty()) View.GONE else View.VISIBLE
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe operation status (accept/reject)
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    Log.e("BookingFragment", "Operation error", error)
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

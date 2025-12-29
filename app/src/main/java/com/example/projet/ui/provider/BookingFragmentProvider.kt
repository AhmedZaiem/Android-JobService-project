package com.example.projet.ui.provider

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projet.data.model.Booking
import com.example.projet.data.repository.AuthRepository
import com.example.projet.data.repository.ProviderRepository
import com.example.projet.databinding.FragmentProviderBookingsBinding
import com.example.projet.viewmodel.AuthViewModel
import com.example.projet.viewmodel.ProviderViewModel
import com.example.projet.viewmodel.ProviderViewModelFactory
import java.util.Calendar

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
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        bookingsAdapter = BookingsAdapter(
            onAccept = { booking ->
                val providerId = authViewModel.userId.value
                if (!providerId.isNullOrEmpty()) viewModel.acceptBooking(booking.id, providerId)
                else Toast.makeText(context, "Provider ID not found", Toast.LENGTH_SHORT).show()
            },
            onReject = { booking ->
                val providerId = authViewModel.userId.value
                if (!providerId.isNullOrEmpty()) viewModel.rejectBooking(booking.id, providerId)
                else Toast.makeText(context, "Provider ID not found", Toast.LENGTH_SHORT).show()
            },
            onUpdateDate = { booking -> showDatePickerDialog(booking) }
        )

        binding.recyclerView.apply {
            adapter = bookingsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun showDatePickerDialog(booking: Booking) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val newDate = "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)
            val providerId = authViewModel.userId.value
            if (!providerId.isNullOrEmpty()) {
                viewModel.updateBookingDate(booking.id, newDate, providerId)
                bookingsAdapter.updateBookingDateInList(booking.id, newDate)
            } else {
                Toast.makeText(requireContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show()
            }
        }, year, month, day).show()
    }

    private fun observeViewModel() {
        authViewModel.userId.observe(viewLifecycleOwner) { providerId ->
            if (!providerId.isNullOrEmpty()) viewModel.loadBookings(providerId)
            else Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }

        viewModel.bookings.observe(viewLifecycleOwner) { bookings ->
            bookingsAdapter.submitList(bookings)
            binding.recyclerView.visibility = if (bookings.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.updateBookingDateSuccess.observe(viewLifecycleOwner) { response ->
            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.updateBookingDateError.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, "Error updating date: $error", Toast.LENGTH_LONG).show()
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() },
                onFailure = { error -> Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show() }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.projet.ui.customer

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projet.data.model.Booking
import com.example.projet.data.model.ReviewRequest
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
        bookingsAdapter = CustomerBookingsAdapter(
            onCancelClick = { booking ->
                val userId = authViewModel.userId.value
                if (userId != null) {
                    viewModel.cancelBooking(booking.id, userId)
                } else {
                    Toast.makeText(requireContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show()
                }
            },
            onCompleteClick = { booking ->
                val userId = authViewModel.userId.value
                if (userId != null) {
                    viewModel.completeReservation(booking.id, userId)
                } else {
                    Toast.makeText(requireContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show()
                }
            },
            onReviewClick = { booking ->
                showReviewDialog(booking)
            }
        )
        binding.recyclerViewBookings.apply {
            adapter = bookingsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun showReviewDialog(booking: Booking) {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val ratingBar = RatingBar(context).apply {
            numStars = 5
            stepSize = 1.0f
        }
        val commentInput = EditText(context).apply { hint = "Comment" }

        layout.addView(ratingBar)
        layout.addView(commentInput)

        AlertDialog.Builder(context)
            .setTitle("Submit Review")
            .setView(layout)
            .setPositiveButton("Submit") { _, _ ->
                val rating = ratingBar.rating.toInt()
                val comment = commentInput.text.toString()
                val customerId = authViewModel.userId.value
                val reservationId = booking.id // use top-level booking ID

                if (customerId != null && reservationId != null) {
                    val review = ReviewRequest(
                        reservationId = reservationId,
                        customerId = customerId,
                        rating = rating,
                        comment = comment
                    )
                    viewModel.submitReview(review)
                } else {
                    Toast.makeText(context, "Could not submit review. Missing data.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()

    }

    private fun observeViewModel() {
        authViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (!userId.isNullOrEmpty()) {
                viewModel.loadCustomerBookings(userId)
            } else {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

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

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.textViewEmpty.visibility = View.GONE
            }
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                // Optionally refresh the bookings list
                 authViewModel.userId.value?.let { viewModel.loadCustomerBookings(it) }
            }.onFailure { error ->
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("CustomerBookings", "Error with operation", error)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

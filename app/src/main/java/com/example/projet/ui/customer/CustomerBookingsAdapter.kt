package com.example.projet.ui.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projet.data.model.Booking
import com.example.projet.databinding.ItemCustomerBookingBinding

class CustomerBookingsAdapter(
    private val onCancelClick: (Booking) -> Unit,
    private val onCompleteClick: (Booking) -> Unit,
    private val onReviewClick: (Booking) -> Unit
) : ListAdapter<Booking, CustomerBookingsAdapter.BookingViewHolder>(BookingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemCustomerBookingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookingViewHolder(
        private val binding: ItemCustomerBookingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: Booking) {
            binding.textViewServiceTitle.text = booking.serviceId?.title ?: "Unknown Service"
            binding.textViewDate.text = booking.date ?: "No Date"
            binding.textViewStatus.text = booking.status ?: "Unknown Status"

            // Reset visibility
            binding.btnCompleteBooking.visibility = View.GONE
            binding.btnCancelBooking.visibility = View.GONE
            binding.btnReviewBooking.visibility = View.GONE

            when (booking.status) {
                "accepted" -> {
                    binding.btnCompleteBooking.visibility = View.VISIBLE
                    binding.btnCancelBooking.visibility = View.VISIBLE
                    binding.btnCompleteBooking.setOnClickListener { onCompleteClick(booking) }
                    binding.btnCancelBooking.setOnClickListener { onCancelClick(booking) }
                }
                "completed" -> {
                    binding.btnReviewBooking.visibility = View.VISIBLE
                    binding.btnReviewBooking.setOnClickListener { onReviewClick(booking) }
                }
                else -> { // "pending", "cancelled", etc.
                    binding.btnCancelBooking.visibility = View.VISIBLE
                    binding.btnCancelBooking.setOnClickListener { onCancelClick(booking) }
                }
            }
        }
    }

    class BookingDiffCallback : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean {
            return oldItem == newItem
        }
    }
}

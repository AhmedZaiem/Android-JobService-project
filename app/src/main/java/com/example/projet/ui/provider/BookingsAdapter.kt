package com.example.projet.ui.provider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projet.data.model.Booking
import com.example.projet.databinding.ItemBookingBinding

class BookingsAdapter(
    private val onAccept: (Booking) -> Unit,
    private val onReject: (Booking) -> Unit
) : ListAdapter<Booking, BookingsAdapter.BookingViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(
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
        private val binding: ItemBookingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: Booking) {
            binding.textViewCustomerName.text = booking.customerId?.name ?: "Unknown Customer"
            binding.textViewServiceTitle.text = booking.serviceId?.title ?: "Unknown Service"
            binding.textViewDate.text = booking.date ?: "No Date"
            binding.textViewStatus.text = booking.status ?: "Unknown"

            val status = booking.status
            if (status != null && status.equals("pending", ignoreCase = true)) {
                binding.acceptButton.visibility = View.VISIBLE
                binding.rejectButton.visibility = View.VISIBLE

                binding.acceptButton.setOnClickListener { onAccept(booking) }
                binding.rejectButton.setOnClickListener { onReject(booking) }
            } else {
                binding.acceptButton.visibility = View.GONE
                binding.rejectButton.visibility = View.GONE
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(oldItem: Booking, newItem: Booking) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Booking, newItem: Booking) =
            oldItem == newItem
    }
}

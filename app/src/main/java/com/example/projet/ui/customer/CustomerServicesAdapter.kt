package com.example.projet.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.projet.data.model.Service
import com.example.projet.databinding.ItemCustomerServiceBinding
import java.text.NumberFormat
import java.util.Locale

class CustomerServicesAdapter(
    private val onBookClick: (Service) -> Unit
) : ListAdapter<Service, CustomerServicesAdapter.ServiceViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemCustomerServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ServiceViewHolder(
        private val binding: ItemCustomerServiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(service: Service) {
            binding.textViewTitle.text = service.title
            binding.textViewDescription.text = service.description
            binding.textViewProviderName.text = "Provider: ${service.providerId.name ?: "Unknown"}"

            val format = NumberFormat.getCurrencyInstance(Locale.US)
            binding.textViewPrice.text = format.format(service.price)

            val imageUrl = service.photoURL
            if (imageUrl.isNullOrEmpty()) {
                binding.imageViewService.load(null as String?) {
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_menu_report_image)
                }
            } else {
                val fullUrl = if (imageUrl.startsWith("http")) {
                    imageUrl
                } else {
                    val serverBase = "http://192.168.1.17:5000"
                    if (imageUrl.startsWith("/")) {
                        serverBase + imageUrl
                    } else {
                        "$serverBase/$imageUrl"
                    }
                }
                binding.imageViewService.load(fullUrl) {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_menu_report_image)
                }
            }

            binding.btnBookService.setOnClickListener {
                onBookClick(service)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Service>() {
        override fun areItemsTheSame(oldItem: Service, newItem: Service) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Service, newItem: Service) =
            oldItem == newItem
    }
}

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
    private val onItemClick: ((Service) -> Unit)? = null
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

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(position))
                }
            }
        }

        fun bind(service: Service) {
            binding.textViewTitle.text = service.title
            binding.textViewDescription.text = service.description
            binding.textViewProviderName.text = "Provider: ${service.providerId.name ?: "Unknown"}"
            
            val format = NumberFormat.getCurrencyInstance(Locale.US)
            binding.textViewPrice.text = format.format(service.price)

            // Load image using Coil
            binding.imageViewService.load(service.photoURL) {
                crossfade(true)
                error(android.R.drawable.ic_menu_report_image) // Use a default error icon or placeholder
                placeholder(android.R.drawable.ic_menu_gallery)
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

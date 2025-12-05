package com.example.projet.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.projet.R
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

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onBookClick(getItem(position))
                }
            }
        }

        fun bind(service: Service) {
            binding.textViewTitle.text = service.title
            binding.textViewDescription.text = service.description
            binding.textViewProviderName.text = "Provider: ${service.providerId.name ?: "Unknown"}"
            
            val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
            binding.textViewPrice.text = format.format(service.price)

            binding.imageViewService.load(service.photoURL) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background) // Replace with your placeholder
                error(R.drawable.ic_launcher_background) // Replace with your error drawable
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Service>() {
        override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem == newItem
        }
    }
}

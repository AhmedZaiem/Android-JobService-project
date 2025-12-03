package com.example.projet.ui.provider

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projet.data.model.Category
import com.example.projet.data.model.Service
import com.example.projet.data.repository.ProviderRepository
import com.example.projet.databinding.FragmentManageServicesBinding
import com.example.projet.databinding.ItemProviderServiceBinding
import com.example.projet.viewmodel.ProviderViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody



class ManageServicesFragment : Fragment() {

    private var _binding: FragmentManageServicesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProviderViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ProviderViewModel(ProviderRepository()) as T
            }
        }
    }
    
    // Placeholder provider ID - in a real app, get this from SharedPrefs or Session
    private val providerId = "USER_ID_FROM_SESSION" 

    private lateinit var servicesAdapter: ServicesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        viewModel.loadProviderServices(providerId)
        viewModel.getCategories()
    }

    private fun setupRecyclerView() {
        servicesAdapter = ServicesAdapter { serviceId ->
            viewModel.deleteService(serviceId, providerId)
        }
        binding.rvServices.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = servicesAdapter
        }
    }

    private fun setupObservers() {
        viewModel.services.observe(viewLifecycleOwner) { services ->
            servicesAdapter.submitList(services)
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                // Clear inputs on success
                binding.etTitle.text.clear()
                binding.etDescription.text.clear()
                binding.etPrice.text.clear()
                if (binding.categorySpinner.adapter != null && binding.categorySpinner.adapter.count > 0) {
                    binding.categorySpinner.setSelection(0)
                }
            }.onFailure { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnAddService.isEnabled = !isLoading
        }
    }

    private fun setupListeners() {
        binding.btnAddService.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val desc = binding.etDescription.text.toString()
            val price = binding.etPrice.text.toString()
            
            val selectedCategory = binding.categorySpinner.selectedItem as? Category
            val categoryId = selectedCategory?.id

            if (title.isNotEmpty() && desc.isNotEmpty() && price.isNotEmpty() && categoryId != null) {
                
                val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val descBody = desc.toRequestBody("text/plain".toMediaTypeOrNull())
                val priceBody = price.toRequestBody("text/plain".toMediaTypeOrNull())
                val catBody = categoryId.toRequestBody("text/plain".toMediaTypeOrNull())
                val providerBody = providerId.toRequestBody("text/plain".toMediaTypeOrNull())

                // Photo is null for now as we haven't implemented picker
                viewModel.createService(titleBody, descBody, priceBody, providerBody, catBody, null)
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ServicesAdapter(private val onDeleteClick: (String) -> Unit) : RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder>() {

    private var services = listOf<Service>()

    fun submitList(newServices: List<Service>) {
        services = newServices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemProviderServiceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(services[position])
    }

    override fun getItemCount() = services.size

    inner class ServiceViewHolder(private val binding: ItemProviderServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(service: Service) {
            binding.tvServiceTitle.text = service.title
            binding.tvServicePrice.text = "Price: $${service.price}"
            
            binding.btnDeleteService.setOnClickListener {
                onDeleteClick(service.id)
            }
        }
    }
}

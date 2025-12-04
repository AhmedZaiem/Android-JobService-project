package com.example.projet.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projet.data.repository.CustomerRepository
import com.example.projet.databinding.FragmentCustomerHomeBinding
import com.example.projet.viewmodel.CustomerViewModel

class HomeFragmentCustomer : Fragment() {

    private var _binding: FragmentCustomerHomeBinding? = null
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

    private lateinit var adapter: CustomerServicesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        // Load services when view is created
        viewModel.loadAllServices()
    }

    private fun setupRecyclerView() {
        adapter = CustomerServicesAdapter { service ->
            // Handle item click, e.g., navigate to details
            Toast.makeText(context, "Clicked: ${service.title}", Toast.LENGTH_SHORT).show()
        }
        
        binding.recyclerViewServices.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeFragmentCustomer.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.services.observe(viewLifecycleOwner) { services ->
            adapter.submitList(services)
            
            if (services.isEmpty()) {
                binding.textViewEmpty.visibility = View.VISIBLE
                binding.recyclerViewServices.visibility = View.GONE
            } else {
                binding.textViewEmpty.visibility = View.GONE
                binding.recyclerViewServices.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onFailure { error ->
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

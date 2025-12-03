package com.example.projet.ui.provider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projet.R
import com.example.projet.data.repository.ProviderRepository
import com.example.projet.databinding.FragmentHomeProviderBinding
import com.example.projet.viewmodel.ProviderViewModel

class HomeFragmentProvider : Fragment() {

    private var _binding: FragmentHomeProviderBinding? = null
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
        _binding = FragmentHomeProviderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        // Load services when view becomes active
        viewModel.loadProviderServices(providerId)
    }

    private fun setupRecyclerView() {
        servicesAdapter = ServicesAdapter { serviceId ->
            viewModel.deleteService(serviceId, providerId)
        }
        binding.rvProviderServices.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = servicesAdapter
        }
    }

    private fun setupObservers() {
        viewModel.services.observe(viewLifecycleOwner) { services ->
            servicesAdapter.submitList(services)
        }
        
        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess { message ->
                 Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnManageServices.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragmentProvider_to_manageServicesFragment)
        }

        binding.btnViewBookings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragmentProvider_to_providerReservationsFragment)
        }

        binding.btnViewReviews.setOnClickListener {
             Toast.makeText(context, "Reviews Feature Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.projet.ui.provider

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projet.R
import com.example.projet.data.repository.AuthRepository
import com.example.projet.data.repository.ProviderRepository
import com.example.projet.databinding.FragmentHomeProviderBinding
import com.example.projet.viewmodel.AuthViewModel
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

    private val authViewModel: AuthViewModel by activityViewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(AuthRepository()) as T
            }
        }
    }

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

    private fun setupRecyclerView() {
        servicesAdapter = ServicesAdapter { serviceId ->
            val providerId = authViewModel.userId.value
            if (providerId != null) {
                viewModel.deleteService(serviceId, providerId)
            } else {
                Toast.makeText(context, "User ID not found", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvProviderServices.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = servicesAdapter
        }
    }

    private fun setupObservers() {
        authViewModel.userId.observe(viewLifecycleOwner) { providerId ->
            if (!providerId.isNullOrEmpty()) {
                Log.d("HomeFragmentProvider", "UserId observed: $providerId")
                viewModel.loadProviderServices(providerId)
            } else {
                Log.w("HomeFragmentProvider", "Provider ID is null, clearing services.")
                servicesAdapter.submitList(emptyList())
            }
        }

        viewModel.services.observe(viewLifecycleOwner) { services ->
            Log.d("HomeFragmentProvider", "Services updated: ${services.size} items")
            servicesAdapter.submitList(services)
            binding.tvEmptyServices.visibility = if (services.isEmpty()) View.VISIBLE else View.GONE
            binding.rvProviderServices.visibility = if (services.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Log.e("HomeFragmentProvider", "Operation failed", e)
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

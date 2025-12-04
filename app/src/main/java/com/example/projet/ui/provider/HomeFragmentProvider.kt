package com.example.projet.ui.provider

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projet.R
import com.example.projet.data.model.Service
import com.example.projet.data.repository.AuthRepository
import com.example.projet.data.repository.ProviderRepository
import com.example.projet.databinding.FragmentHomeProviderBinding
import com.example.projet.viewmodel.AuthViewModel
import com.example.projet.viewmodel.ProviderViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

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
        servicesAdapter = ServicesAdapter(
            onDeleteClick = { serviceId ->
                showDeleteConfirmationDialog(serviceId)
            },
            onEditClick = { service ->
                showEditDialog(service)
            }
        )
        binding.rvProviderServices.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = servicesAdapter
        }
    }

    private fun showDeleteConfirmationDialog(serviceId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Service")
            .setMessage("Are you sure you want to delete this service?")
            .setPositiveButton("Yes") { _, _ ->
                val providerId = authViewModel.userId.value
                if (providerId != null) {
                    viewModel.deleteService(serviceId, providerId)
                } else {
                    Toast.makeText(context, "User ID not found", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showEditDialog(service: Service) {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val titleInput = EditText(context).apply {
            hint = "Title"
            setText(service.title)
        }
        val descInput = EditText(context).apply {
            hint = "Description"
            setText(service.description)
        }
        val priceInput = EditText(context).apply {
            hint = "Price"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(service.price.toString())
        }

        layout.addView(titleInput)
        layout.addView(descInput)
        layout.addView(priceInput)

        AlertDialog.Builder(context)
            .setTitle("Update Service")
            .setView(layout)
            .setPositiveButton("Update") { _, _ ->
                val newTitle = titleInput.text.toString()
                val newDesc = descInput.text.toString()
                val newPrice = priceInput.text.toString()
                val providerId = authViewModel.userId.value

                if (newTitle.isNotEmpty() && newDesc.isNotEmpty() && newPrice.isNotEmpty() && providerId != null) {
                    val titleBody = newTitle.toRequestBody("text/plain".toMediaTypeOrNull())
                    val descBody = newDesc.toRequestBody("text/plain".toMediaTypeOrNull())
                    val priceBody = newPrice.toRequestBody("text/plain".toMediaTypeOrNull())
                    // We reuse the existing category ID for now as the dialog doesn't have a spinner
                    val catBody = service.categoryId.toRequestBody("text/plain".toMediaTypeOrNull())

                    viewModel.updateService(
                        serviceId = service.id,
                        title = titleBody,
                        desc = descBody,
                        price = priceBody,
                        categoryId = catBody,
                        photo = null, // Photo update not supported in this dialog
                        providerId = providerId
                    )
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
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
            findNavController().navigate(R.id.action_homeFragmentProvider_to_BookingFragmentProvider)
        }

        binding.btnViewReviews.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragmentProvider_to_reviewFragmentProvider)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

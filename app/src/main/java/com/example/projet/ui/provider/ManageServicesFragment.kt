package com.example.projet.ui.provider

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projet.data.model.Category
import com.example.projet.data.model.Service
import com.example.projet.data.repository.AuthRepository
import com.example.projet.data.repository.ProviderRepository
import com.example.projet.databinding.FragmentManageServicesBinding
import com.example.projet.viewmodel.AuthViewModel
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
        _binding = FragmentManageServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        authViewModel.userId.observe(viewLifecycleOwner) { providerId ->
            if (providerId != null) {
                viewModel.loadProviderServices(providerId)
            }
        }
        viewModel.getCategories()
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
        binding.rvServices.apply {
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
                    // We reuse the existing category ID for now
                    val catBody = service.categoryId.toRequestBody("text/plain".toMediaTypeOrNull())

                    viewModel.updateService(
                        serviceId = service.id,
                        title = titleBody,
                        desc = descBody,
                        price = priceBody,
                        categoryId = catBody,
                        photo = null,
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
        viewModel.services.observe(viewLifecycleOwner) { services ->
            servicesAdapter.submitList(services)
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
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
            val selectedCategoryPosition = binding.categorySpinner.selectedItemPosition
            val categoryId = viewModel.categories.value?.getOrNull(selectedCategoryPosition)?.id
            val providerId = authViewModel.userId.value

            if (title.isNotEmpty() && desc.isNotEmpty() && price.isNotEmpty() && categoryId != null && providerId != null) {
                val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val descBody = desc.toRequestBody("text/plain".toMediaTypeOrNull())
                val priceBody = price.toRequestBody("text/plain".toMediaTypeOrNull())
                val catBody = categoryId.toRequestBody("text/plain".toMediaTypeOrNull())
                val providerBody = providerId.toRequestBody("text/plain".toMediaTypeOrNull())

                viewModel.createService(titleBody, descBody, priceBody, providerBody, catBody, null)
            } else {
                Log.d("ManageServicesFragment", "Validation failed: title=$title, desc=$desc, price=$price, categoryId=$categoryId, providerId=$providerId")
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

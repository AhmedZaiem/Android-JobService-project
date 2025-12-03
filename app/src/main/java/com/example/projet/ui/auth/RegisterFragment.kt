package com.example.projet.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projet.R
import com.example.projet.data.model.Category
import com.example.projet.data.repository.AuthRepository
import com.example.projet.databinding.FragmentRegisterBinding
import com.example.projet.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    
    private val authViewModel: AuthViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(AuthRepository()) as T
            }
        }
    }

    private var selectedCategory: Category? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
        
        // Fetch categories when view is created
        authViewModel.getCategories()
    }

    private fun setupObservers() {
        authViewModel.categories.observe(viewLifecycleOwner) { categories ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
        }

        authViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }.onFailure {
                Toast.makeText(requireContext(), "Registration Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        // Handle visibility of provider specific fields
        binding.roleRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.providerRadioButton) {
                binding.providerFieldsLayout.visibility = View.VISIBLE
            } else {
                binding.providerFieldsLayout.visibility = View.GONE
            }
        }

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val city = binding.cityEditText.text.toString().trim()
            val tel = binding.telEditText.text.toString().trim()
            
            val role = if (binding.providerRadioButton.isChecked) "provider" else "customer"
            
            var categoryId: String? = null
            var skills: String? = null
            var bio: String? = null

            if (role == "provider") {
                val selectedCategory = binding.categorySpinner.selectedItem as? Category
                categoryId = selectedCategory?.id
                skills = binding.skillsEditText.text.toString().trim()
                bio = binding.bioEditText.text.toString().trim()
            }

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.register(name, email, password, role, city, tel, categoryId, skills, bio)
            } else {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginTextView.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

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
        authViewModel.getCategories()
    }

    private fun setupObservers() {
        authViewModel.categories.observe(viewLifecycleOwner) { categories ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories.map { it.name } // 👈 safer
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
        }

        authViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }.onFailure {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {

        binding.roleRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.providerFieldsLayout.visibility =
                if (checkedId == R.id.providerRadioButton) View.VISIBLE else View.GONE
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
                val position = binding.categorySpinner.selectedItemPosition
                val category = authViewModel.categories.value?.get(position)
                categoryId = category?.id
                skills = binding.skillsEditText.text.toString().trim()
                bio = binding.bioEditText.text.toString().trim()
            }

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.register(name, email, password, role, city, tel, categoryId, skills, bio)
            } else {
                Toast.makeText(requireContext(), "Fill required fields", Toast.LENGTH_SHORT).show()
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

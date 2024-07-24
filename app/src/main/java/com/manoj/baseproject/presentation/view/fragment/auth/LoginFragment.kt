package com.manoj.baseproject.presentation.view.fragment.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.viewModels
import com.manoj.baseproject.R
import androidx.navigation.fragment.findNavController
import com.manoj.baseproject.databinding.FragmentLoginBinding
import com.manoj.baseproject.presentation.common.base.BaseFragment
import com.manoj.baseproject.presentation.common.base.BaseViewModel
import com.manoj.baseproject.utils.setSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreateView(view: View, saveInstanceState: Bundle?) {
        binding.btnLogin.setSingleClickListener {
            navigateToHome()
        }
    }

    override fun getLayoutResource(): Int = R.layout.fragment_login

    override fun getViewModel(): BaseViewModel = viewModel
    override fun setObserver() {

    }

    override fun apiCall() {

    }

    private fun navigateToHome() = findNavController().navigate(LoginFragmentDirections.toHomeFragment())
}
package com.manoj.baseproject.presentation.fragment.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.manoj.baseproject.R
import com.manoj.baseproject.core.utils.extension.setSingleClickListener
import com.manoj.baseproject.databinding.FragmentLoginBinding
import com.manoj.baseproject.core.common.base.BaseFragment
import com.manoj.baseproject.core.common.base.BaseViewModel
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

    override suspend fun apiCall() {

    }

    private fun navigateToHome() = findNavController().navigate(LoginFragmentDirections.toHomeFragment())
}
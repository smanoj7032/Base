package com.manoj.baseproject.presentation.fragment.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.manoj.baseproject.core.common.base.BaseFragment
import com.manoj.baseproject.core.common.base.BaseViewModel
import com.manoj.baseproject.core.utils.extension.Lyt
import com.manoj.baseproject.core.utils.extension.setSingleClickListener
import com.manoj.baseproject.core.utils.validator.isValidEmail
import com.manoj.baseproject.core.utils.validator.isValidPassword
import com.manoj.baseproject.core.utils.validator.setupFieldValidations
import com.manoj.baseproject.core.utils.validator.validateFields
import com.manoj.baseproject.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreateView(view: View, saveInstanceState: Bundle?) {
        initView()
    }

    override fun getLayoutResource(): Int = Lyt.fragment_login

    override fun getViewModel(): BaseViewModel = viewModel
    override fun setObserver() {

    }

    override suspend fun apiCall() {

    }

    private fun initView() = with(binding) {
        btnLogin.setSingleClickListener { login() }
        setupFieldValidations(
            usernameTextInputLayout to { it.isValidEmail() },
            passwordTextInputLayout to { it.isValidPassword() })
    }

    private fun login() = with(binding) {
        if (validateFields(usernameTextInputLayout, passwordTextInputLayout)) {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(LoginFragmentDirections.toHomeFragment())
        sharedPrefManager.saveAccessToken("manoj")
    }
}
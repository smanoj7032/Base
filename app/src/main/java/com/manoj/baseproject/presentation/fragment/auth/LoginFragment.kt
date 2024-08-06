package com.manoj.baseproject.presentation.fragment.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.manoj.baseproject.core.common.base.BaseFragment
import com.manoj.baseproject.core.common.base.BaseViewModel
import com.manoj.baseproject.core.utils.extension.Lyt
import com.manoj.baseproject.core.utils.extension.getEditText
import com.manoj.baseproject.core.utils.extension.isValidEmail
import com.manoj.baseproject.core.utils.extension.isValidPassword
import com.manoj.baseproject.core.utils.extension.setSingleClickListener
import com.manoj.baseproject.core.utils.extension.validate
import com.manoj.baseproject.core.utils.extension.validationPair
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
    }

    private fun login() {
        val emailValidation = listOf(
            validationPair({ it.isNotEmpty() }, "This field cannot be empty"),
            validationPair({ it.isValidEmail() }, "Enter correct email"),
        )
        val passValidation = listOf(
            validationPair({ it.isNotEmpty() }, "This field cannot be empty"),
            validationPair(
                { it.isValidPassword(8, true) },
                "Password must contain at least one special character and must contain at least one digit."
            ),
        )

        if (viewModel.fieldEmail.get()?.validate(baseContext, emailValidation) == true
            && viewModel.fieldPass.get()?.validate(baseContext, passValidation) == true
        ) {
            navigateToHome()
        }
    }

    private fun navigateToHome() =
        findNavController().navigate(LoginFragmentDirections.toHomeFragment())
}
package com.manoj.base.presentation.fragment.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.manoj.base.core.common.base.BaseFragment
import com.manoj.base.core.common.sociallogin.googlelogin.GoogleSignInManager
import com.manoj.base.core.network.helper.apihelper.Result
import com.manoj.base.core.utils.extension.Lyt
import com.manoj.base.core.utils.extension.setSingleClickListener
import com.manoj.base.core.utils.extension.showToast
import com.manoj.base.core.utils.validator.isValidEmail
import com.manoj.base.core.utils.validator.isValidPassword
import com.manoj.base.core.utils.validator.setupFieldValidations
import com.manoj.base.core.utils.validator.validateFields
import com.manoj.base.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(view: View, saveInstanceState: Bundle?) {
        initView()
    }

    override fun getLayoutResource(): Int = Lyt.fragment_login

    override suspend fun setObserver() {}

    override suspend fun apiCall() {}
    override fun onRetry() {}

    private fun initView() = with(binding) {
        btnLogin.setSingleClickListener { socialLogin() }
        socialLogin.ivGoogle.setSingleClickListener { socialLogin() }
        setupFieldValidations(usernameTextInputLayout to { it.isValidEmail() },
            passwordTextInputLayout to { it.isValidPassword() })
    }

    private fun login() = with(binding) {
        if (validateFields(usernameTextInputLayout, passwordTextInputLayout)) {
            navigateToHome()
        }
    }

    private fun socialLogin() {
        lifecycleScope.launch {
            val googleSignInManager = GoogleSignInManager.getInstance(
                GoogleSignInManager.GoogleSignInParams(
                    requireActivity(), // Using activity context
                    parentActivity?.credentialManager,
                    dispatchersProvider,
                    parentActivity?.dataStoreManager
                )
            )

            googleSignInManager.signInWithGoogle().collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        onLoading(false)
                        navigateToHome()
                    }
                    is Result.Error -> {
                        requireContext().showToast(result.message)
                        onLoading(false)
                    }
                    is Result.Loading -> onLoading(true)
                }
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(LoginFragmentDirections.toHomeFragment())
    }
}
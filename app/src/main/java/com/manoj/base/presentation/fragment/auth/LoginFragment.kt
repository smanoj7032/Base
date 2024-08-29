package com.manoj.base.presentation.fragment.auth

import android.os.Bundle
import android.view.View
import androidx.credentials.CredentialManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
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
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(view: View, saveInstanceState: Bundle?) {
        initView()
    }

    override fun getLayoutResource(): Int = Lyt.fragment_login

    override fun setObserver() {

    }

    override suspend fun apiCall() {

    }

    private fun initView() = with(binding) {
        btnLogin.setSingleClickListener { login() }
        socialLogin.ivGoogle.setSingleClickListener { socialLogin() }
        setupFieldValidations(usernameTextInputLayout to { it.isValidEmail() },
            passwordTextInputLayout to { it.isValidPassword() })
    }

    private fun login() = with(binding) {
        if (validateFields(usernameTextInputLayout, passwordTextInputLayout)) {
            navigateToHome(viewModel.fieldEmail.get())
        }
    }

    private fun socialLogin() = lifecycleScope.launch {
        GoogleSignInManager.getInstance(
            GoogleSignInManager.GoogleSignInParams(
                requireContext(), parentActivity?.credentialManager, viewModel.dispatchers
            )
        ).signInWithGoogle().collectLatest {
                when (it) {
                    is Result.Success -> {
                        navigateToHome(it.data?.email)
                        sharedPrefManager.saveUser(it.data)
                        onLoading(false)
                    }

                    is Result.Error -> {
                        requireContext().showToast(it.message)
                        onLoading(false)
                    }

                    is Result.Loading -> onLoading(true)
                }
            }
    }

    private fun navigateToHome(userData: String? = null) {
        findNavController().navigate(LoginFragmentDirections.toHomeFragment())
        sharedPrefManager.saveAccessToken(userData)
    }
}
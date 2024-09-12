package com.manoj.base.presentation.fragment.onboarding.starter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.manoj.base.core.common.base.BaseFragment
import com.manoj.base.core.utils.extension.Lyt
import com.manoj.base.core.utils.extension.setSingleClickListener
import com.manoj.base.databinding.FragmentStarterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StarterFragment : BaseFragment<FragmentStarterBinding, StarterVM>() {
    override val viewModel: StarterVM by viewModels()

    override fun onCreateView(view: View, saveInstanceState: Bundle?) {
        binding.buttonGetStarted.setSingleClickListener { navigateToLogin() }
    }

    override fun getLayoutResource(): Int = Lyt.fragment_starter

    override suspend fun setObserver() {

    }

    override suspend fun apiCall() {

    }

    private fun navigateToLogin() = findNavController().navigate(
        StarterFragmentDirections.toLoginFragment()
    )
}
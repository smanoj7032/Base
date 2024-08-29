package com.manoj.base.presentation.fragment.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.manoj.base.core.common.base.BaseFragment
import com.manoj.base.core.common.sociallogin.googlelogin.GoogleSignInManager
import com.manoj.base.core.utils.extension.Lyt
import com.manoj.base.core.utils.extension.launchAndRepeatWithViewLifecycle
import com.manoj.base.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileVM>() {
    override val viewModel: ProfileVM by viewModels()

    override fun onCreateView(view: View, saveInstanceState: Bundle?) {
        initView()
    }

    private fun initView() = with(binding) {
        launchAndRepeatWithViewLifecycle {
            sharedPrefManager.getCurrentUser<GoogleSignInManager.UserData>()?.apply { bean = this }
        }
    }

    override fun getLayoutResource(): Int = Lyt.fragment_profile

    override fun setObserver() {

    }

    override suspend fun apiCall() {

    }
}
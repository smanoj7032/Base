package com.manoj.base.presentation.fragment.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.manoj.base.core.common.base.BaseFragment
import com.manoj.base.core.network.helper.SystemVariables
import com.manoj.base.core.utils.extension.Lyt
import com.manoj.base.core.utils.extension.loadImage
import com.manoj.base.core.utils.extension.setSingleClickListener
import com.manoj.base.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileVM>() {
    override val viewModel: ProfileVM by viewModels()

    override fun onCreateView(view: View, saveInstanceState: Bundle?) {
        initView()
    }

    private fun initView() = with(binding) {
        ivProfile.setSingleClickListener { picker.show() }
        SystemVariables.onPickerClosed = { itemType, uri, uris -> ivProfile.loadImage(uri) }
        lifecycleScope.launch {
            viewModel.user.collect { bean = it }
        }
    }

    override fun getLayoutResource(): Int = Lyt.fragment_profile

    override suspend fun setObserver() {

    }

    override suspend fun apiCall() {

    }
}
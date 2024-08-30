package com.manoj.base.presentation.fragment.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.manoj.base.core.common.base.BaseFragment
import com.manoj.base.core.network.helper.SystemVariables
import com.manoj.base.core.utils.Logger
import com.manoj.base.core.utils.extension.Drw
import com.manoj.base.core.utils.extension.Lyt
import com.manoj.base.core.utils.extension.loadImage
import com.manoj.base.core.utils.extension.setSingleClickListener
import com.manoj.base.core.utils.picker.MediaModel
import com.manoj.base.core.utils.picker.MediaType
import com.manoj.base.core.utils.picker.PickerDialogHelper
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
        setPickerDialogHelper()
        lifecycleScope.launch {
            viewModel.user.collect { bean = it }
        }
    }

    private fun setPickerDialogHelper() {
        picker = PickerDialogHelper(
            this, false, baseContext, items = arrayListOf(
                MediaModel(MediaType.TAKE_PICTURE, itemIcon = Drw.ic_camera_svg),
                MediaModel(MediaType.CHOOSE_IMAGE, itemIcon = Drw.ic_gallery_svg),
                MediaModel(MediaType.RECORD_VIDEO, itemIcon = Drw.ic_camera_svg),
                MediaModel(MediaType.CHOOSE_VIDEO, itemIcon = Drw.ic_gallery_svg),
                MediaModel(MediaType.SELECT_FILES, itemIcon = Drw.ic_camera_svg)
            )
        ) { mediaType, uri, uris ->
            binding.ivProfile.loadImage(uri)
            Logger.e("onPickerClosed", "$mediaType")
        }
    }

    override fun getLayoutResource(): Int = Lyt.fragment_profile

    override suspend fun setObserver() {

    }

    override suspend fun apiCall() {

    }
}
package com.manoj.baseproject.presentation.view.activity.login

import androidx.activity.viewModels
import com.manoj.baseproject.R
import com.manoj.baseproject.databinding.ActivityLoginBinding
import com.manoj.baseproject.presentation.common.base.BaseActivity
import com.manoj.baseproject.presentation.common.base.BaseViewModel
import com.manoj.baseproject.utils.picker.ItemModel
import com.manoj.baseproject.utils.picker.ItemType
import com.manoj.baseproject.utils.picker.PickerDialogHelper
import com.manoj.baseproject.utils.picker.getFilePathFromUri
import com.manoj.baseproject.utils.picker.realPath
import com.manoj.baseproject.utils.picker.setup
import com.manoj.baseproject.utils.setSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private val picker by lazy {
        PickerDialogHelper(
            this, supportFragmentManager, arrayListOf(
                ItemModel(ItemType.ITEM_CAMERA, itemIcon = R.drawable.ic_camera_svg),
                ItemModel(ItemType.ITEM_GALLERY, itemIcon = R.drawable.ic_gallery_svg),
                ItemModel(ItemType.ITEM_VIDEO, itemIcon = R.drawable.ic_camera_svg),
                ItemModel(ItemType.ITEM_VIDEO_GALLERY, itemIcon = R.drawable.ic_gallery_svg),
                ItemModel(ItemType.ITEM_FILES, itemIcon = R.drawable.ic_camera_svg)
            )
        )
    }


    private val viewModel: LoginViewModel by viewModels()


    override fun apiCall() {

    }

    override fun getLayoutResource(): Int = R.layout.activity_login

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView() {
        binding.btnLogin.setSingleClickListener {
            picker.show()
        }
        picker.initialize(this, true)
        picker.setPickerCloseListener { type, uri, uris ->
            if (type == ItemType.ITEM_FILES) {
                binding.ivImage.setup(
                    "file://${
                        uris?.get(0)?.getFilePathFromUri(this)
                    }"
                ) {
                    javaScriptCanOpenWindowsAutomatically = true
                    domStorageEnabled = true
                }
            } else {
                binding.ivImage.setup(uris?.get(0).toString()) {
                    javaScriptCanOpenWindowsAutomatically = true
                    domStorageEnabled = true
                }
            }

        }
    }

    override fun setObserver() {

    }
}
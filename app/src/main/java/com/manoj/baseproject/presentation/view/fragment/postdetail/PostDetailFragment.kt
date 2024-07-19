package com.manoj.baseproject.presentation.view.fragment.postdetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.manoj.baseproject.BR
import com.manoj.baseproject.R
import com.manoj.baseproject.data.bean.Post
import com.manoj.baseproject.databinding.FragmentPostDetailBinding
import com.manoj.baseproject.databinding.ItemPostBinding
import com.manoj.baseproject.presentation.common.adapter.RVAdapter
import com.manoj.baseproject.presentation.common.base.BaseFragment
import com.manoj.baseproject.presentation.common.base.BaseViewModel
import com.manoj.baseproject.utils.Logger
import com.manoj.baseproject.utils.picker.ItemModel
import com.manoj.baseproject.utils.picker.ItemType
import com.manoj.baseproject.utils.picker.PickerDialogHelper
import com.manoj.baseproject.utils.setSingleClickListener
import com.manoj.baseproject.utils.singleObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailFragment : BaseFragment<FragmentPostDetailBinding>() {
    private val viewModel: PostDetailVM by viewModels()
    private lateinit var postAdapter: RVAdapter<Post, ItemPostBinding>
    private lateinit var picker: PickerDialogHelper
    private var updatePos: Int? = null
    private lateinit var item: Post
    override fun onCreateView(view: View, saveInstanceState: Bundle?) {
        setAdapter()
        picker = PickerDialogHelper(
            this, false, baseContext, childFragmentManager, items = arrayListOf(
                ItemModel(ItemType.ITEM_CAMERA, itemIcon = R.drawable.ic_camera_svg),
                ItemModel(ItemType.ITEM_GALLERY, itemIcon = R.drawable.ic_gallery_svg),
                ItemModel(ItemType.ITEM_VIDEO, itemIcon = R.drawable.ic_camera_svg),
                ItemModel(ItemType.ITEM_VIDEO_GALLERY, itemIcon = R.drawable.ic_gallery_svg),
                ItemModel(ItemType.ITEM_FILES, itemIcon = R.drawable.ic_camera_svg)
            )
        )
        picker.setPickerCloseListener { itemType, uri, uris ->
            updatePos?.let {
                postAdapter.updateItem(
                    item.copy(owner = item.owner.copy(picture = uri.toString())),
                    it
                )
            }
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_post_detail
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun setObserver() {
        viewModel.obrPosts.singleObserver(
            this,
            onLoading = ::onLoading,
            onSuccess = {
                postAdapter.list = it.data
            },
            onError = ::onError
        )
    }

    private fun setAdapter() {
        postAdapter = object : RVAdapter<Post, ItemPostBinding>(R.layout.item_post, BR.bean) {
            override fun onBind(binding: ItemPostBinding, bean: Post, position: Int) {
                super.onBind(binding, bean, position)
                binding.bean = bean
                binding.ivProfile.setSingleClickListener {
                    updatePos = position
                    item = bean
                    picker.show()
                }
            }
        }
        binding.rvPosts.adapter = postAdapter
    }

    override fun apiCall() {
        arguments?.let {
            val id  = PostDetailFragmentArgs.fromBundle(it).id
            Logger.d("ID--->>>","$id")
            viewModel.getPost(id?:"60d21b6767d0d8992e610ce8")
        }

    }
}
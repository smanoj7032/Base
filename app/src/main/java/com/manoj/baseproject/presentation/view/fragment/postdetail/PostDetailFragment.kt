package com.manoj.baseproject.presentation.view.fragment.postdetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.manoj.baseproject.BR
import com.manoj.baseproject.R
import com.manoj.baseproject.data.bean.Post
import com.manoj.baseproject.databinding.FragmentPostDetailBinding
import com.manoj.baseproject.databinding.ItemPostBinding
import com.manoj.baseproject.presentation.common.adapter.CallBackModel
import com.manoj.baseproject.presentation.common.adapter.Callbacks
import com.manoj.baseproject.presentation.common.adapter.CustomAdapter
import com.manoj.baseproject.presentation.common.adapter.RecyclerItemTouchHelper
import com.manoj.baseproject.presentation.common.base.BaseFragment
import com.manoj.baseproject.presentation.common.base.BaseViewModel
import com.manoj.baseproject.utils.Logger
import com.manoj.baseproject.utils.customCollector
import com.manoj.baseproject.utils.picker.ItemModel
import com.manoj.baseproject.utils.picker.ItemType
import com.manoj.baseproject.utils.picker.PickerDialogHelper
import com.manoj.baseproject.utils.singleObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailFragment : BaseFragment<FragmentPostDetailBinding>() {
    private val viewModel: PostDetailVM by viewModels()
    private lateinit var postAdapter: CustomAdapter<ItemPostBinding, Post>
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
       lifecycleScope.launch {
           viewModel.posts.customCollector(
               onLoading = ::onLoading,
               onSuccess = {
                   val arrayList: ArrayList<Post> = it?.data as ArrayList
                   arrayList.addAll(it.data)
                   arrayList.addAll(it.data)
                   arrayList.addAll(it.data)
                   arrayList.addAll(it.data)
                   postAdapter.list = arrayList

               },
               onError = ::onError
           )
       }
    }

    private fun setAdapter() {
        val clickListener = Callbacks<ItemPostBinding, Post>()
        clickListener.add(CallBackModel(R.id.ivProfile) { model, position, binding ->
            updatePos = position
            item = model
            picker.show()
        })
        postAdapter = CustomAdapter(R.layout.item_post, BR.bean, callbacks = clickListener)
        binding.rvPosts.layoutManager = GridLayoutManager(baseContext,3)
        binding.rvPosts.adapter = postAdapter
        val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(postAdapter))
        itemTouchHelper.attachToRecyclerView(binding.rvPosts)
    }

    override fun apiCall() {
        arguments?.let {
            val id = PostDetailFragmentArgs.fromBundle(it).id
            Logger.d("ID--->>>", "$id")
            viewModel.getPost(id ?: "60d21b6767d0d8992e610ce8")
        }

    }
}
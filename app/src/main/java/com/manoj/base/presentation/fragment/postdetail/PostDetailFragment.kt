package com.manoj.base.presentation.fragment.postdetail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.manoj.base.BR
import com.manoj.base.core.common.adapter.BaseAdapter
import com.manoj.base.core.common.adapter.CallBackModel
import com.manoj.base.core.common.adapter.Callbacks
import com.manoj.base.core.common.adapter.RecyclerItemTouchHelper
import com.manoj.base.core.common.base.BaseFragment
import com.manoj.base.core.network.helper.NetworkMonitor
import com.manoj.base.core.network.helper.SystemVariables
import com.manoj.base.core.utils.Logger
import com.manoj.base.core.utils.extension.Drw
import com.manoj.base.core.utils.extension.Ids
import com.manoj.base.core.utils.extension.Lyt
import com.manoj.base.core.utils.extension.customCollector
import com.manoj.base.core.utils.extension.hide
import com.manoj.base.core.utils.extension.showToast
import com.manoj.base.core.utils.picker.MediaModel
import com.manoj.base.core.utils.picker.MediaType
import com.manoj.base.core.utils.picker.PickerDialogHelper
import com.manoj.base.data.bean.Post
import com.manoj.base.databinding.FragmentPostDetailBinding
import com.manoj.base.databinding.ItemPostDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailFragment : BaseFragment<FragmentPostDetailBinding, PostDetailVM>() {
    override val viewModel: PostDetailVM by viewModels()
    private lateinit var postAdapter: BaseAdapter<ItemPostDetailBinding, Post>
    private var updatePos: Int? = null
    private lateinit var item: Post
    private lateinit var cropImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(view: View, saveInstanceState: Bundle?) {
        cropImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val croppedImageUri = result.data?.data
                    updateItem(croppedImageUri.toString())
                } else baseContext.showToast("Unable to crop image")
            }
        setAdapter()
        setPickerListener()
        SystemVariables.onNetworkChange = {
            if (it == NetworkMonitor.NetworkState.Available) {
                getPost()
            }
        }
    }

    private fun setPickerListener() {
        picker = PickerDialogHelper(
            this, false, baseContext, items = arrayListOf(
                MediaModel(MediaType.TAKE_PICTURE, itemIcon = Drw.ic_camera_svg),
                MediaModel(MediaType.CHOOSE_IMAGE, itemIcon = Drw.ic_gallery_svg),
                MediaModel(MediaType.RECORD_VIDEO, itemIcon = Drw.ic_camera_svg),
                MediaModel(MediaType.CHOOSE_VIDEO, itemIcon = Drw.ic_gallery_svg),
                MediaModel(MediaType.SELECT_FILES, itemIcon = Drw.ic_camera_svg)
            )
        ) { mediaType, uri, uris ->
            Logger.e("onPickerClosed", "$mediaType")
            /*when (itemType) {
            MediaType.TAKE_PICTURE, MediaType.CHOOSE_IMAGE ->
                requireActivity().performCrop(uri, cropImageLauncher)

            else -> updateItem(uri.toString())
        }*/
            updateItem(uri.toString())
            Logger.e("onPickerClosed", "$mediaType")
        }
    }

    private fun updateItem(uri: String) {
        updatePos?.let {
            postAdapter.updateItem(
                item.copy(owner = item.owner.copy(picture = uri)), it
            )
        }
    }

    override fun getLayoutResource(): Int {
        return Lyt.fragment_post_detail
    }

    override suspend fun setObserver() {
        viewModel.posts.customCollector(
            onLoading = ::onLoading, onSuccess = {
                val arrayList: ArrayList<Post> = it?.data as ArrayList
                arrayList.addAll(it.data)
                arrayList.addAll(it.data)
                arrayList.addAll(it.data)
                arrayList.addAll(it.data)
                postAdapter.list = arrayList

            }, onError = ::onError
        )
    }

    private fun setAdapter() {
        val clickListener = Callbacks<ItemPostDetailBinding, Post>()
        clickListener.add(CallBackModel(Ids.ivProfile) { model, position, binding ->
            updatePos = position
            item = model
            picker.show()
        })
        postAdapter =
            BaseAdapter(Lyt.item_post_detail, BR.bean, binding.emptyView, callbacks = clickListener)
        binding.rvPosts.layoutManager = LinearLayoutManager(baseContext)
        binding.rvPosts.adapter = postAdapter
        val itemTouchHelper = ItemTouchHelper(
            RecyclerItemTouchHelper(postAdapter, swipeToDelete = true, activity = requireActivity())
        )
        itemTouchHelper.attachToRecyclerView(binding.rvPosts)
    }

    override suspend fun apiCall() {
        Logger.d("Api Call--->>", "Fragment Called()")
        getPost()
    }

    private fun getPost() = arguments?.let {
        val id = PostDetailFragmentArgs.fromBundle(it).id
        Logger.d("ID--->>>", "$id")
        viewModel.getPost(id ?: "60d21b6767d0d8992e610ce8")
    }
}
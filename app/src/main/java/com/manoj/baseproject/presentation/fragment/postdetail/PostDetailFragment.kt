package com.manoj.baseproject.presentation.fragment.postdetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.manoj.baseproject.BR
import com.manoj.baseproject.core.common.adapter.BaseAdapter
import com.manoj.baseproject.core.common.adapter.CallBackModel
import com.manoj.baseproject.core.common.adapter.Callbacks
import com.manoj.baseproject.core.common.adapter.RecyclerItemTouchHelper
import com.manoj.baseproject.core.common.base.BaseFragment
import com.manoj.baseproject.core.network.helper.SystemVariables
import com.manoj.baseproject.core.utils.Logger
import com.manoj.baseproject.core.utils.extension.Ids
import com.manoj.baseproject.core.utils.extension.Lyt
import com.manoj.baseproject.core.utils.extension.customCollector
import com.manoj.baseproject.core.utils.extension.showToast
import com.manoj.baseproject.core.utils.picker.performCrop
import com.manoj.baseproject.data.bean.Post
import com.manoj.baseproject.databinding.FragmentPostDetailBinding
import com.manoj.baseproject.databinding.ItemPostDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailFragment : BaseFragment<FragmentPostDetailBinding,PostDetailVM>() {
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
                    updatePos?.let {
                        postAdapter.updateItem(
                            item.copy(owner = item.owner.copy(picture = croppedImageUri.toString())),
                            it
                        )
                    }
                } else {
                    baseContext.showToast("Error")
                }
            }
        setAdapter()
        setPickerListener()
    }

    private fun setPickerListener() {
        SystemVariables.onPickerClosed = { itemType, uri, uris ->
            requireActivity().performCrop(uri, cropImageLauncher)
            Logger.e("onPickerClosed", "$itemType")
        }
    }

    override fun getLayoutResource(): Int {
        return Lyt.fragment_post_detail
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
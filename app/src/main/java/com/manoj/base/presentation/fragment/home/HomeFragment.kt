package com.manoj.base.presentation.fragment.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.manoj.base.BR
import com.manoj.base.core.common.adapter.CallBackModel
import com.manoj.base.core.common.adapter.LoadMoreAdapter
import com.manoj.base.core.common.adapter.RVAdapterWithPaging
import com.manoj.base.core.common.base.BaseFragment
import com.manoj.base.core.network.helper.SystemVariables
import com.manoj.base.core.utils.Logger
import com.manoj.base.core.utils.extension.Ids
import com.manoj.base.core.utils.extension.Lyt
import com.manoj.base.core.utils.extension.launchAndRepeatWithViewLifecycle
import com.manoj.base.data.bean.Post
import com.manoj.base.databinding.FragmentHomeBinding
import com.manoj.base.databinding.ItemPostBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding,HomeVM>() {
    override val viewModel: HomeVM by viewModels()
    private lateinit var postsAdapter: RVAdapterWithPaging<Post, ItemPostBinding>
    override fun onCreateView(view: View, saveInstanceState: Bundle?) {
        setPagingAdapter()
    }

    override fun getLayoutResource(): Int = Lyt.fragment_home

    override fun setObserver() {
        launchAndRepeatWithViewLifecycle {
            viewModel.posts.collect {
                postsAdapter.submitData(it)
            }
        }
        SystemVariables.onNetworkChange = { postsAdapter.retry() }
        setPickerListener()
    }

    private fun setPickerListener() {
        SystemVariables.onPickerClosed = { itemType, uri, uris ->
            Logger.e("onPickerClosed", "$itemType")
        }
    }

    override suspend fun apiCall() {

    }

    private fun setPagingAdapter() {
        val diffCallback = RVAdapterWithPaging.createDiffCallback<Post> { oldItem, newItem ->
            return@createDiffCallback oldItem.id == newItem.id
        }
        val footerAdapter = LoadMoreAdapter { postsAdapter.retry() }
        val headerAdapter = LoadMoreAdapter { postsAdapter.retry() }
        val layoutManager = LinearLayoutManager(requireActivity().applicationContext)

        /**Un- Comment this code if you want set layout manager in Grid*/
        /*layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if ((position == postsAdapter.itemCount) && footerAdapter.itemCount > 0) 3
                else if (postsAdapter.itemCount == 0 && headerAdapter.itemCount > 0) 3
                else 1
            }
        }*/
        postsAdapter = RVAdapterWithPaging(
            diffCallback,
            Lyt.item_post,
            BR.bean,
            callbacks = arrayListOf(
                CallBackModel(Ids.cvPostRoot) { model, position, binding ->
                    navigateToPostDetail(
                        model.id
                    )
                },
                CallBackModel(Ids.ivProfile) { model, position, binding -> picker.show() }
            )
        )
        binding.rvPosts.apply {
            itemAnimator = null
            this.layoutManager = layoutManager
            adapter = postsAdapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)
        }
        postsAdapter.attachLoadStateListener(onLoading = ::onLoading, onError = ::onError)
    }

    private fun navigateToPostDetail(id: String?) = findNavController().navigate(
        HomeFragmentDirections.toPostDetailFragment(id)
    )
}
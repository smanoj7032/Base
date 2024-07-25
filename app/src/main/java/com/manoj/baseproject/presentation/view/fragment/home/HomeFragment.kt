package com.manoj.baseproject.presentation.view.fragment.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.manoj.baseproject.BR
import com.manoj.baseproject.R
import com.manoj.baseproject.data.bean.Post
import com.manoj.baseproject.databinding.FragmentHomeBinding
import com.manoj.baseproject.databinding.ItemPostBinding
import com.manoj.baseproject.presentation.common.adapter.CallBackModel
import com.manoj.baseproject.presentation.common.adapter.LoadMoreAdapter
import com.manoj.baseproject.presentation.common.adapter.RVAdapterWithPaging
import com.manoj.baseproject.presentation.common.adapter.RecyclerItemTouchHelper
import com.manoj.baseproject.presentation.common.base.BaseFragment
import com.manoj.baseproject.presentation.common.base.BaseViewModel
import com.manoj.baseproject.presentation.view.fragment.auth.LoginFragmentDirections
import com.manoj.baseproject.utils.launchAndRepeatWithViewLifecycle
import com.manoj.baseproject.utils.setSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeVM by viewModels()
    private lateinit var postsAdapter: RVAdapterWithPaging<Post, ItemPostBinding>
    override fun onCreateView(view: View, saveInstanceState: Bundle?) {
        setPagingAdapter()
    }

    override fun getLayoutResource(): Int = R.layout.fragment_home

    override fun getViewModel(): BaseViewModel = viewModel
    override fun setObserver() {
        launchAndRepeatWithViewLifecycle {
            viewModel.posts.collect {
                postsAdapter.submitData(it)
            }
        }
    }

    override fun apiCall() {

    }

    private fun setPagingAdapter() {
        val diffCallback = RVAdapterWithPaging.createDiffCallback<Post> { oldItem, newItem ->
            return@createDiffCallback oldItem.id == newItem.id
        }
        val footerAdapter = LoadMoreAdapter { postsAdapter.retry() }
        val headerAdapter = LoadMoreAdapter { postsAdapter.retry() }
        val layoutManager = GridLayoutManager(requireActivity().applicationContext, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if ((position == postsAdapter.itemCount) && footerAdapter.itemCount > 0) 3
                else if (postsAdapter.itemCount == 0 && headerAdapter.itemCount > 0) 3
                else 1
            }
        }
        postsAdapter = RVAdapterWithPaging(
            diffCallback,
            R.layout.item_post,
            BR.bean,
            callbacks = arrayListOf(CallBackModel(R.id.cvPostRoot) { model, position, binding ->
                navigateToPostDetail(model.id)
            })
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
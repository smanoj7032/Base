package com.manoj.baseproject.presentation.view.activity.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.manoj.baseproject.R
import com.manoj.baseproject.data.bean.Post
import com.manoj.baseproject.data.bean.PostItem
import com.manoj.baseproject.databinding.ActivityMainBinding
import com.manoj.baseproject.databinding.ItemPostBinding
import com.manoj.baseproject.presentation.common.base.BaseActivity
import com.manoj.baseproject.presentation.common.base.BaseViewModel
import com.manoj.baseproject.presentation.common.adapter.RVAdapter
import com.manoj.baseproject.network.helper.NetworkMonitor
import com.manoj.baseproject.presentation.common.adapter.LoadMoreAdapter
import com.manoj.baseproject.presentation.common.adapter.RVAdapterWithPaging
import com.manoj.baseproject.presentation.common.adapter.RVAdapterWithPaging.Companion.createDiffCallback
import com.manoj.baseproject.presentation.common.mediapicker.MediaPickerHelper
import com.manoj.baseproject.presentation.view.activity.post.PostDetailActivity
import com.manoj.baseproject.utils.customCollector
import com.manoj.baseproject.utils.Logger
import com.manoj.baseproject.utils.launchAndRepeatWithViewLifecycle
import com.manoj.baseproject.utils.picker.MediaPickerListener
import com.manoj.baseproject.utils.setSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {


    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val TIME_INTERVAL = 2000
    private var doubleBackToExitPressedOnce: Long = 0
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (doubleBackToExitPressedOnce + TIME_INTERVAL > System.currentTimeMillis()) {
                finish()
            } else showToast("Please click BACK again to exit")
            doubleBackToExitPressedOnce = System.currentTimeMillis()
        }
    }
    private val viewModel: MainViewModel by viewModels()
    private lateinit var postAdapter: RVAdapter<PostItem, ItemPostBinding>
    private lateinit var postsAdapter: RVAdapterWithPaging<Post, ItemPostBinding>
    override fun apiCall() {
        launchAndRepeatWithViewLifecycle {
            viewModel.posts.collect {
                postsAdapter.submitData(it)
            }
        }
    }


    override fun getLayoutResource(): Int {
        return R.layout.activity_main
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        initViews()
        MediaPickerHelper.initialize(this)
    }

    private fun initViews() {
        supportActionBar?.title = "Dashboard"
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        setPagingAdapter()
        MediaPickerHelper.setListener(object : MediaPickerListener {
            override fun onMediaPicked(uri: Uri?) {
                Log.e("Uri---->>>", "onMediaPicked: $uri")

            }

            override fun onMultipleMediaPicked(uri: List<Uri>?) {

            }
        })
    }

    private fun setAdapter() {
        /*  postAdapter = object : RVAdapter<PostItem, ItemPostBinding>(R.layout.item_post, BR.bean) {
              override fun onBind(binding: ItemPostBinding, bean: PostItem, position: Int) {
                  super.onBind(binding, bean, position)
                  binding.bean = bean
              }
          }
          binding.rvPosts.adapter = postAdapter*/
    }

    private fun setPagingAdapter() {
        val diffCallback = createDiffCallback<Post> { oldItem, newItem ->
            return@createDiffCallback oldItem.id == newItem.id
        }
        val footerAdapter = LoadMoreAdapter { postsAdapter.retry() }
        val headerAdapter = LoadMoreAdapter { postsAdapter.retry() }
        val layoutManager = LinearLayoutManager(this)
        postsAdapter = RVAdapterWithPaging(
            diffCallback,
            R.layout.item_post,
            onBind = { itemPostBinding, post, i ->
                itemPostBinding.bean = post
                itemPostBinding.ivProfile.setSingleClickListener {
                    MediaPickerHelper.launchImageAndVideoPicker()
                }
                itemPostBinding.root.setSingleClickListener {
                    PostDetailActivity.start(this, post.id)
                }
            })
        binding.rvPosts.apply {
            itemAnimator = null
            this.layoutManager = layoutManager
            adapter = postsAdapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)
            setHasFixedSize(true)
        }
        postsAdapter.attachLoadStateListener(onLoading = ::onLoading, onError = ::onError)
    }

    override fun setObserver() {
        lifecycleScope.launch {
            networkMonitor.networkState.collectLatest {
                handleNetworkState(it)
            }
            viewModel.collectPosts.customCollector(
                onLoading = ::onLoading,
                onSuccess = { postAdapter.list = it },
                onError = ::onError
            )
        }
    }

    private fun handleNetworkState(state: NetworkMonitor.NetworkState) {
        if (state.isAvailable()) {
            postsAdapter.retry()
        }
        Logger.d("State---->>>", "$state")
    }


    override fun onDestroy() {
        super.onDestroy()
        Logger.d("Test--->>", "onDestroy")
    }
}
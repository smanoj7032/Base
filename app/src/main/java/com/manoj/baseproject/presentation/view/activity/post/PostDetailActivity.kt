package com.manoj.baseproject.presentation.view.activity.post

import android.app.Activity
import android.content.Intent
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.manoj.baseproject.R
import com.manoj.baseproject.databinding.ActivityPostDetailBinding
import com.manoj.baseproject.network.helper.Constants.ID
import com.manoj.baseproject.presentation.common.base.BaseActivity
import com.manoj.baseproject.presentation.common.base.BaseViewModel
import com.manoj.baseproject.utils.customCollector
import com.manoj.baseproject.utils.setSingleClickListener
import com.manoj.baseproject.utils.singleObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailActivity : BaseActivity<ActivityPostDetailBinding>() {


    companion object {
        fun start(activity: Activity, id: String) {
            val intent = Intent(activity, PostDetailActivity::class.java)
            intent.putExtra(ID, id)
            activity.startActivity(intent)
        }
    }

    private val viewModel: PostDetailVM by viewModels()
    override fun apiCall() {

    }

    override fun getLayoutResource(): Int = R.layout.activity_post_detail

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        viewModel.getPost(intent?.getStringExtra(ID) ?: "60d21b6767d0d8992e610ce8")
        binding.tvData.setSingleClickListener {
        }
    }

    override fun setObserver() {
        viewModel.obrPosts.singleObserver(
            this,
            onLoading = ::onLoading,
            onSuccess = {
                binding.tvData.text = "{$it}"
                binding.tvData.movementMethod = ScrollingMovementMethod()
            },
            onError = ::onError
        )
    }
}
package com.manoj.baseproject.presentation.view.activity.splash

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.manoj.baseproject.R
import com.manoj.baseproject.databinding.SplashActivityBinding
import com.manoj.baseproject.presentation.common.base.BaseActivity
import com.manoj.baseproject.presentation.common.base.BaseViewModel
import com.manoj.baseproject.presentation.view.activity.login.LoginActivity
import com.manoj.baseproject.presentation.view.activity.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashActivityBinding>() {
    private val viewModel: SplashViewModel by viewModels()
    override fun apiCall() {

    }

    override fun getLayoutResource(): Int {
        return R.layout.splash_activity
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    override fun setObserver() {

    }
}
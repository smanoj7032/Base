package com.manoj.baseproject.presentation.activity.main

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.manoj.baseproject.R
import com.manoj.baseproject.core.utils.Logger
import com.manoj.baseproject.core.utils.extension.Str
import com.manoj.baseproject.core.utils.extension.showSuccessToast
import com.manoj.baseproject.databinding.ActivityMainBinding
import com.manoj.baseproject.core.common.base.BaseActivity
import com.manoj.baseproject.core.common.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController
    private val listener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            Logger.d("Destination", "$destination")
            when (destination.id) {
                R.id.loginFragment -> setTitle(getString(Str.login))


                R.id.homeFragment -> setTitle(getString(Str.dashboard))


                R.id.postDetailFragment -> setTitle(getString(Str.post_detail), true)
            }
        }

    private val TIME_INTERVAL = 2000
    private var doubleBackToExitPressedOnce: Long = 0
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (doubleBackToExitPressedOnce + TIME_INTERVAL > System.currentTimeMillis()) {
                finish()
            } else showSuccessToast("Please click BACK again to exit")
            doubleBackToExitPressedOnce = System.currentTimeMillis()
        }
    }
    private val viewModel: MainViewModel by viewModels()

    override suspend fun apiCall() {
        Logger.d("Api Call--->>", "Activity Called()")
    }


    override fun getLayoutResource(): Int {
        return R.layout.activity_main
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navController = navHostFragment.navController
        initViews()
    }


    private fun initViews() {
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun setObserver() {

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        navController.removeOnDestinationChangedListener(listener)
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("Test--->>", "onDestroy")
    }

    private fun setTitle(title: String?, isBack: Boolean = false) {
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(isBack)
            this.title = title
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
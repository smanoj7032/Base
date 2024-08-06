package com.manoj.baseproject.presentation.activity.main

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.manoj.baseproject.R
import com.manoj.baseproject.core.utils.Logger
import com.manoj.baseproject.core.utils.extension.Str
import com.manoj.baseproject.core.utils.extension.showSuccessToast
import com.manoj.baseproject.databinding.ActivityMainBinding
import com.manoj.baseproject.core.common.base.BaseActivity
import com.manoj.baseproject.core.common.base.BaseViewModel
import com.manoj.baseproject.core.common.basedialogs.BaseBottomSheetDialog
import com.manoj.baseproject.core.utils.extension.hide
import com.manoj.baseproject.core.utils.extension.setSingleClickListener
import com.manoj.baseproject.core.utils.extension.show
import com.manoj.baseproject.databinding.AlertSheetBinding
import com.manoj.baseproject.presentation.fragment.auth.LoginFragmentDirections
import com.manoj.baseproject.presentation.fragment.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var logoutSheet: BaseBottomSheetDialog<AlertSheetBinding>
    private lateinit var navController: NavController
    private val listener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            Logger.d("Destination", "$destination")
            when (destination.id) {
                R.id.loginFragment -> setTitle(getString(Str.login), isMain = false, isBack = false)


                R.id.homeFragment -> setTitle(
                    getString(Str.dashboard),
                    isMain = true,
                    isBack = false
                )


                R.id.postDetailFragment -> setTitle(
                    getString(Str.post_detail),
                    isMain = false,
                    isBack = true
                )
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
        setUpLogoutSheet()
    }

    private fun setUpLogoutSheet() {
        logoutSheet = BaseBottomSheetDialog(R.layout.alert_sheet, onBind = { binding ->
            with(binding) {
                vTop.title = getString(Str.logout)
                tvMessage.text = getString(Str.are_you_sure_want_to_logout)
                btnOk.setSingleClickListener {
                    navigateToHome()
                    logoutSheet.dismiss()
                }
                btnClose.setSingleClickListener { logoutSheet.dismiss() }
            }
        }, onCancelListener = {})
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

    private fun setTitle(title: String?, isBack: Boolean = false, isMain: Boolean) =
        with(binding.header) {
            if (isBack) ivBack.show() else binding.header.ivBack.hide()
            if (isMain) {
                /*ivProfile.show()*/
                ivLogout.show()
                ivLogout.setSingleClickListener {
                }
            } else {
                /*ivProfile.hide()*/
                ivLogout.hide()
            }
            tvTitle.text = title
            ivBack.setSingleClickListener { onBackPressedDispatcher.onBackPressed() }
            ivLogout.setSingleClickListener {
                logoutSheet.show(
                    supportFragmentManager,
                    "Logout Sheet"
                )
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

    private fun navigateToHome() =
        navController.navigate(HomeFragmentDirections.toLoginFragment())
}
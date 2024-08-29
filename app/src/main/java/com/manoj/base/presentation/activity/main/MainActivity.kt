package com.manoj.base.presentation.activity.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.manoj.base.core.common.base.BaseActivity
import com.manoj.base.core.common.basedialogs.BottomSheetMaterialDialog
import com.manoj.base.core.common.singletonholder.SingletonHolderNoArg
import com.manoj.base.core.common.sociallogin.googlelogin.GoogleSignInManager
import com.manoj.base.core.network.helper.apihelper.Result
import com.manoj.base.core.utils.Logger
import com.manoj.base.core.utils.extension.Ids
import com.manoj.base.core.utils.extension.Lyt
import com.manoj.base.core.utils.extension.logoutSheet
import com.manoj.base.core.utils.extension.setSingleClickListener
import com.manoj.base.core.utils.extension.setupNavGraph
import com.manoj.base.core.utils.extension.showToast
import com.manoj.base.databinding.ActivityMainBinding
import com.manoj.base.presentation.activity.main.backpress.BackPressHandler
import com.manoj.base.presentation.activity.main.navigation.NavigationListener
import com.manoj.base.presentation.fragment.auth.LoginFragment
import com.manoj.base.presentation.fragment.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    companion object : SingletonHolderNoArg<MainActivity>(::MainActivity)


    private var logoutSheet: BottomSheetMaterialDialog? = null
    private lateinit var navigationListener: NavigationListener

    private lateinit var navController: NavController

    override suspend fun apiCall() {
        Logger.d("Api Call--->>", "Activity Called()")
    }


    override fun getLayoutResource() = Lyt.activity_main
    override val viewModel: MainViewModel by viewModels()

    override fun onCreateView() {
        setupNavController()
        initViews()
        setUpLogoutSheet()
    }

    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(Ids.container) as NavHostFragment
        navController = navHostFragment.navController
        navController.setupNavGraph(
            if (sharedPrefManager.getAccessToken()
                    .isNullOrEmpty()
            ) Ids.loginFragment else Ids.homeFragment
        )
        navigationListener = NavigationListener(this, binding, logoutSheet)
    }

    private fun setUpLogoutSheet() {
        logoutSheet = logoutSheet {
            lifecycleScope.launch {
                GoogleSignInManager.getInstance(
                    GoogleSignInManager.GoogleSignInParams(
                        this@MainActivity,
                        credentialManager,
                        viewModel.dispatchers
                    )
                )
                    .signOut().collect {
                        when (it) {
                            is Result.Success -> {
                                navigateToLogin()
                                onLoading(false)
                            }

                            is Result.Error -> {
                                showToast(it.message)
                                onLoading(false)
                            }

                            is Result.Loading -> onLoading(true)

                        }
                    }
            }
        }
    }

    private fun initViews() {
        onBackPressedDispatcher.addCallback(BackPressHandler.getInstance(this))
        supportFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(
                fm: FragmentManager,
                f: Fragment,
                v: View,
                savedInstanceState: Bundle?
            ) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                binding.slidingpanelayout.lockMode =
                    if (f is LoginFragment) SlidingPaneLayout.LOCK_MODE_LOCKED else SlidingPaneLayout.LOCK_MODE_UNLOCKED


            }
        }, true)

        binding.header.ivBack.setSingleClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.header.ivLogout.setSingleClickListener { logoutSheet?.show() }

    }

    override fun setObserver() {

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(navigationListener)
    }

    override fun onPause() {
        navController.removeOnDestinationChangedListener(navigationListener)
        super.onPause()
    }


    private fun navigateToLogin() {
        navController.navigate(HomeFragmentDirections.toLoginFragment())
        sharedPrefManager.clearUser()
    }

    private fun isHome() = navController.currentDestination?.id == Ids.homeFragment
}
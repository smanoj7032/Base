package com.manoj.baseproject.presentation.activity.main

import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.manoj.baseproject.core.common.base.BaseActivity
import com.manoj.baseproject.core.common.basedialogs.AbstractDialog
import com.manoj.baseproject.core.common.basedialogs.BottomSheetMaterialDialog
import com.manoj.baseproject.core.common.basedialogs.MaterialDialog
import com.manoj.baseproject.core.common.basedialogs.interfaces.DialogInterface
import com.manoj.baseproject.core.common.basedialogs.model.DialogButton
import com.manoj.baseproject.core.common.basedialogs.model.TextAlignment
import com.manoj.baseproject.core.common.singletonholder.SingletonHolderNoArg
import com.manoj.baseproject.core.utils.Logger
import com.manoj.baseproject.core.utils.extension.Drw
import com.manoj.baseproject.core.utils.extension.Ids
import com.manoj.baseproject.core.utils.extension.Lyt
import com.manoj.baseproject.core.utils.extension.Str
import com.manoj.baseproject.core.utils.extension.hide
import com.manoj.baseproject.core.utils.extension.setSingleClickListener
import com.manoj.baseproject.core.utils.extension.setupNavGraph
import com.manoj.baseproject.core.utils.extension.show
import com.manoj.baseproject.core.utils.extension.showSuccessToast
import com.manoj.baseproject.databinding.ActivityMainBinding
import com.manoj.baseproject.presentation.fragment.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    companion object : SingletonHolderNoArg<MainActivity>(::MainActivity)

    private var logoutSheet: BottomSheetMaterialDialog? = null

    private var logoutDialog: MaterialDialog? = null
    private lateinit var navController: NavController
    private val listener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            Logger.d("Destination", "$destination")
            logoutSheet?.dismiss()
            when (destination.id) {
                Ids.loginFragment -> setTitle(getString(Str.login), isMain = false, isBack = false)


                Ids.homeFragment -> setTitle(
                    getString(Str.dashboard), isMain = true, isBack = false
                )


                Ids.postDetailFragment -> setTitle(
                    getString(Str.post_detail), isMain = false, isBack = true
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
    }

    private fun setUpLogoutSheet() {
        logoutSheet = BottomSheetMaterialDialog.Builder(
            this
        )
            .setTitle(getString(Str.logout), TextAlignment.CENTER)
            .setMessage(getString(Str.are_you_sure_want_to_logout), TextAlignment.CENTER)
            .setCancelable(false)
            .setPositiveButton(
               DialogButton( "Logout",
                   Drw.ic_delete,
                   object : AbstractDialog.OnClickListener {
                       override fun onClick(dialogInterface: DialogInterface, i: Int) {
                           navigateToLogin()
                           dialogInterface.dismiss()
                       }
                   }))
            .setNegativeButton(
               DialogButton(
                   "Cancel",
                   Drw.ic_close,
                   object : AbstractDialog.OnClickListener {
                       override fun onClick(dialogInterface: DialogInterface, which: Int) {
                           dialogInterface.dismiss()
                       }
                }))
            .build();

    }

    private fun initViews() {
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        logoutDialog = MaterialDialog.Builder(this)
            .setTitle(getString(Str.logout), TextAlignment.START)
            .setMessage(
                getString(Str.are_you_sure_want_to_logout),
                TextAlignment.START
            )
            .setCancelable(false)
            .setPositiveButton(
                DialogButton("Logout",
                    Drw.ic_delete,
                    object : AbstractDialog.OnClickListener {
                        override fun onClick(dialogInterface: DialogInterface, i: Int) {
                            navigateToLogin()
                            dialogInterface.dismiss()
                        }
                    })
            )
            .setNegativeButton(
                DialogButton(
                    "Cancel",
                    Drw.ic_close,
                    object : AbstractDialog.OnClickListener {
                        override fun onClick(dialogInterface: DialogInterface, which: Int) {
                            dialogInterface.dismiss()
                        }
                    })
            )
            .build()
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

    private fun setTitle(title: String?, isBack: Boolean = false, isMain: Boolean) =
        with(binding.header) {
            /*  if (isBack) ivBack.show() else binding.header.ivBack.hide()*/
            ivBack.isVisible = isBack
            if (isMain) {/*ivProfile.show()*/
                ivLogout.show()
                ivLogout.setSingleClickListener {}
            } else {/*ivProfile.hide()*/
                ivLogout.hide()
            }
            tvTitle.text = title
            ivBack.setSingleClickListener { onBackPressedDispatcher.onBackPressed() }

            ivLogout.setSingleClickListener {
                   if (isHome()) logoutSheet?.show()
                   else logoutSheet?.dismiss()
            }
        }

    /** override fun dispatchTouchEvent(event: MotionEvent): Boolean {
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
    }*/

    private fun navigateToLogin() {
        navController.navigate(HomeFragmentDirections.toLoginFragment())
        sharedPrefManager.clearUser()
    }

    private fun isHome() = navController.currentDestination?.id == Ids.homeFragment
}
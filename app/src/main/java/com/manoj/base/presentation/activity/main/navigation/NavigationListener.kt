package com.manoj.base.presentation.activity.main.navigation

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.manoj.base.core.common.basedialogs.BottomSheetMaterialDialog
import com.manoj.base.core.utils.Logger
import com.manoj.base.core.utils.extension.Ids
import com.manoj.base.core.utils.extension.Str
import com.manoj.base.core.utils.extension.hide
import com.manoj.base.core.utils.extension.show
import com.manoj.base.databinding.ActivityMainBinding
import com.manoj.base.presentation.activity.main.MainActivity

class NavigationListener(
    private val activity: MainActivity,
    private val binding: ActivityMainBinding,
    private val logoutSheet: BottomSheetMaterialDialog?
) : NavController.OnDestinationChangedListener {

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        Logger.d("Destination", "$destination")
        logoutSheet?.dismiss()

        val titleResId: Int
        val isMain: Boolean
        val isBack: Boolean

        when (destination.id) {
            Ids.loginFragment -> {
                titleResId = Str.login
                isMain = false
                isBack = false
            }
            Ids.homeFragment -> {
                titleResId = Str.dashboard
                isMain = true
                isBack = false
            }
            Ids.postDetailFragment -> {
                titleResId = Str.post_detail
                isMain = false
                isBack = true
            }
            else -> {
                titleResId = Str.app_name // Default title
                isMain = false
                isBack = false
            }
        }

        setTitle(activity.getString(titleResId), isMain, isBack)
    }

    private fun setTitle(title: String, isMain: Boolean, isBack: Boolean) = with(binding.header) {
        ivBack.isVisible = isBack
        ivLogout.isVisible = isMain
        tvTitle.text = title

        // Close the sliding panel layout if title is "Login"
        if (title == activity.getString(Str.login)) {
            binding.slidingpanelayout.close()
        }
    }
}


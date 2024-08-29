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
        when (destination.id) {
            Ids.loginFragment -> setTitle(
                activity.getString(Str.login),
                isMain = false,
                isBack = false
            )

            Ids.homeFragment -> setTitle(
                activity.getString(Str.dashboard),
                isMain = true,
                isBack = false
            )

            Ids.postDetailFragment -> setTitle(
                activity.getString(Str.post_detail),
                isMain = false,
                isBack = true
            )
        }
    }

    private fun setTitle(title: String?, isBack: Boolean = false, isMain: Boolean) =
        with(binding.header) {
            ivBack.isVisible = isBack
            if (isMain) {
                ivLogout.show()
            } else ivLogout.hide()

            tvTitle.text = title
        }
}

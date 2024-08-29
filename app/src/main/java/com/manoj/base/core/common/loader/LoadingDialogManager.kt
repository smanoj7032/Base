package com.manoj.base.core.common.loader

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.manoj.base.R
import com.manoj.base.core.utils.extension.Lyt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadingDialogManager @Inject constructor() : DefaultLifecycleObserver {

    private var loadingDialog: Dialog? = null

    fun showLoadingDialog(activity: Activity) {
        if (loadingDialog == null) {
            val builder = MaterialAlertDialogBuilder(activity, R.style.MaterialDialog)
            val dialogView = LayoutInflater.from(activity).inflate(Lyt.dialog_loading, null)
            builder.setView(dialogView)
            builder.setCancelable(false)
            loadingDialog = builder.create().apply {
                setOnShowListener {
                    window?.setLayout(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
            }
        }
        loadingDialog?.show()
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    fun handleLoading(activity: Activity,show:Boolean){
        if (show) showLoadingDialog(activity)
        else hideLoadingDialog()
    }
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        hideLoadingDialog()
    }
}

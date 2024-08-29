package com.manoj.base.core.utils.extension

import android.app.Activity
import com.manoj.base.core.common.basedialogs.AbstractDialog
import com.manoj.base.core.common.basedialogs.BottomSheetMaterialDialog
import com.manoj.base.core.common.basedialogs.MaterialDialog
import com.manoj.base.core.common.basedialogs.interfaces.DialogInterface
import com.manoj.base.core.common.basedialogs.model.DialogButton
import com.manoj.base.core.common.basedialogs.model.TextAlignment

fun Activity.logoutDialog(onAction: () -> Unit): MaterialDialog {
    return MaterialDialog.Builder(this)
        .setTitle(getString(Str.logout_title), TextAlignment.START)
        .setMessage(
            getString(Str.are_you_sure_want_to_logout),
            TextAlignment.START
        )
        .setCancelable(false)
        .setPositiveButton(
            DialogButton(getString(Str.logout),
                Drw.ic_delete,
                object : AbstractDialog.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {
                        onAction.invoke()
                        dialogInterface.dismiss()
                    }
                })
        )
        .setNegativeButton(
            DialogButton(
                getString(Str.cancel),
                Drw.ic_close,
                object : AbstractDialog.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, which: Int) {
                        dialogInterface.dismiss()
                    }
                })
        )
        .build()
}


fun Activity.logoutSheet(onAction: () -> Unit): BottomSheetMaterialDialog {
    return BottomSheetMaterialDialog.Builder(this)
        .setTitle(getString(Str.logout_title), TextAlignment.CENTER)
        .setMessage(getString(Str.are_you_sure_want_to_logout), TextAlignment.CENTER)
        .setCancelable(false)
        .setPositiveButton(
            DialogButton(getString(Str.logout),
                Drw.ic_delete,
                object : AbstractDialog.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {
                        onAction.invoke()
                        dialogInterface.dismiss()
                    }
                })
        )
        .setNegativeButton(
            DialogButton(
                getString(Str.cancel),
                Drw.ic_close,
                object : AbstractDialog.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, which: Int) {
                        dialogInterface.dismiss()
                    }
                })
        )
        .build()
}

fun Activity.deleteDialog(
    onPositiveAction: () -> Unit,
    onNegativeAction: () -> Unit
): MaterialDialog {
    return MaterialDialog.Builder(this)
        .setTitle(getString(Str.delete_title), TextAlignment.START)
        .setMessage(
            getString(Str.are_you_sure_want_to_delete),
            TextAlignment.START
        )
        .setCancelable(false)
        .setPositiveButton(
            DialogButton(getString(Str.delete),
                Drw.ic_delete,
                object : AbstractDialog.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, i: Int) {
                        onPositiveAction.invoke()
                        dialogInterface.dismiss()
                    }
                })
        )
        .setNegativeButton(
            DialogButton(
                getString(Str.cancel),
                Drw.ic_close,
                object : AbstractDialog.OnClickListener {
                    override fun onClick(dialogInterface: DialogInterface, which: Int) {
                        onNegativeAction.invoke()
                        dialogInterface.dismiss()
                    }
                })
        )
        .build()
}
package com.manoj.baseproject.core.common.basedialogs

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.manoj.baseproject.R
import com.manoj.baseproject.core.common.basedialogs.model.DialogButton
import com.manoj.baseproject.core.common.basedialogs.model.DialogMessage
import com.manoj.baseproject.core.common.basedialogs.model.DialogTitle

/**
 * Creates a Material Dialog with 2 buttons.
 *
 * Use [Builder] to create a new instance.
 */
class MaterialDialog private constructor(
    activity: Activity,
    title: DialogTitle,
    message: DialogMessage<String>,
    isCancelable: Boolean,
    positiveButton: DialogButton,
    negativeButton: DialogButton
) : AbstractDialog(activity, title, message, positiveButton, negativeButton,false) {

    init {
        // Init Dialog
        val builder = MaterialAlertDialogBuilder(activity, R.style.MaterialDialog)

        val inflater = activity.layoutInflater
        val dialogView = createView(inflater, null)

        builder.setView(dialogView)
        builder.setCancelable(isCancelable)

        // Create and show dialog
        dialog = builder.create()
    }

    /**
     * Builder for [MaterialDialog].
     */
    class Builder(
        activity: Activity
    ) : AbstractDialog.Builder<MaterialDialog>(activity) {


        override fun build(): MaterialDialog {
            return MaterialDialog(
                activity,
                title ?: throw IllegalArgumentException("Title must not be null"),
                message ?: throw IllegalArgumentException("Message must not be null"),
                isCancelable,
                positiveButton ?: throw IllegalArgumentException("PositiveButton must not be null"),
                negativeButton ?: throw IllegalArgumentException("NegativeButton must not be null"),
            )
        }
    }
}

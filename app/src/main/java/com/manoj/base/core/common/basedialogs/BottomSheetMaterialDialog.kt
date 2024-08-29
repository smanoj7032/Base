package com.manoj.base.core.common.basedialogs

import android.app.Activity
import android.content.Context
import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.manoj.base.core.common.basedialogs.BottomSheetMaterialDialog.Builder
import com.manoj.base.core.common.basedialogs.model.DialogButton
import com.manoj.base.core.common.basedialogs.model.DialogMessage
import com.manoj.base.core.common.basedialogs.model.DialogTitle
import com.manoj.base.core.utils.extension.Dmn

/**
 * Creates a BottomSheet Material Dialog with 2 buttons.
 *
 * Use [Builder] to create a new instance.
 */
class BottomSheetMaterialDialog private constructor(
    activity: Activity,
    title: DialogTitle,
    message: DialogMessage<String>,
    private val isCancelable: Boolean,
    positiveButton: DialogButton,
    negativeButton: DialogButton,
) : AbstractDialog(activity, title, message, positiveButton, negativeButton,true) {

    init {
        // Init Dialog, Create Bottom Sheet Dialog
        dialog = BottomSheetDialog(activity).apply {
            val inflater = activity.layoutInflater
            val dialogView = createView(inflater, null)
            setContentView(dialogView)

            // Set Cancelable property
            setCancelable(isCancelable)

            // Clip AnimationView to round Corners
            dialogView.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val radius = activity.resources.getDimension(Dmn.dimen_16)
                    outline.setRoundRect(0, 0, view.width, view.height + radius.toInt(), radius)
                }
            }
            dialogView.clipToOutline = true

            // Expand Bottom Sheet after showing.
            setOnShowListener { dialog ->
                val bottomSheetDialog = dialog as BottomSheetDialog
                val bottomSheet =
                    bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.let {
                    BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }


    /**
     * Builder for [BottomSheetMaterialDialog].
     */
    class Builder(
        activity: Activity
    ) : AbstractDialog.Builder<BottomSheetMaterialDialog>(activity) {


        override fun build(): BottomSheetMaterialDialog {
            return BottomSheetMaterialDialog(
                activity,
                title ?: throw IllegalArgumentException("Title must not be null"),
                message ?: throw IllegalArgumentException("Message must not be null"),
                isCancelable,
                positiveButton ?: throw IllegalArgumentException("PositiveButton must not be null"),
                negativeButton ?: throw IllegalArgumentException("NegativeButton must not be null")
            )
        }
    }

    private class BottomSheetDialog(context: Context) :
        com.google.android.material.bottomsheet.BottomSheetDialog(
            context,
            com.manoj.base.R.style.BottomSheetDialogTheme
        )
}

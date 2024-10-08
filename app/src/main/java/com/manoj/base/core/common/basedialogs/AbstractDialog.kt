package com.manoj.base.core.common.basedialogs

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.manoj.base.core.common.basedialogs.interfaces.DialogInterface
import com.manoj.base.core.common.basedialogs.interfaces.OnCancelListener
import com.manoj.base.core.common.basedialogs.interfaces.OnDismissListener
import com.manoj.base.core.common.basedialogs.interfaces.OnShowListener
import com.manoj.base.core.common.basedialogs.model.DialogButton
import com.manoj.base.core.common.basedialogs.model.DialogMessage
import com.manoj.base.core.common.basedialogs.model.DialogTitle
import com.manoj.base.core.common.basedialogs.model.TextAlignment
import com.manoj.base.databinding.LayoutAlertDialogBinding

abstract class AbstractDialog(
    private val activity: Activity,
    private val title: DialogTitle,
    private val message: DialogMessage<String>,
    private val positiveButton: DialogButton?,
    private val negativeButton: DialogButton?,
    private val isBottomSheet: Boolean?,
) : DialogInterface {

    companion object {
        const val BUTTON_POSITIVE = 1
        const val BUTTON_NEGATIVE = -1
    }

    var dialog: Dialog? = null

    private var onDismissListener: OnDismissListener? = null
    private var onCancelListener: OnCancelListener? = null
    private var onShowListener: OnShowListener? = null
    private lateinit var binding: LayoutAlertDialogBinding


    protected fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = LayoutAlertDialogBinding.inflate(inflater, container, false)


        // Bind the data
        binding.vTop.title = title.text
        binding.message = message.getText()
        binding.positiveButtonText = positiveButton?.title
        binding.negativeButtonText = negativeButton?.title

        // Handle button visibility
        binding.buttonPositive.isVisible = positiveButton != null
        binding.buttonNegative.isVisible = negativeButton != null
        isBottomSheet?.apply {
            binding.vTop.line1.isVisible = false
            binding.vTop.vTop.isVisible = isBottomSheet
        }

        // Set button click listeners
        positiveButton?.let { btn ->
            binding.buttonPositive.setOnClickListener {
                btn.onClickListener.onClick(this@AbstractDialog, BUTTON_POSITIVE)
            }
        }

        negativeButton?.let { btn ->
            binding.buttonNegative.setOnClickListener {
                btn.onClickListener.onClick(this@AbstractDialog, BUTTON_NEGATIVE)
            }
        }

        // Set button icons if available
        positiveButton?.icon?.let { iconResId ->
            binding.buttonPositive.icon = ContextCompat.getDrawable(activity, iconResId)
        }
        negativeButton?.icon?.let { iconResId ->
            binding.buttonNegative.icon = ContextCompat.getDrawable(activity, iconResId)
        }

        return binding.root
    }


    /**
     * Displays the Dialog
     */
    fun show() {
        dialog?.show() ?: throwNullDialog()
    }

    /**
     * Cancels the Dialog
     */
    override fun cancel() {
        dialog?.cancel() ?: throwNullDialog()
    }

    /**
     * Dismisses the Dialog
     */
    override fun dismiss() {
        dialog?.dismiss() ?: throwNullDialog()
    }

    /**
     * @param onShowListener interface for callback events when dialog is shown.
     */
    fun setOnShowListener(onShowListener: OnShowListener) {
        this.onShowListener = onShowListener
        dialog?.setOnShowListener { showCallback() }
    }

    /**
     * @param onCancelListener interface for callback events when dialog is cancelled.
     */
    fun setOnCancelListener(onCancelListener: OnCancelListener) {
        this.onCancelListener = onCancelListener
        dialog?.setOnCancelListener { cancelCallback() }
    }

    /**
     * @param onDismissListener interface for callback events when dialog is dismissed.
     */
    fun setOnDismissListener(onDismissListener: OnDismissListener) {
        this.onDismissListener = onDismissListener
        dialog?.setOnDismissListener { dismissCallback() }
    }


    private fun showCallback() {
        onShowListener?.onShow(this)
    }

    private fun dismissCallback() {
        onDismissListener?.onDismiss(this)
    }

    private fun cancelCallback() {
        onCancelListener?.onCancel(this)
    }

    private fun throwNullDialog() {
        throw NullPointerException("Called method on null Dialog. Create dialog using `Builder` before calling on Dialog")
    }

    interface OnClickListener {
        fun onClick(dialogInterface: DialogInterface, which: Int)
    }

    /**
     * Builder for [AbstractDialog].
     */
    abstract class Builder<D : AbstractDialog>(val activity: Activity) {
        protected var title: DialogTitle? = null
        protected var message: DialogMessage<String>? = null
        protected var isCancelable = false
        protected var positiveButton: DialogButton? = null
        protected var negativeButton: DialogButton? = null

        /**
         * @param title Sets the Title of Material Dialog with the default alignment as center.
         * @return this, for chaining.
         */

        fun setTitle(title: String): Builder<D> {
            return setTitle(title, TextAlignment.CENTER)
        }

        /**
         * @param title     Sets the Title of Material Dialog.
         * @param alignment Sets the Alignment for the title.
         * @return this, for chaining.
         */

        fun setTitle(title: String, alignment: TextAlignment): Builder<D> {
            this.title = DialogTitle(title, alignment)
            return this
        }

        /**
         * @param message Sets the plain text Message of Material Dialog with the default alignment as center.
         * @return this, for chaining.
         */

        fun setMessage(message: String): Builder<D> {
            return setMessage(message, TextAlignment.CENTER)
        }

        /**
         * @param message     Sets the Message of Material Dialog.
         * @param alignment   Sets the Alignment for the message.
         * @return this, for chaining.
         */

        fun setMessage(message: String, alignment: TextAlignment): Builder<D> {
            this.message = DialogMessage.text(message, alignment)
            return this
        }

        /**
         * @param positiveButton Sets the positive button of the Dialog.
         * @return this, for chaining.
         */

        fun setPositiveButton(positiveButton: DialogButton): Builder<D> {
            this.positiveButton = positiveButton
            return this
        }

        /**
         * @param negativeButton Sets the negative button of the Dialog.
         * @return this, for chaining.
         */

        fun setNegativeButton(negativeButton: DialogButton): Builder<D> {
            this.negativeButton = negativeButton
            return this
        }

        /**
         * @param cancelable Sets whether the dialog is cancelable.
         * @return this, for chaining.
         */

        fun setCancelable(cancelable: Boolean): Builder<D> {
            this.isCancelable = cancelable
            return this
        }

        /**
         * Build and return the dialog instance.
         * @return the dialog instance.
         */

        abstract fun build(): D
    }
}

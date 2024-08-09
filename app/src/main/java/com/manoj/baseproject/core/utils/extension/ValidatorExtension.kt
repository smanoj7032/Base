package com.manoj.baseproject.core.utils.extension

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.EditText

fun EditText.isValidEmail(): Boolean {
    return this.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this.text).matches()
}

fun String.isEmpty(): Boolean {
    return (TextUtils.isEmpty(this)
            || this.equals("", ignoreCase = true)
            || this.equals("{}", ignoreCase = true)
            || this.equals("null", ignoreCase = true)
            || this.equals("undefined", ignoreCase = true))
}


fun String.isValidEmail(): Boolean {
    val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    return !this.isEmpty() && this.matches(emailPattern)
}

fun String.isValidPassword(minLength: Int = 8, requireSpecialChar: Boolean = true): Boolean {
    if (this.isEmpty()) return false
    if (this.length < minLength) return false
    if (requireSpecialChar && !this.contains(Regex("[!@#\$%^&*(),.?\":{}|<>]"))) return false
    if (!this.contains(Regex("[0-9]"))) return false
    return true
}

fun EditText.isValidPassword(minLength: Int = 8, requireSpecialChar: Boolean = true): Boolean {
    return this.text.toString().isValidPassword(minLength, requireSpecialChar)
}

fun validationPair(
    validation: (String) -> Boolean,
    errorMessage: String
): Pair<(String) -> Boolean, String> {
    return Pair(validation, errorMessage)
}

fun String.validate(
    context: Context,
    validations: List<Pair<(String) -> Boolean, String>>
): Boolean {
    val editTextValue = this
    validations.firstOrNull { (validation, errorMessage) ->
        !validation(editTextValue).also {
            if (!it) context.showErrorToast(errorMessage)
        }
    }?.let { return false }
    return true
}

fun View.showToast(message: String) {
    this.context.showErrorToast(message)
}
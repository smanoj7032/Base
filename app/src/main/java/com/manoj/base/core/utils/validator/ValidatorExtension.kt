package com.manoj.base.core.utils.validator

import android.text.TextUtils
import com.google.android.material.textfield.TextInputLayout
import com.manoj.base.core.network.helper.Constants
import com.manoj.base.core.utils.extension.queryListener


/**
 * Sets up validation for multiple input fields.
 *
 * @param fields A list of pairs, where each pair has a TextInputLayout and a validation function.
 *               The validation function takes the text the user types in (a String) and checks it.
 *               If the function finds a problem, it returns an error message (a String).
 *               If there's no problem, it returns null (meaning no error).
 */
fun setupFieldValidations(vararg fields: Pair<TextInputLayout, (String) -> String?>) {
    /**Loop through each provided field and its corresponding validation function.*/
    for ((field, validation) in fields) {
        /**Set up a query listener on the editText inside the TextInputLayout.*/
        field.editText?.queryListener { inputText ->
            /**Apply the validation function to the input text and set the resulting error message.*/
            field.error = validation(inputText)
        }
    }
}


fun String.isEmpty(): Boolean {
    return (TextUtils.isEmpty(this)
            || this.equals("", ignoreCase = true)
            || this.equals("{}", ignoreCase = true)
            || this.equals("null", ignoreCase = true)
            || this.equals("undefined", ignoreCase = true))
}

fun validateFields(vararg fields: TextInputLayout): Boolean {
    var isValid = true
    for (field in fields) {
        val inputText = field.editText?.text.toString()
        val error = field.error
        if (error != null || inputText.isEmpty()) {
            field.error = error ?: Constants.EMPTY_FIELD
            isValid = false
        }
    }
    return isValid
}

fun String.isValidEmail(): String? {
    return when {
        this.isEmpty() -> Constants.EMAIL_EMPTY
        !android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches() -> Constants.EMAIL_INVALID

        else -> null
    }
}

fun String.isValidPassword(): String? {
    return when {
        this.isEmpty() -> Constants.PASSWORD_EMPTY
        !this.containsSpecialChar() -> Constants.PASSWORD_SPECIAL_CHAR
        !this.containsDigit() -> Constants.PASSWORD_DIGIT
        !this.containsUpperCase() -> Constants.PASSWORD_UPPER_CASE
        this.length < 8 -> Constants.PASSWORD_LENGTH
        else -> null
    }
}

fun String.containsUpperCase(): Boolean {
    return this.any { it.isUpperCase() }
}

fun String.containsSpecialChar(): Boolean {
    return this.any { !it.isLetterOrDigit() }
}

fun String.containsDigit(): Boolean {
    return this.any { it.isDigit() }
}
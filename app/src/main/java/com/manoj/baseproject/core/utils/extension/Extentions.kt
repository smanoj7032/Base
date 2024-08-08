package com.manoj.baseproject.core.utils.extension

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.manoj.baseproject.R
import com.manoj.baseproject.core.common.toast.CustomToastStyle
import com.manoj.baseproject.data.bean.PlaceDetails
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

typealias Str = R.string
typealias Ids = R.id
typealias Clr = R.color
typealias Dmn = R.dimen
typealias Lyt = R.layout
typealias Drw = R.drawable
typealias Anm = R.anim

fun SharedPreferences.saveValue(key: String, value: Any?) {
    when (value) {
        is String? -> editNdCommit { it.putString(key, value) }
        is Int -> editNdCommit { it.putInt(key, value) }
        is Boolean -> editNdCommit { it.putBoolean(key, value) }
        is Float -> editNdCommit { it.putFloat(key, value) }
        is Long -> editNdCommit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

fun <T> SharedPreferences.getValue(key: String, defaultValue: Any? = null): T? {
    return when (defaultValue) {
        is String? -> {
            getString(key, defaultValue as? String) as? T
        }

        is Int -> {
            getInt(key, defaultValue as? Int ?: -1) as? T
        }

        is Boolean -> getBoolean(key, defaultValue as? Boolean ?: false) as? T
        is Float -> getFloat(key, defaultValue as? Float ?: -1f) as? T
        is Long -> getLong(key, defaultValue as? Long ?: -1) as? T
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

inline fun SharedPreferences.editNdCommit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}

fun Activity.hideKeyboard() {
    val manager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun Activity.showKeyboard() {
    val manager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.toggleSoftInputFromWindow(
        this.currentFocus?.applicationWindowToken, InputMethodManager.SHOW_FORCED, 0
    )
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Activity.showSuccessToast(message: String) {
    showToast(CustomToastStyle.SUCCESS, message)
}

fun Context.showErrorToast(errorMessage: String?) = errorMessage?.let {
    showToast(CustomToastStyle.ERROR, errorMessage)
}

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also {
        it.view.setBackgroundColor(ContextCompat.getColor(this.context, Clr.black))
        it.show()
    }
}

fun <T> Result<T>.log() {
    Log.i("Result", this.toString())
}

fun RecyclerView.setLinearLayoutManger() {
    this.layoutManager = LinearLayoutManager(this.context)
}

fun Fragment.showSheet(sheet: BottomSheetDialogFragment) {
    sheet.show(this.childFragmentManager, sheet.tag)
}

fun FragmentActivity.showSheet(sheet: BottomSheetDialogFragment) {
    sheet.show(this.supportFragmentManager, sheet.tag)
}

fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }

    return name
}

fun Resources.dptoPx(dp: Int): Float {
    return dp * this.displayMetrics.density
}

fun <T> Activity.startNewActivity(s: Class<T>, killCurrent: Boolean = false) {
    val intent = Intent(this, s)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    startActivity(intent)
    if (killCurrent) finish()
}

fun <T> Activity.getNewIntent(s: Class<T>): Intent {
    val intent = Intent(this, s)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    return intent
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Any.toJson(): String {
    val gson = Gson()
    return gson.toJson(this)
}

fun Long.convertLongToTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    return format.format(date)
}

fun <M, T> Map<M, T>.toRequestBody(): RequestBody {
    val jsonString = Gson().toJson(this)
    return jsonString.toRequestBody("application/json".toMediaType())
}

fun Long.convertLongToDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(date)
}

fun Long.formatTime(): String {
    val hours = this / 3600
    val remainingSeconds = this % 3600
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60

    return when {
        hours > 0 -> "${String.format("%02d:%02d:%02d", hours, minutes, seconds)} h"
        minutes > 0 -> "${String.format("%02d:%02d", minutes, seconds)} min"
        else -> "${seconds}s"
    }
}

fun Context.getDrawables(@DrawableRes resId: Int): Drawable? {
    return ContextCompat.getDrawable(this, resId)
}

fun getPlaceDetails(context: Context, placeId: String?, callback: (PlaceDetails?) -> Unit) {
    val placesClient: PlacesClient = Places.createClient(context)

    val placeFields = listOf(
        Place.Field.ADDRESS_COMPONENTS,
        Place.Field.NAME
    )
    val request = placeId?.let { FetchPlaceRequest.newInstance(it, placeFields) }

    if (request != null) {
        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            val addressComponents = place.addressComponents

            var country: String? = null
            var city: String? = null
            var state: String? = null
            var zip: String? = null

            addressComponents?.asList()?.forEach { component ->
                when {
                    component.types.contains("country") -> country = component.name
                    component.types.contains("locality") -> city = component.name
                    component.types.contains("administrative_area_level_1") -> state =
                        component.name

                    component.types.contains("postal_code") -> zip = component.name
                }
            }

            val landmark: String? = place.name

            callback(PlaceDetails(country, city, state, zip, landmark))
        }.addOnFailureListener {
            callback(null)
        }
    }
}


fun FragmentActivity?.replaceFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true,
    isAnimate: Boolean = true
) {
    this?.supportFragmentManager?.let { fragmentManager ->
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (isAnimate) {
            fragmentTransaction.setCustomAnimations(
                Anm.enter_from_right,
                Anm.exit_to_left,
                Anm.enter_from_left,
                Anm.exit_to_right
            )
        }
        fragmentTransaction.replace(containerId, fragment)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }
}


inline fun <T> T?.checkNull(actionIfNull: () -> Unit, actionIfNotNull: (T) -> Unit) {
    if (this != null) actionIfNotNull(this)
    else actionIfNull()
}

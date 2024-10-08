package com.manoj.base.core.utils.extension

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.manoj.base.R
import com.manoj.base.data.bean.PlaceDetails
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



fun <T> Result<T>.log() {
    Log.i("Result", this.toString())
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



val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

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

fun <Data> Data.toJson(): String {
    val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    return gson.toJson(this)
}
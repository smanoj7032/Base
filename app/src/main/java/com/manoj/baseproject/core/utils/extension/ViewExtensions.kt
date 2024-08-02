package com.manoj.baseproject.core.utils.extension

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.manoj.baseproject.R
import com.manoj.baseproject.core.utils.SingleClickListener
import com.manoj.baseproject.data.bean.PlaceDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


/**
 * Use this extension to show the view.
 * The view visibility will be changed to [View.VISIBLE]
 * @see [View.setVisibility]
 * **/


fun View.show() {
    this.visibility = View.VISIBLE
    val fadeIn = AlphaAnimation(0.0f, 1.0f).apply {
        duration = 300
        fillAfter = true
    }
    this.startAnimation(fadeIn)
}


/**
 * Use this extension to hide the view.
 * The view visibility will be changed to [View.GONE]
 * @see [View.setVisibility]
 * **/
fun View.hide() {
    val fadeOut = AlphaAnimation(1f, 0f).apply {
        duration = 300
        fillAfter = true
    }
    this.startAnimation(fadeOut)
    fadeOut.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}

        override fun onAnimationEnd(animation: Animation?) {
            this@hide.visibility = View.GONE
        }

        override fun onAnimationRepeat(animation: Animation?) {}
    })
}

/**
 * Launches a new coroutine and repeats [block] every time the View's viewLifecycleOwner
 * is in and out of [lifecycleState].
 */
inline fun AppCompatActivity.launchAndRepeatWithViewLifecycle(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(lifecycleState) {
            block()
        }
    }
}

/**
 * Launches a new coroutine and repeats [block] every time the View's viewLifecycleOwner
 * is in and out of [lifecycleState].
 */
inline fun Fragment.launchAndRepeatWithViewLifecycle(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            block()
        }
    }
}


fun covertTimeAgoToText(dataString: String?): String {
    if (dataString == null) {
        return ""
    }

    val suffix = "ago"

    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val pasTime = dateFormat.parse(dataString)

        val nowTime = Date()
        val dateDiff = nowTime.time - (pasTime?.time ?: 0L)
        if (dateDiff < 0) {
            return ""  // Handle the case where the parsed time is in the future
        }

        val seconds = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
        val hours = TimeUnit.MILLISECONDS.toHours(dateDiff)
        val days = TimeUnit.MILLISECONDS.toDays(dateDiff).toDouble()

        return when {
            seconds < 60 -> "$seconds seconds $suffix"
            minutes < 60 -> "$minutes minutes $suffix"
            hours < 24 -> "$hours hours $suffix"
            days < 7 -> "$days days $suffix"
            else -> {
                val weeks = days / 7
                if (weeks < 4) {
                    "$weeks weeks $suffix"
                } else {
                    val months = weeks / 4
                    if (months < 12) {
                        "$months months $suffix"
                    } else {
                        val years = months / 12
                        "$years years $suffix"
                    }
                }
            }
        }
    } catch (e: ParseException) {
        e.printStackTrace()
        "((day / 360) * 10.0).roundToInt() / 10.0"
        return ""
    }
}

/**
 *
 * Returns FirstVisibleItemPosition
 *
 */
fun RecyclerView.findFirstVisibleItemPosition(): Int {
    if (layoutManager is LinearLayoutManager) {
        return (layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
    }
    return if (layoutManager is StaggeredGridLayoutManager) {
        val mItemPositionsHolder =
            IntArray((layoutManager as StaggeredGridLayoutManager?)!!.spanCount)
        return min(
            (layoutManager as StaggeredGridLayoutManager?)!!.findFirstVisibleItemPositions(
                mItemPositionsHolder
            )
        )
    } else -1
}


/**
 *
 * Returns the min value in an array.
 *
 * @param array  an array, must not be null or empty
 * @return the min value in the array
 */
private fun min(array: IntArray): Int {

    // Finds and returns max
    var min: Int = array[0]
    for (j in 1 until array.size) {
        if (array[j] < min) {
            min = array[j]
        }
    }
    return min
}


/**
 *
 * Returns true if recyclerView isAtTop
 *
 */
fun RecyclerView.isAtTop(): Boolean {
    val pos: Int =
        (layoutManager as LinearLayoutManager?)?.findFirstCompletelyVisibleItemPosition()!!
    return pos == 0
}


@SuppressLint("DiscouragedApi")
fun Context.getResource(name: String): Drawable? {
    val resID = this.resources.getIdentifier(name, "drawable", this.packageName)
    return ActivityCompat.getDrawable(this, resID)
}

private fun getStoragePermission(): Array<String>? {
    return if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        null
    } else arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}

fun getLocationPermissions(): Array<String> {
    return arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
    )

}

@RequiresApi(Build.VERSION_CODES.Q)
fun getBackgroundLocationPermission(): Array<String> {
    return arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
}

val PERMISSION_READ_STORAGE = getStoragePermission()


inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}

inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
}

fun isSdkVersionGreaterThan(version: Int): Boolean {
    return Build.VERSION.SDK_INT > version
}

fun isSdkVersionGreaterThanOrEqualTo(version: Int): Boolean {
    return Build.VERSION.SDK_INT >= version
}

fun isSdkVersionLessThan(version: Int): Boolean {
    return Build.VERSION.SDK_INT < version
}

fun isSdkVersionLessThanOrEqualTo(version: Int): Boolean {
    return Build.VERSION.SDK_INT <= version
}

fun isSdkVersionEqualTo(version: Int): Boolean {
    return Build.VERSION.SDK_INT == version
}

fun TextView.setClickableText(
    fullText: String, clickableText: Map<String, () -> Unit>, clickableTextColor: Int
) {
    val spannableString = SpannableString(fullText)

    clickableText.forEach { (text, onClick) ->
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClick()
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(context, clickableTextColor)
                ds.isUnderlineText = false
            }
        }

        val start = fullText.indexOf(text)
        val end = start + text.length

        if (start != -1) {
            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    this.text = spannableString
    this.movementMethod = LinkMovementMethod.getInstance()
}

fun Context.initializePlaces(apiKey: String) {
    if (!Places.isInitialized()) {
        Places.initialize(this, apiKey)
    }
}

fun AppCompatActivity.openPlaceSearchBox() {
    val fields = listOf(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.LAT_LNG,
        Place.Field.ADDRESS,
        Place.Field.ADDRESS_COMPONENTS
    )

    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this)
    startActivityForResult(intent, 1)
}

fun AppCompatActivity.handlePlaceSearchResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent,
    onPlaceSelected: (Place) -> Unit,
    callback: (PlaceDetails?) -> Unit
) {
    if (requestCode == 1) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val place = data.let { Autocomplete.getPlaceFromIntent(it) }
                onPlaceSelected(place)
                getPlaceDetails(this, place.id, callback)
            }

            AutocompleteActivity.RESULT_ERROR -> {
                val status = data.let { Autocomplete.getStatusFromIntent(it) }
                Log.i("PlacesAPI", status.toString())
            }
        }
    }
}

fun EditText.getEditText(): String {
    return this.text.toString().trim()
}

fun EditText.isValidEmail(): Boolean {
    return this.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this.text).matches()
}

fun EditText.findEmails(success: (String?, Array<String>?) -> Unit) {
    val emailList = this.text.split(";").map { it.trim() }
    val validEmails = emailList.filter { it.isValidEmail() }
    if (this.text.isNotEmpty()) {
        if (validEmails.size == emailList.size) {
            success(null, validEmails.toTypedArray())
        } else {
            val invalidEmails = emailList.filterNot { it.isValidEmail() }
            success("Invalid emails found: ${invalidEmails.joinToString(", ")}", null)
        }
    } else {
        success("Please enter emails.", null)
    }
}

fun String?.isValidEmail(): Boolean {
    return this?.isNotEmpty() == true && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}


fun <T> Spinner.setSpinnerItems(
    items: List<T>,
    context: Context,
    textColor: Int,
    textSize: Float = 14f,
    typefacePath: String = "nimbus_reg.ttf",
    itemToString: (T) -> String,
    customItem: String,
    onItemSelected: ((position: Int, item: T) -> Unit)? = null
) {
    var isInitial = true
    val allItems = listOf(customItem) + items.map(itemToString)
    val adapter =
        object : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, allItems) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int, convertView: View?, parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                } else {
                    view.setTextColor(ContextCompat.getColor(context, textColor))
                }
                view.typeface = Typeface.createFromAsset(context.assets, typefacePath)
                return view
            }
        }

    adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
    this.adapter = adapter

    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?, view: View?, position: Int, id: Long
        ) {
            if (position != -1) {
                try {
                    val textView = parent?.getChildAt(0) as? TextView
                    textView?.let {
                        it.setTextColor(ContextCompat.getColor(context, textColor))
                        it.typeface = Typeface.createFromAsset(context.assets, typefacePath)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (position != 0 && !isInitial) {
                    val selectedItem = items[position - 1]
                    onItemSelected?.invoke(position - 1, selectedItem)
                }
                isInitial = false
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}

fun View.setSingleClickListener(listener: (v: View) -> Unit) {
    this.setOnClickListener(object : SingleClickListener() {
        override fun onClickView(v: View) {
            listener(v)
        }
    })
}

fun AppCompatActivity.requestPermission(
    vararg permissions: String,
    onPermissionGranted: () -> Unit,
    onPermissionsDenied: ((List<String>) -> Unit)? = null
) {
    val notGrantedPermission = permissions.filter {
        this.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
    }
    if (notGrantedPermission.isEmpty()) {
        onPermissionGranted()
    } else {
        val requestMultiplePermissionsLauncher = this.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsResult: Map<String, Boolean> ->
            val deniedPermissions = permissionsResult.filterValues { !it }.keys.toList()
            if (deniedPermissions.isEmpty()) {
                onPermissionGranted()
            } else {
                onPermissionsDenied?.invoke(deniedPermissions)
            }
        }
        requestMultiplePermissionsLauncher.launch(notGrantedPermission.toTypedArray())
    }
}

fun ImageView.loadImage(uri: Uri?) {
    Glide.with(this.context).load(uri).circleCrop()
        .placeholder(R.drawable.ic_image).error(R.drawable.ic_image)
        .into(this)
}

fun Context.displayDialog(
    title: String?,
    message: String?,
    onPositiveClick: () -> Unit
) {

    val dialogBuilder = android.app.AlertDialog.Builder(this)
    dialogBuilder.setTitle(title)
    dialogBuilder.setMessage(message)
        .setCancelable(false)
        .setPositiveButton("Yes") { _, _ ->
            onPositiveClick()
        }
        .setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
    val alert = dialogBuilder.create()
    alert.show()

}
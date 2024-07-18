package com.manoj.baseproject.utils.picker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.manoj.baseproject.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

typealias Str = R.string
typealias Ids = R.id
typealias Clr = R.color
typealias Dmn = R.dimen
typealias Lyt = R.layout
typealias Drw = R.drawable
typealias Anm = R.anim

internal fun Uri.realPath(context: Context): Uri {
    val result: String
    val cursor = context.contentResolver.query(this, null, null, null, null)

    if (cursor == null) {
        result = this.path.toString()
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        result = cursor.getString(idx)
        cursor.close()
    }
    return Uri.parse(result)
}

internal fun Bitmap.toUri(context: Context, title: String): Uri {
    val bytes = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        context.contentResolver, this,
        title, context.getString(Str.app_name)
    )
    return Uri.parse(path)
}

infix fun ViewGroup.inflate(@LayoutRes lyt: Int): View {
    return LayoutInflater.from(context).inflate(lyt, this, false)
}



infix fun Bitmap.rotate(degree: Int): Bitmap {
    val w = width
    val h = height

    val mtx = Matrix()
    mtx.postRotate(degree.toFloat())

    return Bitmap.createBitmap(this, 0, 0, w, h, mtx, true)
}


infix fun ImageView.set(@DrawableRes id: Int) {
    setImageResource(id)
}

infix fun ImageView.set(bitmap: Bitmap) {
    setImageBitmap(bitmap)
}

infix fun ImageView.set(drawable: Drawable) {
    setImageDrawable(drawable)
}

infix fun ImageView.set(ic: Icon) {
    setImageIcon(ic)
}

infix fun ImageView.set(uri: Uri) {
    setImageURI(uri)
}

infix fun TextView.set(@StringRes id: Int) {
    setText(id)
}

infix fun TextView.set(text: String) {
    setText(text)
}
fun WebView.setup(url: String, configure: (WebSettings.() -> Unit)? = null) {
    this.webViewClient = WebViewClient()
    this.settings.javaScriptEnabled = true
    configure?.let {
        this.settings.apply(it)
    }
    this.loadUrl(url)
}
 fun Uri.getFilePathFromUri(context: Context): String? {
    val cursor = context.contentResolver.query(this, null, null, null, null)
    return cursor?.use {
        if (it.moveToFirst()) {
            val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val fileName = it.getString(idx)
            val file = File(context.cacheDir, fileName)
            val inputStream = context.contentResolver.openInputStream(this)
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.path
        } else {
            null
        }
    }
}

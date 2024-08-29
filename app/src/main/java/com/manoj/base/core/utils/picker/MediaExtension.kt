package com.manoj.base.core.utils.picker

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.LayoutRes
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.manoj.base.core.utils.Logger
import com.manoj.base.core.utils.extension.Str
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getBitmapFromUri(context: Context, imageUri: Uri): Bitmap? {
    try {
        val source = ImageDecoder.createSource(context.contentResolver, imageUri);
        return ImageDecoder.decodeBitmap(source);
    } catch (e: Exception) {
        e.printStackTrace();
    }
    return null
}

/**
 * get file name from uri
 * @param context context
 * @param uri uri
 * @return file name
 * @throws IOException
 */
fun getMakeFile(context: Context, suffix: String): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        imageFileName,  /* prefix */
        suffix,  /* suffix */
        storageDir /* directory */
    )
}

fun File.getUriFromFile(context: Context): Uri {
    return FileProvider.getUriForFile(
        context,
        context.packageName + context.getString(Str.provider_authority_suffix),
        this
    )
}

fun scaleBitmap(inputBitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    var newWidth = inputBitmap.width
    var newHeight = inputBitmap.height
    // Calculate the aspect ratios to maintain the aspect ratio of the original bitmap
    if (newWidth > maxWidth || newHeight > maxHeight) {
        val aspectRatio = newWidth.toFloat() / newHeight.toFloat()
        if (aspectRatio > 1) {
            newWidth = maxWidth
            newHeight = (newWidth / aspectRatio).toInt()
        } else {
            newHeight = maxHeight
            newWidth = (newHeight * aspectRatio).toInt()
        }
    }
    // Scale the bitmap
    return Bitmap.createScaledBitmap(inputBitmap, newWidth, newHeight, true)
}

/**
 * share bitmap to other apps
 * @param context context to start activity
 * @param bitmap bitmap to share
 */
fun shareBitmapToOtherApps(context: Context, bitmap: Bitmap): String? {
    try {
        val name = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US).format(Date())
        val cachePath = File(context.externalCacheDir, "images")
        cachePath.mkdirs()
        val stream = FileOutputStream("$cachePath/$name.png")
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
        val imagePath = File(context.externalCacheDir, "images")
        val newFile = File(imagePath, "$name.png")
        val contentUri = FileProvider.getUriForFile(
            context,
            context.packageName + context.getString(Str.provider_authority_suffix),
            newFile
        )
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        context.startActivity(Intent.createChooser(shareIntent, "Share"))
        return null
    } catch (e: Exception) {
        return e.message
    }
}

/**
 * save bitmap to file
 * @param context context to start activity
 * @param bitmap bitmap to save
 */
fun saveBitmap(
    context: Context,
    bitmap: Bitmap,
    quality: Int = 100,
    onError: ((Exception) -> Unit)? = null
): File? {
    try {
        val name = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US).format(Date())
        val cachePath = File(context.externalCacheDir, "images")
        cachePath.mkdirs()
        val stream = FileOutputStream("$cachePath/$name.png")
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream)
        stream.close()
        val imagePath = File(context.externalCacheDir, "images")
        return File(imagePath, "$name.png")
    } catch (e: Exception) {
        e.printStackTrace()
        onError?.invoke(e)
    }
    return null
}

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
/**
 * Crops an image using the Google Photos app and returns the URI of the cropped image.
 *
 * This function helps in cropping an image selected by the user. It utilizes the
 * Google Photos app to perform the cropping. The cropped image is saved to a file,
 * and the URI of the cropped image file is returned.
 *
 * @param srcImageUri The URI of the source image to be cropped.
 * @param cropImageLauncher The ActivityResultLauncher to launch the crop intent.
 * @return The URI of the cropped image, or null if the cropping fails.
 *
 * @throws ActivityNotFoundException if the Google Photos app is not found on the device.
 *
 * Usage:
 * 1. Ensure you have an ActivityResultLauncher<Intent> initialized in your fragment or activity.
 * 2. Call this function, passing the URI of the image you want to crop and the ActivityResultLauncher.
 * 3. Handle the result in the ActivityResultCallback provided to the launcher.
 *
 * Example:
 * ```
 * private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
 *     if (result.resultCode == Activity.RESULT_OK) {
 *         val croppedImageUri = result.data?.data
 *         // Handle the cropped image URI here
 *     }
 * }
 *
 * val srcImageUri = ... // Get the source image URI
 * val croppedImageUri = performCrop(srcImageUri, cropImageLauncher)
 * ```
 */
fun FragmentActivity.performCrop(
    srcImageUri: Uri?,
    cropImageLauncher: ActivityResultLauncher<Intent>
): Uri? {
    var croppedImageUri: Uri? = null
    try {
        val cropIntent = Intent("com.android.camera.action.CROP")
        cropIntent.setDataAndType(srcImageUri, "image/*")
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        /*cropIntent.setPackage("com.google.android.apps.photos")*/

        val croppedImageFile = getMakeFile(this, ".png")
        croppedImageUri = Uri.fromFile(croppedImageFile)
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri)
        cropImageLauncher.launch(cropIntent)
    } catch (e: ActivityNotFoundException) {
        Logger.e("TAG", "performCrop error $e")
    }
    return croppedImageUri
}

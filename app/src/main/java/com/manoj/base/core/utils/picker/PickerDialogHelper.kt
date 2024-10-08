package com.manoj.base.core.utils.picker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.manoj.base.BR
import com.manoj.base.core.common.adapter.BaseAdapter
import com.manoj.base.core.common.adapter.CallBackModel
import com.manoj.base.core.common.adapter.Callbacks
import com.manoj.base.core.common.adapter.RecyclerItemTouchHelper
import com.manoj.base.core.common.basedialogs.BaseBottomSheetDialog
import com.manoj.base.core.utils.extension.Drw
import com.manoj.base.core.utils.extension.Ids
import com.manoj.base.core.utils.extension.Lyt
import com.manoj.base.core.utils.extension.PERMISSION_READ_STORAGE
import com.manoj.base.core.utils.extension.Str
import com.manoj.base.core.utils.extension.checkNull
import com.manoj.base.core.utils.extension.set
import com.manoj.base.core.utils.permissionutils.runWithPermissions
import com.manoj.base.databinding.DialogPickerBinding
import com.manoj.base.databinding.ItemPickerGridBinding
import java.io.File

class PickerDialogHelper(
    resultCaller: ActivityResultCaller,
    isMultiple: Boolean? = null,
    private var context: Context,
    private var items: ArrayList<MediaModel>,
   private val onPickerClosed: ((MediaType, Uri?, List<Uri>?) -> Unit)? = null
) {

    private var pickerDialog: BaseBottomSheetDialog<DialogPickerBinding>? = null
    private var pickerAdapter: BaseAdapter<ItemPickerGridBinding, MediaModel>? = null

    private val REQUEST_PICK_PHOTO = 1102
    private val REQUEST_VIDEO = 1103
    private val REQUEST_PICK_FILE = 1104


    private var uri: Uri? = null
    private var fileName = ""
    private var resultCaller: ActivityResultCaller? = resultCaller

    /** ACTIVITY RESULT LAUNCHER */
    private lateinit var takePhoto: ActivityResultLauncher<Uri>
    private lateinit var chooseImage: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var chooseVideo: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var recordVideo: ActivityResultLauncher<Uri>
    private lateinit var selectFile: ActivityResultLauncher<Array<String>>


    init {
        setupPickerDialog()
        if (isMultiple == false) setLauncher() else setLauncherForMultiple()
    }

    private fun setupPickerDialog() {
        val clickListener = Callbacks<ItemPickerGridBinding, MediaModel>()
        clickListener.add(CallBackModel(Ids.cvItem) { model, position, binding ->
            when (model.type) {
                MediaType.TAKE_PICTURE -> openCamera()

                MediaType.CHOOSE_IMAGE -> openGallery()

                MediaType.RECORD_VIDEO -> openVideoCamera()

                MediaType.CHOOSE_VIDEO -> openVideoGallery()

                MediaType.SELECT_FILES -> openFilePicker()
            }
        })
        pickerDialog = BaseBottomSheetDialog(context, Lyt.dialog_picker, onBind = { binding ->
            with(binding) {
                pickerAdapter = BaseAdapter(
                    Lyt.item_picker_grid,
                    BR.bean,
                    callbacks = clickListener, onBind = { binding, bean, position ->
                        initIcon(bean, binding)
                        initLabel(bean, binding)
                    }
                )

                val layoutManager = GridLayoutManager(context, 3)
                pickerItems.layoutManager = layoutManager
                pickerItems.adapter = pickerAdapter
                pickerAdapter?.list = items
                val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(pickerAdapter))
                itemTouchHelper.attachToRecyclerView(this.pickerItems)
            }
        })
    }


    private fun initIcon(item: MediaModel, icon: ItemPickerGridBinding) {
        if (item.itemIcon == 0) {
            icon.icon set when (item.type) {
                MediaType.CHOOSE_IMAGE -> Drw.ic_image
                MediaType.RECORD_VIDEO -> Drw.ic_videocam
                MediaType.CHOOSE_VIDEO -> Drw.ic_video_library
                MediaType.SELECT_FILES -> Drw.ic_file
                else -> Drw.ic_camera
            }
        } else {
            icon.icon set item.itemIcon
        }
    }

    private fun initLabel(item: MediaModel, label: ItemPickerGridBinding) {
        if (item.itemLabel == "") {
            label.label set when (item.type) {
                MediaType.CHOOSE_IMAGE -> Str.gallery
                MediaType.RECORD_VIDEO -> Str.video
                MediaType.CHOOSE_VIDEO -> Str.vgallery
                MediaType.SELECT_FILES -> Str.file
                else -> Str.photo
            }
        } else {
            label.label set item.itemLabel
        }
    }

    private fun openCamera() = context.runWithPermissions(
        Manifest.permission.CAMERA
    ) {
        uri = getMakeFile(context, ".png").getUriFromFile(context)
        uri?.let { takePhoto.launch(it) }
    }

    private fun openGallery() {
        PERMISSION_READ_STORAGE.checkNull(actionIfNull = {
            chooseImage.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }, actionIfNotNull = {
            context.runWithPermissions(*it) {
                chooseImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        })
    }

    private fun openImageGallery() {
        PERMISSION_READ_STORAGE?.let {
            context.runWithPermissions(*it) {
                chooseImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            }
        }
    }

    private fun openVideoCamera() = context.runWithPermissions(Manifest.permission.CAMERA) {
        fileName = (System.currentTimeMillis() / 1000).toString() + ".mp4"
        val videoFile = File(context.cacheDir, fileName)
        uri = videoFile.getUriFromFile(context)
        uri?.let { recordVideo.launch(it) }
    }

    private fun openVideoGallery() {
        PERMISSION_READ_STORAGE.checkNull(
            actionIfNull = {
                val pickVideo = Intent(Intent.ACTION_PICK)
                pickVideo.type = "video/*"
                chooseVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            },
            actionIfNotNull = {
                context.runWithPermissions(*it) {
                    val pickVideo = Intent(Intent.ACTION_PICK)
                    pickVideo.type = "video/*"
                    chooseVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                }
            })
    }

    private fun openFilePicker() {
        PERMISSION_READ_STORAGE.checkNull(
            actionIfNull = {
                val pickFile = Intent(Intent.ACTION_GET_CONTENT)
                pickFile.type = "application/pdf"
                selectFile.launch(arrayOf("application/pdf"))
            }, actionIfNotNull = {
                context.runWithPermissions(*it) {
                    val pickFile = Intent(Intent.ACTION_GET_CONTENT)
                    pickFile.type = "application/pdf"
                    selectFile.launch(arrayOf("application/pdf"))
                }
            }
        )
    }

    private fun onActivityResult(requestCode: Int, resultCode: ActivityResult) {
        if (resultCode.resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                REQUEST_PICK_PHOTO -> pickPhoto(resultCode.data)
                REQUEST_VIDEO -> pickVideo(resultCode.data)
                REQUEST_PICK_FILE -> pickFile(resultCode.data)
            }
        }
    }

    private fun pickPhoto(data: Intent?) {
        if (data == null) {
            return
        }
        val uri = data.data ?: return
        onPickerClosed?.invoke(MediaType.CHOOSE_IMAGE, uri, null)
        pickerDialog?.dismiss()
    }

    private fun pickVideo(data: Intent?) {
        if (data == null) {
            return
        }
        val uri = data.data ?: return
        onPickerClosed?.invoke(MediaType.CHOOSE_VIDEO, uri, null)
        pickerDialog?.dismiss()
    }

    private fun pickFile(data: Intent?) {
        if (data == null) {
            return
        }
        val uri = data.data ?: return
        onPickerClosed?.invoke(MediaType.SELECT_FILES, uri, null)
        pickerDialog?.dismiss()
    }

    fun show() {
        pickerDialog?.show()
    }

    private fun setLauncher() {
        resultCaller?.let {
            takePhoto =
                it.registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
                    if (isSaved) {
                        onPickerClosed?.invoke(MediaType.TAKE_PICTURE, uri, null)
                        pickerDialog?.dismiss()
                    }
                }
            chooseImage =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    uri.let { tempUri ->
                        onPickerClosed?.invoke(MediaType.CHOOSE_IMAGE, uri, null)
                        pickerDialog?.dismiss()

                    }
                }
            recordVideo =
                it.registerForActivityResult(ActivityResultContracts.CaptureVideo()) { result ->
                    if (result) {
                        onPickerClosed?.invoke(MediaType.RECORD_VIDEO, uri, null)
                        pickerDialog?.dismiss()
                    }
                }
            chooseVideo =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { result ->
                    onPickerClosed?.invoke(MediaType.CHOOSE_VIDEO, result, null)
                    pickerDialog?.dismiss()
                }
            selectFile =
                it.registerForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
                    onPickerClosed?.invoke(MediaType.SELECT_FILES, result, null)
                    pickerDialog?.dismiss()
                }
        }
    }

    private fun setLauncherForMultiple() {
        resultCaller?.let {
            takePhoto =
                it.registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
                    if (isSaved) {
                        onPickerClosed?.invoke(MediaType.TAKE_PICTURE, uri, null)
                        pickerDialog?.dismiss()
                    }
                }
            chooseImage =
                it.registerForActivityResult(
                    ActivityResultContracts.PickMultipleVisualMedia(
                        5
                    )
                ) { uri ->
                    uri.let { tempUri ->
                        onPickerClosed?.invoke(MediaType.CHOOSE_IMAGE, null, tempUri)
                        pickerDialog?.dismiss()

                    }
                }
            recordVideo =
                it.registerForActivityResult(ActivityResultContracts.CaptureVideo()) { result ->
                    if (result) {
                        onPickerClosed?.invoke(MediaType.RECORD_VIDEO, uri, null)
                        pickerDialog?.dismiss()
                    }
                }
            chooseVideo =
                it.registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { result ->
                    onPickerClosed?.invoke(MediaType.CHOOSE_VIDEO, null, result)
                    pickerDialog?.dismiss()
                }
            selectFile =
                it.registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { result ->
                    onPickerClosed?.invoke(MediaType.SELECT_FILES, null, result)
                    pickerDialog?.dismiss()
                }
        }
    }

    fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
}

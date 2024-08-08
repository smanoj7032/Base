package com.manoj.baseproject.core.utils.picker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.manoj.baseproject.BR
import com.manoj.baseproject.R
import com.manoj.baseproject.core.common.adapter.BaseAdapter
import com.manoj.baseproject.core.common.adapter.CallBackModel
import com.manoj.baseproject.core.common.adapter.Callbacks
import com.manoj.baseproject.core.common.adapter.RecyclerItemTouchHelper
import com.manoj.baseproject.core.common.basedialogs.BaseBottomSheetDialog
import com.manoj.baseproject.core.network.helper.SystemVariables
import com.manoj.baseproject.core.utils.extension.Drw
import com.manoj.baseproject.core.utils.extension.PERMISSION_READ_STORAGE
import com.manoj.baseproject.core.utils.extension.Str
import com.manoj.baseproject.core.utils.extension.checkNull
import com.manoj.baseproject.core.utils.extension.set
import com.manoj.baseproject.core.utils.permissionutils.runWithPermissions
import com.manoj.baseproject.databinding.DialogPickerBinding
import com.manoj.baseproject.databinding.ItemPickerGridBinding
import java.io.File

class PickerDialogHelper(
    resultCaller: ActivityResultCaller,
    isMultiple: Boolean? = null,
    private var context: Context,
    private var items: ArrayList<ItemModel>
) {

    private var pickerDialog: BaseBottomSheetDialog<DialogPickerBinding>? = null
    private var pickerAdapter: BaseAdapter<ItemPickerGridBinding, ItemModel>? = null

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
        val clickListener = Callbacks<ItemPickerGridBinding, ItemModel>()
        clickListener.add(CallBackModel(R.id.cvItem) { model, position, binding ->
            when (model.type) {
                ItemType.ITEM_CAMERA -> openCamera()

                ItemType.ITEM_GALLERY -> openGallery()

                ItemType.ITEM_VIDEO -> openVideoCamera()

                ItemType.ITEM_VIDEO_GALLERY -> openVideoGallery()

                ItemType.ITEM_FILES -> openFilePicker()
            }
        })
        pickerDialog = BaseBottomSheetDialog(context, R.layout.dialog_picker, onBind = { binding ->
            with(binding) {
                pickerAdapter = BaseAdapter(
                    R.layout.item_picker_grid,
                    BR.bean,
                    callbacks = clickListener, onBind = { binding, bean ->
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


    private fun initIcon(item: ItemModel, icon: ItemPickerGridBinding) {
        if (item.itemIcon == 0) {
            icon.icon set when (item.type) {
                ItemType.ITEM_GALLERY -> Drw.ic_image
                ItemType.ITEM_VIDEO -> Drw.ic_videocam
                ItemType.ITEM_VIDEO_GALLERY -> Drw.ic_video_library
                ItemType.ITEM_FILES -> Drw.ic_file
                else -> Drw.ic_camera
            }
        } else {
            icon.icon set item.itemIcon
        }
    }

    private fun initLabel(item: ItemModel, label: ItemPickerGridBinding) {
        if (item.itemLabel == "") {
            label.label set when (item.type) {
                ItemType.ITEM_GALLERY -> Str.gallery
                ItemType.ITEM_VIDEO -> Str.video
                ItemType.ITEM_VIDEO_GALLERY -> Str.vgallery
                ItemType.ITEM_FILES -> Str.file
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
        SystemVariables.onPickerClosed(ItemType.ITEM_GALLERY, uri, null)
        pickerDialog?.dismiss()
    }

    private fun pickVideo(data: Intent?) {
        if (data == null) {
            return
        }
        val uri = data.data ?: return
        SystemVariables.onPickerClosed(ItemType.ITEM_VIDEO_GALLERY, uri, null)
        pickerDialog?.dismiss()
    }

    private fun pickFile(data: Intent?) {
        if (data == null) {
            return
        }
        val uri = data.data ?: return
        SystemVariables.onPickerClosed(ItemType.ITEM_FILES, uri, null)
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
                        SystemVariables.onPickerClosed(ItemType.ITEM_CAMERA, uri, null)
                        pickerDialog?.dismiss()
                    }
                }
            chooseImage =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    uri.let { tempUri ->
                        SystemVariables.onPickerClosed(ItemType.ITEM_GALLERY, uri, null)
                        pickerDialog?.dismiss()

                    }
                }
            recordVideo =
                it.registerForActivityResult(ActivityResultContracts.CaptureVideo()) { result ->
                    if (result) {
                        SystemVariables.onPickerClosed(ItemType.ITEM_VIDEO, uri, null)
                        pickerDialog?.dismiss()
                    }
                }
            chooseVideo =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { result ->
                    SystemVariables.onPickerClosed(ItemType.ITEM_VIDEO_GALLERY, result, null)
                    pickerDialog?.dismiss()
                }
            selectFile =
                it.registerForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
                    SystemVariables.onPickerClosed(ItemType.ITEM_FILES, result, null)
                    pickerDialog?.dismiss()
                }
        }
    }

    private fun setLauncherForMultiple() {
        resultCaller?.let {
            takePhoto =
                it.registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
                    if (isSaved) {
                        SystemVariables.onPickerClosed(ItemType.ITEM_CAMERA, uri, null)
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
                        SystemVariables.onPickerClosed(ItemType.ITEM_GALLERY, null, tempUri)
                        pickerDialog?.dismiss()

                    }
                }
            recordVideo =
                it.registerForActivityResult(ActivityResultContracts.CaptureVideo()) { result ->
                    if (result) {
                        SystemVariables.onPickerClosed(ItemType.ITEM_VIDEO, uri, null)
                        pickerDialog?.dismiss()
                    }
                }
            chooseVideo =
                it.registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { result ->
                    SystemVariables.onPickerClosed(ItemType.ITEM_VIDEO_GALLERY, null, result)
                    pickerDialog?.dismiss()
                }
            selectFile =
                it.registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { result ->
                    SystemVariables.onPickerClosed(ItemType.ITEM_FILES, null, result)
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

package com.manoj.baseproject.presentation.common.mediapicker

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.manoj.baseproject.utils.picker.MediaPickerListener

object MediaPickerHelper {

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var pickMultipleMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var listener: MediaPickerListener? = null
    private var resultCaller: ActivityResultCaller? = null


    fun setListener(listener: MediaPickerListener) {
        this.listener = listener
    }

    fun initialize(resultCaller: ActivityResultCaller) {
        this.resultCaller = resultCaller
        registerPickMedia()
        registerPickMultipleMedia()
    }

    private fun registerPickMedia() {
        resultCaller?.let {
            pickMedia =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    if (uri != null) listener?.onMediaPicked(uri)
                    else listener?.onMediaPicked(null)
                }
        }
    }

    private fun registerPickMultipleMedia() {
        resultCaller?.let {
            pickMultipleMedia =
                it.registerForActivityResult(
                    ActivityResultContracts.PickMultipleVisualMedia(
                        5
                    )
                ) { uris ->
                    if (uris.isNotEmpty()) listener?.onMultipleMediaPicked(uris)
                    else listener?.onMultipleMediaPicked(null)
                }
        }
    }

    fun launchImagePicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun launchVideoPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
    }

    fun launchImageAndVideoPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

    fun launchImageGifPicker() {
        val mimeType = "image/gif"
        pickMedia.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.SingleMimeType(
                    mimeType
                )
            )
        )
    }
}


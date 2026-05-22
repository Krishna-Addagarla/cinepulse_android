package com.partner.cinepulse.utils

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

// ── upload result ─────────────────────────────────────────────────────────

sealed class UploadResult {
    object Idle : UploadResult()
    object Uploading : UploadResult()
    data class Success(val url: String) : UploadResult()
    data class Failure(val msg: String) : UploadResult()
}

// ── per-slot state ────────────────────────────────────────────────────────

class ImagePickerState {

    var uri: Uri? by mutableStateOf(null)

    var uploadResult: UploadResult by mutableStateOf(
        UploadResult.Idle
    )

    val isUploading get() = uploadResult is UploadResult.Uploading

    val uploadedUrl
        get() = (uploadResult as? UploadResult.Success)?.url

    val uploadError
        get() = (uploadResult as? UploadResult.Failure)?.msg

    val hasImage get() = uri != null

    fun reset() {
        uri = null
        uploadResult = UploadResult.Idle
    }
}

// ── helper ────────────────────────────────────────────────────────────────

class ImagePickerHelper(
    private val context: Context,
    val state: ImagePickerState,
    private val _launch: () -> Unit,
    private val _upload: suspend (MultipartBody.Part) -> Result<String>,
    private val scope: CoroutineScope
) {

    fun launch() = _launch()

    fun clear() = state.reset()

    fun upload() {

        val uri = state.uri ?: return

        scope.launch {

            state.uploadResult = UploadResult.Uploading

            try {

                val multipart = uriToMultipart(uri)

                _upload(multipart).fold(
                    onSuccess = { url ->
                        state.uploadResult =
                            UploadResult.Success(url)
                    },
                    onFailure = { err ->
                        state.uploadResult =
                            UploadResult.Failure(
                                err.message ?: "Upload failed"
                            )
                    }
                )

            } catch (e: Exception) {

                state.uploadResult =
                    UploadResult.Failure(
                        e.message ?: "Upload failed"
                    )
            }
        }
    }

    private fun uriToMultipart(
        uri: Uri
    ): MultipartBody.Part {

        val inputStream =
            context.contentResolver.openInputStream(uri)

        val file = File.createTempFile(
            "upload",
            ".jpg",
            context.cacheDir
        )

        file.outputStream().use { output ->
            inputStream?.copyTo(output)
        }

        val requestBody = file.asRequestBody(
            "image/*".toMediaTypeOrNull()
        )

        return MultipartBody.Part.createFormData(
            name = "image",
            filename = file.name,
            body = requestBody
        )
    }
}

// ── composable helper ─────────────────────────────────────────────────────

@Composable
fun rememberImagePicker(
    onUpload: suspend (MultipartBody.Part) -> Result<String>
): ImagePickerHelper {

    val context = LocalContext.current

    val pickerState = remember {
        ImagePickerState()
    }

    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {

            pickerState.uri = uri

            pickerState.uploadResult =
                UploadResult.Idle
        }
    }

    val latestOnUpload by rememberUpdatedState(onUpload)

    return remember(pickerState, scope) {

        ImagePickerHelper(
            context = context,
            state = pickerState,
            _launch = {
                launcher.launch("image/*")
            },
            _upload = { multipart ->
                latestOnUpload(multipart)
            },
            scope = scope
        )
    }
}
package com.partner.cinepulse.utils
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// ── upload result ─────────────────────────────────────────────────────────
sealed class UploadResult {
    object Idle                          : UploadResult()
    object Uploading                     : UploadResult()
    data class Success(val url: String)  : UploadResult()
    data class Failure(val msg: String)  : UploadResult()
}

// ── per-slot state (observable via Compose state) ─────────────────────────
class ImagePickerState {
    var uri          : Uri?          by mutableStateOf(null)
    var uploadResult : UploadResult  by mutableStateOf(UploadResult.Idle)

    val isUploading  get() = uploadResult is UploadResult.Uploading
    val uploadedUrl  get() = (uploadResult as? UploadResult.Success)?.url
    val uploadError  get() = (uploadResult as? UploadResult.Failure)?.msg
    val hasImage     get() = uri != null

    fun reset() {
        uri          = null
        uploadResult = UploadResult.Idle
    }
}

// ── helper exposed to UI ──────────────────────────────────────────────────
class ImagePickerHelper(
    val  state  : ImagePickerState,
    private val _launch : () -> Unit,
    private val _upload : suspend (Uri) -> Result<String>,
    private val scope   : CoroutineScope
) {
    fun launch() = _launch()

    fun clear() = state.reset()

    /** Uploads [state.uri] if present; no-op otherwise. */
    fun upload() {
        val uri = state.uri ?: return
        scope.launch {
            state.uploadResult = UploadResult.Uploading
            _upload(uri).fold(
                onSuccess = { url -> state.uploadResult = UploadResult.Success(url) },
                onFailure = { err -> state.uploadResult = UploadResult.Failure(err.message ?: "Upload failed") }
            )
        }
    }
}

/**
 * Creates and remembers an [ImagePickerHelper] tied to this composable's lifecycle.
 *
 * @param onUpload  Suspend function that uploads the chosen [Uri] and returns
 *                  a [Result<String>] (the remote URL on success).
 *                  Defaults to a no-op stub so callers without upload support compile fine.
 *
 * Example:
 * ```kotlin
 * val profilePicker = rememberImagePicker { uri ->
 *     myRepository.uploadImage(uri)   // returns Result<String>
 * }
 *
 * // Open gallery
 * profilePicker.launch()
 *
 * // Trigger upload after pick
 * profilePicker.upload()
 *
 * // Read state
 * profilePicker.state.uri
 * profilePicker.state.uploadedUrl
 * profilePicker.state.isUploading
 * profilePicker.state.uploadError
 * ```
 */
@Composable
fun rememberImagePicker(
    onUpload: suspend (Uri) -> Result<String> = { Result.success("") }
): ImagePickerHelper {

    val pickerState = remember { ImagePickerState() }
    val scope       = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            pickerState.uri          = uri
            pickerState.uploadResult = UploadResult.Idle   // reset on new pick
        }
    }

    // capture latest onUpload lambda without recreating helper
    val latestOnUpload by rememberUpdatedState(onUpload)

    return remember(pickerState, scope) {
        ImagePickerHelper(
            state   = pickerState,
            _launch = { launcher.launch("image/*") },
            _upload = { uri -> latestOnUpload(uri) },
            scope   = scope
        )
    }
}
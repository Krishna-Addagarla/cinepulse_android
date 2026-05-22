package com.partner.cinepulse.data.repository

import com.partner.cinepulse.data.remote.models.imageUploadResponse
import com.partner.cinepulse.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface helperRepository {

    suspend fun uploadImage( image : MultipartBody.Part) : Flow<Resource<imageUploadResponse>>
}
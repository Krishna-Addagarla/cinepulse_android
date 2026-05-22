package com.partner.cinepulse.data.remote.apis

import com.partner.cinepulse.data.remote.models.imageUploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Part

interface helperApiService {

    @POST("/upload/image")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<imageUploadResponse>

}
package com.partner.cinepulse.data.repositoryImpl

import com.partner.cinepulse.data.remote.apis.helperApiService
import com.partner.cinepulse.data.remote.models.imageUploadResponse
import com.partner.cinepulse.data.repository.helperRepository
import com.partner.cinepulse.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class HelperRepositoryImpl @Inject constructor(
    val helperApiService: helperApiService
) : helperRepository {

    override suspend fun uploadImage(image: MultipartBody.Part): Flow<Resource<imageUploadResponse>> = flow{
        try {
            emit(Resource.Loading())
            val response = helperApiService.uploadImage(image)

            if (response.isSuccessful && response.body() != null){
                emit(Resource.Success(response.body()!!))
            }else{
                emit(Resource.Error(response.message()))
            }
        }catch (e: HttpException){
            emit(Resource.Error(e.message()))
        }catch (e: IOException){
            emit(Resource.Error("Network Error : ${e.message}"))
        }catch (e: Exception){
            emit(Resource.Error("Unexpected Error : ${e.message}"))
        }
    }
}
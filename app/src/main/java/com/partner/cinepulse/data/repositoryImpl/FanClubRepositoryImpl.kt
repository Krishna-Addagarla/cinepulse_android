package com.partner.cinepulse.data.repositoryImpl

import com.partner.cinepulse.data.remote.apis.fanClubApiService
import com.partner.cinepulse.data.remote.models.FanClubResponse
import com.partner.cinepulse.data.repository.FanClubRepository
import com.partner.cinepulse.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class FanClubRepositoryImpl @Inject constructor(
    val fanClubApiService: fanClubApiService
) : FanClubRepository {

    override suspend fun getUserFanClubs(): Flow<Resource<List<FanClubResponse>>> = flow {
        try {
            emit(Resource.Loading())
            val response = fanClubApiService.getUserFanClubs()
            if (response.isSuccessful && response.body()!=null){
                emit(Resource.Success(response.body()!!))
            }else{
                emit(Resource.Error(response.message()?:" Failed to Fetch Your FanClubs"))
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
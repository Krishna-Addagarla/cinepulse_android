package com.partner.cinepulse.data.repositoryImpl

import com.partner.cinepulse.data.remote.apis.fanClubApiService
import com.partner.cinepulse.data.remote.models.FanClubResponse
import com.partner.cinepulse.data.repository.FanClubRepository
import com.partner.cinepulse.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FanClubRepositoryImpl @Inject constructor(
    val fanClubApiService: fanClubApiService
) : FanClubRepository {

    override suspend fun getFanClubs(): Flow<Resource<List<FanClubResponse>>> {
        TODO("Not yet implemented")
    }
}
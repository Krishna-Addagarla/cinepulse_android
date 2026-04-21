package com.partner.cinepulse.data.repository

import com.partner.cinepulse.data.remote.models.FanClubResponse
import com.partner.cinepulse.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FanClubRepository {

    suspend fun getUserFanClubs () : Flow<Resource<List<FanClubResponse>>>
}
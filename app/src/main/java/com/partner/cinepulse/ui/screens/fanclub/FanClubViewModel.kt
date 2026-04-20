package com.partner.cinepulse.ui.screens.fanclub

import androidx.lifecycle.ViewModel
import com.partner.cinepulse.data.repository.FanClubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FanClubViewModel @Inject constructor(
    private val fanClubRepository: FanClubRepository
): ViewModel(){

}

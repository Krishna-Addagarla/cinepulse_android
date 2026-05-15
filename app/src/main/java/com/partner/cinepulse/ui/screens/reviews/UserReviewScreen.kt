package com.partner.cinepulse.ui.screens.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.partner.cinepulse.ui.screens.movieinfo.UserReview
import com.partner.cinepulse.ui.theme.BgDark
import com.partner.cinepulse.ui.theme.TextPrimary

@Composable
fun UserReviewScreen(
    onBackClick:()-> Unit,
    movieId : Int
){
    UserReviewContent(onBackClick)
}

@Composable
fun UserReviewContent(
    onBackClick:()-> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ){
        Box(
            modifier = Modifier
                .padding(top = 48.dp, start = 14.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint               = TextPrimary,
                modifier           = Modifier.size(20.dp)
            )
        }


    }

}

@Composable
fun TopSection(){

}

@Composable
@Preview(showBackground = true)
fun UserReviewPreview(){
    UserReviewContent(onBackClick ={})
}


package com.partner.cinepulse.ui.screens.userInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.partner.cinepulse.ui.components.TopBar
import com.partner.cinepulse.ui.theme.BgDark

@Composable
fun UserInfoScreen(
    onLogout :() -> Unit,
    onBackClick : () -> Unit
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        TopBar(
            title = "Profile",
            showBackButton = true,
            showNotificationIcon = false,
            showProfileIcon = false,
            onBackClick = {}
        )

        Column (
            modifier = Modifier.fillMaxSize()
        ){
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.size(128.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFF1C2333), CircleShape)
                , contentAlignment = Alignment.Center
            ){
                AsyncImage(
                    model = "",
                    contentDescription = "User Profile",
                    modifier = Modifier.fillMaxSize(),
                    alignment = Alignment.Center
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                modifier = Modifier.fillMaxWidth()
                    .padding(50.dp),
                onClick = onLogout,
                colors = ButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.Red,
                    disabledContainerColor = Color.DarkGray,
                    disabledContentColor = Color.DarkGray
                )
            ) {
                Text(text = "LogOut",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold)
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun userInfoScreenPreview(){
    UserInfoScreen(
        onLogout = {},
        onBackClick = {}
    )
}

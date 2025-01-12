package com.example.pawfect

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.appinterface.R

@Preview
@Composable
fun PreviewFriendListScreen() {
    FriendListScreen(rememberNavController())
}

@Composable
fun FriendListScreen(navController: NavHostController) {

    val friendsList = Database.getUserFriends(0)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF4F8)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable( indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                        { navController.navigate("profile_screen") }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Your matches",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // FriendList
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(friendsList) { friendId ->
                    MatchCard(friendId = friendId) {
                        navController.navigate("plan_activity_screen/${friendId}")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun MatchCard(friendId: Int, onClick: () -> Unit) {

    val friend = Database.getUserById(friendId)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFFFD1DC), shape = RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile image
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color = Color.LightGray, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = friend.profileImage),
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp).clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Name and status
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = friend.dogName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = friend.statusText,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

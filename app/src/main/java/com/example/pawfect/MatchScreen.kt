package com.example.pawfect

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun PreviewMatchScreen() {
    MatchScreen(rememberNavController())
}

@Composable
fun MatchScreen(navController: NavHostController) {

    val userData = mutableListOf<User>()

    Database.getUserFriends(0).forEach{ friendId ->
        userData.add(Database.getUserById(friendId))
    }

    var currentUserIndex by remember { mutableStateOf(0) }

    // Manage animation offset
    var offsetY by remember { mutableStateOf(0f) }
    val animatedOffsetY by animateFloatAsState(targetValue = offsetY)

    var overlayColor by remember { mutableStateOf(Color.Transparent) }
    val animatedColor by animateColorAsState(targetValue = overlayColor)

    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF4F8)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
        ) {
            // Top navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                        { navController.navigate("profile_screen") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated user card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .offset(y = animatedOffsetY.dp),
                contentAlignment = Alignment.Center
            ) {
                val currentUser = userData[currentUserIndex]
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = currentUser.profileImage),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(350.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = currentUser.dogName,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(
                                color = Color(0xFFFFD1DC),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .fillMaxWidth(0.8f)
                    ) {
                        Text(
                            text = currentUser.statusText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(46.dp))

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Dislike Button
                Icon(
                    painter = painterResource(id = R.drawable.ic_cross_match),
                    contentDescription = "Dislike",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable( indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                        {

                            coroutineScope.launch {
                                overlayColor = Color(0x80FF0000)

                                offsetY = 300f
                                delay(300)
                                currentUserIndex =
                                    (currentUserIndex + 1) % userData.size
                                offsetY = -300f
                                delay(100)
                                offsetY = 0f

                                overlayColor = Color.Transparent
                            }
                        }
                )

                // Like button
                Icon(
                    painter = painterResource(id = R.drawable.ic_heart_match),
                    contentDescription = "Like",
                    tint = Color.Green,
                    modifier = Modifier
                        .size(80.dp)
                        .clickable( indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                        {
                            overlayColor = Color(0x8032CD32)

                            coroutineScope.launch {
                                offsetY = 300f
                                delay(300)
                                currentUserIndex =
                                    (currentUserIndex + 1) % userData.size
                                offsetY = -300f
                                delay(100)
                                offsetY = 0f

                                overlayColor = Color.Transparent
                            }
                        }
                )
            }
        }

        // Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedColor)
        )
    }
}

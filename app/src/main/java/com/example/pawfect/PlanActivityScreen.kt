package com.example.pawfect

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
fun PreviewPlanActivityScreen() {
    PlanActivityScreen(rememberNavController(), 1)
}


@Composable
fun PlanActivityScreen(navController: NavHostController, friendId: Int) {

    val friend = Database.getUserById(friendId)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF4F8)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Top bar with back button
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
                        { navController.navigate("friend_list_screen") }
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Profile images and names
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                // Profile 1 (Logged user)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.frenchbuldog),
                        contentDescription = "Appa",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = "Appa",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(0.dp))

                // Profile 2 (Friend user)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = friend.profileImage),
                        contentDescription = friend.dogName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = friend.dogName,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Matched text
            Text(
                text = "Matched!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Plan your actions text
            Text(
                text = "Plan your actions with ${friend.dogName}!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF007A) // Pink color
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
            ) {
                // Location button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.map),
                        contentDescription = "Location",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable( indication = null,
                                interactionSource = remember { MutableInteractionSource() })
                            { navController.navigate("profile_screen")}
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Message button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.envelope),
                        contentDescription = "Message",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable( indication = null,
                                interactionSource = remember { MutableInteractionSource() })
                            {
                                navController.navigate("chat_screen/${friend.id}")
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
            ) {
                // Baby cart button
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.baby_cart),
                        contentDescription = "Stroller",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable( indication = null,
                                interactionSource = remember { MutableInteractionSource() })
                            { navController.navigate("common_puppies_screen") }
                    )
                }
            }
        }
    }
}

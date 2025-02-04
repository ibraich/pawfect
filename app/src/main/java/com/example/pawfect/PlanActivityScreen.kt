package com.example.pawfect

import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.appinterface.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore

@Preview
@Composable
fun PreviewPlanActivityScreen() {
    PlanActivityScreen(rememberNavController(), "friendUserId") // Replace with a sample ID
}

@Composable
fun PlanActivityScreen(navController: NavHostController, friendId: String) {
    val firestore = Firebase.firestore
    val auth = Firebase.auth
    val currentUser = remember { mutableStateOf<UserFetch?>(null) }
    val friendUser = remember { mutableStateOf<UserFetch?>(null) }
    val currentUserUid = auth.currentUser?.uid

    // Fetch current user and friend user data
    LaunchedEffect(Unit) {

        if (currentUserUid != null) {
            // Fetch current user
            firestore.collection("Users").document(currentUserUid)
                .get()
                .addOnSuccessListener { document ->
                    currentUser.value = document.toObject(UserFetch::class.java)?.copy(id = currentUserUid)
                }
                .addOnFailureListener { exception ->
                    Log.e("PlanActivityScreen", "Error fetching current user", exception)
                }

            // Fetch friend user
            firestore.collection("Users").document(friendId)
                .get()
                .addOnSuccessListener { document ->
                    friendUser.value = document.toObject(UserFetch::class.java)?.copy(id = friendId)
                }
                .addOnFailureListener { exception ->
                    Log.e("PlanActivityScreen", "Error fetching friend user", exception)
                }
        }
    }

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
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            navController.navigate("friend_list_screen")
                        }
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Profile images and names
            if (currentUser.value != null && friendUser.value != null) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    // Profile 1 (Logged user)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .background(color = Color.LightGray, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(ImageProcessor.decodeBase64ToBitmap(currentUser.value!!.dogProfileImage))
                                    .crossfade(true)
                                    .build(),
                                error = painterResource(R.drawable.image_not_found_icon),
                                contentDescription = "Profile Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(350.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Text(
                            text = currentUser.value?.dogName ?: "Your Dog",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Profile 2 (Friend user)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .background(color = Color.LightGray, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(ImageProcessor.decodeBase64ToBitmap(friendUser.value!!.dogProfileImage))
                                    .crossfade(true)
                                    .build(),
                                error = painterResource(R.drawable.image_not_found_icon),
                                contentDescription = "Profile Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(350.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Text(
                            text = friendUser.value?.dogName ?: "Friend's Dog",
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
                    text = "Plan your actions with ${friendUser.value?.dogName}!",
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
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    navController.navigate("gemini_wait_screen")
                                }
                        )
                    }

                    // Message button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.envelope),
                            contentDescription = "Message",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    navController.navigate("chat_screen/$friendId")
                                }
                        )
                    }

                    // Common puppies button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baby_cart),
                            contentDescription = "Stroller",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    navController.navigate("common_puppies_screen/$currentUserUid/$friendId")
                                }
                        )
                    }
                }
            } else {
                // Loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Loading...", fontSize = 20.sp, color = Color.Gray)
                }
            }
        }
    }
}

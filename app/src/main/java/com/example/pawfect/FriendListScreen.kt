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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
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
fun PreviewFriendListScreen() {
    FriendListScreen(rememberNavController())
}

@Composable
fun FriendListScreen(navController: NavHostController) {
    val firestore = Firebase.firestore
    val auth = Firebase.auth
    val matchesList = remember { mutableStateListOf<UserFetch>() } // Store matched users

    // Fetch matches from Firestore
    LaunchedEffect(Unit) {
        val currentUserUid = auth.currentUser?.uid
        Log.d("FriendListScreen", "Current User UID: $currentUserUid")

        if (currentUserUid != null) {
            // Query Matches where current user is user1 or user2
            firestore.collection("Matches")
                .whereEqualTo("user1", currentUserUid) // Check if current user is user1
                .get()
                .addOnSuccessListener { user1Matches ->
                    firestore.collection("Matches")
                        .whereEqualTo("user2", currentUserUid) // Check if current user is user2
                        .get()
                        .addOnSuccessListener { user2Matches ->
                            // Combine both results
                            val allMatches = user1Matches.documents + user2Matches.documents
                            val matchedUserIds = allMatches.mapNotNull { document ->
                                val user1 = document.getString("user1")
                                val user2 = document.getString("user2")
                                when (currentUserUid) {
                                    user1 -> user2 // If current user is user1, return user2
                                    user2 -> user1 // If current user is user2, return user1
                                    else -> null
                                }
                            }

                            Log.d("FriendListScreen", "Matched User IDs: $matchedUserIds")

                            // Fetch details of matched users
                            firestore.collection("Users").get()
                                .addOnSuccessListener { userDocuments ->
                                    val matchedUsers = userDocuments.mapNotNull { userDocument ->
                                        if (userDocument.id in matchedUserIds) {
                                            userDocument.toObject(UserFetch::class.java).copy(id = userDocument.id)
                                        } else null
                                    }

                                    matchesList.clear()
                                    matchesList.addAll(matchedUsers)
                                    Log.d("FriendListScreen", "Matched Users Retrieved: $matchedUsers")
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("FriendListScreen", "Error fetching matched users", exception)
                                }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("FriendListScreen", "Error fetching matches as user2", exception)
                        }
                }
                .addOnFailureListener { exception ->
                    Log.e("FriendListScreen", "Error fetching matches as user1", exception)
                }

        } else {
            Log.e("FriendListScreen", "No logged-in user found")
        }
    }

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
                        .clickable(indication = null,
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

            // Matches List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(matchesList) { match ->
                    MatchCard(match = match) {
                        navController.navigate("plan_activity_screen/${match.id}") // Pass match.id as a string
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun MatchCard(match: UserFetch, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFFFD1DC), shape = RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile placeholder
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color = Color.LightGray, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(ImageProcessor.decodeBase64ToBitmap(match.dogProfileImage))
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

        Spacer(modifier = Modifier.width(16.dp))

        // Name and status
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = match.dogName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = match.addInfo.ifEmpty { "No additional info" }, // Fallback if info is empty
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

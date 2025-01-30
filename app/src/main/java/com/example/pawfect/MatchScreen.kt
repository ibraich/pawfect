package com.example.pawfect

import android.util.Log
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.ui.text.style.TextAlign
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore


@Preview
@Composable
fun PreviewMatchScreen() {
    MatchScreen(rememberNavController())
}

@Composable
fun MatchScreen(navController: NavHostController) {
    val userData = remember { mutableStateListOf<UserFetch>() }
    val filteredMatches = remember { mutableStateListOf<Pair<UserFetch, Double>>() }
    val firestore = Firebase.firestore
    val auth = Firebase.auth
    val seenUserIds = remember { mutableStateListOf<String>() }
    var swipesRemaining by remember { mutableStateOf(0) }
    var showMatchAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val currentUserUid = auth.currentUser?.uid

        if (currentUserUid != null) {
            // Listen for changes in HaveSeen collection
            firestore.collection("HaveSeen")
                .document(currentUserUid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("MatchScreen", "Error listening to HaveSeen", e)
                        return@addSnapshotListener
                    }

                    val seenIds = snapshot?.get("seen") as? List<String> ?: emptyList()
                    seenUserIds.clear()
                    seenUserIds.addAll(seenIds)

                    // Fetch all users whenever HaveSeen changes
                    fetchUsers(currentUserUid, seenUserIds, firestore, filteredMatches) {
                        swipesRemaining = it
                    }
                }
        } else {
            Log.e("MatchScreen", "No logged-in user found")
        }
    }

    // Display users or loading state
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF4F8) // Background color
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (swipesRemaining > 0 && filteredMatches.isNotEmpty()) {
                DisplayUserCards(
                    userData = filteredMatches.map { it.first },
                    navController = navController,
                    onSwipe = { swipesRemaining-- },
                    onMatchConfirmed = { showMatchAnimation = true } // Trigger animation
                )
            } else {
                val imageLoader = ImageLoader.Builder(LocalContext.current)
                    .components {
                        add(GifDecoder.Factory())
                    }
                    .build()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(color = Color(0xFFFFF4F8), shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Oops! You've swiped through all the paws!\nCheck back later for more doggy matches 🐾",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF4081),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp) // Adjust height to make the GIF taller
                                .clip(RoundedCornerShape(16.dp)) // Ensure rounded corners
                                .background(color = Color(0xFFFFC1CC)), // Optional background color for contrast
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(R.raw.sadodogo) // Replace with your actual GIF file
                                    .build(),
                                contentDescription = "No more matches",
                                imageLoader = imageLoader,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            if (showMatchAnimation) {
                MatchCelebrationAnimation {
                    showMatchAnimation = false // Hide animation after it's done
                }
            }
        }
    }
}

private fun fetchUsers(
    currentUserUid: String,
    seenUserIds: List<String>,
    firestore: FirebaseFirestore,
    filteredMatches: MutableList<Pair<UserFetch, Double>>,
    onFetched: (Int) -> Unit
) {
    firestore.collection("Users").get()
        .addOnSuccessListener { documents ->
            val totalUsers = documents.size()
            val currentUser = documents.firstOrNull { it.id == currentUserUid }
                ?.toObject(UserFetch::class.java)
                ?.copy(id = currentUserUid)

            if (currentUser != null) {
                val candidates = documents.mapNotNull { document ->
                    if (document.id != currentUserUid && document.id !in seenUserIds) {
                        document.toObject(UserFetch::class.java).copy(id = document.id)
                    } else null
                }

                // Use the utility function to filter and sort matches
                val matches = filterAndSortMatches(currentUser, candidates)

                // Update the filteredMatches list
                filteredMatches.clear()
                filteredMatches.addAll(matches)

                // Update swipes remaining
                onFetched(totalUsers - seenUserIds.size)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("MatchScreen", "Error fetching users", exception)
        }
}

@Composable
fun DisplayUserCards(
    userData: List<UserFetch>,
    navController: NavHostController,
    onSwipe: () -> Unit, // Callback to handle swipe action
    onMatchConfirmed: () -> Unit
) {
    var currentUserIndex by remember { mutableStateOf(0) }

    if (currentUserIndex >= userData.size) currentUserIndex = 0
    val currentUser = userData[currentUserIndex]

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF4F8) // Background color matching ProfileScreen
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Image
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .background(color = Color.LightGray, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(ImageProcessor.decodeBase64ToBitmap(currentUser.dogProfileImage))
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

            Spacer(modifier = Modifier.height(16.dp))

            // Dog Name
            Text(
                text = currentUser.dogName, // Dog name
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Dog Info
            Text(
                text = "Age: ${currentUser.dogAge.toSafeInt()}", // Dog age
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Additional Info
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(color = Color(0xFFFFD1DC), shape = RoundedCornerShape(10.dp))
                    .fillMaxWidth(0.8f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentUser.dogBreed, // Dog info
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(46.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Dislike Button
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_cross_match),
                    contentDescription = "Dislike",
                    tint = Color.Red,
                    modifier = Modifier.size(64.dp)
                        .clickable {
                            val currentUserId = Firebase.auth.currentUser?.uid
                            val matchedUserId = userData[currentUserIndex].id
                            if (currentUserId != null && matchedUserId.isNotEmpty()) {
                                addToHaveSeen(currentUserId, matchedUserId)
                                onSwipe()
                                currentUserIndex = (currentUserIndex + 1) % userData.size
                            }
                        }
                )

                // Like Button
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_heart_match),
                    contentDescription = "Like",
                    tint = Color.Green,
                    modifier = Modifier.size(64.dp)
                        .clickable {
                            val currentUserId = Firebase.auth.currentUser?.uid
                            val matchedUserId = userData[currentUserIndex].id
                            if (currentUserId != null && matchedUserId.isNotEmpty()) {
                                handleMatch(currentUserId, matchedUserId, onMatchConfirmed)
                                addToHaveSeen(currentUserId, matchedUserId)
                                onSwipe()
                                currentUserIndex = (currentUserIndex + 1) % userData.size
                            }
                        }
                )
            }
        }
    }
}
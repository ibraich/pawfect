package com.example.pawfect

import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.appinterface.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.getField
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.net.URL

@Preview
@Composable
fun PreviewProfileScreen() {
    ProfileScreen(rememberNavController())
}


@Composable
fun ProfileScreen(navController: NavHostController) {

    var dogName by remember { mutableStateOf("Loading...") }
    var userInfo by remember { mutableStateOf("Fetching user information...") }
    var profileImageBase64 by remember { mutableStateOf<String?>(null) }
    var profilePicUrl by remember { mutableStateOf<String?>(null) }

    val fs = Firebase.firestore
    val auth = Firebase.auth


    LaunchedEffect(Unit) {
        val currentUserUid = auth.currentUser?.uid
        fs.collection("Users").document(currentUserUid.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    dogName = document.getString("dogName") ?: "Unknown Dog"
                    userInfo = document.getString("userInfo") ?: "No additional info"
                    profileImageBase64 = document.getString("dogProfileImage")
                    profilePicUrl = document.getString("profilePicUrl")
                } else {
                    navController.navigate("login_screen")
                }
            }
            .addOnFailureListener {
                dogName = "Error"
                userInfo = "Failed to fetch data"
            }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF4F8)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.menu),
                    contentDescription = "Menu",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                        .clickable ( indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                        { navController.navigate("profile_settings_screen") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile image
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .background(color = Color.LightGray, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (profileImageBase64 != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(ImageProcessor.decodeBase64ToBitmap(profileImageBase64!!))
                            .crossfade(true)
                            .build(),
                        error = painterResource(R.drawable.image_not_found_icon),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(350.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_image),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(350.dp).clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Text(
                text = dogName,
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Description
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(color = Color(0xFFFFD1DC), shape = RoundedCornerShape(10.dp))
                    .fillMaxWidth(0.8f)
            ) {
                Text(
                    text = userInfo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(46.dp))

            // Bottom navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.pet),
                    contentDescription = "Pets",
                    tint = Color.Black,
                    modifier = Modifier.size(45.dp)
                        .clickable( indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                        { navController.navigate("friend_list_screen")}
                )
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.compass),
                    contentDescription = "Place",
                    tint = Color.Black,
                    modifier = Modifier.size(45.dp)
                        .clickable ( indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                        { navController.navigate("routes_screen")}
                )
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.heart),
                    contentDescription = "Favorite",
                    tint = Color.Black,
                    modifier = Modifier.size(45.dp)
                        .clickable( indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                        { navController.navigate("match_screen")}
                )
            }
        }
    }
}

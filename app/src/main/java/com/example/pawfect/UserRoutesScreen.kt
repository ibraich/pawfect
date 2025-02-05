package com.example.pawfect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import com.example.appinterface.R
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Preview
@Composable
fun PreviewUserRoutesScreen() {
    UserRoutesScreen(rememberNavController())
}

@Composable
fun UserRoutesScreen(navController: NavHostController) {
    val auth = Firebase.auth
    val userId = auth.currentUser?.uid ?: return

    val suggestedRoutes = remember { mutableStateOf<List<Map<String, Double>>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }

    fun fetchUserRoutes() {
        FirestoreHelper.getUserSuggestedRoutes(
            userId = userId,
            onSuccess = { routes ->
                suggestedRoutes.value = routes
                isLoading.value = false
            },
            onFailure = { error ->
                Log.e("Firestore", "Error fetching routes: $error")
                isLoading.value = false
            }
        )
    }

    LaunchedEffect (Unit) {
        fetchUserRoutes()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF4F8)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_arrow),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp).clickable { navController.navigateUp() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Your routes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading.value) {
                Text("Loading routes...", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            } else if (suggestedRoutes.value.isEmpty()) {
                NoRoutesPlaceholder()
            } else {
                // Display routes one by one
                suggestedRoutes.value.forEach { route ->
                    RouteItem(route)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Refresh button
            Box(
                modifier = Modifier.size(56.dp)
                    .background(Color(0x8032CD32), shape = CircleShape)
                    .clickable { fetchUserRoutes() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_update),
                    contentDescription = "Refresh",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun NoRoutesPlaceholder() {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components { add(GifDecoder.Factory()) }
        .build()

    Box(
        modifier = Modifier.padding(horizontal = 16.dp)
            .background(Color(0xFFFFC1CC), shape = RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .aspectRatio(0.8f)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context).data(R.raw.sad_dog).build(),
            contentDescription = "Sad dog",
            imageLoader = imageLoader,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "No one has suggested going for a walk yet :'(",
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFFF4081),
        modifier = Modifier.padding(horizontal = 16.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun RouteItem(route: Map<String, Double>) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(Color(0xFFFFE0B2), shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text("Start: ${route["startLatitude"]}, ${route["startLongitude"]}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Stop: ${route["stopLatitude"]}, ${route["stopLongitude"]}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

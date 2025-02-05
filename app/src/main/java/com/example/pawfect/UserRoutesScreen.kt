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
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
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

    val suggestedRoutes = remember { mutableStateOf<List<List<LatLng>>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val currentIndex = remember { mutableStateOf(0) }

    fun fetchUserRoutes() {
        FirestoreHelper.getUserSuggestedRoutes(
            userId = userId,
            onSuccess = { routes ->
                suggestedRoutes.value = routes.map { route ->
                    listOf(
                        LatLng(route["startLatitude"] ?: 0.0, route["startLongitude"] ?: 0.0),
                        LatLng(route["stopLatitude"] ?: 0.0, route["stopLongitude"] ?: 0.0)
                    )
                }
                isLoading.value = false
            },
            onFailure = { error ->
                Log.e("Firestore", "Error fetching routes: $error")
                isLoading.value = false
            }
        )
    }

    LaunchedEffect(Unit) {
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
            // Back button and title
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
                    text = "Your Routes",
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Show Map for the current route
                    DisplayRouteOnMap(suggestedRoutes.value[currentIndex.value])

                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigation between routes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (currentIndex.value > 0) {
                            Icon(
                                painter = painterResource(id = R.drawable.back_arrow),
                                contentDescription = "Previous Suggested Route",
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp)
                                    .clickable { currentIndex.value-- }
                            )
                        }

                        if (currentIndex.value < suggestedRoutes.value.size - 1) {
                            Icon(
                                painter = painterResource(id = R.drawable.forward_arrow),
                                contentDescription = "Next Suggested Route",
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp)
                                    .clickable { currentIndex.value++ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayRouteOnMap(route: List<LatLng>) {
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                onCreate(null)
                onStart()
                onResume()
                getMapAsync { googleMap ->
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    googleMap.uiSettings.isMyLocationButtonEnabled = true

                    if (route.isNotEmpty()) {
                        val startPoint = route.first()
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 12f))

                        // Add polyline and markers
                        googleMap.addPolyline(PolylineOptions().addAll(route).width(8f))
                        route.forEach { latLng ->
                            googleMap.addMarker(MarkerOptions().position(latLng).title("Point: ${latLng.latitude}, ${latLng.longitude}"))
                        }
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    )
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

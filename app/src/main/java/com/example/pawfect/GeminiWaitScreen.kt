package com.example.pawfect

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.appinterface.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

@Preview
@Composable
fun PreviewGeminiWaitScreen() {
    GeminiWaitScreen(rememberNavController(), "1")
}

@Composable
fun GeminiWaitScreen(navController: NavHostController, friendId: String?) {
    val context = LocalContext.current
    var userlatitude = 0.0
    var userlongitude = 0.0

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, get the location
            getLocation(context) { latitude, longitude ->
                userlatitude = latitude
                userlongitude = longitude
                Log.d("Location", "Updated user location: $userlatitude, $userlongitude")
                try {
                    val model = Gemini()
                    val query = context.getString(R.string.gemini_query) +
                            " ${userlatitude}, ${userlongitude}"
                    model.getResponse(query, object : ResponseCallback {
                        override fun onResponse(response: String) {
                            navController.navigate("walk_path_screen/$response/$friendId")
                        }

                        override fun onError(throwable: Throwable) {
                            val fallbackCoordinates = listOf(
                                LatLng(49.9327659, 11.5687332),
                                LatLng(49.9213951, 11.5579074)
                            )

                            val coordinatesJson = """
            [[49.9327659, 11.5687332],
            [49.9213951, 11.5579074]]
        """.trimIndent()

                            navController.navigate("walk_path_screen/$coordinatesJson/$friendId")
                        }
                    }
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    navController.navigate("map_screen")
                }
            }
        } else {
            // Permission denied, handle accordingly
            Log.e("Location", "Location permission is required")
            navController.navigate("map_screen")
        }
    }

    // Check if the permission is already granted
    val hasLocationPermission = remember {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Background call to initialize GeminiPro
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)

    }



    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_arrow),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable ( indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                        { navController.navigate("profile_screen") }
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

            val gifLoader = ImageLoader.Builder(LocalContext.current)
                .components {
                    add(GifDecoder.Factory())
                }
                .build()

            //gif
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .aspectRatio(0.8f)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(R.drawable.load_dog)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Load dog",
                        imageLoader = gifLoader,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // Adjust content scaling
                    )

                }
            }

        }
    }
}

private fun getLocation(context: Context, onLocationRetrieved: (Double, Double) -> Unit) {
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    // Check if permissions are granted
    if (ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                onLocationRetrieved(latitude, longitude)
            } else {
                Log.d("Location", "Location is null")
            }
        }
        .addOnFailureListener { e ->
            // Handle failure
            Log.e("Location", "Error getting location", e)
        }
}
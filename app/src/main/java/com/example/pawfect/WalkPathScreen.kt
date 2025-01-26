package com.example.pawfect

import android.content.ContentValues.TAG
import android.util.Log
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.appinterface.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

@Preview
@Composable
fun WalkPathScreen() {
    val coordinates = "37.7749, -122.4194, 37.774, -122.425, 37.7749, -122.4194"
    WalkPathScreen(rememberNavController(), coordinates)
}

@Composable
fun WalkPathScreen(navController: NavHostController, coordinates: String?) {
    val currentUser = Database.getUserById("0")

    val coordinatesList = parseCoordinates(coordinates)


    Log.d(TAG, "What do we get: " + coordinates);

    /*
    val coordinates = listOf(
        LatLng(37.7749, -122.4194),
        LatLng(37.774, -122.425),
        LatLng(37.7735, -122.43),
        LatLng(37.772, -122.435),
        LatLng(37.77, -122.438),
        LatLng(37.768, -122.441),
        LatLng(37.77, -122.445),
        LatLng(37.772, -122.45),
        LatLng(37.7749, -122.4194)
    )
    */


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF4F8)
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
                        { navController.navigateUp() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Your route",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AndroidView(
                factory = { mapViewContext ->
                    MapView(mapViewContext).apply {
                        onCreate(null)
                        onStart()
                        onResume()
                        getMapAsync { googleMap ->
                            googleMap.uiSettings.isZoomControlsEnabled = true
                            googleMap.uiSettings.isMyLocationButtonEnabled = true

                            val startPoint = coordinatesList[0]
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 12f))

                            val polylineOptions = PolylineOptions()
                                .addAll(coordinatesList)
                                .width(8f)
                            googleMap.addPolyline(polylineOptions)

                            // Add markers to the map for each point in the coordinates list
                            coordinatesList.forEach { latLng ->
                                googleMap.addMarker(
                                    MarkerOptions().position(latLng).title("Point: ${latLng.latitude}, ${latLng.longitude}")
                                )
                            }

                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) { mapView ->
                mapView.onResume()
                mapView.onStart()
            }



            Spacer(modifier = Modifier.height(32.dp))

            // Refresh button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color = Color(0x8032CD32), shape = CircleShape)
                    .clickable( indication = null, interactionSource = remember { MutableInteractionSource() })
                    { navController.navigate("gemini_wait_screen") },
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

fun parseCoordinates(coordinatesString: String?): List<LatLng> {
    return coordinatesString
        ?.trim()
        ?.split("\n")
        ?.map { coordinate ->
            val (latitude, longitude) = coordinate.split(",").map { it.trim().toDouble() }
            LatLng(latitude, longitude)
        } ?: emptyList()
}

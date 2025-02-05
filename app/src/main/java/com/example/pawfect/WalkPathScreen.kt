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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun WalkPathScreen(navController: NavHostController, coordinates: String?, friendId: String?) {

    val coordinatesList = parseCoordinates(coordinates)
    val auth = Firebase.auth
    val userId = auth.currentUser?.uid

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
                        { navController.navigate("plan_activity_screen/$friendId") }
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

                            val apiKey = mapViewContext.getString(R.string.google_maps_api_key)
                            val origin = "${coordinatesList.first().latitude},${coordinatesList.first().longitude}"
                            val destination = "${coordinatesList.last().latitude},${coordinatesList.last().longitude}"
                            val url = "https://maps.googleapis.com/maps/api/directions/json?origin=$origin&destination=$destination&key=$apiKey"

                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val response = OkHttpClient().newCall(
                                        Request.Builder().url(url).build()).execute().body?.string()

                                    response?.let {
                                        val points = JSONObject(it)
                                            .getJSONArray("routes")
                                            .getJSONObject(0)
                                            .getJSONObject("overview_polyline")
                                            .getString("points")

                                        val path = decodePolyline(points)
                                        withContext(Dispatchers.Main) {
                                            googleMap.addPolyline(PolylineOptions().addAll(path).width(8f))
                                            coordinatesList.forEach { latLng ->
                                                googleMap.addMarker(MarkerOptions().position(latLng).title("Point: ${latLng.latitude}, ${latLng.longitude}"))
                                            }
                                        }
                                    }

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
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

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            )
            {

                // Accept Route Button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(color = Color(0xFFFF9800), shape = CircleShape)
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            if (userId != null && friendId != null) {
                                saveRouteForMatch(userId, friendId, coordinatesList)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus),
                        contentDescription = "Suggest Route",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Refresh button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(color = Color(0x8032CD32), shape = CircleShape)
                        .clickable( indication = null, interactionSource = remember { MutableInteractionSource() })
                        { navController.navigate("gemini_wait_screen/$friendId") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_update),
                        contentDescription = "Refresh",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

            }
        }
    }
}

fun saveRouteForMatch(userId: String, friendId: String, coordinatesList: List<LatLng>) {
    if (coordinatesList.size < 2) return

    FirestoreHelper.getMatchIdForUsers(
        user1Id = userId,
        user2Id = friendId,
        onSuccess = { matchId ->
            Log.d("Firestore", "Match found with ID: $matchId")

            val routeData = coordinatesList.zipWithNext { start, stop ->
                mapOf(
                    "startLatitude" to start.latitude,
                    "startLongitude" to start.longitude,
                    "stopLatitude" to stop.latitude,
                    "stopLongitude" to stop.longitude
                )
            }

            FirestoreHelper.storeRoutesInFirestore(matchId, routeData)
        },
        onFailure = { error ->
            Log.e("Firestore", "Match not found: $error")
        }
    )
}



fun parseCoordinates(coordinatesString: String?): List<LatLng> {
    if (coordinatesString.isNullOrBlank()) return emptyList()

    val coordinatesList = mutableListOf<LatLng>()

    try {
        val jsonArray = JSONArray(coordinatesString)

        for (i in 0 until jsonArray.length()) {
            val coordinateArray = jsonArray.getJSONArray(i)
            val latitude = coordinateArray.getDouble(0)
            val longitude = coordinateArray.getDouble(1)
            coordinatesList.add(LatLng(latitude, longitude))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return coordinatesList
}


fun decodePolyline(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    var lat = 0
    var lng = 0

    while (index < encoded.length) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        poly.add(LatLng(lat / 1E5, lng / 1E5))
    }
    return poly
}



package com.example.pawfect

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.appinterface.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Preview
@Composable
fun AIPreviewCalibrationScreen() {
    AIPersonalityCalibrationScreen(rememberNavController())
}

@Composable
fun AIPersonalityCalibrationScreen(navController: NavHostController) {

    val auth = Firebase.auth
    val userId = auth.currentUser?.uid
    val context = LocalContext.current

    var calibratedDogPersonality by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF4F8))
            .padding(16.dp)
    ) {
        // Top Bar with Back Button and Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.back_arrow),
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { navController.navigate("profile_settings_screen") }
            )
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "AI Calibrate Personality",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        if (isLoading) {
            // Loading Screen
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(R.drawable.dog_loading_thinking)
                            .build(),
                        contentDescription = "Loading...",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Calibrating personality using AI...", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }

            }
        } else if (calibratedDogPersonality.isEmpty()) {
            isLoading = true
            LaunchedEffect(Unit) {
                try {
                    val model = Gemini()

                    var dogName: String = "Unknown Dog"
                    var dogBreed: String = "Unknown Breed"
                    var dogGender: String = "Unknown Gender"
                    var dogAge: Int = 0
                    var userInfo: String = "No User info"
                    var statusText: String = "No Status Text"

                    FirestoreHelper.fetchDocumentAsMap(
                        collectionName = "Users",
                        documentId = userId.toString(),
                        onSuccess = { data ->
                            // Access fields dynamically from the map
                            dogName = data["dogName"] as? String ?: "Unknown Dog"
                            dogBreed = data["dogBreed"] as? String ?: "Unknown Breed"
                            dogGender = data["dogGender"] as? String ?: "Unknown Gender"
                            dogAge = (data["dogAge"] as? Number)?.toInt() ?: 0
                            userInfo = data["userInfo"] as? String ?: "No User info"
                            statusText = data["statusText"] as? String ?: "No Status Text"
                        },
                        onFailure = { errorMessage ->
                            println("Failed to fetch document: $errorMessage")
                        }
                    )

                    val query = String.format(
                        context.getString(R.string.gemini_dog_personality_query),
                        dogName,
                        dogBreed,
                        dogGender,
                        dogAge.toString(),
                        userInfo,
                        statusText,
                        Personality.getAllPersonalities()
                    )

                    val imageFiles = mutableListOf<Bitmap>()

                    val imageFile = BitmapFactory.decodeResource(context.resources, R.drawable.doberman)
                    imageFiles.add(imageFile)

                    model.getResponseForImages(query, imageFiles, object : ResponseCallback {
                        override fun onResponse(response: String) {
                            calibratedDogPersonality = response
                            isLoading = false
                        }

                        override fun onError(throwable: Throwable) {
                            throwable.printStackTrace()
                            isLoading = false
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                    isLoading = false
                    navController.navigate("profile_settings_screen")
                }
            }
        } else {

            Spacer(modifier = Modifier.height(150.dp))

            Text(
                text = buildAnnotatedString {
                    append("Your dog's dominant personality is: ")
                    withStyle(
                        style = SpanStyle(color = Color.Blue),
                    ) {
                        append(calibratedDogPersonality)
                    }
                },
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(50.dp))

            LazyColumn (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                item {
                    // Option 1: Accept and Save
                    Button(
                        onClick = {
                            FirestoreHelper.updateFields(collectionName = "Users", documentId = userId.toString(),
                                updates = mapOf("dogPersonality" to calibratedDogPersonality), navController.context,
                                onSuccessMessage = "Dog personality updated successfully!",
                                onNavigate = { navController.navigate("profile_settings_screen") }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(
                            text = "Accept",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                item {
                    // Option 2: Self Recalibrate
                    SelfCalibrateButton(navController = navController)
                }

                item {
                    // Option 3: AI Recalibrate
                    AiCalibrateButton(navController = navController)
                }

                item {
                    // Option 4: Cancel and Return
                    Button(
                        onClick = {
                            navController.navigate("profile_settings_screen")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2753))
                    ) {
                        Text(
                            text = "Cancel and Return",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

    }
}



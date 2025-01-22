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
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.Icon

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
import com.example.appinterface.R

@Preview
@Composable
fun AIPreviewCalibrationScreen() {
    AIPersonalityCalibrationScreen(rememberNavController())
}

@Composable
fun AIPersonalityCalibrationScreen(navController: NavHostController) {

    val currentUser = Database.getUserById(0)
    val context = LocalContext.current

    var calibratedDogPersonality by remember { mutableStateOf<String?>(null) }
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
                CircularProgressIndicator(color = Color.Blue)
            }
        } else if (calibratedDogPersonality == null) {
            isLoading = true
            LaunchedEffect(Unit) {
                try {
                    val model = Gemini()

                    val query = String.format(
                        context.getString(R.string.gemini_dog_personality_query),
                        Database.getAllUsers()[0].dogName,  // %1$s
                        Database.getAllUsers()[0].dogBreed, // %2$s
                        "Male",               // %3$s (Gender, hardcoded for now)
                        "4 years",            // %4$s (Age, hardcoded for now)
                        Database.getAllUsers()[0].userInfo, // %5$s
                        Database.getAllUsers()[0].statusText, // %6$s
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
                            calibratedDogPersonality = "Uncalibrated"
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
                        append(calibratedDogPersonality ?: "Uncalibrated")
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
                            // Save the personality in the database
                            //Database.updateDogPersonality(calibratedDogPersonality ?: "")
                            navController.navigate("profile_settings_screen")
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
                            calibratedDogPersonality = "Uncalibrated"
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



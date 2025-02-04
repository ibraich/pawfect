package com.example.pawfect

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.appinterface.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@Preview
@Composable
fun PreviewCommonPuppiesScreen() {
    CommonPuppiesScreen(rememberNavController(), "Dog1", "Dog2")
}

@Composable
fun CommonPuppiesScreen(navController: NavHostController, currentUserId: String, friendId: String) {

    val currentUser = remember { mutableStateOf<UserFetch?>(null) }
    val friendUser = remember { mutableStateOf<UserFetch?>(null) }

    val firestoreHelper = remember { FirestoreHelper }

    val context = LocalContext.current
    val openAI = remember { OpenAI(context) }
    val imageUrl = remember { mutableStateOf<String?>(null) }
    val isLoading = remember { mutableStateOf(true) }

    var bitmapImage = remember { mutableStateOf<Bitmap?>(null) }


        fun fetchImageIfReady() {
            val user1 = currentUser.value
            val user2 = friendUser.value

            if (user1 != null && user2 != null) {
                val validUser1Image = user1.offspringImageUrl.takeIf { it.isNotBlank() }
                val validUser2Image = user2.offspringImageUrl.takeIf { it.isNotBlank() }
                val existingImageUrl = validUser1Image ?: validUser2Image

                if (existingImageUrl != null) {
                    imageUrl.value = existingImageUrl
                    isLoading.value = false
                } else {
                    isLoading.value = true
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val openAiImageUrl = openAI.generateMatchOffspringImage(user1, user2)

                            val bitmap = ImageProcessor.downloadAndCacheImage(context, openAiImageUrl.toString())

                            if (bitmap != null) {
                                val fileUri = ImageProcessor.saveBitmapToCacheAndGetUri(context, bitmap)

                                if (fileUri != null) {
                                    val fileName = "offspring_${System.currentTimeMillis()}.jpg"

                                    FirebaseStorageHelper.uploadFile("users/$currentUserId/offspringImages",
                                        fileName,
                                        fileUri as Uri,
                                        onSuccess = { downloadUrl ->
                                            FirestoreHelper.updateFields(
                                                collectionName = "Users",
                                                documentId = currentUserId,
                                                updates = mapOf("offspringImageUrl" to downloadUrl.toString()),
                                                context = context
                                            )
                                            imageUrl.value = downloadUrl.toString()
                                        },
                                        onFailure = { error ->
                                            Log.e("Firebase Upload", "Upload failed: $error")
                                        }
                                    )

                                } else {
                                    Log.e("Bitmap Conversion", "Failed to convert Bitmap to Uri")
                                    withContext(Dispatchers.Main) { isLoading.value = false }
                                }
                            } else {
                                Log.e("AI Image", "Failed to download AI-generated image")
                                withContext(Dispatchers.Main) { isLoading.value = false }
                            }
                        } catch (e: Exception) {
                            Log.e("AI Image", "Error generating image: ${e.message}")
                            withContext(Dispatchers.Main) { isLoading.value = false }
                        }
                    }
                }
            }
        }

    LaunchedEffect (Unit) {
        firestoreHelper.fetchDocument(
            collectionName = "Users",
            documentId = currentUserId,
            onSuccess = { user ->
                currentUser.value = user
                fetchImageIfReady()
                        },
            onFailure = { error -> Log.e("Firestore", "Error fetching current user: $error") }
        )

        firestoreHelper.fetchDocument(
            collectionName = "Users",
            documentId = friendId,
            onSuccess = { user ->
                friendUser.value = user
                fetchImageIfReady()
                        },
            onFailure = { error -> Log.e("Firestore", "Error fetching friend user: $error") }
        )

    }

    LaunchedEffect(imageUrl.value) {
        imageUrl.value?.let { url ->
            bitmapImage.value = ImageProcessor.downloadAndCacheImage(context, url)
            withContext(Dispatchers.Main) {
                isLoading.value = false
            }
        }
    }

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
            // Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.back_arrow),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable( indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                        { navController.navigateUp() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "${currentUser.value?.dogName} and ${friendUser.value?.dogName}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Image Section
            if (isLoading.value) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(R.drawable.dog_loading_thinking)
                            .build(),
                        contentDescription = "Loading...",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Generating image...", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
            } else {

                bitmapImage.value?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Future Puppies",
                        modifier = Modifier
                            .size(350.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "Your puppies may\nlook something like this ;)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF4081),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Disclaimer
            Text(
                text = "AI can make mistakes. We are not responsible for the final result.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}



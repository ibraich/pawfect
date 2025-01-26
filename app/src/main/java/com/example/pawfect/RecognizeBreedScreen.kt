package com.example.pawfect

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.appinterface.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Preview
@Composable
fun PreviewRecognizeBreedScreen() {
    RecognizeBreedScreen(rememberNavController())
}

@Composable
fun RecognizeBreedScreen(navController: NavHostController) {

    val context = LocalContext.current
    val selectedImage = remember { mutableStateOf<Bitmap?>(null) }
    val profileImageBase64 = remember { mutableStateOf<String?>(null) }
    val breedName = remember { mutableStateOf("") }

    val fs = Firebase.firestore
    val auth = Firebase.auth

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedImage.value = bitmap
        }
    }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            selectedImage.value = bitmap
        }
    }

    LaunchedEffect(Unit) {
        val currentUserUid = auth.currentUser?.uid
        currentUserUid?.let {
            fs.collection("Users").document(it)
                .get()
                .addOnSuccessListener { document ->
                    profileImageBase64.value = document.getString("dogProfileImage")
                }
                .addOnFailureListener {
                    profileImageBase64.value = null
                }
        }
    }

    LaunchedEffect(profileImageBase64.value) {
        profileImageBase64.value?.let { base64 ->
            selectedImage.value = ImageProcessor.decodeBase64ToBitmap(base64)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF4F8))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
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
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { navController.navigate("profile_settings_screen") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Breed Recognizer",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Image display area
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                val imageBitmap = selectedImage.value?.asImageBitmap()
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "No Image Selected",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons for loading and taking photos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        launcher.launch("image/*")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC4D0))
                ) {
                    Text(
                        text = "Load Photo",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = {takePictureLauncher.launch(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC4D0))
                ) {
                    Text(
                        text = "Take Photo",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dog's breed
            ProfileInfoField(label = "Dog’s breed:", initialValue = breedName.value)

            Spacer(modifier = Modifier.height(8.dp))

            LaunchedEffect(selectedImage.value) {
                selectedImage.value?.let { bitmap ->
                    val classifier = TensorFlowImageClassifier(context, bitmap)
                    val results = classifier.recognizeImage(bitmap)
                    if (results.isNotEmpty()) {
                        breedName.value = results[0].title
                    } else {
                        breedName.value = "Unknown"
                    }
                } ?: run {
                    Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}




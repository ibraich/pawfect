package com.example.pawfect


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.appinterface.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Preview
@Composable
fun PreviewRegistrationSecondScreen() {
    RegistrationSecondScreen(rememberNavController(), "", "")
}

@Composable
fun RegistrationSecondScreen(navController: NavHostController,
                             email: String?,
                             password: String?) {

    var dogName by remember { mutableStateOf("") }
    var dogAge by remember { mutableStateOf("") }
    var dogBreed by remember { mutableStateOf("") }
    var dogGender by remember { mutableStateOf("Male") }
    var ownerName by remember { mutableStateOf("") }
    var userStatus by remember { mutableStateOf("") }
    var userInfo by remember { mutableStateOf("") }
    var ownerAge by remember { mutableStateOf("") }

    val context = LocalContext.current
    val selectedImage = remember {
        mutableStateOf<Bitmap?>(
            BitmapFactory.decodeResource(context.resources, R.drawable.default_image)
        )
    }

    var selectedImageUri : Uri? = null

    val loadImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts
        .GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedImage.value = bitmap
            selectedImageUri = uri
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            selectedImage.value = bitmap
            val tempUri = saveBitmapToCache(context, bitmap)
            if (tempUri != null) {
                selectedImageUri = tempUri
            } else {
                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
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
            // Back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.back_arrow),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { navController.navigate("registration_first_screen") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "A few more steps before we begin ...",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Dog's Name Input
            InputField(label = "Your dog’s name:", value = dogName) { dogName = it }
        }

        item {
            // Dog's Age Input
            InputField(label = "Your dog’s age:", value = dogAge) { dogAge = it }
        }

        item {
            // Dog's Breed Input
            InputField(label = "Your dog’s breed:", value = dogBreed) { dogBreed = it }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {

            Text(
                text = "Select dog's gender",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.Center) {
                    RadioButton(
                        selected = dogGender == "Male",
                        onClick = { dogGender = "Male" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFFFFB6C1),
                            unselectedColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Male",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        textAlign = TextAlign.Right
                    )
                }

                Spacer(modifier = Modifier.width(64.dp))

                Column(verticalArrangement = Arrangement.Center) {
                    RadioButton(
                        selected = dogGender == "Female",
                        onClick = { dogGender = "Female" },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFFFFD1DC),
                            unselectedColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Female",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            // Profile Picture Section
            Text(
                text = "Load the profile picture of your dog here:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                val imageBitmap = selectedImage.value?.asImageBitmap()
                imageBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "Dog Profile Placeholder",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(350.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                } ?: run {
                    Toast.makeText(context, "No image was chosen", Toast.LENGTH_SHORT).show()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { loadImageLauncher.launch("image/*") },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB6C1))
                ) {
                    Text(
                        text = "Load from device",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { takePictureLauncher.launch(null) },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB6C1))
                ) {
                    Text(
                        text = "Take a picture",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            // Profile Status Input
            InputField(label = "Write your status here:", value = userStatus) { userStatus = it }
        }

        item {
            // Profile User Info Input
            InputField(label = "Write a short story about your dog:", value = userInfo)
            { userInfo = it }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            // Owner's Name Input
            InputField(label = "Owner’s name:", value = ownerName) { ownerName = it }
        }

        item {
            // Owner's Age Input
            InputField(label = "Owner’s age:", value = ownerAge) { ownerAge = it }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            // Sign Up Button
            var errorMessage by remember { mutableStateOf<String?>(null) }
            Button(
                onClick = {
                    if (dogName.isEmpty() || dogAge.isEmpty()|| dogBreed.isEmpty()||
                        ownerName.isEmpty()|| ownerAge.isEmpty()) {
                        errorMessage = "All fields are required"
                    } else {
                        ImageProcessor.imageBitmapToBitmap(selectedImage
                            .value?.asImageBitmap())
                            ?.let { ImageProcessor.encodeImageToBase64(it) }?.let { img ->
                                signUp(
                                    auth = FirebaseAuth.getInstance(),
                                    email = email,
                                    password = password,
                                    dogName = dogName,
                                    dogAge = dogAge,
                                    dogBreed = dogBreed,
                                    dogGender = dogGender,
                                    dogProfileImage = img,
                                    dogProfileImageUri = selectedImageUri,
                                    userStatus = userStatus,
                                    userInfo = userInfo,
                                    ownerName = ownerName,
                                    ownerAge = ownerAge,
                                    navController = navController,
                                    setError = { errorMessage = it }
                                )
                            }

                    }
                },

                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(16.dp))
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAACC))
            ) {
                Text(
                    text = "Sign up",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD1DC), shape = RoundedCornerShape(12.dp)),
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

private fun signUp(
    auth: FirebaseAuth,
    email: String?,
    password: String?,
    dogName: String,
    dogAge: String,
    dogBreed: String,
    dogGender: String,
    dogProfileImage: String,
    dogProfileImageUri: Uri?,
    userStatus: String,
    userInfo: String,
    ownerName: String,
    ownerAge: String,
    navController: NavHostController,
    setError: (String?) -> Unit
) {
    if (email != null && password != null) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    userId?.let { uid ->
                        val user = hashMapOf(
                            "dogName" to dogName,
                            "dogAge" to dogAge,
                            "dogBreed" to dogBreed,
                            "dogGender" to dogGender,
                            "dogProfileImage" to dogProfileImage,
                            "userStatus" to userStatus,
                            "userInfo" to userInfo,
                            "ownerName" to ownerName,
                            "ownerAge" to ownerAge
                        )

                        val db = Firebase.firestore
                        db.collection("Users")
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener {
                                val currentTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                                    .apply { timeZone = TimeZone.getTimeZone("Europe/Berlin") }
                                    .format(Date())

                                FirebaseStorageHelper.uploadFile(
                                    folderPath = "users/$userId/profilePic",
                                    fileName = "profilePic_$currentTime",
                                    fileUri = dogProfileImageUri!!,
                                    onSuccess = { downloadUrl ->
                                        FirestoreHelper.updateFields(collectionName = "Users", documentId = userId.toString(),
                                            updates = mapOf("profilePicUrl" to downloadUrl.toString()), navController.context,
                                            onSuccessMessage = "User data saved successfully!",
                                            onNavigate = { navController.navigate("profile_screen") })
                                    },
                                    onFailure = { errorMessage ->
                                        setError("Failed to save user image to Firebase storage.")
                                    }
                                )
                            }
                            .addOnFailureListener {
                                setError("Failed to save user data to Firestore.")
                            }
                    } ?: run {
                        setError("Failed to retrieve user ID.")
                    }
                } else {
                    setError("Sign-up failed: ${task.exception?.message}")
                    Log.e("SignUp", "Error: ${task.exception?.message}")
                }
            }
    }
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
    return try {
        // Create a unique file name
        val fileName = "temp_image_${System.currentTimeMillis()}.jpg"
        // Get the cache directory
        val file = File(context.cacheDir, fileName)
        // Write the bitmap to the file as JPEG
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        // Return the file's Uri
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    } catch (e: Exception) {
        Log.e("ImageSave", "Error saving bitmap to cache", e)
        null
    }
}
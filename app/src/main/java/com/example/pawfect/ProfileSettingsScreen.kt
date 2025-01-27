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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import coil3.request.crossfade
import com.example.appinterface.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Preview
@Composable
fun PreviewProfileSettingsScreen() {
    ProfileSettingsScreen(rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(navController: NavHostController) {

    val context = LocalContext.current
    var dogName by remember { mutableStateOf("Loading...") }
    var dogBreed by remember { mutableStateOf("Loading...") }
    var dogAge by remember { mutableStateOf("Loading...") }
    var dogGender by remember { mutableStateOf("Loading...") }
    var ownerName by remember { mutableStateOf("Loading...") }
    var ownerAge by remember { mutableStateOf("Loading...") }
    var userStatus by remember { mutableStateOf("Fetching user information...") }
    var userInfo by remember { mutableStateOf("Fetching user information...") }
    var dogPersonality by remember { mutableStateOf("Fetching dog personality...") }
    var profileImageBase64 by remember { mutableStateOf<String?>(null) }
    val selectedImage = remember { mutableStateOf<Bitmap?>(null) }

    val fs = Firebase.firestore
    val auth = Firebase.auth

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedImage.value = bitmap
            profileImageBase64 = ImageProcessor.encodeImageToBase64(bitmap)
        }
    }

    LaunchedEffect(Unit) {
        val currentUserUid = auth.currentUser?.uid
        fs.collection("Users").document(currentUserUid.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    dogName = document.getString("dogName") ?: "Unknown Dog"
                    dogBreed = document.getString("dogBreed") ?: "Unknown Breed"
                    dogAge = document.getString("dogAge") ?: "Unknown Age"
                    dogGender = document.getString("dogGender") ?: "Unknown Gender"
                    ownerName = document.getString("ownerName") ?: "Unknown Name"
                    ownerAge = document.getString("ownerAge")?: "Unknown Age"
                    userStatus = document.getString("userStatus") ?: "No additional info"
                    userInfo = document.getString("userInfo") ?: "No additional info"
                    dogPersonality = document.getString("dogPersonality") ?: "Unknown Personality"
                    profileImageBase64 = document.getString("dogProfileImage")
                } else {
                    dogName = "No User Found"
                    dogBreed = "No information available"
                    dogAge = "No information available"
                    dogGender = "No information available"
                    ownerName = "No information available"
                    ownerAge = "No information available"
                    userStatus = "No information available"
                    userInfo = "No information available"
                    dogPersonality = "No information available"
                    profileImageBase64 = null
                }
            }
            .addOnFailureListener {
                dogName = "Error"
                dogBreed = "Failed to fetch data"
                dogAge = "Failed to fetch data"
                dogGender = "Failed to fetch data"
                ownerName = "Failed to fetch data"
                ownerAge = "Failed to fetch data"
                userStatus = "Failed to fetch data"
                userInfo = "Failed to fetch data"
                dogPersonality = "Failed to fetch data"
                profileImageBase64 = null
            }
    }

    // Scrollable container
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF4F8))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Back button and title row
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
                        ) { navController.navigate("profile_screen") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Profile settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile image with edit button
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                val imageBitmap = selectedImage.value?.asImageBitmap()
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Dog Profile Picture",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                } else if (profileImageBase64 != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(ImageProcessor.decodeBase64ToBitmap(profileImageBase64!!))
                            .crossfade(true)
                            .build(),
                        contentDescription = "Dog Profile Picture",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_image),
                        contentDescription = "Dog Profile Picture",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFC4D0))
                        .clickable {  launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_plus),
                        contentDescription = "Edit Profile Picture",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dog's name
            ProfileInfoField(label = "Dog's name", initialValue = dogName) { updatedDogName ->
                dogName = updatedDogName
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dog's age
            ProfileInfoField(label = "Dog's age", initialValue = dogAge) { updatedDogAge ->
                dogAge = updatedDogAge
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dog's gender
            Text(
                text = "Dog's gender",
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

            Spacer(modifier = Modifier.height(16.dp))


            val breeds = context.resources.getStringArray(R.array.breeds_array).toList() // List of breeds from the resource
            var expanded by remember { mutableStateOf(false) } // Controls the dropdown menu visibility
            // Dog's breed
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Dog's breed:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFE1E7))
                        .padding(8.dp) 
                ) {
                    Text(
                        text = dogBreed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(8.dp)
                            .clickable { expanded = !expanded }, // Toggle dropdown visibility
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFE1E7))

                    ) {
                        breeds.forEach { breed ->
                            DropdownMenuItem(onClick = {
                                dogBreed = breed
                                expanded = false // Hide the dropdown after selection
                            }) {
                                Text(text = breed)
                            }
                        }
                    }
                }
            }





            Spacer(modifier = Modifier.height(8.dp))

            // Recognize breed button
            Button(
                onClick = { navController.navigate("recognize_breed_screen") }, // Update breed here
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC4D0))
            ) {
                Text(
                    text = "Recognize the breed!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Owner's name
            ProfileInfoField(label = "Owner’s name:", initialValue = ownerName) { updatedOwnerName ->
                ownerName = updatedOwnerName
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Owner's age
            ProfileInfoField(label = "Owner’s age:", initialValue = ownerAge) { updatedOwnerAge ->
                ownerAge = updatedOwnerAge
            }

            Spacer(modifier = Modifier.height(24.dp))

            //TODO
            // Fetch from DB On return navigation
            ProfileInfoField(label = "Your dog’s personality:", initialValue = dogPersonality) {
                    calibratedDogPersonality -> dogPersonality = calibratedDogPersonality
            }

            Spacer(modifier = Modifier.height(8.dp))

            SelfCalibrateButton(navController = navController)

            AiCalibrateButton(navController = navController)

            Spacer(modifier = Modifier.height(16.dp))

            // StatusText field
            ProfileInfoField(label = "Status:", initialValue = userInfo) { updatedUserInfo ->
                userInfo = updatedUserInfo
            }

            Spacer(modifier = Modifier.height(16.dp))

            // InfoText field
            ProfileInfoField(label = "Additional Info:", initialValue = userStatus) { updatedUserStatus ->
                userStatus = updatedUserStatus
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save changes button
            Button(
                onClick = {
                    val currentUserUid = auth.currentUser?.uid
                    val updatedData = mapOf(
                        "dogName" to dogName,
                        "dogAge" to dogAge,
                        "dogBreed" to dogBreed,
                        "dogGender" to dogGender,
                        "dogProfileImage" to profileImageBase64,
                        "userStatus" to userStatus,
                        "userInfo" to userInfo,
                        "dogPersonality" to dogPersonality,
                        "ownerName" to ownerName,
                        "ownerAge" to ownerAge
                    )

                    currentUserUid?.let {
                        fs.collection("Users").document(it)
                            .set(updatedData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Changes saved successfully!",
                                    Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Failed to save changes: ${e.message}",
                                    Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text(
                    text = "Save changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val auth = Firebase.auth

            // Logout button
            Button(
                onClick = { signOut(auth, navController) },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2753))
            ) {
                Text(
                    text = "Logout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SelfCalibrateButton(navController: NavHostController) {
    Button(
        onClick = { navController.navigate("calibrate_personality_screen") },
        //onClick = { navController.navigate("drag_and_drop_question_screen") },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC4D0))
    ) {
        Text(
            text = "Self Calibrate Personality",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun AiCalibrateButton(navController: NavHostController) {
    Button(
        onClick = { navController.navigate("ai_calibrate_personality_screen") },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC4D0))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ai),
                contentDescription = "AI Icon",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AI Calibrate Personality",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoField(label: String, initialValue: String, onValueChange: (String) -> Unit = {}) {
    var content by remember { mutableStateOf(initialValue) }

    LaunchedEffect(initialValue) {
        content = initialValue
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))

        TextField(
            value = content,
            onValueChange = {
                content = it
                onValueChange(it)
            },
            placeholder = { Text(text = initialValue) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFFE1E7), // TextField background
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}


private fun signOut(auth: FirebaseAuth, navController: NavHostController) {
    auth.signOut()
    navController.navigate("login_screen")
}
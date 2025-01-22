package com.example.pawfect

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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

@Composable
fun ProfileSettingsScreen(navController: NavHostController) {
    
    

    val currentUser = Database.getUserById(0)

    //var dogBreed by remember { mutableStateOf(currentUser.dogBreed) }
    var dogPersonality by remember { mutableStateOf(currentUser.dogPersonality) }


    var dogName by remember { mutableStateOf("Loading...") }
    var dogBreed by remember { mutableStateOf("Loading...") }
    var ownerName by remember { mutableStateOf("Loading...") }
    var ownerAge by remember { mutableStateOf(0) }
    var addInfo by remember { mutableStateOf("Fetching user information...") }
    var userInfo by remember { mutableStateOf("Fetching user information...") }
    val fs = Firebase.firestore
    val auth = Firebase.auth

    LaunchedEffect(Unit) {
        val currentUserUid = auth.currentUser?.uid
        fs.collection("Users").document(currentUserUid.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    dogName = document.getString("dogName") ?: "Unknown Dog"
                    dogBreed = document.getString("dogBreed") ?: "Unknown Breed"
                    ownerName = document.getString("ownerName") ?: "Unknown Name"
                    ownerAge = document.getString("ownerAge")?.toInt() ?: 0
                    addInfo = document.getString("addInfo") ?: "No additional info"
                    userInfo = document.getString("userInfo") ?: "No additional info"
                } else {
                    dogName = "No User Found"
                    dogBreed = "No information available"
                    ownerName = "No information available"
                    ownerAge = 0
                    addInfo = "No information available"
                    userInfo = "No information available"
                }
            }
            .addOnFailureListener {
                dogName = "Error"
                dogBreed = "Failed to fetch data"
                ownerName = "Failed to fetch data"
                ownerAge = 0
                addInfo = "Failed to fetch data"
                userInfo = "Failed to fetch data"
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
                Image(
                    painter = painterResource(id = currentUser.profileImage),
                    contentDescription = "Dog Profile Picture",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFC4D0))
                        .clickable { /* TODO */ },
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
            ProfileInfoField(label = dogName, initialValue = dogName)

            Spacer(modifier = Modifier.height(16.dp))

            // Dog's breed (uses shared state)
            ProfileInfoField(label = dogBreed, initialValue = dogBreed)

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
            ProfileInfoField(label = "Owner’s name:", initialValue = ownerName)

            Spacer(modifier = Modifier.height(16.dp))

            // Owner's age
            ProfileInfoField(label = "Owner’s age:", initialValue = ownerAge.toString())

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
            ProfileInfoField(label = "Status:", initialValue = userInfo)

            Spacer(modifier = Modifier.height(16.dp))

            // InfoText field
            ProfileInfoField(label = "Additional Info:", initialValue = addInfo)

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
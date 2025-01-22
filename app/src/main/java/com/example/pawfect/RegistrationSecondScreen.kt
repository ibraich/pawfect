package com.example.pawfect

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.appinterface.R

@Preview
@Composable
fun PreviewRegistrationSecondScreen() {
    RegistrationSecondScreen(rememberNavController())
}

@Composable
fun RegistrationSecondScreen(navController: NavHostController) {
    var dogName by remember { mutableStateOf("") }
    var dogAge by remember { mutableStateOf("") }
    var dogBreed by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var ownerAge by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    val loadImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri.toString()
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        /*TODO*/
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
            // Recognize Breed Button
            Button(
                /*
                *  Take into account that it is possible to get from
                *  the `Recognize Breed Screen` to the `Profile Settings Screen`
                * */
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB6C1))
            ) {
                Text(
                    text = "Recognize the breed!",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            // Profile Picture Section
            Text(
                text = "Load the profile picture of your dog here:",
                fontSize = 16.sp,
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
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Dog Profile Picture",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_image),
                        contentDescription = "Dog Profile Placeholder",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(350.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
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
            Button(
                onClick = { /* TODO  Redirect to the profile*/ },
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

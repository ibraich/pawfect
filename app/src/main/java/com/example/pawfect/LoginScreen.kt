package com.example.pawfect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen(rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF4F8)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Let's get started",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            var textLoginState by remember { mutableStateOf("") }

            TextField(
                value = textLoginState,
                onValueChange = { textLoginState = it },
                placeholder = { Text(text = "Login") },
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp)),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFFE1E7), // TextField background
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            var textPasswordState by remember { mutableStateOf("") }

            TextField(
                value = textPasswordState,
                onValueChange = { textPasswordState = it },
                placeholder = { Text(text = "Password") },
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp)),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFFE1E7), // TextField background
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Button(
                onClick = {
                    /* TODO */
                    navController.navigate("profile_screen") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB6C1)),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(0.7f)
                    .height(50.dp)
            ) {
                Text(text = "Sign up", fontSize = 18.sp, color = Color.Black)
            }

            Text(
                text = "Already have an account? Sign in",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF1493),
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

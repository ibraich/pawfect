package com.example.pawfect

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation.Companion.None
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth


@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen(rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val auth = Firebase.auth

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
                text = "Welcome back!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            var textLoginState by remember { mutableStateOf("") }
            var errorMessage by remember { mutableStateOf<String?>(null) }

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
            var passwordVisible by remember { mutableStateOf(false) }

            TextField(
                value = textPasswordState,
                onValueChange = { textPasswordState = it },
                placeholder = { Text(text = "Password") },
                visualTransformation = if (passwordVisible) None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
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

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    /* TODO */
                    if (textLoginState.isEmpty() || textPasswordState.isEmpty()) {
                        errorMessage = "Both fields are required"
                    } else {
                        signIn(auth, textLoginState, textPasswordState, navController) {error -> errorMessage = error}
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB6C1)),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(0.7f)
                    .height(50.dp)
            ) {
                Text(text = "Sign in", fontSize = 18.sp, color = Color.Black)
            }

            ClickableText(
                text = AnnotatedString("Don't have an account? Sign up"),
                onClick = { navController.navigate("registration_first_screen") },
                style = TextStyle(
                    color = Color(0xFFFF1493),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

private fun signIn(auth: FirebaseAuth, email: String, password: String, navController: NavHostController, setError: (String?) -> Unit) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navController.navigate("profile_screen")
            } else {
                val errorMessage = when (task.exception) {
                    is FirebaseAuthInvalidUserException -> "User not found. Please sign up first."
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email or password. Try again."
                    else -> "Authentication failed. Please try again."
                }
                setError(errorMessage)
                Log.e("SignIn", "Error: ${task.exception?.message}")

            }
        }
}
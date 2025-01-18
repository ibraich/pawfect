package com.example.pawfect

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import coil.util.CoilUtils.result
import com.example.appinterface.R

@Preview
@Composable
fun GeminiWaitScreen() {
    GeminiWaitScreen(rememberNavController())
}

@Composable
fun GeminiWaitScreen(navController: NavHostController) {
    val currentUser = Database.getUserById(0)
    val context = LocalContext.current


    // Background call to initialize GeminiPro
    LaunchedEffect(Unit) {
        try {
            val model = Gemini()
            val query = context.getString(R.string.gemini_query) +
                    " ${currentUser.location.latitude}, ${currentUser.location.longitude}"
            model.getResponse(query, object : ResponseCallback {
                override fun onResponse(response: String) {
                    navController.navigate("walk_path_screen/$response")
                }

                override fun onError(throwable: Throwable) {
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            navController.navigate("map_screen")
        }
    }



    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_arrow),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable ( indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                        { navController.navigate("profile_screen") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Your routes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val imageLoader = ImageLoader.Builder(LocalContext.current)
                .components {
                    add(GifDecoder.Factory())
                }
                .build()

            //gif
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .aspectRatio(0.8f)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(R.drawable.load_dog)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Load dog",
                        imageLoader = imageLoader,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // Adjust content scaling
                    )

                }
            }

        }
    }
}
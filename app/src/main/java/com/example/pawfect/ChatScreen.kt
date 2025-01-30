package com.example.pawfect


import android.util.Log
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Preview
@Composable
fun PreviewChatScreen() {
    ChatScreen(rememberNavController(), "1")
}

@Composable
fun ChatScreen(navController: NavHostController, friendId: String) {
    val db = FirebaseFirestore.getInstance()
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var friend by remember { mutableStateOf<User?>(null) }
    val messages = remember { mutableStateListOf<Message>() }
    var newMessage by remember { mutableStateOf("") }

    LaunchedEffect(friendId) {
        ChatScreenManager.setChatOpen(friendId)
    }

    DisposableEffect(Unit) {
        onDispose {
            ChatScreenManager.setChatOpen(null)
        }
    }

    LaunchedEffect(friendId) {
        db.collection("Users").document(friendId).get()
            .addOnSuccessListener { document ->
                friend = document.toObject(User::class.java)
            }
    }

    LaunchedEffect(friendId) {
        val chatId = getChatId(currentUserUid, friendId)

        db.collection("Chats").document(chatId)
            .collection("Messages")
            .orderBy("timeMillis", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                messages.clear()
                snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) }?.let { messageList ->
                    messages.addAll(messageList)

                    for (document in snapshot.documents) {
                        if (document.getBoolean("isRead") == false) {
                            document.reference.update("isRead", true)
                        }
                    }
                }
            }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF4F8)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar with friend Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFFFC1CC))
                    .padding(16.dp),
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
                        ) { navController.navigateUp() }
                )
                Spacer(modifier = Modifier.width(8.dp))

                if (friend?.dogProfileImage != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(ImageProcessor.decodeBase64ToBitmap(friend?.dogProfileImage!!))
                            .crossfade(true)
                            .build(),
                        error = painterResource(R.drawable.image_not_found_icon),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_image),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = friend?.dogName ?: "Unknown",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chat messages
            LazyColumn(modifier = Modifier.weight(1f).padding(16.dp), reverseLayout = true) {
                items(messages.reversed()) { message ->
                    val isUserMessage = message.senderId == currentUserUid
                    ChatBubble(message, isUserMessage)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input field and send button
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFFFC1CC))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    placeholder = { Text(text = "Start a message") },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    singleLine = true
                )
                Button(onClick = {
                    if (newMessage.isNotBlank()) {
                        sendMessage(currentUserUid, friendId, newMessage, db)
                        newMessage = ""
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAA87A5))) {
                    Icon(painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message, isUserMessage: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .background(
                    color = if (isUserMessage) Color(0xFFFFC1CC) else Color(0xFFAA87A5),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = message.messageText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.timestamp,
                    fontSize = 10.sp,
                    color = Color.White
                )
            }
        }
    }
}

fun getChatId(user1: String, user2: String): String {
    return if (user1 < user2) "$user1-$user2" else "$user2-$user1"
}

fun sendMessage(senderId: String, receiverId: String, messageText: String, db: FirebaseFirestore) {
    getFirebaseServerTime { ntpTime ->
        val chatId = getChatId(senderId, receiverId)
        val timestamp = ntpTime ?: System.currentTimeMillis()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedTime = sdf.format(Date(timestamp))
        val message = Message(senderId, receiverId, messageText, formattedTime, timestamp, false)

        db.collection("Chats").document(chatId).collection("Messages")
            .add(message)
            .addOnSuccessListener {
                Log.d("Chat", "Message sent successfully")
            }
            .addOnFailureListener {
                Log.e("Chat", "Failed to send message", it)
            }
    }
}

fun getFirebaseServerTime(callback: (Long?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("serverTime").document("time")

    val data = hashMapOf("timestamp" to FieldValue.serverTimestamp())
    docRef.set(data).addOnSuccessListener {
        docRef.get().addOnSuccessListener { document ->
            val timestamp = document.getTimestamp("timestamp")?.toDate()?.time
            callback(timestamp)
        }.addOnFailureListener {
            callback(null)
        }
    }.addOnFailureListener {
        callback(null)
    }
}

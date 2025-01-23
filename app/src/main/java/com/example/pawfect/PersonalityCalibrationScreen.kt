package com.example.pawfect

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
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

import androidx.compose.material3.Icon

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.appinterface.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Preview
@Composable
fun PreviewPersonalityCalibrationScreen() {
    PersonalityCalibrationScreen(rememberNavController())
}

@Composable
fun PersonalityCalibrationScreen(navController: NavHostController) {
    val categorizedQuestions = Database.getCategorizedQuestions()
    val selectedQuestions = remember { mutableStateListOf<Question>() }
    val selectedAnswers = remember { mutableStateListOf<Option>() }
    var isQuestionSelectionComplete by remember { mutableStateOf(false) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var calibratedDogPersonality by rememberSaveable { mutableStateOf("") }

    val auth = Firebase.auth
    val userId = auth.currentUser?.uid


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF4F8))
            .padding(16.dp)
    ) {
        // Top Bar with Back Button and Title
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
                    .clickable { navController.navigate("profile_settings_screen") }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = if (!isQuestionSelectionComplete) "Select 5 Questions" else "Answer Questions",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isQuestionSelectionComplete) {
            // Scrollable list of categories for selection
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                categorizedQuestions.forEach { (category, questions) ->
                    item {
                        ExpandableCategoryBox(
                            category = category,
                            questions = questions,
                            selectedQuestions = selectedQuestions,
                            maxSelection = 5,
                            onSelectionChange = {
                                isQuestionSelectionComplete = selectedQuestions.size == 5
                            }
                        )
                    }
                }
            }
        } else if (currentQuestionIndex < selectedQuestions.size) {
            // Show selected questions one by one
            val question = selectedQuestions[currentQuestionIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = question.question,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                question.options.forEach { option ->
                    QuizOptionButton(
                        text = option.text,
                        onClick = {
                            selectedAnswers.add(option)
                            currentQuestionIndex++
                            if (currentQuestionIndex == selectedQuestions.size) {
                                calibratedDogPersonality = calculateDominantPersonality(selectedAnswers)
                            }
                        }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = buildAnnotatedString {
                            append("Your dog's dominant personality is: ")
                            withStyle(
                                style = SpanStyle(color = Color.Blue),
                            ) {
                                append(calibratedDogPersonality)
                            }
                        },
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    // Option 1: Accept and Save
                    Button(
                        onClick = {
                            FirestoreHelper.updateFields(collectionName = "Users", documentId = userId.toString(),
                                updates = mapOf("dogPersonality" to calibratedDogPersonality), navController.context,
                                onSuccessMessage = "Dog personality updated successfully!",
                                onNavigate = { navController.navigate("profile_settings_screen") }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(
                            text = "Accept",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                item {
                    // Option 2: Self Recalibrate
                    SelfCalibrateButton(navController = navController)
                }

                item {
                    // Option 3: AI Recalibrate
                    AiCalibrateButton(navController = navController)
                }

                item {
                    // Option 4: Cancel and Return
                    Button(
                        onClick = {
                            navController.navigate("profile_settings_screen")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2753))
                    ) {
                        Text(
                            text = "Cancel and Return",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableCategoryBox(
    category: String,
    questions: List<Question>,
    selectedQuestions: MutableList<Question>,
    maxSelection: Int,
    onSelectionChange: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFE1E7))
            .padding(16.dp)
    ) {
        // Category Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        if (expanded) {
            questions.forEach { question ->
                val isSelected = selectedQuestions.contains(question)
                QuestionItem(
                    question = question,
                    isSelected = isSelected,
                    onClick = {
                        if (isSelected) {
                            selectedQuestions.remove(question)
                        } else if (selectedQuestions.size < maxSelection) {
                            selectedQuestions.add(question)
                        }
                        onSelectionChange()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun QuestionItem(
    question: Question,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFB2DFDB) else Color.White)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = question.question,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = if (isSelected) Color.Black else Color.Gray,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_plus),
                contentDescription = "Selected",
                tint = Color.Green,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun QuizOptionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC4D0))
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}


fun calculateDominantPersonality(selectedAnswers: List<Option>): String {
    val personalityScores = mutableMapOf<String, Int>()

    // Tally scores for each personality trait
    for (answer in selectedAnswers) {
        for (personality in answer.personalities) {
            personalityScores[personality.name] = personalityScores.getOrDefault(personality.name, 0) + 1
        }
    }

    // Find the personality with the highest score
    val dominantPersonality = personalityScores.maxByOrNull { it.value }?.key
    return dominantPersonality ?: "Unknown Personality"
}


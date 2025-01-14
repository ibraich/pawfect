package com.example.pawfect

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore

import androidx.compose.ui.text.style.TextAlign
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

import androidx.compose.material3.Icon

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.appinterface.R

@Preview
@Composable
fun PreviewCalibrationScreen() {
    CalibrationScreen(rememberNavController())
}

/*@Composable
fun CalibrationScreen(navController: NavHostController) {
    val questionList = Database.listOfQuestions

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var showSubmitButton by remember { mutableStateOf(false) }

    // Main container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF4F8))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                        .clickable { navController.navigate("profile_settings_screen") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Answer the quiz questions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Question number and progress
            Text(
                text = "${currentQuestionIndex + 1} / ${questionList.size}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Question card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFE1E7))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = questionList[currentQuestionIndex].getQuestion(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Answer buttons
            questionList[currentQuestionIndex].getOptions().forEach { option ->
                QuizOptionButton(text = option.getText()) {
                    if (currentQuestionIndex < questionList.size - 1) {
                        currentQuestionIndex++
                    } else {
                        showSubmitButton = true // show submit button after the last question
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

        }

        // Submit button
        if (showSubmitButton) {
            Button(
                onClick = { navController.navigate("profile_settings_screen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text(
                    text = "Submit quiz",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}*/


@Composable
fun CalibrationScreen(navController: NavHostController) {
    val categorizedQuestions = Database.getCategorizedQuestions() // Fetch categorized questions
    val selectedQuestions = remember { mutableStateListOf<Question>() }
    var isQuestionSelectionComplete by remember { mutableStateOf(false) }
    var currentQuestionIndex by remember { mutableStateOf(0) }

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
        } else {
            // Show the selected questions one by one
            if (currentQuestionIndex < selectedQuestions.size) {
                val question = selectedQuestions[currentQuestionIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = question.getQuestion(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    question.getOptions().forEach { option ->
                        QuizOptionButton(
                            text = option.getText(),
                            onClick = { currentQuestionIndex++ }
                        )
                    }
                }
            } else {
                // Quiz completed
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Quiz Complete!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Green
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("profile_settings_screen") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(
                            text = "Return to Profile",
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

        // Questions List (Expandable)
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
            text = question.getQuestion(),
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



package com.gradeassessment.ui.screen.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gradeassessment.domain.model.Question
import com.gradeassessment.domain.model.QuestionType
import com.gradeassessment.domain.model.Subject
import com.gradeassessment.ui.components.AssessmentProgressBar
import com.gradeassessment.ui.components.QuestionCard

@Composable
fun ActiveAssessmentScreen(
    question: Question?,
    subject: Subject,
    questionIndex: Int,
    timeRemaining: Int,
    onSubmitAnswer: (Question, String, List<String>, List<String>, Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    if (question == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading question...", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    var selectedAnswer by remember(question.id) { mutableStateOf("") }
    var selectedAnswers by remember(question.id) { mutableStateOf<List<String>>(emptyList()) }
    var orderedItems by remember(question.id) {
        // For ORDERING questions, always shuffle the initial display so items aren't in correct order.
        // Use orderedItems as the source (it's the correct answer), then shuffle.
        // If orderedItems is empty, fall back to choices (also shuffled).
        val source = question.orderedItems.ifEmpty { question.choices }
        val shuffled = source.shuffled()
        // If the shuffle happened to produce the correct order, shuffle again
        val initial = if (shuffled == question.orderedItems && shuffled.size > 1) {
            shuffled.shuffled()
        } else {
            shuffled
        }
        mutableStateOf(initial)
    }
    var fluencySelections by remember(question.id) { mutableStateOf<Set<String>>(emptySet()) }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp)
    ) {
        AssessmentProgressBar(
            currentQuestion = questionIndex,
            totalQuestions = 30,
            subject = subject,
            timeRemainingSeconds = timeRemaining
        )

        Spacer(Modifier.height(24.dp))

        // Passage-based questions handle their own scrolling internally;
        // other question types need an outer scroll wrapper.
        val contentModifier = if (question.type == QuestionType.PASSAGE_BASED) {
            Modifier.weight(1f)
        } else {
            Modifier.weight(1f).verticalScroll(rememberScrollState())
        }

        Box(modifier = contentModifier) {
            QuestionCard(
                question = question,
                selectedAnswer = selectedAnswer,
                selectedAnswers = selectedAnswers,
                orderedItems = orderedItems,
                fluencySelections = fluencySelections,
                onAnswerSelected = { selectedAnswer = it },
                onMultiAnswerToggled = { choice ->
                    if (question.type == QuestionType.CLOZE) {
                        selectedAnswers = choice.split("|")
                    } else {
                        selectedAnswers = if (choice in selectedAnswers) {
                            selectedAnswers - choice
                        } else {
                            selectedAnswers + choice
                        }
                    }
                },
                onOrderChanged = { orderedItems = it },
                onFluencyToggled = { item ->
                    fluencySelections = if (item in fluencySelections) {
                        fluencySelections - item
                    } else {
                        fluencySelections + item
                    }
                },
                onSubmit = {
                    onSubmitAnswer(question, selectedAnswer, selectedAnswers, orderedItems, fluencySelections)
                }
            )
        }
    }
}

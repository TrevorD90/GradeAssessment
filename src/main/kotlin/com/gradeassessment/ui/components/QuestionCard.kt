package com.gradeassessment.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gradeassessment.domain.model.Question
import com.gradeassessment.domain.model.QuestionType
import com.gradeassessment.domain.model.Subject

@Composable
fun QuestionCard(
    question: Question,
    selectedAnswer: String,
    selectedAnswers: List<String>,
    orderedItems: List<String>,
    fluencySelections: Set<String>,
    onAnswerSelected: (String) -> Unit,
    onMultiAnswerToggled: (String) -> Unit,
    onOrderChanged: (List<String>) -> Unit,
    onFluencyToggled: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHint by remember(question.id) { mutableStateOf(false) }

    if (showHint) {
        AlertDialog(
            onDismissRequest = { showHint = false },
            title = { Text("Help", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = generateHint(question),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHint = false }) {
                    Text("Got it")
                }
            }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            when (question.type) {
                QuestionType.PASSAGE_BASED -> {
                    PassageQuestion(
                        question = question,
                        selectedAnswer = selectedAnswer,
                        onAnswerSelected = onAnswerSelected,
                        onSubmit = onSubmit,
                        helpButton = { HelpButton { showHint = true } }
                    )
                }
                QuestionType.TIMED_FLUENCY -> {
                    TimedFluencyPanel(
                        question = question,
                        selections = fluencySelections,
                        onItemToggled = onFluencyToggled,
                        onSubmit = onSubmit
                    )
                }
                else -> {
                    // Prompt row with help button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = question.prompt,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        HelpButton { showHint = true }
                    }
                    Spacer(Modifier.height(16.dp))

                    when (question.type) {
                        QuestionType.MULTIPLE_CHOICE -> {
                            AnswerChoices(
                                choices = question.choices,
                                selectedAnswer = selectedAnswer,
                                onAnswerSelected = onAnswerSelected
                            )
                        }
                        QuestionType.MULTI_SELECT -> {
                            MultiSelectChoices(
                                choices = question.choices,
                                selectedAnswers = selectedAnswers,
                                onToggle = onMultiAnswerToggled
                            )
                        }
                        QuestionType.FILL_IN_BLANK -> {
                            FillInBlankField(
                                answer = selectedAnswer,
                                onAnswerChanged = onAnswerSelected
                            )
                        }
                        QuestionType.CLOZE -> {
                            ClozeQuestion(
                                question = question,
                                answers = selectedAnswers,
                                onAnswerChanged = { index, value ->
                                    val updated = selectedAnswers.toMutableList()
                                    while (updated.size <= index) updated.add("")
                                    updated[index] = value
                                    onMultiAnswerToggled(updated.joinToString("|"))
                                }
                            )
                        }
                        QuestionType.ORDERING -> {
                            OrderingQuestion(
                                items = orderedItems.ifEmpty { question.choices },
                                onOrderChanged = onOrderChanged
                            )
                        }
                        else -> {}
                    }

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = when (question.type) {
                            QuestionType.MULTIPLE_CHOICE -> selectedAnswer.isNotEmpty()
                            QuestionType.MULTI_SELECT -> selectedAnswers.isNotEmpty()
                            QuestionType.FILL_IN_BLANK -> selectedAnswer.isNotBlank()
                            QuestionType.CLOZE -> selectedAnswers.any { it.isNotBlank() }
                            QuestionType.ORDERING -> orderedItems.isNotEmpty()
                            else -> true
                        }
                    ) {
                        Text("Submit Answer")
                    }
                }
            }
        }
    }
}

@Composable
private fun HelpButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Help,
            contentDescription = "Help",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Generates a contextual hint tailored to the specific question.
 * Uses the question's Bloom's level, type, subject, prompt content,
 * and number of choices to give useful guidance without revealing the answer.
 */
private fun generateHint(question: Question): String {
    val parts = mutableListOf<String>()

    // 1. Bloom's-level strategy hint — tells the student WHAT kind of thinking is needed
    val bloomsHint = when (question.bloomsLevel) {
        1 -> "This question asks you to recall a basic fact. Think about what you already know or have learned."
        2 -> "This question asks you to show understanding. Try to explain the idea in your own words to yourself."
        3 -> "This question asks you to apply a concept to a specific situation. Think about the rules or steps that apply here."
        4 -> "This question asks you to analyze — break the problem into parts and think about how the pieces relate."
        5 -> "This question asks you to evaluate and make a judgment. Consider the evidence before deciding."
        else -> "This is a challenging question. Try combining ideas from different things you know."
    }
    parts.add(bloomsHint)

    // 2. Type-specific actionable hint
    val typeHint = when (question.type) {
        QuestionType.MULTIPLE_CHOICE -> {
            val numChoices = question.choices.size
            if (numChoices == 4)
                "There are 4 answer choices. Try to rule out the 2 that seem least likely, then compare the remaining 2 carefully."
            else
                "Read each choice. Eliminate any that are clearly wrong, then focus on what's left."
        }
        QuestionType.MULTI_SELECT ->
            "Test each option independently — ask yourself 'Does THIS one fit?' for each. Don't assume only one or two are correct."
        QuestionType.FILL_IN_BLANK ->
            "Try reading the sentence with different words in the blank. Which one makes it sound correct and complete?"
        QuestionType.CLOZE ->
            "Read the whole passage first to understand the meaning. Then go blank by blank — the surrounding words are your best clue."
        QuestionType.ORDERING ->
            "Look for a logical starting point (what happens first?). Then ask 'what must come right after this?' for each step."
        QuestionType.PASSAGE_BASED ->
            "The answer is in the passage. Scan for key words from the question, then read that section carefully."
        QuestionType.TIMED_FLUENCY ->
            "Work through items you're confident about first, then come back to harder ones if time allows."
    }
    parts.add(typeHint)

    // 3. Subject-specific contextual hint based on what's actually being asked
    val contextHint = when (question.subject) {
        Subject.MATH -> when {
            question.prompt.contains("fraction", true) -> "Think about parts of a whole. Drawing a picture can help."
            question.prompt.contains("area", true) || question.prompt.contains("perimeter", true) ->
                "Remember: area fills the inside, perimeter goes around the outside."
            question.prompt.contains("equation", true) || question.prompt.contains("solve", true) ->
                "Try to isolate what you're looking for by doing the same operation to both sides."
            question.prompt.contains("graph", true) || question.prompt.contains("coordinate", true) ->
                "Think about the x-axis (horizontal) and y-axis (vertical) and where the point falls."
            question.prompt.contains("angle", true) || question.prompt.contains("triangle", true) ->
                "Remember the properties of shapes — angles in a triangle add up to 180 degrees."
            else -> "Break the problem into smaller steps and work through it carefully."
        }
        Subject.READING -> when {
            question.prompt.contains("main idea", true) -> "The main idea is what the whole passage is mostly about, not just one detail."
            question.prompt.contains("infer", true) || question.prompt.contains("conclude", true) ->
                "The answer isn't stated directly. Use clues from the text to figure it out."
            question.prompt.contains("vocabulary", true) || question.prompt.contains("means", true) ->
                "Look at the words around the unknown word — they can help you figure out its meaning."
            else -> "Go back to the text and find the specific part that relates to this question."
        }
        Subject.WRITING -> when {
            question.prompt.contains("punctuation", true) || question.prompt.contains("comma", true) ->
                "Read the sentence aloud. Pauses often tell you where punctuation belongs."
            question.prompt.contains("tense", true) || question.prompt.contains("verb", true) ->
                "Pay attention to time clues in the sentence — words like 'yesterday,' 'now,' or 'tomorrow.'"
            question.prompt.contains("sentence", true) && question.prompt.contains("correct", true) ->
                "Check for subject-verb agreement, proper capitalization, and end punctuation."
            question.prompt.contains("parallel", true) ->
                "Items in a list should follow the same grammatical form."
            else -> "Say the sentence out loud. Your ear can often catch what looks okay on paper."
        }
        Subject.SCIENCE -> when {
            question.prompt.contains("experiment", true) || question.prompt.contains("hypothesis", true) ->
                "Think about variables — what changed, what stayed the same, and what was measured."
            question.prompt.contains("energy", true) -> "Think about the different forms of energy and how energy changes from one form to another."
            question.prompt.contains("cell", true) || question.prompt.contains("organism", true) ->
                "Think about the parts and what job each part does."
            question.prompt.contains("force", true) || question.prompt.contains("motion", true) ->
                "Think about pushes, pulls, and what makes things speed up or slow down."
            else -> "Think about cause and effect — what happens and why does it happen?"
        }
        Subject.ARTS -> when {
            question.prompt.contains("color", true) || question.prompt.contains("colour", true) ->
                "Think about the color wheel — primary, secondary, warm, cool, and complementary colors."
            question.prompt.contains("music", true) || question.prompt.contains("tempo", true) ->
                "Think about the vocabulary of music: tempo, rhythm, dynamics, pitch."
            question.prompt.contains("movement", true) || question.prompt.contains("period", true) ||
                question.prompt.contains("style", true) ->
                "Think about when and where this art style developed and what made it unique."
            else -> "Think about the elements of art (line, shape, color, texture) and the principles of design (balance, emphasis, rhythm)."
        }
        Subject.WORLD_HISTORY -> when {
            question.prompt.contains("cause", true) || question.prompt.contains("why", true) ->
                "Think about what conditions or events led up to this moment."
            question.prompt.contains("effect", true) || question.prompt.contains("result", true) ||
                question.prompt.contains("consequence", true) ->
                "Think about what changed after this event and who was affected."
            question.prompt.contains("order", true) || question.prompt.contains("chronolog", true) ||
                question.prompt.contains("first", true) ->
                "Think about what you know about the time periods and which events came earlier or later."
            else -> "Think about the who, what, where, when, and why of this historical topic."
        }
        Subject.SOCIAL_STUDIES -> when {
            question.prompt.contains("government", true) || question.prompt.contains("law", true) ->
                "Think about the different parts of government and what role each one plays."
            question.prompt.contains("map", true) || question.prompt.contains("geograph", true) ->
                "Think about the map tools: compass rose, legend, scale bar."
            question.prompt.contains("econom", true) || question.prompt.contains("price", true) ||
                question.prompt.contains("supply", true) ->
                "Think about how supply, demand, and competition affect prices and choices."
            else -> "Think about how people, communities, and systems are connected."
        }
        Subject.CRITICAL_THINKING -> when {
            question.prompt.contains("fallacy", true) || question.prompt.contains("flaw", true) ->
                "Look for a logical error — is the argument attacking the person, limiting choices, or jumping to conclusions?"
            question.prompt.contains("assumption", true) ->
                "An assumption is something the argument takes for granted without proving it. What is being accepted without evidence?"
            question.prompt.contains("conclude", true) || question.prompt.contains("inference", true) ->
                "Only choose a conclusion that MUST follow from the given information — not just something that might be true."
            question.prompt.contains("pattern", true) ->
                "Look at how each step relates to the one before it. What rule connects them?"
            question.prompt.contains("argument", true) || question.prompt.contains("strongest", true) ->
                "A strong argument uses specific evidence, not opinions, popularity, or single examples."
            else -> "Separate the facts from the reasoning. Are the facts true? Does the conclusion actually follow from them?"
        }
        Subject.IQ_ASSESSMENT -> when {
            question.prompt.contains("next", true) || question.prompt.contains("sequence", true) ||
                question.prompt.contains("pattern", true) ->
                "Look at the differences between each item. Is the change constant, or does it follow its own pattern?"
            question.prompt.contains(" is to ", true) || question.prompt.contains("analogy", true) ->
                "Figure out the relationship in the first pair, then apply that same relationship to the second pair."
            question.prompt.contains("NOT belong", true) || question.prompt.contains("does not belong", true) ->
                "Find the category that most items share. The odd one out breaks that rule."
            question.prompt.contains("fold", true) || question.prompt.contains("mirror", true) ||
                question.prompt.contains("cube", true) ->
                "Try to picture the transformation in your mind step by step."
            else -> "Look for the underlying rule or relationship that connects the items."
        }
    }
    parts.add(contextHint)

    return parts.joinToString("\n\n")
}
